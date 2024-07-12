package ru.bstrdn.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bstrdn.data.dto.UserRegisterPostRequest;

@Service
@RequiredArgsConstructor
public class ParserService {

  private final UserService userService;
  private final int batchSize = 10000;

  public void parseUser(List<String> list) {
    int batchCount = 0;
    List<UserRegisterPostRequest> userRegisterRequests = new ArrayList<>();
    for (String line : list) {
      String[] userLine = line.split(",");
      if (userLine.length == 3) {
        String[] name = userLine[0].split(" ");
        if (name.length == 2) {
          UserRegisterPostRequest newUser = UserRegisterPostRequest.builder()
              .firstName(name[0])
              .secondName(name[1])
              .birthdate(LocalDate.parse(userLine[1]))
              .city(userLine[2])
              .password("{noop}" + UUID.randomUUID())
              .build();
          userRegisterRequests.add(newUser);
          batchCount++;
        }
      }
      if (batchCount >= batchSize) {
        userService.createUsers(userRegisterRequests);
        batchCount = 0;
        userRegisterRequests.clear();
      }
    }
  }
}
