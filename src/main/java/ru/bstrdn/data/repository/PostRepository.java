package ru.bstrdn.data.repository;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import ru.bstrdn.data.enums.Operation;
import ru.bstrdn.data.model.Post;
import ru.bstrdn.data.model.PostCreatePostRequest;
import ru.bstrdn.data.model.PostCreateRequest;
import ru.bstrdn.data.model.PostUpdatePutRequest;
import ru.bstrdn.output.NotificationService;
import ru.bstrdn.service.UserService;

@Repository
public class PostRepository {

  @Value("${spring.liquibase.default-schema}")
  private String schema;

  private final NotificationService notificationService;
  private final UserService userService;


  private String CREATE_POST_SQL;
  private String DELETE_POST_SQL;
  private String GET_POST_SQL;
  private String UPDATE_POST_SQL;
  private String QUERY_FEED;


  //todo вынести
  private final NamedParameterJdbcTemplate masterNamedParameterJdbcTemplate;
  private final NamedParameterJdbcTemplate slaveNamedParameterJdbcTemplate;

  public PostRepository(
      NotificationService notificationService, UserService userService,
      @Qualifier("masterNamedParameterJdbcTemplate") NamedParameterJdbcTemplate masterNamedParameterJdbcTemplate,
      @Qualifier("slaveNamedParameterJdbcTemplate") NamedParameterJdbcTemplate slaveNamedParameterJdbcTemplate) {
    this.notificationService = notificationService;
    this.userService = userService;
    this.masterNamedParameterJdbcTemplate = masterNamedParameterJdbcTemplate;
    this.slaveNamedParameterJdbcTemplate = slaveNamedParameterJdbcTemplate;
  }

  @PostConstruct
  private void initSqlQuery() {
    CREATE_POST_SQL = String.format(
        "INSERT INTO %s.post (id, text, created_at, author_user_id)"
            + "VALUES (:id, :text, :created_at, :author_user_id) RETURNING id",
        schema);
    DELETE_POST_SQL = String.format(
        "DELETE FROM %s.post where id = :id and author_user_id = :author_user_id", schema
    );
    GET_POST_SQL = String.format(
        "SELECT * FROM %s.post where id = :id and author_user_id = :author_user_id", schema
    );
    UPDATE_POST_SQL = "UPDATE %s.post SET text = :text, created_at = :created_at WHERE id = :id"
        + "and author_user_id = :author_user_id";
    QUERY_FEED = String.format("SELECT * FROM %s.post where id in :friendsId OFFSET :offset LIMIT :limit", schema);
  }

  @CacheEvict(cacheNames = "feed")
  public void createPosts(List<PostCreateRequest> postCreateRequests) {
    List<SqlParameterSource> sqlParameterSources = new ArrayList<>();
    for (PostCreateRequest postCreateRequest : postCreateRequests) {
      sqlParameterSources.add(getPostRegisterParameterForGeneration(postCreateRequest));
    }
    masterNamedParameterJdbcTemplate.batchUpdate(CREATE_POST_SQL,
        sqlParameterSources.toArray(new SqlParameterSource[0]));

  }

  private SqlParameterSource getPostRegisterParameterForGeneration(
      PostCreateRequest postCreateRequest) {
    return new MapSqlParameterSource()
        .addValue("id", UUID.randomUUID().toString())
        .addValue("text", postCreateRequest.getText())
        .addValue("created_at", postCreateRequest.getCreatedAt())
        .addValue("author_user_id", postCreateRequest.getAuthorUserId());
  }

  private SqlParameterSource getPostParameter(
      PostCreatePostRequest postCreateRequest) {
    return new MapSqlParameterSource()
        .addValue("id", UUID.randomUUID().toString())
        .addValue("text", postCreateRequest.getText())
        .addValue("created_at", LocalDateTime.now())
        .addValue("author_user_id",
            SecurityContextHolder.getContext().getAuthentication().getName());
  }

  @CacheEvict(cacheNames = "feed")
  public String createPost(PostCreatePostRequest postCreatePostRequest) {
    String postId = masterNamedParameterJdbcTemplate.queryForObject(CREATE_POST_SQL,
        getPostParameter(postCreatePostRequest), String.class);
    notificationService.sendModifiedFeedNotification(Operation.CREATE, postId);
    return postId;
  }

  @CacheEvict(cacheNames = "feed")
  public void deleteByPostIdAndUserId(String id, String author) {
    Map<String, String> parameterMap = Map.of("id", id, "author_user_id", author);
    masterNamedParameterJdbcTemplate.update(DELETE_POST_SQL, parameterMap);
    notificationService.sendModifiedFeedNotification(Operation.DELETE, id);
  }

  public Post getPostById(String id, String author) {
    Map<String, String> parameterMap = Map.of("id", id, "author_user_id", author);
    return slaveNamedParameterJdbcTemplate.queryForObject(GET_POST_SQL, parameterMap, Post.class);
  }

  @CacheEvict(cacheNames = "feed")
  public void updatePost(PostUpdatePutRequest postUpdatePutRequest, String userId) {
    MapSqlParameterSource parameterSource = new MapSqlParameterSource()
        .addValue("id", postUpdatePutRequest.getId())
        .addValue("text", postUpdatePutRequest.getText())
        .addValue("created_at", LocalDateTime.now())
        .addValue("author_user_id", userId);
    masterNamedParameterJdbcTemplate.update(UPDATE_POST_SQL, parameterSource);
    notificationService.sendModifiedFeedNotification(Operation.UPDATE, postUpdatePutRequest.getId());
  }

  @Cacheable(cacheNames = "feed", key = "userId")
  public List<Post> getFriendsFeed(long offset, long limit, String userId) {
    SqlParameterSource parameterSource = new MapSqlParameterSource()
        .addValue("offset", offset)
        .addValue("limit", limit)
        .addValue("friendsId", getFriendsIdAsParameter(userId));
    return slaveNamedParameterJdbcTemplate.query(QUERY_FEED, parameterSource,
        (rs, rowNum) -> Post.builder()
            .id(rs.getString("id"))
            .text(rs.getString("text"))
            .authorUserId("author_user_id").build());
  }

  private String getFriendsIdAsParameter(String userId) {
    List<String> currentUserFriendsId = userService.getUserFriendsId(userId);
    String joined = currentUserFriendsId.stream()
        .collect(Collectors.joining(", ", "'", "'"));
    return "(" + joined + ")";
  }
}
