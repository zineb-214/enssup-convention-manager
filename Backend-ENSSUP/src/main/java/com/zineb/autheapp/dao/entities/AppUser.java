package com.zineb.autheapp.dao.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;


@Entity
@Data @NoArgsConstructor
@AllArgsConstructor

@Table(name = "users")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Column(nullable = true)
    private String email;
    @Column(name = "created_at")
    private LocalDate createdAt = LocalDate.now();
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<AppRole> roles=new ArrayList<>();
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<AppPermission> permissions=new ArrayList<>();
    private boolean isDeleted=false;
}
