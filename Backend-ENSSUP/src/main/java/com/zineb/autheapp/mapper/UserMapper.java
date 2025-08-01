package com.zineb.autheapp.mapper;

import com.zineb.autheapp.dao.entities.AppPermission;
import com.zineb.autheapp.dao.entities.AppRole;
import com.zineb.autheapp.dao.entities.AppUser;
import com.zineb.autheapp.dto.PermissionRequestDTO;
import com.zineb.autheapp.dto.UserDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Component
public class UserMapper {
    public UserDTO mapToDTO(AppUser user) {
        PermissionRequestDTO permDto = new PermissionRequestDTO();

        Optional<AppPermission> optionalPermission = user.getPermissions() != null
                ? user.getPermissions().stream().findFirst()
                : Optional.empty();

        if (optionalPermission.isPresent()) {
            AppPermission perm = optionalPermission.get();
            permDto.setUsername(user.getUsername());
            permDto.setCanRead(perm.isCanRead());
            permDto.setCanCreate(perm.isCanCreate());
            permDto.setCanUpdate(perm.isCanUpdate());
            permDto.setCanDelete(perm.isCanDelete());
        } else {
            permDto.setUsername(user.getUsername());
            permDto.setCanRead(false);
            permDto.setCanCreate(false);
            permDto.setCanUpdate(false);
            permDto.setCanDelete(false);
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail()); // <-- Ajout
        dto.setDeleted(user.isDeleted());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setPermissions(permDto);

        List<String> roleNames = user.getRoles().stream()
                .map(AppRole::getName)
                .collect(Collectors.toList());
        dto.setRoles(roleNames);

        return dto;
    }



}

