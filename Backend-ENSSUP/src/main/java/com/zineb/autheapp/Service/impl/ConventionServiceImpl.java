package com.zineb.autheapp.Service.impl;

import com.zineb.autheapp.Service.interfaces.ConventionService;
import com.zineb.autheapp.dao.entities.*;
import com.zineb.autheapp.dao.repository.*;
import com.zineb.autheapp.dto.ConventionRequestDTO;
import com.zineb.autheapp.dto.ConventionResponseDTO;
import com.zineb.autheapp.mapper.ConventionMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ConventionServiceImpl implements ConventionService {

    private final ConventionRepository conventionRepository;
    private final ConventionTypeRepository typeRepo;
    private final UserRepository userRepo;
    private final ConventionMapper conventionMapper;

    @Override
    public AppConvention createConvention(ConventionRequestDTO dto) {
        AppConventionType type = typeRepo.findById(dto.getTypeId())
                .orElseThrow(() -> new RuntimeException("Type non trouvé"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        String username = authentication.getName();
        AppUser user = userRepo.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Utilisateur introuvable pour le nom d'utilisateur : " + username);
        }

        AppConvention convention;

        switch (type.getCode()) {
            case "ECHANGES":
                ConventionEchange echange = new ConventionEchange();
                echange.setNatureEchange(dto.getNatureEchange());
                echange.setModaliteEchange(dto.getModaliteEchange());
                echange.setLogistique(dto.getLogistique());
                echange.setAssuranceResponsabilite(dto.getAssuranceResponsabilite());
                echange.setRenouvellement(dto.getRenouvellement());
                echange.setResiliation(dto.getResiliation());
                convention = echange;
                break;

            case "ACCORD":
                ConventionAccord accord = new ConventionAccord();
                accord.setPerimetre(dto.getPerimetre());
                accord.setModalite(dto.getModalite());
                convention = accord;
                break;

            default:
                throw new IllegalArgumentException("Type de convention inconnu : " + type.getCode());
        }

        if (dto.getCustomFields() != null) {
            convention.setCustomFields(dto.getCustomFields());
        }

        convention.setConventionType(type);
        convention.setCreatedBy(user);
        convention.setTitle(dto.getTitle());
        convention.setConventionNumber(dto.getConventionNumber());
        convention.setObject(dto.getObject());
        convention.setSignatureDate(dto.getSignatureDate());
        convention.setStartDate(dto.getStartDate());
        convention.setEndDate(dto.getEndDate());
        convention.setPartners(dto.getPartners());
        convention.setFilePath(dto.getFilePath());

        return conventionRepository.save(convention);
    }




    @Override
    public ConventionResponseDTO toResponseDTO(AppConvention conv) {
        return conventionMapper.toResponseDTO(conv);
    }

    @Override
    @Transactional
    public AppConvention updateConvention(Long id, ConventionRequestDTO dto) {
        AppConvention existing = conventionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Convention non trouvée"));

        AppConventionType newType = typeRepo.findById(dto.getTypeId())
                .orElseThrow(() -> new RuntimeException("Type non trouvé"));

        String oldCode = existing.getConventionType().getCode();
        String newCode = newType.getCode();

        // Cas où le type change : suppression + recréation propre
        if (!oldCode.equals(newCode)) {
            // Supprimer l'ancienne convention
            conventionRepository.delete(existing);
            // Forcer Hibernate à exécuter la suppression
            conventionRepository.flush();

            // Recréation du bon type
            AppConvention updated;

            if ("ECHANGES".equals(newCode)) {
                ConventionEchange echange = new ConventionEchange();
                echange.setNatureEchange(dto.getNatureEchange());
                echange.setModaliteEchange(dto.getModaliteEchange());
                echange.setLogistique(dto.getLogistique());
                echange.setAssuranceResponsabilite(dto.getAssuranceResponsabilite());
                echange.setRenouvellement(dto.getRenouvellement());
                echange.setResiliation(dto.getResiliation());
                updated = echange;
            } else if ("ACCORD".equals(newCode)) {
                ConventionAccord accord = new ConventionAccord();
                accord.setPerimetre(dto.getPerimetre());
                accord.setModalite(dto.getModalite());
                updated = accord;
            } else {
                throw new RuntimeException("Type de convention inconnu : " + newCode);
            }

            // Champs communs
            updated.setConventionType(newType);
            updated.setTitle(dto.getTitle());
            updated.setConventionNumber(dto.getConventionNumber());
            updated.setObject(dto.getObject());
            updated.setSignatureDate(dto.getSignatureDate());
            updated.setStartDate(dto.getStartDate());
            updated.setEndDate(dto.getEndDate());
            updated.setPartners(dto.getPartners());
            updated.setFilePath(dto.getFilePath());
            updated.setCreatedAt(LocalDate.now());
            updated.setCreatedBy(existing.getCreatedBy()); // garder le créateur

            if (dto.getCustomFields() != null) {
                updated.setCustomFields(dto.getCustomFields());
            }

            return conventionRepository.save(updated);
        }

        // Sinon, simple mise à jour
        existing.setConventionType(newType);
        existing.setTitle(dto.getTitle());
        existing.setConventionNumber(dto.getConventionNumber());
        existing.setObject(dto.getObject());
        existing.setSignatureDate(dto.getSignatureDate());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setPartners(dto.getPartners());
        existing.setFilePath(dto.getFilePath());

        if (existing instanceof ConventionEchange echange) {
            echange.setNatureEchange(dto.getNatureEchange());
            echange.setModaliteEchange(dto.getModaliteEchange());
            echange.setLogistique(dto.getLogistique());
            echange.setAssuranceResponsabilite(dto.getAssuranceResponsabilite());
            echange.setRenouvellement(dto.getRenouvellement());
            echange.setResiliation(dto.getResiliation());
        } else if (existing instanceof ConventionAccord accord) {
            accord.setPerimetre(dto.getPerimetre());
            accord.setModalite(dto.getModalite());
        }

        if (dto.getCustomFields() != null) {
            existing.setCustomFields(dto.getCustomFields());
        }

        return conventionRepository.save(existing);
    }



    @Override
    public Optional<AppConvention> findById(Long id) {
        return conventionRepository.findById(id);
    }

    @Override
    public void deleteConvention(Long id) {
        AppConvention convention = conventionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Convention introuvable"));
        conventionRepository.delete(convention);
    }

    @Override
    public List<ConventionResponseDTO> getConventions() {
        List<AppConvention> conventions = conventionRepository.findAll();
        return conventions.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ConventionResponseDTO> filterConventions(String code, String title, LocalDate startDateFrom, LocalDate startDateTo, Pageable pageable) {
        Specification<AppConvention> spec = Specification.where(null);

        if (code != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("conventionType").get("code"), code));
        }

        if (title != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }

        if (startDateFrom != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("startDate"), startDateFrom));
        }

        if (startDateTo != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("startDate"), startDateTo));
        }

        Page<AppConvention> resultPage = conventionRepository.findAll(spec, pageable);
        return resultPage.map(this::toResponseDTO);
    }

    @Override
    public ConventionResponseDTO getConventionDtoById(Long id) {
        AppConvention conv = conventionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Convention not found with id = " + id));

        return conventionMapper.toResponseDTO(conv);
    }
}

