package com.internship.tool.controller;

import com.internship.tool.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;

import java.nio.file.Path;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;


    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        String filename =
                fileStorageService.storeFile(file);

        Map<String, String> response =
                new HashMap<>();

        response.put("message", "File uploaded successfully");
        response.put("filename", filename);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/files/{filename}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String filename
    ) throws IOException {

        Path filePath =
                fileStorageService.loadFile(filename);

        Resource resource =
                new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("File not found");
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                resource.getFilename() + "\""
                )
                .body(resource);
    }
}