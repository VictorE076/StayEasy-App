# StayEasy-App
A web platform for accommodation and rental management, developed using **Java (Spring Boot)** and **Angular** as part of a university project.

## Team Members
  - **George Tănase** (Product Owner / Developer)
  - **Victor Economu** (Scrum Master / Developer)
  - **Alexandru-Mihail Radu** (Developer)
  - **Andreea Pop** (Developer)
  - **Sofia Mihail** (Developer)

## Project Overview
**StayEasy** aims to simplify property and booking management by providing an intuitive web interface for users and administrators.  
The project is developed within the context of the **Analysis & Modeling of Software Systems** and **Software Project Management** university courses.

## Technologies & Tools (to be finalized)
  - Frontend: **Angular**
  - Backend: **Spring Boot**
  - Database: **MySQL** (Railway Cloud Service)
  - Other tools: **Jira / Trello / ClickUp, JUnit, Postman, Spring DevTools**

## Technological Versions (to be finalized)
  - **Java:** OpenJDK 21.0.2  
  - **Maven:** 3.9.6  
  - **Spring Boot:** 3.3.5  
  - **Node.js:** 20.19.5  
  - **npm:** 10.8.2  
  - **Angular:** 20.3.9 (CLI 20.3.8)  
  - **TypeScript:** 5.9.3  
  - **RxJS:** 7.8.2  
  - **Zone.js:** 0.15.1
  - **MySQL:** 9.5.0

## Development Modes
  - **Dev Mode**
      - **npm start** (Angular, port **4200**) + **mvn spring-boot:run** (Spring, port **8080**)
  - **Demo Mode** (checkpoint presentations)
      - **npm run build** + **mvn spring-boot:run**
      - Single server on port **8080**

## Local Configuration Setup

This project uses an `application.properties` file for sensitive configuration (DB credentials, JWT secret, etc.), which is **not included in the repo** for security reasons.

After cloning the project, follow these steps:

1. Navigate to: **src/main/resources/**
2. Copy the example file: **application-example.properties** and rename the copy to **application.properties**
3. Open the new `application.properties` file and fill in your local credentials:
- `spring.datasource.url` → MySQL/Railway connection URL  
- `spring.datasource.username` → DB username  
- `spring.datasource.password` → DB password  
- `application.security.jwt.secret` → Any long random string (used as JWT signing key)  
- `application.security.jwt.expiration` → Token expiration time in ms (default: 3600000ms = 1h)

⚠️ **IMPORTANT:**  
Do **NOT** commit or push your local `application.properties` file.  
It is listed in `.gitignore` and should remain local only.

## University Context
  - Faculty of Mathematics and Computer Science, University of Bucharest
  - Team Project
  - <mark>🚩 **Checkpoint 2 (ongoing):**</mark> Implementation of at least **1/3** of the backlog **(Authentication module)**

