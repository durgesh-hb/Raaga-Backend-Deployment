# 🎵 Raaga Backend

Welcome to the backend repository for **Raaga** — a lightweight, high-performance Java Spring Boot service designed to handle music discovery, proxying, user authentication, and user library management (liked songs & custom playlists).

---

## 💡 Overview

Raaga Backend acts as the core engine behind the Raaga music experience. It connects front-end client applications with music providers (such as JioSaavn), while managing user library state (liked tracks, playlists, profile data) using a PostgreSQL database (hosted on Supabase or locally).

Whether you're developing locally, running automated containers via Docker, or deploying to cloud environments like Render, this backend is structured for smooth development and effortless deployment.

---

## ✨ Features

- 🔍 **Music Search & Streaming Proxy**: Search songs, retrieve track metadata, and proxy music stream URLs reliably via integrated provider services.
- ❤️ **Liked Songs Management**: Seamlessly fetch, toggle (like/unlike), and soft-delete saved tracks for individual users.
- 🎼 **Custom Playlists**: Create playlists, add/remove tracks, and fetch curated track lists per user.
- 🔑 **Google Authentication**: Built-in support for verifying Google OAuth ID tokens and managing user sign-ins.
- 🗄️ **Supabase / PostgreSQL Integration**: Database connection logging with automatic schema synchronization via Hibernate JPA.
- 🐳 **Docker-Ready**: Packaged with a multi-stage Docker build and Docker Compose configuration for quick setup.

---

## 🛠️ Tech Stack

- **Framework**: Java 17 + Spring Boot 3
- **Database**: PostgreSQL (Supabase compatible) with Spring Data JPA & Hibernate
- **Build Tool**: Apache Maven (with included Maven Wrapper `./mvnw`)
- **Environment Management**: `cdimascio/dotenv-java` for loading `.env` variables automatically
- **Containerization**: Docker & Docker Compose

---

## 🚀 Getting Started

Follow these simple steps to get the server running on your machine.

### Prerequisites

Make sure you have installed:
- [Java 17 OpenJDK](https://adoptium.net/) or higher
- [Maven](https://maven.apache.org/) (or use the bundled `./mvnw` script)
- [Docker & Docker Compose](https://www.docker.com/) *(optional, for containerized setup)*

---

### 1. Clone & Setup Environment

1. Copy the example environment template to create your local `.env` file:

   ```bash
   cp .env.example .env
   ```

2. Open `.env` and fill in your database credentials and API configuration:

   ```env
   # Database Configuration (PostgreSQL / Supabase)
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=your_secure_password

   # External Services
   MUSIC_API_BASE_URL=https://jiosaavn-api-gato.onrender.com

   # Server Port (Optional)
   PORT=8080
   ```

---

### 2. Run Locally

You can launch the backend using the Maven wrapper:

```bash
# On Linux/macOS
./mvnw spring-boot:run

# On Windows PowerShell / Command Prompt
.\mvnw.cmd spring-boot:run
```

Once started, the backend will print a connection status banner in the terminal:
```text
=================================================
✅ SUCCESS: Connected to Supabase PostgreSQL Database!
Database Product Name: PostgreSQL
...
=================================================
```

The server will be available at `http://localhost:8080`.

---

### 3. Run with Docker Compose

If you prefer running everything in a container:

```bash
docker-compose up --build
```

To stop the containers:
```bash
docker-compose down
```

---

## 📡 Key API Endpoints

### 🩺 System & Health Checks
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/music/test` | Quick sanity check to confirm the backend is live |
| `GET` | `/api/music/db-check` | Verifies PostgreSQL / Supabase connection status |

### 🎵 Music Discovery
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/music/search?q={query}` | Search songs across music provider API |
| `GET` | `/api/v1/music/track/{id}` | Get detailed information and stream link for a track |

### ❤️ User Library & Playlists
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/library/liked-songs?userId={id}` | Retrieve liked songs for a user |
| `POST` | `/api/library/liked-songs` | Toggle liked status for a track |
| `DELETE` | `/api/library/liked-songs/{trackId}` | Remove a song from liked collection |
| `GET` | `/api/library/playlists?userId={id}` | Get all custom playlists for a user |
| `POST` | `/api/library/playlists` | Create a new playlist |
| `DELETE` | `/api/library/playlists/{playlistId}` | Delete a playlist |
| `GET` | `/api/library/playlists/{playlistId}/tracks` | Get all tracks inside a specific playlist |
| `POST` | `/api/library/playlists/{playlistId}/tracks` | Add a track to a playlist |
| `DELETE` | `/api/library/playlists/{playlistId}/tracks/{trackId}` | Remove a track from a playlist |

### 🔐 Authentication
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/auth/google/url` | Retrieve Google OAuth consent URL |
| `POST` | `/api/auth/google/verify` | Verify Google ID token or code and authenticate user |

---

## 🤝 Project Structure

```text
Raaga-Backend-Deployment/
├── src/
│   └── main/
│       ├── java/com/mymusic/backend/
│       │   ├── config/          # CORS & Web Configuration
│       │   ├── controller/      # REST API Controllers (Auth, Library, Music)
│       │   ├── dto/             # Data Transfer Objects
│       │   ├── model/           # JPA Database Entities
│       │   ├── repository/      # Spring Data JPA Repositories
│       │   └── service/         # Business Logic & External API Providers
│       └── resources/
│           └── application.yml  # Application Spring Properties
├── .env.example                 # Environment variable reference
├── Dockerfile                   # Multi-stage Docker build file
├── docker-compose.yml           # Local container orchestra configuration
└── pom.xml                      # Maven build dependencies
```

---

## 💬 Need Help or Have Questions?

If you run into issues connecting to your database or fetching music tracks:
1. Double-check your `.env` settings against [`.env.example`](.env.example).
2. Hit the `/api/music/db-check` endpoint to verify database connectivity.
3. Check application logs for detailed database error messages.

Happy Coding! 🎧✨
