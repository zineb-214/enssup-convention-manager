package com.zineb.autheapp.web.controller;

import com.zineb.autheapp.Service.impl.MinioService;
import com.zineb.autheapp.Service.interfaces.DocumentService;
import com.zineb.autheapp.dao.entities.Documents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import java.io.InputStream;

@RestController
@RequestMapping("/files")
@PreAuthorize("hasRole('USER')")
public class MinioController {
    @Autowired
    private MinioService minioService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.getSize() > 20 * 1024 * 1024) {
                throw new IllegalArgumentException("Fichier trop volumineux (>20MB)");
            }
            String fileName = minioService.uploadFile(file);
            return ResponseEntity.ok("Fichier uploadé : " + fileName);
        } catch (Exception e) {
            e.printStackTrace(); // s'affichera dans la console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
    }


    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> download(@PathVariable String fileName) throws Exception {
        InputStream inputStream = minioService.downloadFile(fileName);
        long fileSize = minioService.getFileSize(fileName);

        InputStreamResource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(fileSize)
                .body(resource);
    }


    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> delete(@PathVariable String fileName) throws Exception {
        minioService.deleteFile(fileName);
        return ResponseEntity.ok("Fichier supprimé : " + fileName);
    }


}
