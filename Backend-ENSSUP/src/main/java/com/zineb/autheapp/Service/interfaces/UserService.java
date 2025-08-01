package com.zineb.autheapp.Service.interfaces;

import com.zineb.autheapp.dao.entities.AppRole;
import com.zineb.autheapp.dao.entities.AppUser;
import com.zineb.autheapp.dto.PermissionRequestDTO;
import com.zineb.autheapp.dto.UserDTO;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface UserService {


    AppUser addUser(AppUser appUser);
  AppUser updateUser(Long id, AppUser updatedUser);
  void restoreUser(Long id);
  AppUser getUserById(Long id);
  AppRole addRole(AppRole appRole);
    void addRoleToUser(String rolename, String username);
  void addPerToUser(PermissionRequestDTO request);
    AppUser loadUserByUsername(String username);
  List<AppUser> listUsers();
    List<AppRole> getroles();
  void softDeleteUser(Long id);
  Page<AppUser> filterUsers(String username, Boolean isDeleted, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
