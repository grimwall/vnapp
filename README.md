# VNAPP

This document explains how to compile and run this project.


## Compile & Run

This is a Java 8 Spring Boot project meant to be built by maven.
1. Requirements: Install the latest Java 8 SDK and Maven tool
2. execute **'mvnw install'** (without quotes) to build and run all tests
3. execute **'mvnw spring-boot:run'** to run the server in localhost:8080/

## DB restore

This app uses H2 file based persistence for DB operations and Flyway for DB migrations.
To restore the app to it's initial state, simply delete the data folder in the project root

## API description
### DOCTORS


todo
