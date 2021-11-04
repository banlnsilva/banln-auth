package com.example.banana.service.security;

import com.example.banana.advice.exception.CUserNotFoundException;
import com.example.banana.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UserJpaRepository userJpaRepository;

    public UserDetails loadUserByUsername(String userPk) {
        return userJpaRepository.findById(Long.valueOf(userPk)).orElseThrow(CUserNotFoundException::new);
    }
}
