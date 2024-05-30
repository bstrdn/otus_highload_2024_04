package ru.bstrdn.service;

import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bstrdn.data.dto.LoginPostRequest;
import ru.bstrdn.data.dto.User;
import ru.bstrdn.data.dto.UserRegisterPostRequest;
import ru.bstrdn.data.dto.UserWithPassword;
import ru.bstrdn.data.repository.UserRepository;

/**
 * Сервис для работы с пользователями
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  /**
   * Создание пользователя
   * @param userRegisterPostRequest - запрос на создание пользователя
   * @return id созданного пользователя
   */
  public String createUser(UserRegisterPostRequest userRegisterPostRequest) {
    userRegisterPostRequest.setPassword("{noop}" + userRegisterPostRequest.getPassword());
    return userRepository.createUser(userRegisterPostRequest);
  }

  /**
   * Получение пользователя по id
   * @param id - id пользователя
   * @return пользователь
   */
  public User getUserById(String id) {
    return userRepository.getUserById(id).orElseThrow(RuntimeException::new);
  }

  /**
   * Получение пользователя по id с паролем
   * @param id - id пользователя
   * @return пользователь с паролем
   */
  public UserWithPassword getUserByIdWithCred(String id) {
    return userRepository.getUserByIdWithCred(id);
  }

  /**
   * Получение токена
   * @param loginPostRequest - запрос на авторизацию
   * @return токен
   */
  public String getToken(LoginPostRequest loginPostRequest) {
    UserWithPassword userById = userRepository.getUserByIdWithCred(loginPostRequest.getId());
    if (userById.getPassword().substring(6).equals(loginPostRequest.getPassword())) {
      return token(loginPostRequest);
    }
    throw new RuntimeException();
  }

  private String token(LoginPostRequest loginPostRequest) {
    String credentials = loginPostRequest.getId() + ":" + loginPostRequest.getPassword();
    return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
  }
}
