package com.zineb.autheapp.web.controller;

import com.zineb.autheapp.dao.entities.AppRole;
import com.zineb.autheapp.dao.entities.AppUser;
import com.zineb.autheapp.dto.PermissionRequestDTO;
import com.zineb.autheapp.Service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/admin")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {
    @Autowired
    private UserService userService;
    @GetMapping("/users")
    public List<AppUser> getUsers() {
        return userService.listUsers();
    }

    @PostMapping("/addUser")
    public AppUser addUser(@RequestBody AppUser appUser ){

        return userService.addUser(appUser);
    }

    @PostMapping(path = "/addRoleToUser")
    void addRoleToUser(@RequestBody UserRole userRole) {
        userService.addRoleToUser(userRole.getRoleName(),userRole.getUsername());
    }
    @PostMapping("/addPermissionToUser")
    public ResponseEntity<String> addPermissionToUser(@RequestBody PermissionRequestDTO request) {
        try {
            userService.addPerToUser(request);
            return ResponseEntity.ok("Permissions ajoutées à l'utilisateur " + request.getUsername());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody AppUser updatedUser) {
        try {
            AppUser user = userService.updateUser(id, updatedUser);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur : " + e.getMessage());
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<AppUser> getUserById(@PathVariable Long id) {
        try {
            AppUser user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/roles")
    public List<AppRole> getRoles() {
        return userService.getroles();
    }

    @PutMapping("/users/{id}/soft-delete")
    public ResponseEntity<String> softDeleteUser(@PathVariable Long id) {
        userService.softDeleteUser(id);
        return ResponseEntity.ok("Utilisateur marqué comme supprimé.");
    }

    @PutMapping("/users/{id}/restore")
    public ResponseEntity<String> restoreUser(@PathVariable Long id) {
        userService.restoreUser(id);
        return ResponseEntity.ok("Utilisateur restauré.");
    }
    @GetMapping("/filterUsers")
    public Page<AppUser> filterUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.filterUsers(username, isDeleted, startDate, endDate, pageable);
    }

}
