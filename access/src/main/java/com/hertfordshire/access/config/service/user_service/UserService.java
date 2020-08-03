package com.hertfordshire.access.config.service.user_service;


import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.model.psql.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public interface UserService {

    UserDetailsDto getPrincipal(HttpServletResponse res, HttpServletRequest request, Authentication authentication);

    String fetchFormOfIdentification();

    Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles);
}
