package com.zineb.autheapp.secutiry;

import com.zineb.autheapp.dao.entities.AppPermission;
import com.zineb.autheapp.dao.entities.AppUser;
import com.zineb.autheapp.dao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/auth")
public class SecurityController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private  JwtEncoder jwtEncoder;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public Authentication authentication(Authentication authentication){
        return authentication;
    }
    @PostMapping("/login")
    public Map<String, String> login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        Instant now = Instant.now();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());


        AppUser user = userRepository.findByUsername(username); // ou autre repo
        if (user == null) throw new UsernameNotFoundException("User not found");

        Map<String, Boolean> permissions = new HashMap<>();
        permissions.put("canCreate", user.getPermissions().stream().anyMatch(AppPermission::isCanCreate));
        permissions.put("canRead", user.getPermissions().stream().anyMatch(AppPermission::isCanRead));
        permissions.put("canUpdate", user.getPermissions().stream().anyMatch(AppPermission::isCanUpdate));
        permissions.put("canDelete", user.getPermissions().stream().anyMatch(AppPermission::isCanDelete));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(authentication.getName())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .claim("roles", roles)
                .claim("permissions", permissions)
                .claim("deleted", user.isDeleted())
                .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                claims
        );

        String token = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();

        Map<String, String> response = new HashMap<>();
        response.put("access_token", token);
        return response;
    }


}
