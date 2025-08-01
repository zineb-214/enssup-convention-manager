package com.zineb.autheapp.dao.repository;

import com.zineb.autheapp.dao.entities.AppPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<AppPermission,Long> {

}
