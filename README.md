# VNAPP

This document explains how to compile and run this project.


## Compile & Run

This is a Java 8 Spring Boot project meant to be built by maven.
1. Requirements: Install the latest Java 8 SDK and Maven tool
2. execute **'mvnw install'** (without quotes) to build and run all tests
3. execute **'mvnw spring-boot:run'** to run the server in localhost:8080/

There are already some initial seed data in the DB to facilitate API testing, 
simply use the provided REST endpoints to retrieve them (detailed how to below).

## DB restore

This app uses H2 file based persistence for DB operations and Flyway for DB migrations.
To restore the app to it's initial state, simply delete the data folder in the project root

## API description

Please use the following format for all Date/time fields:

Date/time format: 'yyyy-MM-dd'T'HH:mmxxx', example: 2020-01-11T12:01+03:00

### DOCTORS
#### root path: /doctors
#### GET:
   returns all doctors
#### POST:
   creates a new doctor.
    
   example body:
    
    
    {
    	"firstName": "ox",
    	"lastName": "memoli",
    	"hourlyRate": 15.3
    }
    
#### PUT: /{id}
   updates a new doctor. id must be an existing doctor
    
   example body:
    
    
    {
    	"firstName": "ox",
    	"lastName": "memoli",
    	"hourlyRate": 15.3
    }
    
    
   returns: 200 in case of success
### PATIENTS
#### root path: /patients
#### GET:
   returns all patients
#### POST:
   creates a new patients. Returns 201 in case of success.
   
   example body:
   
    {
        "firstName": "Omarboy",
        "lastName": "Patientoglu"
    }
    
### TRANSACTIONS
#### root path: /transactions
#### GET:
   returns all transactions
#### GET: /{id}
   retrieves specified transaction
   
   example response:
    
    {
        "firstName": "Omarboy",
        "lastName": "Patientoglu"
    }

### APPOINTMENTS
#### root path: /appointments
#### GET:
   returns all appointments
#### GET: /{id}
   retrieves specified appointment
    
   example response:
    
    {
        "id": "e110f25a-b718-40a8-8f45-f47cfc9aac60",
        "doctorId": "d0810b24-5fc6-4c3e-aa1d-99fa6da91ec8",
        "patientId": "fb7b6b60-2035-4b18-80ea-20faba5fb919",
        "startDate": "2020-01-11T12:01+00:00",
        "duration": 0,
        "endDate": "2020-01-11T13:01+00:00",
        "state": "CANCELLED",
        "transactions": [
            "f8574092-20b5-4fb0-a72a-c9d479f622c6",
            "9887f752-d490-45c6-ad3a-6db066fc1d88"
        ]
    }
    
#### POST: /search
   searches all appointments with the following parameters in the request body, case sensitive.
  
   example body:
    
    {
        "doctorId": "d0810b24-5fc6-4c3e-aa1d-99fa6da91ec8"
        "patientId": "fb7b6b60-2035-4b18-80ea-20faba5fb919",
        "startDate": "2020-01-11T12:01+00:00",
        "endDate": "2020-01-11T13:01+00:00",
        "state": "CANCELLED"
    }
    
#### POST: 
   Reserves a new appointment reservation. This is the first step to be done to finalize 
   a reservation. Duration must be between 60 and 360 minutes.
   
   codes: 
   
   201: Success
   
   400: Doctor or patient does not exist or required interval for the doctor already has a non cancelled appointment.
   
    
   example body:
    
    {
        "doctorId": "d0810b24-5fc6-4c3e-aa1d-99fa6da91ec8",
        "patientId": "fb7b6b60-2035-4b18-80ea-20faba5fb919",
        "startDate": "2020-02-03T16:00+05:00",
        "duration": 60
    }
    
    
   example return:
   
       {
           "id": "1e645872-4c74-471f-955f-9dbda2360b78",
           "doctorId": "d0810b24-5fc6-4c3e-aa1d-99fa6da91ec8",
           "patientId": "fb7b6b60-2035-4b18-80ea-20faba5fb919",
           "startDate": "2020-02-03T11:00+00:00",
           "duration": 0,
           "endDate": "2020-02-03T12:00+00:00",
           "state": "RESERVED",
           "transactions": null
       }

#### POST: /{id}/finalize
   Finalizes a previously reserved appointment and creates a transaction with the hourly rate of the doctor. We assume the patient will be charged 
   by another API endpoint in this exercise.
   
   codes: 
   
   200: Success
   
   400: Doctor or patient does not exist.
   
    
   example return (ignore the duration):
   
    {
        "id": "1e645872-4c74-471f-955f-9dbda2360b78",
        "doctorId": "d0810b24-5fc6-4c3e-aa1d-99fa6da91ec8",
        "patientId": "fb7b6b60-2035-4b18-80ea-20faba5fb919",
        "startDate": "2020-02-03T11:00+00:00",
        "duration": 0,
        "endDate": "2020-02-03T12:00+00:00",
        "state": "FINALIZED",
        "transactions": [
            "7b4fb5b3-84ee-486c-b6f0-147cb12a5152"
        ]
    }
  
#### POST: /{id}/cancel
   Cancels a previously FINALIZED appointment and possibly creates a  cancellation transaction if the cancellation came too late. 
   
   The cancel fee is based on the original price of the finalization transaction.
   
   We assume the patient will be charged 
   by another API endpoint in this exercise.
   
   codes: 
   
   200: Success
   
   400: Doctor or patient does not exist.
   
    
   example return (ignore the duration):
   
    {
        "id": "1e645872-4c74-471f-955f-9dbda2360b78",
        "doctorId": "d0810b24-5fc6-4c3e-aa1d-99fa6da91ec8",
        "patientId": "fb7b6b60-2035-4b18-80ea-20faba5fb919",
        "startDate": "2020-02-03T11:00+00:00",
        "duration": 0,
        "endDate": "2020-02-03T12:00+00:00",
        "state": "CANCELLED",
        "transactions": [
            "7b4fb5b3-84ee-486c-b6f0-147cb12a5152"
        ]
    }
               
    
todo
