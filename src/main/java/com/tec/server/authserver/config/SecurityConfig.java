package com.tec.server.authserver.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.tec.server.authserver.filter.JwtAuthFilter;
import com.tec.server.authserver.service.UserInfoService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Autowired
  private JwtAuthFilter authFilter;

  // User Creation
  @Bean
  UserDetailsService userDetailsService() {
    return new UserInfoService();
  }

  // Configuring HttpSecurity
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.disable())
        .cors(cors -> cors.disable())
        .authorizeHttpRequests(
            auth -> auth.requestMatchers(
              "/auth/welcome",
              "/auth/addNewUser",
              "/auth/login",
              "auth/user/getAllUsers",
              "auth/user/updateUser",
              "auth/user/deleteUser"
              ).permitAll())
        .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/user/**").authenticated())
        .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/admin/**").authenticated())
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  // Password Encoding
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userDetailsService());
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    return authenticationProvider;
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS", "HEAD"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Requestor-Type"));
    configuration.setExposedHeaders(Arrays.asList("X-Get-Header"));
    configuration.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

}
