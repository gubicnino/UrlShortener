# Student Task: URL Shortener REST Service

## Objective

Build a small backend service in Java. The task is intentionally compact but contains enough substance to demonstrate how you structure code, handle edge cases, and write tests.

## Tech Stack

- **Language:** Java 17+
- **Framework:** Spring Boot
- **Build:** Maven or Gradle
- **Persistence:** H2 (in-memory) is acceptable; PostgreSQL is a plus
- **Containerization:** Docker
- **Version control:** Git

## Functional Requirements

| Endpoint | Behavior |
|----------|----------|
| `POST /api/shorten` | Accepts a long URL, returns a short code. Invalid or missing URL → **400**. |
| `GET /{code}` | Redirects with **HTTP 302** to the original URL. Unknown code → **404**. |
| `GET /api/stats/{code}` | Returns visit count and creation time. Unknown code → **404**. |

### Request / Response Format

`POST /api/shorten` request body:

```json
{
  "url": "https://example.com/some/long/path"
}
```

Response:

```json
{
  "code": "aZ3x9",
  "shortUrl": "http://localhost:8080/aZ3x9"
}
```

## Non-Functional Requirements

- **URL validation:** only `http` and `https` URLs are accepted; anything else returns 400.
- **Unique codes:** generate unique short codes and handle potential collisions.
- **Visit count:** incremented on every successful redirect (i.e. only for an existing code, not on a 404).
- **Error handling:** return meaningful HTTP status codes and clear error messages.
- **Testing:** include unit tests and at least one integration test, covering the happy path and at least two edge cases.
- **Structure:** separate concerns into clear layers (controller / service / repository) where it makes sense.

## Run Requirement

The service must be runnable with a single command via Docker, for example:

```
docker compose up
```

This command should build the application and start all required services (including the database, if used) without any additional manual setup.

## Acceptance Criteria

- [ ] The service starts with a single command and includes a `README` with run instructions.
- [ ] All three endpoints behave as specified.
- [ ] Invalid URL returns 400; unknown code returns 404.
- [ ] Tests cover the happy path and at least two edge cases.
- [ ] Code is maintained in a Git repository; the commit history should reflect incremental work rather than a single bulk commit.

## Submission

Publish the solution to a public GitHub repository and send the repository link upon completion. The repository must contain:

- the source code,
- a `README.md` with run instructions and a brief description of the approach,
- the tests.

## Notes

A clean, working implementation with clear structure, correct HTTP behavior, basic tests, and simple Docker-based startup is more important than adding extra features.

Consider in advance how short codes are generated and what happens on a collision, and which edge cases warrant a test.