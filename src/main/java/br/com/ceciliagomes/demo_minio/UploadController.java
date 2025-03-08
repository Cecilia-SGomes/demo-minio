package br.com.ceciliagomes.demo_minio;

import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

@RestController
@RequestMapping("images")
public class UploadController {
    private final MinioClient minioClient;
    private final JdbcClient jdbcClient;


    public UploadController(MinioClient minioClient, JdbcClient jdbcClient) {
        this.minioClient = minioClient;
        this.jdbcClient = jdbcClient;
    }

    @PostMapping
    public  void upload(@RequestParam MultipartFile file) throws Exception{
        var inputStream = file.getInputStream();
        var objectId = UUID.randomUUID().toString();

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("images")
                        .object(objectId)
                        .stream(inputStream, inputStream.available(), -1)
                        .contentType("image/png")
                        .build()
        );
        jdbcClient.sql("""
            INSERT INTO images (object_id) VALUES (:objectId)
            """)
            .param("objectId", objectId)
            .update();
    }

    @GetMapping(value = "/{objectId}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getImage(@PathVariable String objectId) throws Exception{
        var stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("images")
                        .object(objectId)
                        .build()
        );

        return  IOUtils.toByteArray(stream);        
    }
}


