# E-Learning / Server

## 1. Technology used

| Technology      | Version    | Describe                             |
|:----------------|:-----------|:-------------------------------------|
| **Java**        | 17         | Main Language                        |
| **Spring Boot** | 3.5.6      | Application development framework.   |
| **Maven**       | 3.9.11     | Dependency management and Build tool.|
| **Database**    | PostgreSQL | Database.                            |


## 2. Directory structure

```
Server/
├── .mvn/
├── src/
│   ├── main/                               # Production Code
│   │   ├── java/                           # Java Code (.java)
│   │   │   └── com.example.server/         # Root Package
│   │   │       ├── config/                 # Application configuration
│   │   │       ├── controller/             # Class for handling HTTP requests (REST Endpoints).
│   │   │       ├── service/                # Class containing the main Business Logic.
│   │   │       ├── repository/             # Class for communicating with the Database (Data Access Layer - JPA/Hibernate).
│   │   │       ├── model/                  # Entity/Model class that maps to a table in the DB.
│   │   │       └── dto/                    # Data Transfer Object class, used for data transmission.
│   │   └── resources/                      # Contains static resources and configuration files
│   │       ├── application.properties/yml  # Main Spring Boot configuration file (DB, Server Port, Logging).
│   │       └── static/                     # Static files (Images, CSS, JS) if served by the server.
│   └── test/                               # Contains source code for Unit and Integration Tests.
└── pom.xml                                 # Main Maven configuration file.
```


## 3. Config database

- Database name: e-learning
- Host: localhost
- Port: 5432
- Username: postgres
- Password: 123456