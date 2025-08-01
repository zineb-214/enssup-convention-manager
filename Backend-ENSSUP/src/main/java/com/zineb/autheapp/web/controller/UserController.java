package com.zineb.autheapp.web.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zineb.autheapp.dao.entities.AppUser;
import com.zineb.autheapp.Service.interfaces.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class UserController {

    private  final UserService accountService;

    public UserController(UserService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(path = "/refreshToken")
    public void refreshtoken(HttpServletResponse response, HttpServletRequest request) throws Exception{
        String token=request.getHeader("Authorization");
        if(token!=null && token.startsWith("Bearer ")){
            try{
                String refreshToken=token.substring(7);
                Algorithm algorithm = Algorithm.HMAC256("secret");
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT= verifier.verify(refreshToken);
                String username = decodedJWT.getSubject();
                AppUser user=accountService.loadUserByUsername(username);
                String accessToken = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 1 * 60 * 1000)) // 1 min
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles().stream()
                                .map(role -> role.getName())
                                .collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String, String> idtoken = new HashMap<>();
                idtoken.put("access_token", accessToken);
                idtoken.put("refresh_token", refreshToken);
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(),idtoken);
            }
            catch(Exception e){
               throw  e;
            }
        }
        else{
            throw new RuntimeException("Refresh token required");
        }

    }

}
@Data
class UserRole{
    private String username;
    private String roleName;
}