package ru.bstrdn.input;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.bstrdn.data.model.PostNotification;
import ru.bstrdn.output.NotificationService;
import ru.bstrdn.service.FeedService;
import ru.bstrdn.service.UserService;

@Component
@RequiredArgsConstructor
public class FeedUpdateListener {

  private final UserService userService;
  private final NotificationService notificationService;
  private final FeedService feedService;


  @KafkaListener(topics = "${highload.kafka.topic.out.feed-notification}", containerFactory = "postNotificationContainerFactory")
  public void updateFeedNotification(PostNotification postNotification) {
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    List<String> friendsId = userService.getUserFriendsId(userId);
    for (String friendId : friendsId) {
      notificationService.sendRebuildFeedNotification(friendId);
    }
  }


  @KafkaListener(topics = "${highload.kafka.topic.out.update-feed-notification}")
  public void rebuildFeedRequest(String userId) {
    feedService.updateCashedFeed(userId);
  }
}