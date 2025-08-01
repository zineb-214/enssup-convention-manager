package com.zineb.autheapp.secutiry;

import com.zineb.autheapp.Service.interfaces.UserService;
import com.zineb.autheapp.dao.entities.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private UserService accountService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user=accountService.loadUserByUsername(username);
        if(user==null) throw new UsernameNotFoundException(String.format("Username %s not found", username));
        Collection<GrantedAuthority> grantedAuthorities=new ArrayList<>();
        user.getRoles().forEach(role->{
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        });
      return new User(user.getUsername(),user.getPassword(),grantedAuthorities);
    }
}
