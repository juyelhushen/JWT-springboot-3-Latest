package com.springsecuritylatest.service;

import com.springsecuritylatest.config.PasswordEncoderConfiguration;
import com.springsecuritylatest.dao.UserRepository;
import com.springsecuritylatest.entity.User;
import com.springsecuritylatest.entity.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final PasswordEncoderConfiguration config;

    @Autowired
    private final UserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
    } else {
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword()
        ,new ArrayList<>());
        }
    }


    public User saveUser(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(config.passwordEncoder().encode(userDTO.getPassword()));
        return repository.save(user);
    }
}
