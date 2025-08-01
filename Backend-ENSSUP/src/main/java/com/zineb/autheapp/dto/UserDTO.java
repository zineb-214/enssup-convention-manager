package com.zineb.autheapp.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private boolean deleted;
    private LocalDate createdAt;
    private PermissionRequestDTO permissions;
    private List<String> roles;
}
