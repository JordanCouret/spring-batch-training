DROP TABLE IF EXISTS student;

CREATE TABLE IF NOT EXISTS student (
    id          bigint not null constraint student_primary_key primary key,
    firstName   varchar(255),
    lastName    varchar(255),
    address     varchar(255),
    birth       date
);
