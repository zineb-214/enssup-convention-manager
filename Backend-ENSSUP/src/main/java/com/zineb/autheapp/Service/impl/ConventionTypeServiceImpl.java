package com.zineb.autheapp.Service.impl;

import com.zineb.autheapp.Service.interfaces.ConventionTypeService;
import com.zineb.autheapp.dao.entities.AppConventionType;
import com.zineb.autheapp.dao.repository.ConventionTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

@Service
@Transactional
@AllArgsConstructor
public class ConventionTypeServiceImpl implements ConventionTypeService {


    private ConventionTypeRepository conventionTypeRepository;
    @Override
    public AppConventionType addtype(AppConventionType appConventionType) {
        return conventionTypeRepository.save(appConventionType);
    }

    @Override
    public List<AppConventionType> listTypes() {
        return conventionTypeRepository.findAll();
    }

    @Override
    public void deleteType(Long id) {
        if (!conventionTypeRepository.existsById(id)) {
            throw new RuntimeException("Type de convention non trouvé avec l'id : " + id);
        }
        conventionTypeRepository.deleteById(id);
    }

    @Override
    public AppConventionType update(Long id, AppConventionType appConventionType) {
        AppConventionType existingType = conventionTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type de convention non trouvé avec l'id : " + id));

        existingType.setName(appConventionType.getName());
        existingType.setDescription(appConventionType.getDescription());
        existingType.setCode(appConventionType.getCode());

        return conventionTypeRepository.save(existingType);
    }

    @Override
    public Page<AppConventionType> filterTypes(String name, String code, Pageable pageable) {
        Specification<AppConventionType> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (code != null && !code.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return conventionTypeRepository.findAll(specification, pageable);
    }
}
