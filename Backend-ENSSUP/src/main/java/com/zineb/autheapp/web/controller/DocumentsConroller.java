package com.zineb.autheapp.web.controller;

import com.zineb.autheapp.Service.interfaces.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class DocumentsConroller {
    private final DocumentService documentService;
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addDocument(@RequestParam Long conventionId,
                                                           @RequestParam String nom) {
        try {
            documentService.addDocument(conventionId, nom);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Document ajouté avec succès");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

}
