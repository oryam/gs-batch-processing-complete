DROP TABLE people IF EXISTS;

CREATE TABLE people  (
    row_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    id BIGINT,
    first_name VARCHAR(20),
    last_name VARCHAR(20),
    age INT
);
