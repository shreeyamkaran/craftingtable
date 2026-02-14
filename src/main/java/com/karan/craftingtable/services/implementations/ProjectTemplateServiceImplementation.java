package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.configurations.PropertiesConfiguration;
import com.karan.craftingtable.entities.ProjectEntity;
import com.karan.craftingtable.entities.ProjectFileEntity;
import com.karan.craftingtable.exceptions.ResourceNotFoundException;
import com.karan.craftingtable.repositories.ProjectFileRepository;
import com.karan.craftingtable.repositories.ProjectRepository;
import com.karan.craftingtable.services.ProjectTemplateService;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectTemplateServiceImplementation implements ProjectTemplateService {

    private final MinioClient minioClient;
    private final ProjectFileRepository projectFileRepository;
    private final ProjectRepository projectRepository;
    private final PropertiesConfiguration propertiesConfiguration;

    @Override
    public void initialiseProjectFromTemplate(Long projectId) {
        final String PROJECT_BUCKET = propertiesConfiguration.getMinioProjectBucket();
        final String TEMPLATE_BUCKET = propertiesConfiguration.getMinioTemplateBucket();
        final String TEMPLATE_NAME = propertiesConfiguration.getMinioTemplateName();
        ProjectEntity project = projectRepository.findById(projectId).orElseThrow(
                () -> new ResourceNotFoundException("Project with id " + projectId + " not found"));
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(TEMPLATE_BUCKET)
                            .prefix(TEMPLATE_NAME + "/")
                            .recursive(true)
                            .build()
            );
            List<ProjectFileEntity> filesToSave = new ArrayList<>(); // for metadata in postgres db
            for (Result<Item> result : results) {
                Item item = result.get();
                String sourceKey = item.objectName();
                String cleanPath = sourceKey.replaceFirst(TEMPLATE_NAME + "/", "");
                String destKey = projectId + "/" + cleanPath;
                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(PROJECT_BUCKET)
                                .object(destKey)
                                .source(
                                        CopySource.builder()
                                                .bucket(TEMPLATE_BUCKET)
                                                .object(sourceKey)
                                                .build()
                                )
                                .build()
                );
                ProjectFileEntity pf = ProjectFileEntity.builder()
                        .project(project)
                        .path(cleanPath)
                        .storageBucketObjectKey(destKey)
                        .build();
                filesToSave.add(pf);
            }
            projectFileRepository.saveAll(filesToSave);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize project from template", e);
        }
    }

}
