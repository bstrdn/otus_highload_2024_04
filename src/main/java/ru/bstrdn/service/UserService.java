package ru.bstrdn.service;

import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bstrdn.data.model.LoginPostRequest;
import ru.bstrdn.data.model.User;
import ru.bstrdn.data.model.UserRegisterPostRequest;
import ru.bstrdn.data.model.UserWithPassword;
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
   * Создание группы пользователей
   * @param usersRegisterPostRequest - запрос на создание пользователя
   * @return id созданного пользователя
   */
  public void createUsers(List<UserRegisterPostRequest> usersRegisterPostRequest) {
    userRepository.createUsers(usersRegisterPostRequest);
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

  /**
   * Поиск юзера по префиксу имени и фамилии (одновременно)
   * @param firstName Имя
   * @param lastName Фамилия
   * @return найденные пользователи
   */
  public List<User> searchUsers(String firstName, String lastName) {
    return userRepository.searchUsersByPrefixFirstAndLastName(firstName, lastName);
  }

  private String token(LoginPostRequest loginPostRequest) {
    String credentials = loginPostRequest.getId() + ":" + loginPostRequest.getPassword();
    return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
  }
}
