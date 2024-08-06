package ru.bstrdn.data.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class PostCreateRequest extends Post {

  private LocalDateTime createdAt;
}
