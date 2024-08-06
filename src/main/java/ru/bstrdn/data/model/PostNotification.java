package ru.bstrdn.data.model;

import lombok.Builder;
import lombok.Data;
import ru.bstrdn.data.enums.Operation;

@Data
@Builder
public class PostNotification {

  private String postId;
  private String userId;
  private Operation operation;
}
