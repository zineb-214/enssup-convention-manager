package com.zineb.autheapp;

import com.zineb.autheapp.dao.entities.*;
import com.zineb.autheapp.dto.PermissionRequestDTO;
import com.zineb.autheapp.Service.interfaces.ConventionTypeService;
import com.zineb.autheapp.Service.interfaces.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.time.LocalDate;
import java.util.ArrayList;

@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class AutheAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AutheAppApplication.class, args);
    }

    CommandLineRunner initData(ConventionTypeService conventionTypeService,UserService accountService ) {
        return args -> {
            accountService.addUser(new AppUser(null, "user", "U123",
                    "user@gamil.com", LocalDate.now(), new ArrayList<>(), new ArrayList<>(), false));

            accountService.addUser(new AppUser(null, "admin", "A123",
                    "admin@gamil.com", LocalDate.now(), new ArrayList<>(), new ArrayList<>(), false));

            accountService.addRole(new AppRole(null, "USER"));
            accountService.addRole(new AppRole(null, "ADMIN"));

            accountService.addRoleToUser("USER", "user");
            accountService.addRoleToUser("ADMIN", "admin");

            accountService.addPerToUser(new PermissionRequestDTO("admin", false, false, false, false));
            accountService.addPerToUser(new PermissionRequestDTO("user", true, true, true, true));
            // Création du type
            AppConventionType type1 = conventionTypeService.addtype(
                    new AppConventionType(null, "convention d'echange", "description", "ECHANGES")
            );
            AppConventionType type2 = conventionTypeService.addtype(
                    new AppConventionType(null, "convention d'Accord", "description", "ACCORD")
            );

        };
    }

}
