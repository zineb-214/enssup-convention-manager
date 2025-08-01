package com.zineb.autheapp.dao.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@DiscriminatorValue("ECHANGES")
public class ConventionEchange extends AppConvention{
    private String natureEchange;
    private String modaliteEchange;
    private String logistique;
    private String assuranceResponsabilite;
    private String renouvellement;
    private String resiliation;
}
