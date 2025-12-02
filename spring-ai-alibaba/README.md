# Spring AI Alibaba Chat Application

This is a chat application that demonstrates how to integrate Vue 3 with a Spring Boot backend using Alibaba AI services.

## Features

- Vue 3 front-end with a chat interface
- Distinct user and AI roles with avatars
- Multi-turn conversation support
- Responsive design
- Loading states and error handling

## Front-end Implementation

The front-end is implemented with Vue 3 and consists of:

1. `chat.html` - A standalone HTML file that demonstrates the Vue 3 chat interface
2. `js/app.js` - The Vue 3 application logic

### Running the Front-end

To run the front-end:

1. Open `src/main/resources/static/chat.html` directly in a browser
2. The chat interface will load with a sample welcome message
3. Type your question in the input field and press Enter or click "提问"

### Integration with Back-end

To connect the front-end to your Spring Boot back-end:

1. Make sure your Spring Boot application is running
2. Update the API endpoint in `js/app.js` to point to your back-end
3. Replace the `simulateApiCall` function with the actual API call:

```javascript
const response = await fetch('/api/question/ask', {
    method: 'POST',
    headers: {
        'Content-Type': 'text/plain'
    },
    body: userQuestion
});
```

## Back-end Implementation

The back-end is a Spring Boot application with:

1. `QuestionController` - Handles the `/api/question/ask` endpoint
2. Alibaba DashScope integration for AI responses

### Running the Back-end

To run the back-end:

```bash
./mvnw spring-boot:run
```

Then access the application at `http://localhost:8080`

## Architecture

This application follows a modern separation of concerns:

- **Front-end**: Vue 3 for reactive UI components
- **Back-end**: Spring Boot for REST API and AI integration
- **Communication**: RESTful API calls between front-end and back-end

This separation makes it easy to:
- Develop and test the front-end independently
- Scale the application by deploying front-end and back-end separately
- Optimize each tier independently