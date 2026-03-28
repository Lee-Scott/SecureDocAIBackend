# SecureDocAI Platform (Backend)

SecureDocAI is a robust, general-purpose AI Tooling Platform (White-label SaaS) designed to empower various businesses—including telehealth providers, law firms, and tax professionals—to build secure, AI-enhanced portals.

This repository contains the backend application that powers the white-label B2B2C infrastructure. It provides the essential services and APIs for professionals to manage their practice, and for their clients to securely interact with AI-driven workflows and document management.

## 🏗️ Core Architecture Principles

The backend is designed around three core principles to ensure it is secure, scalable, and adaptable for any professional industry.

### 1. Guaranteed Data Isolation with Hibernate Filters
For a B2B2C platform hosting sensitive data from different industries (e.g., a Law Firm next to a Medical Practice), logical data isolation must be architecturally guaranteed, not just a procedural best-practice.

**The Strategy:** The platform is moving to **Hibernate Filters**. A global filter will be defined and applied to all tenant-specific entities, automatically appending `AND tenant_id = :currentTenant` to every SQL statement at the persistence layer.

**The Outcome:** This makes cross-tenant data leakage structurally impossible, as the security is handled by the data access layer itself, not by individual developer queries. This is the cornerstone of our "enterprise-grade" promise.

### 2. The "Profile" Entity: Decoupling Identity from Industry
To be truly industry-agnostic, the core data model must avoid "leaky abstractions" like a `PatientEntity` in a law firm's portal.

**The Strategy:** We are refactoring to a **Core + Extension** model. A universal `ProfileEntity` will store common PII (Name, Email, DOB, Phone, TenantID). Industry-specific metadata (e.g., `blood_type` for medicine, `case_number` for law) will be stored in a flexible `ProfessionalContext` (either a JSONB column or a linked table).

**The Outcome:** This allows the platform to support any professional service without code changes. It also provides a clean, reusable data structure for the AI RAG pipeline to query against, simplifying development and enhancing AI capabilities.

### 3. Asynchronous AI Orchestration
AI tasks, like parsing a 50-page medical history, can be long-running and will time out a standard synchronous REST request.

**The Strategy:** The AI pipeline is built on an **Asynchronous Job Pattern**. When a document is submitted for analysis, the API will immediately return a `202 Accepted` status with a `job_id`. The actual Gemini Vision call is handled in the background by a Spring Boot `@Async` worker.

**The Outcome:** This ensures a resilient and responsive user experience. The frontend can poll a status endpoint or receive a WebSocket notification when the AI-generated report is ready, creating a scalable and robust system for handling complex AI workflows.

## 🌟 Key Capabilities

- **White-Label B2B2C Infrastructure**: Flexible multi-tenant backend architecture allowing businesses to easily adapt the platform to their own operational models.
- **Asynchronous AI Workflows**: A robust, scalable, non-blocking pipeline for AI-powered document analysis, conversational interfaces, and dynamic questionnaires.
- **Secure, Context-Aware Document Analysis**: A sophisticated pipeline leveraging **Gemini 1.5 Pro's native Vision capability** to understand the layout and context of complex documents.
- **Practice & Client Management**: Secure services featuring robust JWT-based authentication and **mandatory Multi-Factor Authentication (MFA/TOTP)**.
- **Extensible Architecture**: Designed around the "Profile" entity to be instantly adaptable to any professional service vertical.

## 🔌 Sample API Workflow (Async AI)

1. **Authenticate User & Verify MFA**
   - `POST /user/login`
   - `POST /user/verify/code` (Returns the final JWT)

2. **Upload Secure Document** (Requires JWT)
   ```bash
   curl -X POST http://localhost:8085/document/upload \
   -H "Authorization: Bearer <YOUR_JWT>" \
   -F "file=@/path/to/lab_results.pdf"
   ```

3. **Trigger Asynchronous AI Extraction**
   ```bash
   curl -X POST http://localhost:8085/ai/report/generate/document/123 \
   -H "Authorization: Bearer <YOUR_JWT>"
   ```
   - **Response:**
     ```json
     {
       "job_id": "bf8b58a-a4e3-4d76-9f43-0a7e8e29a9e3",
       "status": "QUEUED",
       "message": "AI analysis has been queued. Check the job status endpoint for updates."
     }
     ```

4. **Poll for Job Status**
   ```bash
   curl -X GET http://localhost:8085/ai/report/job/bf8b58a-a4e3-4d76-9f43-0a7e8e29a9e3 \
   -H "Authorization: Bearer <YOUR_JWT>"
   ```
   - **Response (when complete):**
     ```json
     {
       "job_id": "bf8b58a-a4e3-4d76-9f43-0a7e8e29a9e3",
       "status": "COMPLETED",
       "report_url": "/document/123/report/latest"
     }
     ```

## 🛠️ Technology Stack

* **Language**: Java 21
* **Framework**: Spring Boot 3.3
* **Database**: PostgreSQL (Spring Data JPA / Hibernate)
* **AI/ML**: Google Cloud Vertex AI (Gemini 1.5 Pro)
* **Security**: Spring Security, JWT (`jjwt`), MFA/TOTP (`dev.samstevens.totp`), **Hibernate Filters** (planned)
* **Async Processing**: Spring Boot `@Async` (initial), potentially moving to Redis Queue or RabbitMQ.
* **Document Processing**: Gemini Vision API
* **API Documentation**: SpringDoc OpenAPI (Swagger 3)
* **Build Tool**: Maven

## 📝 Roadmap

For a detailed breakdown of the strategic technical initiatives that will evolve this platform into a true SaaS Infrastructure, please see the official [Project Roadmap](ROADMAP.md).

---
*The remaining sections (Prerequisites, Configuration, Getting Started, etc.) remain the same.*
