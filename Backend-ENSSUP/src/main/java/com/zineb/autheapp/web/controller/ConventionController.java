package com.zineb.autheapp.web.controller;

import com.zineb.autheapp.dao.entities.*;
import com.zineb.autheapp.dao.repository.ConventionRepository;
import com.zineb.autheapp.dto.ConventionRequestDTO;
import com.zineb.autheapp.dto.ConventionResponseDTO;
import com.zineb.autheapp.Service.interfaces.ConventionService;
import com.zineb.autheapp.Service.interfaces.ConventionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/user")
@PreAuthorize("hasRole('USER')")
public class ConventionController {
    @Autowired
    private ConventionService conventionService;
    @Autowired
    private ConventionTypeService conventionTypeService;

    @PostMapping("/createConvention")
    public ResponseEntity<?> create(@RequestBody ConventionRequestDTO dto) {
        try {
            AppConvention created = conventionService.createConvention(dto);
            ConventionResponseDTO response = conventionService.toResponseDTO(created); // ou une réponse simple
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            // Affiche clairement l’erreur dans la console (backend)
            ex.printStackTrace();

            // Retourne l'erreur dans la réponse HTTP
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }
    }


    @GetMapping("/conventions")
    public List<ConventionResponseDTO> getConventions() {
        return conventionService.getConventions();
    }


    @PutMapping("/updateConvention/{id}")
    public ResponseEntity<?> updateConvention(@PathVariable Long id, @RequestBody ConventionRequestDTO dto) {
        try {

            AppConvention updated = conventionService.updateConvention(id, dto);
            ConventionResponseDTO response = conventionService.toResponseDTO(updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
    }

    @GetMapping("/convention/{id}")
    public ResponseEntity<ConventionResponseDTO> getConventionById(@PathVariable Long id) {
        Optional<AppConvention> optConv = conventionService.findById(id);
        if (optConv.isPresent()) {
            ConventionResponseDTO dto = conventionService.toResponseDTO(optConv.get());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/deleteConvention/{id}")
    public ResponseEntity<?> deleteConvention(@PathVariable Long id) {
        try {
            conventionService.deleteConvention(id);
            return ResponseEntity.ok("Convention supprimée avec succès");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
    }
    @GetMapping("/public/types")
    public List<AppConventionType> getTypesForUsers() {
        return conventionTypeService.listTypes();
    }

    @GetMapping("/conventions/filter")
    public ResponseEntity<Page<ConventionResponseDTO>> filter(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateTo,
            @PageableDefault(size = 10, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ConventionResponseDTO> page = conventionService.filterConventions(code, title, startDateFrom, startDateTo, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/conventions/{id}/pdf")
    public ResponseEntity<byte[]> generateConventionPdf(@PathVariable Long id) throws Exception {
        ConventionResponseDTO dto = conventionService.getConventionDtoById(id);  // Appelle le mapper
     //   byte[] pdf = pdfGenerationService.generatePdf(dto);

        String rawFilename = dto.getFilePath() != null ? dto.getFilePath() : "convention-" + id + ".pdf";
        String filename = Paths.get(rawFilename).getFileName().toString();  // Supprime le chemin

        // S’assurer que le fichier a bien l’extension .pdf
        if (!filename.toLowerCase().endsWith(".pdf")) {
            filename = filename + ".pdf";
        }
        System.out.println("DTO FilePath = " + dto.getFilePath());
        return null;
            /*    .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(pdf);*/
    }



}
