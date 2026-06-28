# Ahadu Real Estate

[https://github.com/Mihre15/Ahadu-estate/actions/workflows/ci.yml/badge.svg](https://github.com/Mihre15/Ahadu-estate-/edit/main/README.md)

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
- Nginx
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
- Nginx: `localhost:80`
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

## Nginx Setup

# Nginx Integration

## Why Nginx?

The application uses **Nginx** as a reverse proxy in front of the Spring Boot application.

This was added to:

- Provide a single entry point for incoming HTTP requests.
- Hide the Spring Boot application's internal port (8080) from clients.
- Make it easier to add HTTPS, load balancing, caching, or rate limiting in the future without modifying the application.
- Follow a production-ready deployment architecture where the web server handles incoming traffic and forwards requests to the application.

The request flow is now:

```text
Client
   │
   ▼
Nginx (Port 80)
   │
   ▼
Spring Boot (Port 8080)
```

Users access the application through **Nginx** instead of connecting directly to the Spring Boot server.

---

## Project Structure

```
project/
├── Dockerfile
├── docker-compose.yml
├── nginx/
│   └── nginx.conf
├── src/
├── pom.xml
└── target/
```

### File Descriptions

- **Dockerfile**
  - Builds the Spring Boot Docker image.

- **docker-compose.yml**
  - Starts the Spring Boot application, the Nginx reverse proxy together, elasticsearch index and postgres database.

- **nginx/nginx.conf**
  - Contains the Nginx configuration that forwards incoming requests to the Spring Boot container and can be configured as a load balancer when more containers of springboot are initialized.

---

## Starting the Application

### 1. Build the Spring Boot application

From the `project` directory:

```bash
mvn clean package
```

This generates the application JAR in the `target/` directory.

---

### 2. Build and start the Docker containers

From the same directory, run:

```bash
docker compose up --build
```

This command will:

1. Build the Spring Boot Docker image.
2. Start the Spring Boot container.
3. Start the Nginx container and other services.
4. Connect both containers on the same Docker network.

---

### 3. Access the application

Once the containers are running, open:

```
http://localhost
```

Do **not** access:

```
http://localhost:8080
```

Port `8080` is intended for internal communication between Nginx and the Spring Boot container. All client requests should go through Nginx on port `80`.

---

## Stopping the Application

To stop the application:

```bash
docker compose down
```

---

## Updating the Application

Whenever changes are made to the Spring Boot code:

1. Rebuild the application:

```bash
mvn clean package
```

2. Rebuild and restart the containers:

```bash
docker compose up --build
```

This ensures the latest application JAR is included in the Docker image before the containers are started.

## Project Note

The core application logic and UI were implemented by us, and only the Elasticsearch search feature was AI-assisted.
