# Decodify

**Decodify** is a full-stack AI-powered code explanation tool designed to help beginners understand unfamiliar code.

Paste a code snippet, select its programming language, and Decodify uses the **Google Gemini API** to generate a concise explanation with time complexity, space complexity, possible issues, and improvement suggestions.

## Live Demo

**[Try Decodify →](https://decodify-two.vercel.app/)**

## Demo


https://github.com/user-attachments/assets/ffea1faa-9e2c-47da-98b4-dec7d38b4071


## Features

- 🤖 AI-powered code explanations using Google Gemini
- 💡 Beginner-friendly summaries
- ⏱️ Time complexity analysis
- 💾 Space complexity analysis
- ⚠️ Possible bugs and edge cases
- ✨ Suggestions for improving code
- 🔄 Interactive loading messages while the AI processes the request
- 📋 Copy generated explanations to the clipboard
- 🌐 Deployed frontend and backend

## Tech Stack

**Frontend**
- React.js
- Vite
- Axios
- CSS

**Backend**
- Java
- Spring Boot
- Spring WebFlux / WebClient
- REST API

**AI**
- Google Gemini API
- Structured JSON responses

**Deployment**
- Vercel — Frontend
- Render — Backend

## How It Works

```text
User
  ↓
React + Vite frontend
  ↓
POST /api/explain
  ↓
Spring Boot backend
  ↓
Gemini API
  ↓
Structured JSON response
  ↓
Spring Boot parses response
  ↓
React displays explanation
```

1. The user pastes code and selects its programming language.
2. React sends the language and code to `POST /api/explain`.
3. Spring Boot builds a beginner-friendly analysis prompt.
4. The backend sends the request to the Gemini API.
5. Gemini returns a structured JSON response containing the explanation and analysis.
6. Spring Boot parses the response into an `ExplainResponse` object.
7. React displays the result as separate cards.

## Project Structure

```
Decodify/
├── backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/aiexplainer/backend/
│   │       │   ├── controller/
│   │       │   │   └── ExplainController.java
│   │       │   ├── service/
│   │       │   │   └── GeminiService.java
│   │       │   ├── dto/
│   │       │   │   ├── ExplainRequest.java
│   │       │   │   ├── ExplainResponse.java
│   │       │   │   └── ErrorResponse.java
│   │       │   ├── config/
│   │       │   │   ├── WebClientConfig.java
│   │       │   │   └── CorsConfig.java
│   │       │   └── exception/
│   │       │       ├── GeminiApiException.java
│   │       │       └── GlobalExceptionHandler.java
│   │       └── resources/
│   │           └── application.properties
│   │
│   └── Dockerfile
│
└── frontend/
    ├── src/
    │   ├── components/
    │   │   ├── LanguageSelector.jsx
    │   │   ├── CodeInput.jsx
    │   │   ├── ExplainButton.jsx
    │   │   ├── ResultCards.jsx
    │   │   └── Spinner.jsx
    │   ├── pages/
    │   │   └── Home.jsx
    │   ├── services/
    │   │   └── api.js
    │   ├── App.jsx
    │   └── index.css
    └── package.json
```

## Getting Started Locally

### Prerequisites

- Java 17+
- Maven
- Node.js 18+
- A Google Gemini API key

### 1. Clone the repository

```bash
git clone https://github.com/nishh-tha/Decodify.git
cd Decodify
```

### 2. Configure the backend

Create a Gemini API key using [Google AI Studio](https://aistudio.google.com/).

Set the API key as an environment variable — never store it directly in `application.properties` or commit it to the repo.

**Windows**
```bash
set GEMINI_API_KEY=your_api_key
```

**macOS/Linux**
```bash
export GEMINI_API_KEY=your_api_key
```

Then start the backend:

```bash
cd backend
mvn spring-boot:run
```

The backend runs on:

```
http://localhost:8080
```

### 3. Configure the frontend

Open a second terminal:

```bash
cd frontend
npm install
```

Create a `.env` file inside the frontend folder:

```
VITE_API_URL=http://localhost:8080/api
```

Then start the frontend:

```bash
npm run dev
```

The frontend runs on:

```
http://localhost:5173
```

## Environment Variables

**Backend**
```
GEMINI_API_KEY=your_gemini_api_key
```

**Frontend**
```
VITE_API_URL=http://localhost:8080/api
```

For the deployed frontend, `VITE_API_URL` points to the Render backend:

```
VITE_API_URL=https://decodify-backend.onrender.com/api
```

> ⚠️ Never commit API keys or `.env` files containing secrets to GitHub.

## Deployment

Decodify is deployed using separate frontend and backend services.

**Frontend**

The React/Vite frontend is deployed on Vercel.

```
GitHub → Vercel → React/Vite
```

**Backend**

The Spring Boot backend is containerized using Docker and deployed on Render.

```
GitHub → Render → Docker → Spring Boot
```

The deployed services communicate through the REST API:

```
https://decodify-backend.onrender.com/api
```

**CORS**

The backend's CORS configuration allows requests from both the local dev server and the deployed frontend:

```
http://localhost:5173
https://decodify-two.vercel.app
```

## API

### `POST /api/explain`

**Accepts:**

```json
{
  "language": "Java",
  "code": "public class Main { ... }"
}
```

**Returns:**

```json
{
  "summary": "Explanation of the code",
  "timeComplexity": "O(n) - ...",
  "spaceComplexity": "O(1) - ...",
  "issues": [
    "Possible issue"
  ],
  "suggestions": [
    "Possible improvement"
  ]
}
```

## Design Decisions

**Structured Gemini Responses**

Instead of relying only on free-form AI output, the backend requests a structured JSON response containing the exact fields required by the frontend. This makes the communication between Gemini, Spring Boot, and React more predictable.

**Separate Frontend and Backend**

The frontend and backend are independently deployable:

```
React/Vite → Vercel
Spring Boot → Render
```

This keeps the frontend lightweight while allowing the backend to securely communicate with the Gemini API without exposing the API key to the browser.

## Limitations

Decodify is intentionally a lightweight code explanation tool. Currently it does not include:

- User authentication
- Database storage
- Explanation history
- File uploads
- Code execution
- User-specific profiles

AI-generated explanations may occasionally contain inaccuracies, so the output should be treated as an educational aid rather than a substitute for testing or reviewing code.

## Future Improvements

- Add syntax highlighting
- Support larger code files
- Add explanation history
- Add user accounts
- Add more programming languages
- Add code execution and output comparison
- Improve AI analysis of edge cases
- Add streaming AI responses for faster perceived performance

## License

This project is for educational and portfolio purposes.
