package com.zineb.autheapp.mapper;

import com.zineb.autheapp.dao.entities.AppConvention;
import com.zineb.autheapp.dao.entities.ConventionAccord;
import com.zineb.autheapp.dao.entities.ConventionEchange;
import com.zineb.autheapp.dto.ConventionResponseDTO;
import com.zineb.autheapp.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class ConventionMapper {
    public ConventionResponseDTO toResponseDTO(AppConvention conv) {
        if (conv == null) return null;
        ConventionResponseDTO dto = new ConventionResponseDTO();

        dto.setId(conv.getId());
        dto.setTitle(conv.getTitle());
        dto.setConventionNumber(conv.getConventionNumber());
        dto.setObject(conv.getObject());
        dto.setSignatureDate(conv.getSignatureDate());
        dto.setStartDate(conv.getStartDate());
        dto.setEndDate(conv.getEndDate());
        dto.setPartners(conv.getPartners());
        dto.setFilePath(conv.getFilePath());
        dto.setCreatedAt(conv.getCreatedAt());
        dto.setCustomFields(conv.getCustomFields());

        // Type de convention
        if (conv.getConventionType() != null) {
            dto.setTypeCode(conv.getConventionType().getCode());
        }
        if (conv.getCreatedBy() != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(conv.getCreatedBy().getId());
            userDTO.setUsername(conv.getCreatedBy().getUsername());
            dto.setCreatedBy(userDTO);
        }
        if (conv instanceof ConventionEchange echange) {
            dto.setNatureEchange(echange.getNatureEchange());
            dto.setModaliteEchange(echange.getModaliteEchange());
            dto.setLogistique(echange.getLogistique());
            dto.setAssuranceResponsabilite(echange.getAssuranceResponsabilite());
            dto.setRenouvellement(echange.getRenouvellement());
            dto.setResiliation(echange.getResiliation());
        } else if (conv instanceof ConventionAccord accord) {
            dto.setPerimetre(accord.getPerimetre());
            dto.setModalite(accord.getModalite());
        }

        return dto;
    }
}
