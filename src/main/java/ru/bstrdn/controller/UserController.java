package ru.bstrdn.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.bstrdn.data.model.LoginPost200Response;
import ru.bstrdn.data.model.LoginPostRequest;
import ru.bstrdn.data.model.User;
import ru.bstrdn.data.model.UserRegisterPost200Response;
import ru.bstrdn.data.model.UserRegisterPostRequest;
import ru.bstrdn.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerApi {

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

  @Override
  public ResponseEntity<List<User>> userSearchGet(String firstName, String lastName) {
    List<User> users = userService.searchUsers(firstName, lastName);
    return ResponseEntity.ok(users);
  }

  @Override
  public ResponseEntity<Void> friendDeleteUserIdPut(String userId) {
    return UserControllerApi.super.friendDeleteUserIdPut(userId);
  }

  @Override
  public ResponseEntity<Void> friendSetUserIdPut(String userId) {
    return UserControllerApi.super.friendSetUserIdPut(userId);
  }
}
