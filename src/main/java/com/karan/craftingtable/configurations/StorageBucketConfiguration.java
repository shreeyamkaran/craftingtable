package com.karan.craftingtable.configurations;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StorageBucketConfiguration {

    private final PropertiesConfiguration propertiesConfiguration;

    @Bean
    public MinioClient minioClient() {
        final String url = propertiesConfiguration.getMinioURL();
        final String accessKey = propertiesConfiguration.getMinioAccessKey();
        final String secretKey = propertiesConfiguration.getMinioSecretKey();
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

}
