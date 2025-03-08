package br.com.ceciliagomes.demo_minio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class MinioClientConfig {
    @Bean
    MinioClient minioClient(){
        return MinioClient.builder().endpoint("http://127.0.0.1:9000").credentials("ROOTUSER", "CHANGEME123")
        .build();}

        
}

