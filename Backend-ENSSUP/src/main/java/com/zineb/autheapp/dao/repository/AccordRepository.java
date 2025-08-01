package com.zineb.autheapp.dao.repository;

import com.zineb.autheapp.dao.entities.ConventionAccord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccordRepository extends JpaRepository<ConventionAccord,Long> {
}
