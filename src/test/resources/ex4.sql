CREATE TABLE IF NOT EXISTS student (
    id          bigint not null constraint student_primary_key primary key,
    firstName   varchar(255),
    lastName    varchar(255),
    address     varchar(255),
    birth       date
);
INSERT INTO student(id, firstName, lastName, address, birth) VALUES (1, 'Jean', 'Dupont', '1', {ts '1997-08-29 00:00:00.00'});
INSERT INTO student(id, firstName, lastName, address, birth) VALUES (2, '', 'Dupont', 'Rejected, missing first name', {ts '1997-08-29 00:00:00.00'});
INSERT INTO student(id, firstName, lastName, address, birth) VALUES (3, 'Jean', '', 'Rejected, missing last name', {ts '1997-08-29 00:00:00.00'});
INSERT INTO student(id, firstName, lastName, address, birth) VALUES (4, 'Jean', 'Dupont', 'Other', {ts '1997-08-29 00:00:00.00'});
