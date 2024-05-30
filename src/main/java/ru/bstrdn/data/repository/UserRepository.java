package ru.bstrdn.data.repository;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.bstrdn.data.dto.User;
import ru.bstrdn.data.dto.UserRegisterPostRequest;
import ru.bstrdn.data.dto.UserWithPassword;

@Repository
@RequiredArgsConstructor
public class UserRepository {

  private static final String CREATE_USER_SQL = "INSERT INTO \"user\" (id, first_name, second_name, birthdate, biography, city, password)"
      + "VALUES (:id, :first_name, :second_name, :birthdate, :biography, :city, :password) RETURNING id";


  private static final String GET_USER_BY_ID_SQL = "SELECT u.id, u.first_name, u.second_name, u.birthdate, u.biography, u.city, u.password "
      + "FROM \"user\" u WHERE u.id = :id";


  private final NamedParameterJdbcTemplate template;

  public String createUser(UserRegisterPostRequest userRegisterPostRequest) {
    SqlParameterSource parameterSource = new MapSqlParameterSource()
        .addValue("id", UUID.randomUUID().toString())
        .addValue("first_name", userRegisterPostRequest.getFirstName())
        .addValue("second_name", userRegisterPostRequest.getSecondName())
        .addValue("birthdate", userRegisterPostRequest.getBirthdate())
        .addValue("biography", userRegisterPostRequest.getBiography())
        .addValue("city", userRegisterPostRequest.getCity())
        .addValue("password", userRegisterPostRequest.getPassword());
    return template.queryForObject(CREATE_USER_SQL, parameterSource, String.class);
  }

  public UserWithPassword getUserByIdWithCred(String id) {
    SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id", id);
    return template.queryForObject(GET_USER_BY_ID_SQL, parameterSource, (RowMapper<UserWithPassword>) (rs, rowNum) -> UserWithPassword.builder()
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
    User user = template.queryForObject(GET_USER_BY_ID_SQL, parameterSource,
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
}
