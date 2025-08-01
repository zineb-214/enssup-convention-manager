package com.zineb.autheapp.Service.impl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import com.zineb.autheapp.Service.interfaces.UserService;
import com.zineb.autheapp.dao.entities.AppPermission;
import com.zineb.autheapp.dao.entities.AppRole;
import com.zineb.autheapp.dao.entities.AppUser;
import com.zineb.autheapp.dto.PermissionRequestDTO;
import com.zineb.autheapp.dao.repository.PermissionRepository;
import com.zineb.autheapp.dao.repository.RoleRepository;
import com.zineb.autheapp.dao.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userReppsitory;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private PermissionRepository permissionRepository;






    @Transactional
    @Override
    public AppRole addRole(AppRole appRole) {
        return  roleRepository.save(appRole);
    }
@Override
    public void addPerToUser(PermissionRequestDTO request) {
        AppUser user = userReppsitory.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("Utilisateur non trouvé : " + request.getUsername());
        }

        AppPermission newPermission = new AppPermission();
        newPermission.setCanCreate(request.isCanCreate());
        newPermission.setCanRead(request.isCanRead());
        newPermission.setCanUpdate(request.isCanUpdate());
        newPermission.setCanDelete(request.isCanDelete());

        newPermission = permissionRepository.save(newPermission);

        if (user.getPermissions() == null) {
            user.setPermissions(new ArrayList<>());
        }

        user.getPermissions().add(newPermission);
        userReppsitory.save(user);
    }



    @Override
    public AppUser updateUser(Long id, AppUser updatedUser) {
        AppUser existingUser = userReppsitory.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec id : " + id));
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());

        // Encoder le mot de passe si non vide
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
            existingUser.setPassword(encodedPassword);
        }

        if (updatedUser.getRoles() != null) {
            List<AppRole> newRoles = new ArrayList<>();
            for (AppRole role : updatedUser.getRoles()) {
                // Chercher par nom, ou créer s'il n'existe pas
                AppRole existingRole = roleRepository.findByName(role.getName());
                if (existingRole == null) {
                    existingRole = roleRepository.save(new AppRole(null, role.getName()));
                }
                newRoles.add(existingRole);
            }
            existingUser.setRoles(newRoles);
        }

        if (updatedUser.getPermissions() != null) {
            List<AppPermission> newPermissions = new ArrayList<>();
            for (AppPermission perm : updatedUser.getPermissions()) {
                AppPermission newPerm = new AppPermission();
                newPerm.setCanRead(perm.isCanRead());
                newPerm.setCanCreate(perm.isCanCreate());
                newPerm.setCanDelete(perm.isCanDelete());
                newPerm.setCanUpdate(perm.isCanUpdate());
                permissionRepository.save(newPerm);
                newPermissions.add(newPerm);
            }
            existingUser.setPermissions(newPermissions);
        }



        return userReppsitory.save(existingUser);
    }






    @Override
    public AppUser getUserById(Long id) {
        return userReppsitory.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @Override
    public AppUser addUser(AppUser appUser) {
        String encodedPassword = passwordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUser.setDeleted(false);
        AppRole userRole = roleRepository.findByName("USER");

        List<AppRole> rolesToSet = new ArrayList<>();
        rolesToSet.add(userRole);
        appUser.setRoles(rolesToSet);

        // Charger les permissions comme avant
        List<AppPermission> existingPermissions = new ArrayList<>();
        if (appUser.getPermissions() != null) {
            for (AppPermission perm : appUser.getPermissions()) {
                AppPermission existingPerm = permissionRepository.findById(perm.getId())
                        .orElseThrow(() -> new RuntimeException("Permission not found"));
                existingPermissions.add(existingPerm);
            }
        }
        appUser.setPermissions(existingPermissions);

        return userReppsitory.save(appUser);
    }


    @Override
    public void softDeleteUser(Long id) {
        Optional<AppUser> userOptional = userReppsitory.findById(id);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("Utilisateur non trouvé avec ID: " + id);
        }

        AppUser user = userOptional.get();
 user.setDeleted(true);
        userReppsitory.save(user);
    }


    @Override
    public void addRoleToUser(String rolename, String username) {
        AppUser appUser = userReppsitory.findByUsername(username);
        if (appUser == null) {
            throw new RuntimeException("Utilisateur non trouvé : " + username);
        }

        AppRole appRole = roleRepository.findByName(rolename);
        if (appRole == null) {
            throw new RuntimeException("Rôle non trouvé : " + rolename);
        }

        appUser.getRoles().add(appRole);
        userReppsitory.save(appUser); // important si pas en cascade
    }
    @Override
    public void restoreUser(Long id) {
        AppUser user = userReppsitory.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setDeleted(false);
        userReppsitory.save(user);
    }


    @Override
    public AppUser loadUserByUsername(String username) {
        return userReppsitory.findByUsername(username);
    }

    @Override
    public List<AppUser> listUsers() {
        return userReppsitory.findAll()
                .stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("USER")))
                .collect(Collectors.toList());
    }
    @Override
    public List<AppRole> getroles() {
        return roleRepository.findAll();
    }

    @Override
    public Page<AppUser> filterUsers(String username, Boolean isDeleted, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return userReppsitory.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (username != null && !username.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
            }

            if (isDeleted != null) {
                predicates.add(cb.equal(root.get("isDeleted"), isDeleted));
            }


            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
        }
    }
