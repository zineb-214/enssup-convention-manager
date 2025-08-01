package com.zineb.autheapp.Service.interfaces;

import com.zineb.autheapp.dao.entities.AppConvention;
import com.zineb.autheapp.dao.entities.AppConventionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConventionTypeService {
    AppConventionType addtype(AppConventionType appConventionType);
    List<AppConventionType> listTypes();
    AppConventionType update(Long id,AppConventionType appConventionType);
    void deleteType(Long id);
    Page<AppConventionType> filterTypes(String name,String code ,Pageable pageable);

}
