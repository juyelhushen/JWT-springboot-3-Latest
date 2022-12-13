package com.springsecuritylatest.config;

import com.springsecuritylatest.jwt.AuthEntryPoint;
import com.springsecuritylatest.jwt.JwtAuthFilter;
import com.springsecuritylatest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {



    private final UserService userService;
    private final JwtAuthFilter jwtAuthFilter;


    @Autowired
    private final AuthEntryPoint unAuthHandler;

    private final PasswordEncoderConfiguration configuration;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)throws Exception {
        auth.userDetailsService(userService).passwordEncoder(configuration.passwordEncoder());
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(configuration.passwordEncoder());
        return authenticationProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unAuthHandler)
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/**","/api/v1/auth/authenticate")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                  .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }



//    @Bean
//    public UserDetailsService userDetailsService() {
//        return email -> APPLICATION_USERS.stream()
//                .filter(u -> u.getUsername().equals(email))
//                .findFirst()
//                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
//    }


//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new UserDetailsService() {
//            @Override
//            public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//                return userDoa.findUserByEmail(email);
//            }
//        };
//    }
}
