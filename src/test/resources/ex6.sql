CREATE TABLE IF NOT EXISTS student
(
    id        bigint not null
        constraint student_primary_key primary key,
    firstName varchar(255),
    lastName  varchar(255),
    birth     date
);

CREATE TABLE IF NOT EXISTS address
(
    id bigint not null
        constraint address_primary_key primary key,
    fullName varchar(255),
    student_id int,
    foreign key (student_id) references student(id)
);

CREATE SEQUENCE address_seq;
