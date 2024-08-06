package ru.bstrdn.data.model;

import lombok.Data;

@Data
public class UserFriend {

  private int id;
  private String userId;
  private String friendId;
}
