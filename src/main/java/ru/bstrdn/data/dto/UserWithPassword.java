package ru.bstrdn.data.dto;

import java.util.Set;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Пользователь с паролем, для аутентификации
 */
@Data
@SuperBuilder
public class UserWithPassword extends User {

  private String password;

  private final Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
}

