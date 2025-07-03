package com.tecsup.demo.content.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Service
public class S3Service {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private AmazonS3 amazonS3;

    public String uploadFile(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        amazonS3.putObject(bucketName, filename, file.getInputStream(), metadata);

        return amazonS3.getUrl(bucketName, filename).toString();
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.contains(bucketName)) {
            System.out.println("⚠️  URL no válida o externa, no se eliminará: " + fileUrl);
            return;
        }

        try {
            URL url = new URL(fileUrl);
            String key = url.getPath().substring(1); // Remueve el "/" inicial

            amazonS3.deleteObject(bucketName, key);
            System.out.println("✅ Archivo eliminado de S3: " + key);

        } catch (AmazonServiceException e) {
            System.err.println("Error al eliminar archivo de S3: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado al procesar la URL: " + e.getMessage());
            throw new RuntimeException("Error al procesar la URL del archivo", e);
        }
    }
}
