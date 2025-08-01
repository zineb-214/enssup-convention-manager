package com.zineb.autheapp.dao.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor @NoArgsConstructor @Data
@Table(name = "documents", uniqueConstraints = {
        @UniqueConstraint(columnNames = "nom")
})
public class Documents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "convention_id")
    private AppConvention convention;
    @Column(unique = true, nullable = false)
    private String nom;
}
