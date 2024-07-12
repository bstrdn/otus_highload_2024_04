CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_first_name_trgm ON otus_highload."user" USING GIN (first_name gin_trgm_ops);
CREATE INDEX idx_second_name_trgm ON otus_highload."user" USING GIN (second_name gin_trgm_ops);