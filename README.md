# Ahadu Real Estate

Ahadu Real Estate is a Spring Boot web application for managing real estate listings between two main users: agents and buyers. Agents can publish properties, update or delete their own listings, and see buyers who requested a listing. Buyers can browse properties, request listings they are interested in, and manage their profile.

This project was built by our group as part of our application development work. Most of the implementation, including the authentication flow, profile pages, listing management, image upload, buyer/agent business logic, Flyway migration setup, UI pages, and tests, was written by us. The only part that was implemented with AI assistance is the Elasticsearch search implementation.

## Main Features

- Buyer and agent registration/login
- Separate profile pages for buyers and agents
- Agents can create, update, and delete their own property listings
- Property images are uploaded
- Buyers can request listings they want to buy or rent
- Agents can view buyers who requested each of their listings
- Home page search bar with Elasticsearch support
- Flyway database migration for the main schema
- Test configuration using H2 and Hibernate schema generation

## Tech Stack

- Java 17
- Spring Boot 4
- Spring MVC and Thymeleaf
- Spring Security
- Spring Data JPA
- PostgreSQL for development
- Flyway for database migrations
- Elasticsearch for listing search
- H2 for tests
- Docker Compose for local services

## Running the App Locally

Make sure Docker is running, then start PostgreSQL and Elasticsearch:

```powershell
docker compose up -d postgres elasticsearch
```

Then run the Spring Boot application:

```powershell
.\mvnw.cmd spring-boot:run
```

By default, the app expects:

- PostgreSQL: `localhost:5432`
- Elasticsearch: `localhost:9200`
- Database name: `ahadu_db`
- Database user: `ahadu_user`

The database schema is managed with Flyway migrations from:

```text
src/main/resources/db/migration
```

## Search

The home page search bar searches listings by location, title, description, address, city, listing status, and property type. The search uses Elasticsearch when it is available. If Elasticsearch is not running, the app falls back to database filtering so the application can still work during development.

Again, the Elasticsearch implementation is the only implementation in this project that was written with AI assistance.

## Tests

Tests use a separate configuration file:

```text
src/test/resources/application.properties
```

The test environment uses H2 with Hibernate `create-drop`, while the development environment uses PostgreSQL with Flyway migrations.

Run tests with:

```powershell
.\mvnw.cmd test
```

## Future Nginx Setup

Later on, we plan to include an Nginx implementation for serving the application in a more production-like setup. The expected idea is to use Nginx as a reverse proxy in front of the Spring Boot application, and the README will be updated with the final Nginx configuration and deployment steps when that part is added.

## Project Note

The core application logic and UI were implemented by us, and only the Elasticsearch search feature was AI-assisted.
