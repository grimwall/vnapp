DROP TABLE IF EXISTS DOCTOR;

CREATE TABLE DOCTOR
(
    id         VARCHAR(36) PRIMARY KEY,
    first_name VARCHAR(250) NOT NULL,
    last_name  VARCHAR(250) NOT NULL
);

DROP TABLE IF EXISTS PATIENT;

CREATE TABLE PATIENT
(
    id         VARCHAR(36) PRIMARY KEY,
    first_name VARCHAR(250) NOT NULL,
    last_name  VARCHAR(250) NOT NULL
);

alter table DOCTOR
    add COLUMN hourly_rate decimal;

insert INTO DOCTOR (id, first_name, last_name, hourly_rate)
VALUES ('fb7b6b60-2035-4b18-80ea-20faba5fb919', 'Osman', 'Butcher', 15.50),
       ('d0810b24-5fc6-4c3e-aa1d-99fa6da91ec8', 'Memo', 'Doktoroglu', 100.0);