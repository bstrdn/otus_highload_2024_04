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
    return ResponseEntity.ok(postService.createPost(postCreatePostRequest));
  }

  @Override
  public ResponseEntity<Void> postDeleteIdPut(String id) {
    postService.deletePost(id);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Post> postGetIdGet(String id) {
    Post post = postService.getPost(id);
    if (post == null) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(post);
    }
  }

  @Override
  public ResponseEntity<Void> postUpdatePut(PostUpdatePutRequest postUpdatePutRequest) {
    postService.updatePost(postUpdatePutRequest);
    return ResponseEntity.ok().build();
  }
}
