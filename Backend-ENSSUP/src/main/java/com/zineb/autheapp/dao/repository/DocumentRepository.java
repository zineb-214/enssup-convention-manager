package com.zineb.autheapp.dao.repository;

import com.zineb.autheapp.dao.entities.Documents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Documents,Long> {
    boolean existsByNom(String nom);
}
