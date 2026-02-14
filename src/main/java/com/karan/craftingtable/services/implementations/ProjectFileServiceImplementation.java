package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.configurations.PropertiesConfiguration;
import com.karan.craftingtable.entities.ProjectEntity;
import com.karan.craftingtable.entities.ProjectFileEntity;
import com.karan.craftingtable.exceptions.ResourceNotFoundException;
import com.karan.craftingtable.mappers.ProjectFileMapper;
import com.karan.craftingtable.models.responses.FileContentResponseDTO;
import com.karan.craftingtable.models.responses.FileNodeResponseDTO;
import com.karan.craftingtable.repositories.ProjectFileRepository;
import com.karan.craftingtable.repositories.ProjectRepository;
import com.karan.craftingtable.services.ProjectFileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectFileServiceImplementation implements ProjectFileService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;
    private final PropertiesConfiguration propertiesConfiguration;
    private final MinioClient minioClient;
    private final ProjectFileMapper projectFileMapper;

    @Override
    public List<FileNodeResponseDTO> getFileTree(Long projectId) {
        List<ProjectFileEntity> projectFileList = projectFileRepository.findByProjectId(projectId);
        return projectFileMapper.toFileNodeResponseDTOList(projectFileList);
    }

    @Override
    public FileContentResponseDTO getFileContent(Long projectId, String filePath) {
        final String PROJECT_BUCKET = propertiesConfiguration.getMinioProjectBucket();
        String objectName = projectId + "/" + filePath;
        try (
                InputStream is = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(PROJECT_BUCKET)
                                .object(objectName)
                                .build()
                )
        ) {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return new FileContentResponseDTO(filePath, content);
        } catch (Exception e) {
            log.error("Failed to read file: {}/{}", projectId, filePath, e);
            throw new RuntimeException("Failed to read file content", e);
        }
    }

    @Override
    public void saveFile(Long projectId, String filePath, String fileContent) {
        ProjectEntity project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));
        String cleanPath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        String objectKey = projectId + "/" + cleanPath;
        final String projectBucket = propertiesConfiguration.getMinioProjectBucket();
        try {
            byte[] contentBytes = fileContent.getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(contentBytes);
            // saving the file content
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(projectBucket)
                            .object(objectKey)
                            .stream(inputStream, contentBytes.length, -1)
                            .contentType(this.determineContentType(filePath))
                            .build());
            // Saving the metadata
            ProjectFileEntity file = projectFileRepository.findByProjectIdAndPath(projectId, cleanPath)
                    .orElseGet(() -> ProjectFileEntity.builder()
                            .project(project)
                            .path(cleanPath)
                            .storageBucketObjectKey(objectKey)
                            .build());
            projectFileRepository.save(file);
            log.info("Saved file: {}", objectKey);
        } catch (Exception e) {
            log.error("Failed to save file {}/{}", projectId, cleanPath, e);
            throw new RuntimeException("File save failed", e);
        }
    }

    private String determineContentType(String path) {
        String type = URLConnection.guessContentTypeFromName(path);
        if (type != null) return type;
        if (path.endsWith(".js") || path.endsWith(".jsx") || path.endsWith(".ts") || path.endsWith(".tsx")) return "text/javascript";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".css")) return "text/css";
        return "text/plain";
    }

}
