package ru.bstrdn.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bstrdn.data.model.PostCreateRequest;
import ru.bstrdn.data.model.UserRegisterPostRequest;

@Service
@RequiredArgsConstructor
public class ParserService {

  List<String> testUsersId = List.of("u1", "u2", "u3", "u4", "u5", "u6");
  Random random = new Random();

  private final UserService userService;
  private final PostService postService;
  private final int batchSize = 1000;

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

  public void parsePostForRandomUsers(List<String> list) {
    int batchCount = 0;
    List<PostCreateRequest> userPosts = new ArrayList<>();
    for (String line : list) {
      PostCreateRequest userPost = PostCreateRequest.builder()
          .text(line)
          .createdAt(LocalDateTime.now())
          .id(UUID.randomUUID().toString())
          .authorUserId(testUsersId.get(random.nextInt(0, testUsersId.size()))).build();
      userPosts.add(userPost);

      if (batchCount >= batchSize) {
        postService.createPosts(userPosts);
        batchCount = 0;
        userPosts.clear();
      }
    }
    if (batchCount < batchSize) {
      postService.createPosts(userPosts);
    }
  }
}
