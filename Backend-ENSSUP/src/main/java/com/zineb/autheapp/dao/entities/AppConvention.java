package com.zineb.autheapp.dao.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "convention_type_code", discriminatorType = DiscriminatorType.STRING)
public class AppConvention {
    @Id @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "convention_type_id")
    private AppConventionType conventionType;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by_id")
    private AppUser createdBy;
    private String title;
    private String conventionNumber;
    private String object;

    private LocalDate signatureDate;
    private LocalDate startDate;
    private LocalDate endDate;

    private String partners;
    private String filePath;

    private LocalDate createdAt = LocalDate.now();


    @ElementCollection
    @CollectionTable(name = "custom_fields", joinColumns = @JoinColumn(name = "convention_id"))
    @MapKeyColumn(name = "field_name")
    @Column(name = "field_value")
    private Map<String, String> customFields = new HashMap<>();
}
