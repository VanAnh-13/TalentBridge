package com.example.talentbridge.auth.infrastructure.security;

import com.example.talentbridge.user.application.GetUserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/** Loads the credentials Spring Security checks a login against. Authorities come from the JWT, not from here. */
@Service("userDetailsService")
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {

    private final GetUserAccount getUserAccount;

    @Override
    public UserDetails loadUserByUsername(String username) {
        var user = getUserAccount.find(username)
                .orElseThrow(() -> new UsernameNotFoundException(null))
                .user();

        return User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(Collections.emptyList())
                .build();
    }
}
