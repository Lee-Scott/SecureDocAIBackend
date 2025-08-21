SecureDocAI Backend

The SecureDocAI Backend is the engine that powers AI-driven, secure document management. Built with Spring Boot and PostgreSQL, it provides APIs for authentication, document storage, AI-powered analysis, questionnaires, and collaborative chat rooms — all designed to remove everyday friction in how individuals and teams manage knowledge.

Why SecureDocAI?

Teams and individuals often face:

Scattered information & version chaos → We provide a single source of truth.

Search frustration → Smart tagging & AI-powered search make documents instantly findable.

Onboarding overload → AI summaries guide new team members.

Collaboration bottlenecks → Real-time chat + document sharing streamline teamwork.

Security & compliance headaches → Self-hosted, privacy-first, HIPAA/GDPR friendly.

Features

🔑 Authentication & Security: User registration, login, MFA, roles, and JWT tokens

📄 Document Management: Upload, checkout/check-in, review, search, version history, and deletion

🤖 AI Processing: Summaries, jargon translation, insights, and automated data entry

💬 Chat Integration: Share AI insights directly into chat rooms

📊 Questionnaires: Create, distribute, analyze responses

🛡 Compliance-Ready: Data ownership, encryption, and self-hosting options

Tech Stack

Language: Java 21

Framework: Spring Boot 3.x

Security: Spring Security 6, JWT

Database: PostgreSQL (via Docker)

Build Tool: Maven

Docs: OpenAPI/Swagger (/v3/api-docs)

Getting Started
Prerequisites

JDK 21+

Maven 3.9+

Docker & Docker Compose

Setup

Clone repo

git clone https://github.com/Lee-Scott/-PutOnYourGenes_SecureDocAIBackend.git
cd -PutOnYourGenes_SecureDocAIBackend


Start PostgreSQL in Docker

docker run --name securedocai-postgres \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_DB=securedocai \
  -p 5432:5432 \
  -d postgres:15


Configure Spring properties
Edit src/main/resources/application.properties (or create application-docker.properties):

spring.datasource.url=jdbc:postgresql://localhost:5432/securedocai
spring.datasource.username=admin
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=update

# Security
jwt.secret=your-secret-key


Run the app

mvn spring-boot:run


Backend will be available at:

http://localhost:8085

API Overview

Explore the APIs at:

http://localhost:8085/swagger-ui.html

Document Resource

POST /documents/upload → Upload a document

GET /documents/{id} → Retrieve document

GET /documents/{id}/versions → Version history

DELETE /documents/delete/{id} → Delete document

AI Document Processing

POST /api/v1/ai/documents/process → Process document with AI

POST /api/v1/ai/documents/process/batch → Batch process

POST /api/v1/ai/documents/process-and-send → Process & send to chat

User Resource

POST /user/register → Register

POST /user/login → Authenticate & receive JWT

PATCH /user/updatePassword → Change password

GET /user/profile → Fetch profile

Chat Room Resource

GET /api/chatrooms → List chat rooms

POST /api/chatrooms/{chatRoomId}/messages → Send message

POST /api/chatrooms/{chatRoomId}/share-results → Share AI output

Questionnaire Resource

POST /api/questionnaires → Create questionnaire

GET /api/questionnaires/{id} → Fetch questionnaire

GET /api/questionnaires/{id}/analytics → Analytics on responses

Roadmap

 AI-powered onboarding assistant

 Document insight dashboard

 HIPAA/GDPR compliance presets

 Docker Compose stack (backend + Postgres + pgAdmin)

 Architecture
 Service Flow (High-Level)
 flowchart LR
     subgraph Client["Clients"]
         FE["Frontend (SecureDocAIApp)"]
         Integrations["Integrations (Google Drive, Dropbox, etc.)"]
     end

     FE -->|REST/JSON| API["SecureDocAI Backend (Spring Boot)"]

     subgraph API_Internal["Backend Modules"]
         U[User/Auth Controller]
         D[Document Controller]
         A[AI Controller]
         Q[Questionnaire Controller]
         C[Chat Room Controller]

         USvc[User Service]
         DSvc[Document Service]
         ASvc[AI Service]
         QSvc[Questionnaire Service]
         CSvc[Chat Service]

         Repo[(Spring Data JPA Repositories)]
     end

     API --> U & D & A & Q & C
     U --> USvc --> Repo
     D --> DSvc --> Repo
     Q --> QSvc --> Repo
     C --> CSvc --> Repo

     ASvc -->|LLM/OCR/Summarization| AIProv["AI Provider(s) / Model Gateway"]
     DSvc --> Storage[(PostgreSQL: metadata, versions)]
     DSvc -.-> Blob["File Storage (disk/volume/S3-compatible)"]

     Integrations --> DSvc

     Repo --> DB[(PostgreSQL)]

 Data Model (ER/Domain Overview)
 erDiagram
     USERS ||--o{ ROLES : "has"
     USERS ||--o{ DOCUMENTS : "owns"
     USERS ||--o{ CHATROOMS : "memberOf"
     CHATROOMS ||--o{ MESSAGES : "contains"
     DOCUMENTS ||--o{ DOC_VERSIONS : "has versions"
     DOCUMENTS ||--o{ AI_REPORTS : "produces"
     QUESTIONNAIRES ||--o{ QUESTIONS : "contains"
     QUESTIONS ||--o{ QUESTION_OPTIONS : "has"
     QUESTIONNAIRES ||--o{ QUESTIONNAIRE_PAGES : "has"
     QUESTIONNAIRES ||--o{ QUESTIONNAIRE_RESPONSES : "receives"
     QUESTIONNAIRE_RESPONSES ||--o{ QUESTION_RESPONSES : "includes"

     USERS {
       uuid id PK
       string email
       string password_hash
       string display_name
       bool enabled
       bool mfa_enabled
       timestamp created_at
     }

     ROLES {
       uuid id PK
       string name  // USER, MOD, ADMIN
     }

     DOCUMENTS {
       uuid id PK
       uuid owner_id FK
       string filename
       string mime_type
       string storage_ref  // path or object key
       string status       // checked_in/out, processing, ready
       jsonb tags         // smart meta tags
       timestamp created_at
       timestamp updated_at
     }

     DOC_VERSIONS {
       uuid id PK
       uuid document_id FK
       int version
       string storage_ref
       timestamp created_at
     }

     AI_REPORTS {
       uuid id PK
       uuid document_id FK
       string provider
       jsonb result_payload
       timestamp created_at
     }

     CHATROOMS {
       uuid id PK
       string name
       string context   // optional: doc/ref context
       timestamp created_at
     }

     MESSAGES {
       uuid id PK
       uuid chatroom_id FK
       uuid author_id FK
       text content
       jsonb attachments
       timestamp created_at
     }

     QUESTIONNAIRES {
       uuid id PK
       string title
       string status  // draft, active, archived
       jsonb metadata
       timestamp created_at
     }

     QUESTIONS {
       uuid id PK
       uuid questionnaire_id FK
       string type    // text, mcq, scale, etc.
       text prompt
       int order_index
     }

     QUESTION_OPTIONS {
       uuid id PK
       uuid question_id FK
       text label
       text value
     }

     QUESTIONNAIRE_PAGES {
       uuid id PK
       uuid questionnaire_id FK
       int order_index
       text title
     }

     QUESTIONNAIRE_RESPONSES {
       uuid id PK
       uuid questionnaire_id FK
       uuid respondent_id FK
       jsonb category_scores
       timestamp submitted_at
     }

     QUESTION_RESPONSES {
       uuid id PK
       uuid questionnaire_response_id FK
       uuid question_id FK
       jsonb answer
     }

 One-Command Dev Stack (Docker)

 Add a .env file (or .env.example) at the repo root:

 # ---- PostgreSQL ----
 POSTGRES_DB=securedocai
 POSTGRES_USER=admin
 POSTGRES_PASSWORD=admin
 POSTGRES_PORT=5432

 # ---- App ----
 APP_PORT=8085
 JWT_SECRET=change-me-in-dev
 SPRING_PROFILES_ACTIVE=docker

 # Optional: pgAdmin
 PGADMIN_DEFAULT_EMAIL=admin@local.test
 PGADMIN_DEFAULT_PASSWORD=admin
 PGADMIN_PORT=5050


 Create docker-compose.yml at the repo root:

 version: "3.9"

 services:
   db:
     image: postgres:15
     container_name: securedocai-postgres
     restart: unless-stopped
     environment:
       POSTGRES_DB: ${POSTGRES_DB}
       POSTGRES_USER: ${POSTGRES_USER}
       POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
     ports:
       - "${POSTGRES_PORT}:5432"
     volumes:
       - db_data:/var/lib/postgresql/data
     healthcheck:
       test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB}"]
       interval: 5s
       timeout: 5s
       retries: 10

   app:
     # If you already have a Dockerfile in this repo, keep the 'build:' section.
     build:
       context: .
       dockerfile: Dockerfile
     # If you prefer to run a prebuilt JAR, replace 'build:' with:
     # image: your-dockerhub-username/securedocai-backend:latest
     container_name: securedocai-backend
     depends_on:
       db:
         condition: service_healthy
     environment:
       # Spring Boot datasource
       SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/${POSTGRES_DB}
       SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
       SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
       SPRING_JPA_HIBERNATE_DDL_AUTO: update
       # App
       SERVER_PORT: ${APP_PORT}
       JWT_SECRET: ${JWT_SECRET}
       SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE}
       # (Optional) Increase request size for uploads
       SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE: 250MB
       SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE: 250MB
     ports:
       - "${APP_PORT}:${APP_PORT}"
     volumes:
       # Persist uploaded files if you store on local disk
       - uploads_data:/app/uploads
     restart: unless-stopped

   pgadmin:
     image: dpage/pgadmin4:8
     container_name: securedocai-pgadmin
     depends_on:
       - db
     environment:
       PGADMIN_DEFAULT_EMAIL: ${PGADMIN_DEFAULT_EMAIL}
       PGADMIN_DEFAULT_PASSWORD: ${PGADMIN_DEFAULT_PASSWORD}
     ports:
       - "${PGADMIN_PORT}:80"
     restart: unless-stopped
     # Optional: persist pgAdmin config
     volumes:
       - pgadmin_data:/var/lib/pgadmin

 volumes:
   db_data:
   uploads_data:
   pgadmin_data:


 Run it:

 docker compose up -d --build


 Backend: http://localhost:${APP_PORT}/

 Swagger UI: http://localhost:${APP_PORT}/swagger-ui/index.html

 OpenAPI JSON: http://localhost:${APP_PORT}/v3/api-docs

 pgAdmin: http://localhost:${PGADMIN_PORT}/
  (connect to host db, port 5432)

 (Optional) Dockerfile for the Backend

 If you don’t already have one, drop this Dockerfile at the repo root (multi-stage build, small runtime):

 # ---- Build Stage ----
 FROM eclipse-temurin:21-jdk AS build
 WORKDIR /app
 COPY . .
 # If using Maven:
 RUN ./mvnw -ntp -DskipTests clean package
 # If using Gradle, replace the above line with:
 # RUN ./gradlew clean bootJar

 # ---- Runtime Stage ----
 FROM eclipse-temurin:21-jre
 WORKDIR /app
 # Copy fat jar (adjust path if needed)
 COPY --from=build /app/target/*-SNAPSHOT.jar app.jar
 # File upload directory (mapped by docker volume)
 RUN mkdir -p /app/uploads
 EXPOSE 8085
 ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]


 If your JAR name isn’t *-SNAPSHOT.jar, update the COPY line accordingly.
 If you’re on Gradle, switch to bootJar and copy from build/libs/*.jar.

 Spring Profile (Docker)

 If you want a separate profile, add src/main/resources/application-docker.yml:

 server:
   port: ${SERVER_PORT:8085}

 spring:
   datasource:
     url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/securedocai}
     username: ${SPRING_DATASOURCE_USERNAME:admin}
     password: ${SPRING_DATASOURCE_PASSWORD:admin}
   jpa:
     hibernate:
       ddl-auto: ${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
   servlet:
     multipart:
       max-file-size: ${SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE:250MB}
       max-request-size: ${SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE:250MB}

 securedocai:
   jwt:
     secret: ${JWT_SECRET:change-me-in-dev}


 Then run with SPRING_PROFILES_ACTIVE=docker (already set in the compose file).

 Quick Verify Checklist

 ✅ docker compose up -d --build starts db, app, and pgadmin

 ✅ Swagger UI loads at /swagger-ui/index.html

 ✅ /v3/api-docs returns your OpenAPI (shows endpoints like /documents/**, /api/v1/ai/**, /api/chatrooms/**, /api/questionnaires/**, /user/**)

 ✅ You can log in/register via /user/register & /user/login (JWT)

 ✅ Document upload works; files persist in the uploads_data

License

MIT

⚡️ This backend is designed to be the secure, AI-powered brain for your documents — whether for individuals or teams, healthcare or finance, onboarding or compliance.