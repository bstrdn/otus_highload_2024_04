package ru.bstrdn.service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import ru.bstrdn.data.model.Post;
import ru.bstrdn.data.model.PostCreateRequest;
import ru.bstrdn.data.repository.PostRepository;

@Service
@RequiredArgsConstructor
public class PostService {

  private final Jedis jedis;
  private final PostRepository postRepository;

  @PostConstruct
  void test() {
    Set<String> keys = jedis.keys("*");
    List<String> mget = jedis.mget(keys.toArray(new String[0]));
    System.out.println(mget);
  }



  public List<Post> getFeed(long offset, long limit) {
    return List.of();
  }


  /**
   * Генерация постов
   * @param userPosts - набор постов для генерации
   */
  public void createPosts(List<PostCreateRequest> userPosts) {
    postRepository.createUsers(userPosts);
  }

}
