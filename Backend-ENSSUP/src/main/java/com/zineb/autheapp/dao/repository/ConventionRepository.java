package com.zineb.autheapp.dao.repository;

import com.zineb.autheapp.dao.entities.AppConvention;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ConventionRepository extends JpaRepository<AppConvention,Long>, JpaSpecificationExecutor<AppConvention> {

    Page<AppConvention> findAll(Pageable pageable);

}
