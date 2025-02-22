# SecureDocAIBackend

## Overview
SecureDocAIBackend is a comprehensive Spring Boot backend designed to securely store, manage, and edit documents. It integrates various AI capabilities to scan documents either locally or via API keys. Users can interact with the AI to chat, test knowledge, summarize information, and add new content, making it ideal for onboarding or teaching purposes. The system places a strong emphasis on security to protect sensitive information.

## Features
- **User Authentication and Authorization**: Secure user management with JWT-based security and multi-factor authentication (MFA).
- **Document Management**: Secure storage, retrieval, and editing of documents.
- **AI Integration**: Use multiple AI keys to scan documents locally or through APIs.
- **AI Interactions**: Chat with AI, test knowledge, summarize information, and add new content.
- **Email Notifications**: Account verification and password reset emails.
- **Security Focus**: Advanced security measures to protect user data and documents.

## Getting Started

### Prerequisites
- Java 17
- Maven
- Docker

### Setup
1. **Clone the repository**
   ```bash
   git clone https://github.com/Lee-Scott/SecureDocAIBackend.git
   cd SecureDocAIBackend
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the Docker containers**
   ```bash
   docker-compose up -d
   ```

4. **Start the Spring Boot application**
   ```bash
   mvn spring-boot:run
   ```

## Usage
- Access the application at `http://localhost:8080`
- Use the provided API endpoints to manage users, authenticate, and interact with documents.
- Configure AI keys in the application settings to enable AI functionalities.

## API Endpoints
### User Management
- **Register a new user**: `POST /api/users/register`
- **Verify account**: `GET /api/users/verify`
- **Setup MFA**: `POST /api/users/mfa`

### Document Management
- **Upload a document**: `POST /api/documents/upload`
- **Edit a document**: `PUT /api/documents/edit/{id}`
- **Get a document**: `GET /api/documents/{id}`

### AI Interactions
- **Chat with AI**: `POST /api/ai/chat`
- **Test knowledge**: `POST /api/ai/test`
- **Summarize document**: `POST /api/ai/summarize`
- **Add content**: `POST /api/ai/add`

## Contributing
Contributions are welcome! Please create a pull request with your changes or open an issue to discuss your ideas.

## License
This project is licensed under the FamilyFirstSoftware, LLC License. See the LICENSE file for details.

## Contact
For any inquiries or support, please contact the project maintainer at FamilyFirstSoftware@gmail.com.