package ru.bstrdn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.bstrdn.data.dto.LoginPost200Response;
import ru.bstrdn.data.dto.LoginPostRequest;
import ru.bstrdn.data.dto.User;
import ru.bstrdn.data.dto.UserRegisterPost200Response;
import ru.bstrdn.data.dto.UserRegisterPostRequest;
import ru.bstrdn.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController implements DefaultApi {

  private final UserService userService;

  @Override
  public ResponseEntity<LoginPost200Response> loginPost(LoginPostRequest loginPostRequest) {
    String token = userService.getToken(loginPostRequest);
    return ResponseEntity.ok(LoginPost200Response.builder().token(token).build());
  }

  @Override
  public ResponseEntity<User> userGetIdGet(String id) {
    User userById = userService.getUserById(id);
    return ResponseEntity.ok(userById);
  }

  @Override
  public ResponseEntity<UserRegisterPost200Response> userRegisterPost(
      UserRegisterPostRequest userRegisterPostRequest) {
    String userId = userService.createUser(userRegisterPostRequest);
    return ResponseEntity.ok(UserRegisterPost200Response.builder().userId(userId).build());
  }
}
