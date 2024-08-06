CREATE TABLE otus_highload.post
(
    id           varchar primary key,
    text         varchar,
    created_at   timestamp(0),
    authorUserId varchar references otus_highload.user
);