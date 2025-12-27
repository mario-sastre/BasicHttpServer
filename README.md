# Simple Java HTTP Server (from Scratch)

A minimal HTTP/1.1 server implemented **from scratch in Java**, using only **raw sockets**
(`ServerSocket`, `Socket`).  
No frameworks, no servlet containers, no Spring.

This project is intentionally low-level and educational. Its goal is to help understand
how HTTP servers actually work under the hood: TCP connections, request parsing,
routing, threading, and response generation.

---

## Features

- Pure Java (no external runtime dependencies)
- Raw TCP socket communication
- Manual HTTP/1.1 request parsing
- GET and POST request support
- Query parameter parsing
- Case-insensitive header parsing
- Request body handling via `Content-Length`
- Simple routing system
- Lambda-based route handlers
- Thread-per-connection execution model
- Runnable JAR generated with Maven

---

## Project Structure
```
src/main/java/com/sastremario/practices/basic/
├── HttpServer.java        # Main entry point
├── HttpParser.java        # HTTP request parsing
├── HttpRequest.java      # Request model
├── HttpResponse.java     # Response model
├── Router.java           # Route registry
└── RouteHandler.java     # Functional handler interface

```

---

## Getting Started

### Prerequisites

- Java **17+** (tested with Java 23)
- Maven **3.8+**

Verify installation:

```bash
java -version
mvn -version
```

## Build the project

From the project root directory
```
mvn clean package
```

This will generate a runnable JAR file at:

```
target/HttpServer-1.0-SNAPSHOT.jar
```

## Run the server
```
java -jar target/HttpSever-1.0-SNAPSHOT.jar
```
Expected output: 

```
Server listening on port 8082
```

## Testing the server

### GET Requests

```bash
curl http://localhost:8082/
curl "http://localhost:8082/hello?name=Mario"

```

### POST Requests

```bash
curl -X POST http://localhost:8080/echo -d "Hello server"
```


### JSON Response

```bash
curl http://localhost:8080/json
```


## Design Overview
- HTTP parsing is implemented manually to expose protocol details
- Routing is handled via a simple Router abstraction
- Handlers are implemented using Java lambdas via a functional interface
- Responses are constructed and written directly to the socket output stream
- Each connection is handled in its own thread for simplicity

This architecture is inspired by early servlet containers and helps demystify how
frameworks like Spring Boot operate internally.


## Limitations

This server is not production-ready.

Current limitations include:
- No keep-alive / persistent connections
- No HTTPS / TLS
- No chunked transfer encoding
- No request validation or security
- No graceful shutdown handling

This project is intended for learning purposes only.

## Possible Improvements
- Thread pool (ExecutorService)
- Middleware / filters
- Static file serving
- JSON body parsing
- Configurable port and thread count
- HTTP keep-alive support
- Logging
- Fat JAR (shade plugin)

## Why This Project?

Modern frameworks abstract away too much too early.

This project focuses on understanding:
- TCP and socket programming
- HTTP protocol basics
- Classpath and JAR execution
- Build tooling with Maven
- Server architecture fundamentals

Understanding these concepts makes you a stronger backend engineer and helps you use
frameworks with confidence rather than treating them as magic.