insert INTO DOCTOR (id, first_name, last_name, hourly_rate)
VALUES ('fb7b6b60-2035-4b18-80ea-20faba5fb919', 'Osman', 'Butcher', 15.50),
       ('d0810b24-5fc6-4c3e-aa1d-99fa6da91ec8', 'Memo', 'Doktoroglu', 100.0);

insert INTO PATIENT (id, first_name, last_name)
VALUES ('fb7b6b60-2035-4b18-80ea-20faba5fb919', 'Omar', 'Patientoglu'),
       ('d0810b24-5fc6-4c3e-aa1d-99fa6da91ec8', 'Ramo', 'Salatalikoglu');

insert INTO APPOINTMENT (id, DOCTOR_ID, PATIENT_ID, START_DATE, END_DATE, STATE)
VALUES ('9542f006-cf80-4dcc-82ec-cd2f6a14a87e',
        'fb7b6b60-2035-4b18-80ea-20faba5fb919',
        'd0810b24-5fc6-4c3e-aa1d-99fa6da91ec8',
        '2020-01-30T10:00:35+00:00',
        '2020-01-30T15:00:35+00:00',
        'FINALIZED');

insert INTO TRANSACTION (id, APPOINTMENT_ID, PATIENT_ID, CREATION_DATE, TYPE, COST)
VALUES ('ef284a9a-eefe-4016-88ab-b4e381ef4a69',
        '9542f006-cf80-4dcc-82ec-cd2f6a14a87e',
        'd0810b24-5fc6-4c3e-aa1d-99fa6da91ec8',
        '2020-01-30T10:09:35+00:00',
        'FINALIZATION',
        46.50);