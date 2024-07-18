package ru.bstrdn.data.repository;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.bstrdn.data.model.User;
import ru.bstrdn.data.model.UserRegisterPostRequest;
import ru.bstrdn.data.model.UserWithPassword;

@Repository
public class UserRepository {

  @Value("${spring.liquibase.default-schema}")
  private String schema;

  private String CREATE_USER_SQL;
  private String GET_USER_BY_ID_SQL;
  private String SEARCH_USERS_BY_PREFIX_FIRST_AND_LAST_NAME;

  private final NamedParameterJdbcTemplate masterNamedParameterJdbcTemplate;
  private final NamedParameterJdbcTemplate slaveNamedParameterJdbcTemplate;

  public UserRepository(
      @Qualifier("masterNamedParameterJdbcTemplate") NamedParameterJdbcTemplate masterNamedParameterJdbcTemplate,
      @Qualifier("slaveNamedParameterJdbcTemplate") NamedParameterJdbcTemplate slaveNamedParameterJdbcTemplate) {
    this.masterNamedParameterJdbcTemplate = masterNamedParameterJdbcTemplate;
    this.slaveNamedParameterJdbcTemplate = slaveNamedParameterJdbcTemplate;
  }

  @PostConstruct
  private void initSqlQuery() {
    CREATE_USER_SQL = String.format(
        "INSERT INTO %s.\"user\" (id, first_name, second_name, birthdate, biography, city, password)"
            + "VALUES (:id, :first_name, :second_name, :birthdate, :biography, :city, :password) RETURNING id",
        schema);
    GET_USER_BY_ID_SQL = String.format(
        "SELECT * FROM %s.\"user\" WHERE id = :id", schema);
    SEARCH_USERS_BY_PREFIX_FIRST_AND_LAST_NAME = String.format("SELECT * FROM "
            + "%s.\"user\" WHERE first_name LIKE :firstName and second_name LIKE :secondName ORDER BY id",
        schema);
  }

  public String createUser(UserRegisterPostRequest userRegisterPostRequest) {
    SqlParameterSource parameterSource = getUserRegisterParameter(userRegisterPostRequest);
    return masterNamedParameterJdbcTemplate.queryForObject(CREATE_USER_SQL, parameterSource, String.class);
  }

  public UserWithPassword getUserByIdWithCred(String id) {
    SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id", id);
    return slaveNamedParameterJdbcTemplate.queryForObject(GET_USER_BY_ID_SQL,
        parameterSource,
        (RowMapper<UserWithPassword>) (rs, rowNum) -> UserWithPassword.builder()
            .id(rs.getString("id"))
            .firstName(rs.getString("first_name"))
            .secondName(rs.getString("second_name"))
            .birthdate(rs.getDate("birthdate").toLocalDate())
            .biography(rs.getString("biography"))
            .city(rs.getString("city"))
            .password(rs.getString("password"))
            .build());
  }

  public Optional<User> getUserById(String id) {
    SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id", id);
    User user = slaveNamedParameterJdbcTemplate.queryForObject(GET_USER_BY_ID_SQL, parameterSource,
        (rs, rowNum) -> User.builder()
            .id(rs.getString("id"))
            .firstName(rs.getString("first_name"))
            .secondName(rs.getString("second_name"))
            .birthdate(rs.getDate("birthdate").toLocalDate())
            .biography(rs.getString("biography"))
            .city(rs.getString("city"))
            .build());
    return Optional.ofNullable(user);
  }

  public List<User> searchUsersByPrefixFirstAndLastName(String firstName, String lastName) {
    SqlParameterSource parameterSource = new MapSqlParameterSource()
        .addValue("firstName", firstName + '%')
        .addValue("secondName", lastName + '%');
    return slaveNamedParameterJdbcTemplate.query(SEARCH_USERS_BY_PREFIX_FIRST_AND_LAST_NAME, parameterSource,
        (rs, rowNum) -> User.builder()
            .id(rs.getString("id"))
            .firstName(rs.getString("first_name"))
            .secondName(rs.getString("second_name"))
            .birthdate(rs.getDate("birthdate").toLocalDate())
            .biography(rs.getString("biography"))
            .city(rs.getString("city"))
            .build());

  }

  public void createUsers(List<UserRegisterPostRequest> usersRegisterPostRequest) {

    List<SqlParameterSource> sqlParameterSources = new ArrayList<>();

    for (UserRegisterPostRequest userRequest : usersRegisterPostRequest) {
      sqlParameterSources.add(getUserRegisterParameter(userRequest));
    }

    masterNamedParameterJdbcTemplate.batchUpdate(CREATE_USER_SQL, sqlParameterSources.toArray(new SqlParameterSource[0]));
  }

  private SqlParameterSource getUserRegisterParameter(
      UserRegisterPostRequest userRegisterPostRequest) {
    return new MapSqlParameterSource()
        .addValue("id", UUID.randomUUID().toString())
        .addValue("first_name", userRegisterPostRequest.getFirstName())
        .addValue("second_name", userRegisterPostRequest.getSecondName())
        .addValue("birthdate", userRegisterPostRequest.getBirthdate())
        .addValue("biography", userRegisterPostRequest.getBiography())
        .addValue("city", userRegisterPostRequest.getCity())
        .addValue("password", userRegisterPostRequest.getPassword());
  }
}
