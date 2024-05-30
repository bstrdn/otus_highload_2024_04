CREATE TABLE otus_highload.user
(
    id          varchar,
    first_name  varchar,
    second_name varchar,
    birthdate   date,
    biography   varchar,
    city        varchar,
    password    varchar,
        CONSTRAINT pk_user PRIMARY KEY (id)
);

COMMENT ON TABLE otus_highload.user IS 'Пользователи';
COMMENT ON COLUMN otus_highload.user.id IS 'Идентификатор';
COMMENT ON COLUMN otus_highload.user.first_name IS 'Имя';
COMMENT ON COLUMN otus_highload.user.second_name IS 'Фамилия';
COMMENT ON COLUMN otus_highload.user.birthdate IS 'Дата рождения';
COMMENT ON COLUMN otus_highload.user.biography IS 'Биография';
COMMENT ON COLUMN otus_highload.user.city IS 'Город';
COMMENT ON COLUMN otus_highload.user.password IS 'Пароль';

INSERT INTO otus_highload.user (id, first_name, second_name, birthdate, biography, city, password)
VALUES ('admin', 'Админ', 'Админов', '2000-01-01', '', 'Moscow', '{noop}123');