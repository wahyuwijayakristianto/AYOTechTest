# Booking Test

Automation test to validate booking price and detect duplicate bookings.
Data is read from schedules.xlsx and bookings.xlsx in the data/ folder.

## Requirements

- Python 3
- Java 11 or higher

## Setup

Run once in terminal: python setup.py

This will download JUnit and generate the editor config automatically.

## How to run

### IntelliJ
Open this folder, wait for Maven sync, open BookingTest.java, click ▶

### VS Code
Install Extension Pack for Java, then open this folder. VS Code will auto-detect pom.xml and set up the project. Open BookingTest.java and click Run Test above the class name.

### Terminal
mvn test