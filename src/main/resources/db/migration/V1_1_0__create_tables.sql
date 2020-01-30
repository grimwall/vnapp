DROP TABLE IF EXISTS DOCTOR;

CREATE TABLE DOCTOR
(
    ID         VARCHAR(36) PRIMARY KEY,
    FIRST_NAME VARCHAR(250) NOT NULL,
    LAST_NAME  VARCHAR(250) NOT NULL
);

DROP TABLE IF EXISTS PATIENT;

CREATE TABLE PATIENT
(
    ID         VARCHAR(36) PRIMARY KEY,
    FIRST_NAME VARCHAR(250) NOT NULL,
    LAST_NAME  VARCHAR(250) NOT NULL
);


alter table DOCTOR
    add COLUMN hourly_rate decimal;

DROP TABLE IF EXISTS APPOINTMENT;

CREATE TABLE APPOINTMENT
(
    ID         VARCHAR(36) PRIMARY KEY,
    DOCTOR_ID  VARCHAR(36)              NOT NULL,
    PATIENT_ID VARCHAR(36)              NOT NULL,
    START_DATE TIMESTAMP WITH TIME ZONE NOT NULL,
    END_DATE   TIMESTAMP WITH TIME ZONE NOT NULL,
    STATE      VARCHAR(50)              NOT NULL
);

ALTER TABLE APPOINTMENT
    ADD CONSTRAINT FK_APPOINTMENT_DOCTOR
        FOREIGN KEY (DOCTOR_ID)
            REFERENCES DOCTOR (ID);

ALTER TABLE APPOINTMENT
    ADD CONSTRAINT FK_APPOINTMENT_PATIENT
        FOREIGN KEY (PATIENT_ID)
            REFERENCES DOCTOR (ID);

DROP TABLE IF EXISTS TRANSACTION;

CREATE TABLE TRANSACTION
(
    ID             VARCHAR(36) PRIMARY KEY,
    APPOINTMENT_ID VARCHAR(36)              NOT NULL,
    PATIENT_ID     VARCHAR(36)              NOT NULL,
    CREATION_DATE  TIMESTAMP WITH TIME ZONE NOT NULL,
    TYPE           VARCHAR(50)              NOT NULL,
    COST           DECIMAL                  NOT NULL
);

ALTER TABLE TRANSACTION
    ADD CONSTRAINT FK_TRANSACTION_APPOINTMENT
        FOREIGN KEY (APPOINTMENT_ID)
            REFERENCES APPOINTMENT (ID);

ALTER TABLE TRANSACTION
    ADD CONSTRAINT FK_TRANSACTION_PATIENT
        FOREIGN KEY (PATIENT_ID)
            REFERENCES PATIENT (ID);


