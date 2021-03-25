CREATE TABLE IF NOT EXISTS address
(
    id bigint not null
        constraint address_primary_key primary key,
    fullName varchar(255),
    student_id int
);

CREATE SEQUENCE address_seq;
