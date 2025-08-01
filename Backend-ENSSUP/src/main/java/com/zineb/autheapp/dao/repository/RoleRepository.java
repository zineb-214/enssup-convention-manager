package com.zineb.autheapp.dao.repository;

import com.zineb.autheapp.dao.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<AppRole, Long> {
       AppRole findByName(String name);
}
