DROP TABLE IF EXISTS DOCTOR;

CREATE TABLE DOCTOR
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(250) NOT NULL,
    last_name  VARCHAR(250) NOT NULL
);

DROP TABLE IF EXISTS PATIENT;

CREATE TABLE PATIENT
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(250) NOT NULL,
    last_name  VARCHAR(250) NOT NULL
);

alter table DOCTOR add COLUMN hourly_rate decimal;

insert INTO DOCTOR (first_name, last_name, hourly_rate) VALUES
('Osman', 'Butcher', 15.50),
('Memo', 'Doktoroglu', 100.0);