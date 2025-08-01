package com.zineb.autheapp.dao.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
@DiscriminatorValue("ACCORD")
public class ConventionAccord extends  AppConvention {
    private String perimetre;
    private String Modalite;
}
