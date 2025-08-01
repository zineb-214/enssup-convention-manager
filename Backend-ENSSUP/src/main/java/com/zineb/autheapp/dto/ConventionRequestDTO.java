package com.zineb.autheapp.dto;


import lombok.Data;

import java.time.LocalDate;

import java.util.Map;

@Data
public class ConventionRequestDTO {
    private Long typeId;
    private String title;
    private String conventionNumber;
    private String object;
    private LocalDate signatureDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String partners;
    private String filePath;


    private String natureEchange;
    private String modaliteEchange;
    private String logistique;
    private String assuranceResponsabilite;
    private String renouvellement;
    private String resiliation;

    private String perimetre;
    private String modalite;

    private Map<String, String> customFields;
}


