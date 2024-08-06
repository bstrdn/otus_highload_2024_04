package ru.bstrdn.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.bstrdn.data.model.Post;
import ru.bstrdn.data.model.PostCreatePostRequest;
import ru.bstrdn.data.model.PostCreateRequest;
import ru.bstrdn.data.model.PostUpdatePutRequest;
import ru.bstrdn.data.repository.PostRepository;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;

  public List<Post> getFeed(long offset, long limit) {
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    return getFeedWithUserId(offset, limit, userId);
  }

  public List<Post> getFeedWithUserId(long offset, long limit, String userId) {
    return postRepository.getFriendsFeed(offset, limit, userId);
  }


  /**
   * Генерация постов
   *
   * @param userPosts - набор постов для генерации
   */
  public void createPosts(List<PostCreateRequest> userPosts) {
    postRepository.createPosts(userPosts);
  }

  /**
   * Добавление поста
   *
   * @param postCreatePostRequest запрос на добавление поста
   * @return идентификатор новой записи
   */
  public String createPost(PostCreatePostRequest postCreatePostRequest) {
    return postRepository.createPost(postCreatePostRequest);
  }

  public void deletePost(String id) {
    postRepository.deleteByPostIdAndUserId(id,
        SecurityContextHolder.getContext().getAuthentication().getName());
  }

  public Post getPost(String id) {
    return postRepository.getPostById(id,
        SecurityContextHolder.getContext().getAuthentication().getName());
  }

  public void updatePost(PostUpdatePutRequest postUpdatePutRequest) {
    postRepository.updatePost(postUpdatePutRequest,
        SecurityContextHolder.getContext().getAuthentication().getName());
  }
}
