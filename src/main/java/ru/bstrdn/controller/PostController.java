package ru.bstrdn.controller;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.bstrdn.data.model.Post;
import ru.bstrdn.data.model.PostCreatePostRequest;
import ru.bstrdn.data.model.PostUpdatePutRequest;
import ru.bstrdn.service.PostService;

@RestController
@RequiredArgsConstructor
public class PostController implements PostControllerApi {

  private final PostService postService;

  @Override
  public ResponseEntity<List<Post>> postFeedGet(BigDecimal offset, BigDecimal limit) {
    List<Post> posts = postService.getFeed(offset.longValue(), limit.longValue());
    return ResponseEntity.ok(posts);
  }

  @Override
  public ResponseEntity<String> postCreatePost(PostCreatePostRequest postCreatePostRequest) {
    return PostControllerApi.super.postCreatePost(postCreatePostRequest);
  }

  @Override
  public ResponseEntity<Void> postDeleteIdPut(String id) {
    return PostControllerApi.super.postDeleteIdPut(id);
  }

  @Override
  public ResponseEntity<Post> postGetIdGet(String id) {
    return PostControllerApi.super.postGetIdGet(id);
  }

  @Override
  public ResponseEntity<Void> postUpdatePut(PostUpdatePutRequest postUpdatePutRequest) {
    return PostControllerApi.super.postUpdatePut(postUpdatePutRequest);
  }
}
