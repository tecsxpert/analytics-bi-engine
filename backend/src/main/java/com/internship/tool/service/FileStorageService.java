package com.internship.tool.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;


    public String storeFile(MultipartFile file) throws IOException {


        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }


        long maxSize = 10 * 1024 * 1024;

        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException(
                    "File size exceeds 10 MB limit"
            );
        }


        String contentType = file.getContentType();

        if (contentType == null ||
                !(contentType.equals("image/png") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("application/pdf"))) {

            throw new IllegalArgumentException(
                    "Only PNG, JPG, and PDF files are allowed"
            );
        }


        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }


        String originalFilename =
                file.getOriginalFilename();

        String extension = "";

        if (originalFilename != null &&
                originalFilename.contains(".")) {

            extension =
                    originalFilename.substring(
                            originalFilename.lastIndexOf(".")
                    );
        }

        String storedFilename =
                UUID.randomUUID() + extension;


        Path filePath =
                uploadPath.resolve(storedFilename);

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        return storedFilename;
    }

    public Path loadFile(String filename) {

        return Paths.get(uploadDir)
                .resolve(filename)
                .normalize();
    }
}