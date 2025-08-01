package com.zineb.autheapp.Service.interfaces;

import com.zineb.autheapp.dao.entities.Documents;
import org.springframework.stereotype.Service;


public interface DocumentService {
    void addDocument(Long conventionId, String nom);

}
