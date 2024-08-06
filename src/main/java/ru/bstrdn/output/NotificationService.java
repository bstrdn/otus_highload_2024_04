package ru.bstrdn.output;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.bstrdn.data.enums.Operation;
import ru.bstrdn.data.model.PostNotification;

@Component
@RequiredArgsConstructor
public class NotificationService {

  private final KafkaTemplate<String, PostNotification> notificationKafkaTemplate;
  private final KafkaTemplate<String, String> stringKafkaTemplate;
  @Value("${highload.kafka.topic.out.feed-notification}")
  private String notificationTopic;
  @Value("${highload.kafka.topic.out.update-feed-notification}")
  private String updateFeedNotificationTopic;


  public void sendModifiedFeedNotification(Operation operation, String postId) {
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    PostNotification notification = PostNotification.builder()
        .postId(postId)
        .userId(userId)
        .operation(operation).build();
    notificationKafkaTemplate.send(notificationTopic, userId, notification);
  }

  public void sendRebuildFeedNotification(String userId) {
    stringKafkaTemplate.send(updateFeedNotificationTopic, userId, userId);
  }
}