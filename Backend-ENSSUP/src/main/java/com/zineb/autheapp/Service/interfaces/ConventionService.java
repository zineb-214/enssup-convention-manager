package com.zineb.autheapp.Service.interfaces;

import com.zineb.autheapp.dao.entities.AppConvention;
import com.zineb.autheapp.dto.ConventionRequestDTO;
import com.zineb.autheapp.dto.ConventionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public interface ConventionService {
    AppConvention createConvention(ConventionRequestDTO dto);
    List<ConventionResponseDTO> getConventions();
    ConventionResponseDTO toResponseDTO(AppConvention conv);
    AppConvention updateConvention(Long id, ConventionRequestDTO dto);
    void deleteConvention(Long id);
    Page<ConventionResponseDTO> filterConventions(
            String code,
            String title,
            LocalDate startDateFrom,
            LocalDate startDateTo,
            Pageable pageable
    );

    Optional<AppConvention> findById(Long id);
    ConventionResponseDTO getConventionDtoById(Long id);
}
