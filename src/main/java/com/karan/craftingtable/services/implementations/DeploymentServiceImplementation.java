package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.constants.KubernetesConstants;
import com.karan.craftingtable.models.responses.DeploymentResponseDTO;
import com.karan.craftingtable.services.DeploymentService;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeploymentServiceImplementation implements DeploymentService {

    private final KubernetesClient kubernetesClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public DeploymentResponseDTO deploy(Long projectId) {
        String domain = "project-" + projectId + ".app.domain.com";
        Pod existingPod = this.findActivePod(projectId);
        if (existingPod != null) {
            registerRoute(domain, existingPod);
            String previewURL = "http://" + domain + ":" + KubernetesConstants.REVERSE_PROXY_PORT;
            return new DeploymentResponseDTO(previewURL);
        }
        return this.claimAndStartNewPod(projectId, domain);
    }

    private DeploymentResponseDTO claimAndStartNewPod(Long projectId, String domain) {
        Pod pod = kubernetesClient.pods()
                    .inNamespace(KubernetesConstants.NAMESPACE)
                    .withLabel(KubernetesConstants.POOL_LABEL, KubernetesConstants.IDLE)
                    .list()
                    .getItems()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No idle pod available. Please scale-up the the runner-pool"));
        String podName = pod.getMetadata().getName();
        log.info("Claiming pod {} for project {}", podName, projectId);
        kubernetesClient.pods()
                .inNamespace(KubernetesConstants.NAMESPACE)
                .withName(podName)
                .edit(p -> {
                    p.getMetadata().getLabels().put(KubernetesConstants.POOL_LABEL, KubernetesConstants.BUSY);
                    p.getMetadata().getLabels().put(KubernetesConstants.PROJECT_LABEL, String.valueOf(projectId));
                    return p;
                });
        try {
            // Syncer Commands
            String initialSyncCmd = String.format(
                    "mc mirror --overwrite myminio/projects/%d/ /app/",
                    projectId);

            log.info("Starting initial sync for project {} in pod {}", projectId, podName);
            execCommand(podName, KubernetesConstants.SYNCER_CONTAINER, "sh", "-c", initialSyncCmd);

            String watchCmd = String.format(
                    "nohup mc mirror --overwrite --watch myminio/projects/%d/ /app/ > /app/sync.log 2>&1 &",
                    projectId);
            execCommand(podName, KubernetesConstants.SYNCER_CONTAINER, "sh", "-c", watchCmd);

            // Runner Commands
            String startCmd = "npm install && nohup npm run dev -- --host 0.0.0.0 --port 5173 > /app/dev.log 2>&1 &";

            log.info("Starting dev server for project {}...", projectId);
            execCommand(podName, KubernetesConstants.RUNNER_CONTAINER, "sh", "-c", startCmd);

            registerRoute(domain, pod);

            log.info("Deployment successful: http://{}:{}", domain, KubernetesConstants.REVERSE_PROXY_PORT);
            return new DeploymentResponseDTO("http://" + domain + ":" + KubernetesConstants.REVERSE_PROXY_PORT);

        } catch(Exception e) {
            log.error("Deployment failed for project {}. Releasing pod {}.", projectId, podName, e);
            kubernetesClient.pods().inNamespace(KubernetesConstants.NAMESPACE).withName(podName).delete();
            throw new RuntimeException("Failed to deploy the project with id: "+projectId);
        }
    }

    private void execCommand(String podName, String container, String... command) {
        log.debug("Exec in {}:{} -> {}", podName, container, String.join(" ", command));

        CompletableFuture<String> data = new CompletableFuture<>();
        try (ExecWatch ignored = kubernetesClient.pods().inNamespace(KubernetesConstants.NAMESPACE).withName(podName)
                .inContainer(container)
                .writingOutput(new ByteArrayOutputStream())
                .writingError(new ByteArrayOutputStream())
                .usingListener(new ExecListener() {
                    @Override
                    public void onClose(int code, String reason) {
                        data.complete("Done");
                    }
                })
                .exec(command)) {

            // Wait briefly to ensure command fired (Fabric8 exec is async)
            // For long-running background jobs (nohup), we don't wait for "Done"
            if (command[command.length - 1].trim().endsWith("&")) {
                Thread.sleep(500);
            } else {
                data.get(30, TimeUnit.SECONDS); // Block for synchronous setup commands (npm install)
            }

        } catch (Exception e) {
            log.error("Exec failed", e);
            throw new RuntimeException("Pod Execution Failed", e);
        }
    }

    private void registerRoute(String domain, Pod pod) {
        String podIp = pod.getStatus().getPodIP();
        if (podIp == null) throw new RuntimeException("Pod is running but has no IP!");

        stringRedisTemplate.opsForValue().set("route:" + domain, podIp + ":5173", 6, TimeUnit.HOURS);
    }

    private Pod findActivePod(Long projectId) {
        return kubernetesClient.pods()
                .inNamespace(KubernetesConstants.NAMESPACE)
                .withLabel(KubernetesConstants.PROJECT_LABEL, String.valueOf(projectId))
                .withLabel(KubernetesConstants.POOL_LABEL, KubernetesConstants.BUSY)
                .list()
                .getItems()
                .stream()
                .filter(pod -> pod.getStatus().getPhase().equals("Running"))
                .findFirst()
                .orElse(null);
    }

}
