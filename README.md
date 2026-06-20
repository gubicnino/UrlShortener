# URL Shortener

REST service that shortens URLs, redirects, and tracks visit counts.

## Run

```bash
docker compose up
```

Service starts on `http://localhost:8080`.

## Endpoints

### `POST /api/shorten`
```json
{ "url": "https://example.com/some/long/path" }
```
```json
{ "code": "aZ3x9", "shortUrl": "http://localhost:8080/aZ3x9" }
```

### `GET /{code}`
Redirects (302) to the original URL. Returns 404 if code unknown.

### `GET /api/stats/{code}`
```json
{ "clickCount": 5, "createdAt": "2026-06-20T10:00:00Z" }
```
Returns 404 if code unknown.

## Validation

- Only `http://` and `https://` URLs accepted — anything else returns 400.
- Blank or missing URL returns 400.

## Approach

Short codes are derived from the auto-incremented database ID encoded in base62. This guarantees uniqueness without collision handling. Visit count is incremented on every successful redirect and persisted in PostgreSQL.
