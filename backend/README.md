# Book Review Platform Backend

A comprehensive book review platform backend built with Java 17, Spring Boot, and Gradle.

## Features

- **Authentication & Authorization**: JWT-based authentication with role-based access control
- **Book Management**: Complete CRUD operations for books with advanced search and filtering
- **Review System**: Full review lifecycle with rating calculations and user feedback
- **User Profiles**: Profile management with favorite books and reading statistics
- **Recommendation Engine**: AI-powered recommendations with multiple strategies
- **API Documentation**: OpenAPI/Swagger documentation for all endpoints

## Technology Stack

- **Java 17**: Latest LTS version with modern language features
- **Spring Boot 3.2.1**: Latest stable release with comprehensive auto-configuration
- **Spring Security**: JWT-based stateless authentication
- **Spring Data JPA**: Data access layer with Hibernate
- **H2 Database**: In-memory database for development
- **PostgreSQL**: Production database
- **Gradle**: Build automation and dependency management
- **JUnit 5**: Testing framework with Mockito
- **JaCoCo**: Code coverage reporting
- **OpenAPI 3**: API documentation

## Project Structure

```
src/
├── main/
│   ├── java/com/bookreview/
│   │   ├── auth/              # Authentication module
│   │   ├── book/              # Book management module
│   │   ├── review/            # Review system module
│   │   ├── user/              # User profile module
│   │   ├── recommendation/    # Recommendation engine module
│   │   ├── config/            # Configuration classes
│   │   ├── dto/               # Data Transfer Objects
│   │   ├── exception/         # Exception handling
│   │   └── util/              # Utility classes
│   └── resources/
│       ├── application.yaml   # Main configuration
│       ├── application-dev.yaml
│       ├── application-test.yaml
│       ├── application-prod.yaml
│       └── data.sql           # Sample data
└── test/
    ├── java/com/bookreview/   # Unit and integration tests
    └── resources/
        └── application-test.yaml
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle 8.5 or higher (or use included wrapper)

### Running the Application

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd book-review-platform/backend
   ```

2. **Build the project**:
   ```bash
   ./gradlew build
   ```

3. **Run the application**:
   ```bash
   ./gradlew bootRun
   ```

4. **Access the application**:
   - API Base URL: `http://localhost:8080/api/v1`
   - H2 Console: `http://localhost:8080/h2-console`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

### Testing

**Run all tests**:
```bash
./gradlew test
```

**Generate coverage report**:
```bash
./gradlew jacocoTestReport
```

**Check coverage thresholds**:
```bash
./gradlew jacocoTestCoverageVerification
```

Coverage reports are generated in `build/reports/jacoco/test/html/index.html`

### Code Quality

**Run Checkstyle**:
```bash
./gradlew checkstyleMain checkstyleTest
```

**Build with quality checks**:
```bash
./gradlew build
```

## API Endpoints

### Authentication
- `POST /auth/register` - Register new user
- `POST /auth/login` - User login
- `POST /auth/refresh` - Refresh access token
- `POST /auth/logout` - User logout
- `POST /auth/forgot-password` - Request password reset
- `POST /auth/reset-password` - Reset password

### Books
- `GET /books` - Get all books (with filtering and pagination)
- `GET /books/{id}` - Get book by ID
- `POST /books` - Create book (Admin only)
- `PUT /books/{id}` - Update book (Admin only)
- `DELETE /books/{id}` - Delete book (Admin only)
- `GET /books/search` - Search books
- `GET /books/top-rated` - Get top-rated books

### Reviews
- `GET /reviews` - Get all reviews (with filtering)
- `GET /reviews/{id}` - Get review by ID
- `POST /reviews` - Create review
- `PUT /reviews/{id}` - Update review (Owner only)
- `DELETE /reviews/{id}` - Delete review (Owner/Admin only)
- `GET /reviews/book/{bookId}` - Get reviews for specific book

### User Profile
- `GET /users/profile` - Get current user profile
- `PUT /users/profile` - Update current user profile
- `GET /users/{id}/public-profile` - Get public user profile
- `GET /users/favorites` - Get favorite books
- `POST /users/favorites/{bookId}` - Add book to favorites
- `DELETE /users/favorites/{bookId}` - Remove book from favorites
- `GET /users/stats` - Get user statistics

### Recommendations
- `GET /recommendations` - Get personalized recommendations
- `POST /recommendations/generate` - Generate new recommendations
- `GET /recommendations/top-rated` - Get top-rated recommendations
- `GET /recommendations/by-genre` - Get genre-based recommendations
- `GET /recommendations/ai-powered` - Get AI-powered recommendations
- `POST /recommendations/feedback` - Provide recommendation feedback

## Configuration

### Environment Variables

**Development**:
- `SPRING_PROFILES_ACTIVE=dev`

**Production**:
- `DB_HOST` - Database host
- `DB_PORT` - Database port
- `DB_NAME` - Database name
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret
- `OPENAI_API_KEY` - OpenAI API key
- `REDIS_HOST` - Redis host (for caching)
- `REDIS_PASSWORD` - Redis password

### Database Configuration

**Development (H2 in-memory)**:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
```

**Production (PostgreSQL)**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bookreview
    driver-class-name: org.postgresql.Driver
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

## Security

- JWT-based stateless authentication
- BCrypt password hashing
- Role-based authorization (USER, ADMIN)
- CORS configuration for frontend integration
- Input validation and sanitization
- SQL injection prevention via JPA

## Monitoring & Observability

- Spring Boot Actuator endpoints
- Health checks: `/actuator/health`
- Metrics: `/actuator/metrics`
- Prometheus metrics: `/actuator/prometheus`

## Development Guidelines

1. **Code Style**: Follow Checkstyle configuration
2. **Testing**: Maintain 80%+ test coverage
3. **Documentation**: Document all public APIs
4. **Error Handling**: Use global exception handler
5. **Logging**: Use structured logging with SLF4J
6. **Security**: Never expose sensitive data in logs

## Deployment

### Docker
```dockerfile
FROM openjdk:17-jre-slim
COPY build/libs/book-review-platform-0.1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Build and Run
```bash
./gradlew bootJar
docker build -t book-review-platform .
docker run -p 8080:8080 book-review-platform
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes with tests
4. Ensure coverage >= 80%
5. Run quality checks
6. Submit pull request

## License

This project is licensed under the MIT License.
