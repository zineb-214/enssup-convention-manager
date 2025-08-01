package com.zineb.autheapp.dao.repository;

import com.zineb.autheapp.dao.entities.ConventionEchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EchangeRepository extends JpaRepository<ConventionEchange, Long>
{
}
