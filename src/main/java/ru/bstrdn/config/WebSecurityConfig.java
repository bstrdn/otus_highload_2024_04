package ru.bstrdn.config;

import static org.springframework.security.config.Customizer.withDefaults;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import ru.bstrdn.data.dto.UserWithPassword;
import ru.bstrdn.service.UserService;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final UserService userService;

  private static final String[] WHITE_LIST_URL = {
      "/login",
      "/swagger-ui/**",
      "/actuator/health",
      "/actuator/prometheus",
      "/api-docs/**",
      "/v2/api-docs",
      "/v3/api-docs",
      "/v3/api-docs/**",
      "/swagger-resources",
      "/swagger-resources/**",
      "/configuration/ui",
      "/configuration/security",
      "/swagger-ui/**",
      "/webjars/**",
      "/swagger-ui.html",
  };

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)

        .authorizeHttpRequests((auth) -> auth
            .requestMatchers(WHITE_LIST_URL).permitAll()
            .anyRequest().authenticated()
        )
        .httpBasic(withDefaults())
        .userDetailsService(userDetailsService());
    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return id -> {
      UserWithPassword userById = userService.getUserByIdWithCred(id);
      log.debug("Authenticating user: {}", id);
      return new User(id, userById.getPassword(), userById.getAuthorities());
    };
  }
}
