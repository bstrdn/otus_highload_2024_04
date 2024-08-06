package ru.bstrdn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedService {

  private final PostService postService;

  @CacheEvict(cacheNames = "feed", key = "userId")
  public void updateCashedFeed(String userId) {
    postService.getFeedWithUserId(1, 1000, userId);
  }

}