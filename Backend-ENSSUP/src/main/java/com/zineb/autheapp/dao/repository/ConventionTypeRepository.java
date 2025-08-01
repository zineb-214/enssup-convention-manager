package com.zineb.autheapp.dao.repository;

import com.zineb.autheapp.dao.entities.AppConvention;
import com.zineb.autheapp.dao.entities.AppConventionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ConventionTypeRepository extends JpaRepository<AppConventionType,Long> , JpaSpecificationExecutor<AppConventionType> {
    Page<AppConventionType> findAll(Pageable pageable);

}
