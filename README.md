# BizLocator V2.0

BizLocator V2.0 is a full-stack application that allows users to search for business locations by name and retrieve real-world address information using the OpenStreetMap Nominatim API. The application displays location details in a React-based web interface and stores the results in an H2 database using a Spring Boot backend.

## Features

- Search businesses by name
- Fetch real-world location data using OpenStreetMap Nominatim API
- Display multiple business locations/branches
- Store retrieved locations in H2 database
- View previously saved business locations
- REST API-based architecture
- Responsive React UI

## Technology Stack

### Frontend
- React
- Axios
- HTML/CSS
- Bootstrap (Optional)

### Backend
- Java 21
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- H2 Database
- Maven

### External Service
- OpenStreetMap Nominatim API

## Project Structure

```text
BizLocator/
├── frontend/
│   ├── src/
│   ├── public/
│   └── package.json
│
├── backend/
│   ├── src/main/java
│   ├── src/main/resources
│   ├── pom.xml
│   └── mvnw
│
└── README.md
```

## Application Flow

1. User enters a business name in the React UI.
2. React sends a request to the Spring Boot API.
3. Spring Boot calls the OpenStreetMap Nominatim API.
4. Location details are retrieved and processed.
5. Results are stored in the H2 database.
6. Location information is returned to the UI.
7. User can view current and previously saved records.

## Database

### H2 Console

```text
http://localhost:8080/h2-console
```

### Default Configuration

```properties
spring.datasource.url=jdbc:h2:mem:bizlocator
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```

## API Endpoints

### Search Business Locations

```http
POST /api/business/search
```

Request:

```json
{
  "businessName": "Starbucks"
}
```

Response:

```json
{
  "businessName": "Starbucks",
  "locations": [
    {
      "address": "123 Main Street",
      "city": "Seattle",
      "state": "Washington",
      "country": "USA"
    }
  ]
}
```

### Retrieve Saved Locations

```http
GET /api/business/locations
```

## Running the Application

### Backend

Navigate to backend folder:

```bash
cd backend
```

Build project:

```bash
mvn clean install
```

Run application:

```bash
mvn spring-boot:run
```

Backend runs on:

```text
http://localhost:8080
```

### Frontend

Navigate to frontend folder:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Start application:

```bash
npm start
```

Frontend runs on:

```text
http://localhost:3000
```

## Future Enhancements

- PostgreSQL support
- Google Maps integration
- Location filtering by state and country
- Export results to CSV/Excel
- Authentication and user management
- Docker deployment
- Caching for faster searches

## Author

Vinith

## License

This project is developed for learning and demonstration purposes.