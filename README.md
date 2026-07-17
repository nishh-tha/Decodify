# AI Code Explainer

A simple full-stack app for beginners: paste code, pick a language, and get an
AI-generated explanation (summary, time/space complexity, issues, and
suggestions) powered by the Google Gemini API.

# demo


https://github.com/user-attachments/assets/99180bb0-2214-491b-ae71-35dfd2dc9d59



```
ai-code-explainer/
├── backend/     Spring Boot REST API (Java)
└── frontend/    React app (Vite)
```

## How it works

1. You open the app and paste code into the editor.
2. You pick a language (Java, Python, C++, JavaScript) and click **Explain Code**.
3. React sends `{ language, code }` to `POST /api/explain` on the backend.
4. The backend builds a prompt and calls the Gemini API.
5. The backend parses Gemini's JSON reply into a clean response object.
6. React displays the result as cards: Summary, Time Complexity, Space
   Complexity, Issues, and Suggestions.

## 1. Get a Gemini API key

Create a free key at [Google AI Studio](https://aistudio.google.com/app/apikey).

## 2. Run the backend

```bash
cd backend

# Set your API key as an environment variable
export GEMINI_API_KEY=your_real_api_key_here     # macOS/Linux
# set GEMINI_API_KEY=your_real_api_key_here       # Windows (cmd)

mvn spring-boot:run
```

The backend starts on **http://localhost:8080**. (Requires Java 17+ and Maven
installed locally. If you'd rather not install Maven, generate a wrapper once
with `mvn -N wrapper:wrapper` and use `./mvnw spring-boot:run` afterwards.)

Alternatively, instead of an environment variable, you can paste the key
directly into `backend/src/main/resources/application.properties`:

```properties
gemini.api.key=your_real_api_key_here
```
(Only do this for local testing — don't commit a real key to version control.)

## 3. Run the frontend

In a separate terminal:

```bash
cd frontend
npm install
npm run dev
```

The frontend starts on **http://localhost:5173** and calls the backend at
`http://localhost:8080/api/explain`.

## 4. Use the app

Open http://localhost:5173, paste some code, choose its language, and click
**Explain Code**.

## Project structure

```
backend/
  src/main/java/com/aiexplainer/backend/
    controller/   ExplainController.java      -> POST /api/explain
    service/      GeminiService.java          -> builds prompt, calls Gemini, parses response
    dto/          ExplainRequest.java, ExplainResponse.java, ErrorResponse.java
    config/       WebClientConfig.java, CorsConfig.java
    exception/    GeminiApiException.java, GlobalExceptionHandler.java
  src/main/resources/application.properties

frontend/
  src/
    components/   LanguageSelector, CodeInput, ExplainButton, ResultCards, Spinner
    pages/        Home.jsx
    services/     api.js (Axios call to the backend)
```

## Notes

- This project intentionally has **no authentication, database, history, or
  file uploads** — it's a single-request tool by design, meant to stay simple
  enough to build and understand in a few days.
- If the Gemini API key is missing or invalid, the backend returns a clear
  JSON error (`502 Bad Gateway`) instead of crashing.
- CORS is configured to only allow requests from `http://localhost:5173`
  (the Vite dev server). Update `cors.allowed-origin` in
  `application.properties` if you deploy the frontend elsewhere.
