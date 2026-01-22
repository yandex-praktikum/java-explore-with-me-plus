# Explore With Me Plus

[🇷🇺 Русская версия](README.md)

---

## 📋 Project Description

**Explore With Me** is an event-sharing application that allows users to share information about interesting events and find companions to participate in them.

---

## 🚀 Implemented Stages

### ✅ Stage 1: Statistics Service (Stats Service)
A microservice for collecting and storing endpoint visit statistics.

**Features:**
- Saving request information to endpoints (`POST /hit`)
- Retrieving view statistics (`GET /stats`)
- Support for filtering by unique IP addresses
- Filtering by date range and URI list

**Technologies:**
- Spring Boot 3.3.0
- Spring Data JPA
- PostgreSQL
- MapStruct
- Lombok

### ✅ Stage 2: Statistics Service Client (Stats Client)
HTTP client for interacting with the statistics service from the main service.

**Features:**
- Sending event view information
- Retrieving statistics for displaying view counts

### ✅ Stage 3: DTO Assembly (Data Transfer Objects)
Common DTO classes for data exchange between services.

**Classes:**
- `EndpointHitDto` — endpoint visit data
- `ViewStatsDto` — view statistics

---

## 🏗️ Project Architecture

```
explore-with-me/
├── stats-service/          # Statistics Service
│   ├── controller/         # REST controllers
│   ├── service/            # Business logic
│   ├── repository/         # Data layer
│   ├── dto/                # Data Transfer Objects
│   ├── model/              # JPA entities
│   ├── mapper/             # MapStruct mappers
│   └── exception/          # Exception handling
└── pom.xml                 # Parent POM
```

---

## 🛠️ Technology Stack

| Technology | Version |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.3.0 |
| PostgreSQL | 16 |
| Maven | 3.9+ |
| Docker | 24+ |
| MapStruct | 1.5.5 |
| Lombok | 1.18.32 |

---

## 🚀 Running the Project

### Using Docker Compose

```bash
docker-compose up -d
```

### Locally

```bash
# Build the project
mvn clean package

# Run the statistics service
cd stats-service
mvn spring-boot:run
```

---

## 📡 API Endpoints

### Stats Service (port 9090)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/hit` | Save request information |
| GET | `/stats` | Retrieve view statistics |

#### Example POST /hit Request

```json
{
  "app": "ewm-main-service",
  "uri": "/events/1",
  "ip": "192.168.1.1",
  "timestamp": "2024-01-15 10:30:00"
}
```

#### Example GET /stats Request

```
GET /stats?start=2024-01-01 00:00:00&end=2024-12-31 23:59:59&uris=/events/1&unique=true
```

---

## 👥 Authors

Yandex Practicum Team

---

## 📄 License

This project was created as part of the Yandex Practicum training program.
