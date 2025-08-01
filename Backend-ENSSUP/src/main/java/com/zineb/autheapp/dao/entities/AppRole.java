    package com.zineb.autheapp.dao.entities;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.io.Serializable;

    @Entity
    @Data
   @NoArgsConstructor @AllArgsConstructor
    @Table(name = "roles")
    public class AppRole implements Serializable {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
    }
