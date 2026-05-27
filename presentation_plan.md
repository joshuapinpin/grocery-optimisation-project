# Presentation Plan & Demo Script

This file collects the presentation structure, demo steps, commands, evidence checklist, and Q&A prep for the Team Presentation (Week 12). Use it as your single-run script during the demo and to prepare slides.

---

## Quick checklist (what the rubric expects)

- Individual Contribution & Requirement (25%)
  - Explain requirement(s) implemented (FR-004 shopping list)
  - Show files changed: `ShoppingListController`, `ShoppingListServiceImpl`, `ShoppingListRepository`, DTOs
  - Explain how the work fits into the system (auth -> session -> lists)

- Design & Engineering Decisions (25%)
  - Explain session-based Spring Security and SecurityContext persistence
  - Justify mocked items vs persisted items; ownership checks; trade-offs (session vs JWT)

- Implementation & Demo (30%)
  - Live demo: register, login, show JSESSIONID, protected GET, create list, show DB row, delete list, logout -> 401

- Communication & Presentation Quality (20%)
  - Concise slides, demo recording backup, practiced timing

---

## Slide / Talk Outline (20–22 minutes target)

1. (0:00–1:30) Brief context: project objective, scope (Part 1 - context)
2. (1:30–3:30) Your contribution: FR-004 shopping list — what you implemented (controller/service/repo) and what was mocked (items)
3. (3:30–7:30) Design & architecture (session flow diagram)
   - Auth controller, SecurityConfig, session persistence
   - Why session-based auth (simplicity, single backend)
4. (7:30–14:30) Live demo (7 minutes)
   - Follow the demo script below
   - If live demo fails, play 2–3 minute recorded video
5. (14:30–18:00) Implementation excerpts & key code (show snippets)
   - `AuthController.login` — saving SecurityContext into session
   - `SecurityConfig` — session-backed SecurityContextRepository
   - `ShoppingListServiceImpl.createShoppingList` and `deleteShoppingList` (ownership check)
6. (18:00–20:00) Evidence & tests (H2 console screenshots, SQL queries)
7. (20:00–22:00) Lessons, trade-offs, next steps, Q&A

---

## Demo script (definitive steps)

Preconditions:
- Backend running at `http://localhost:8080`
- Use Postman (cookie jar) or curl with cookies

### 1) Start backend (if not already)

```powershell
cd backend
.\mvnw spring-boot:run
```

### 2) Register (Postman)

- POST `http://localhost:8080/api/auth/register`
- Body JSON:

```json
{"username":"demo","password":"Passw0rd!"}
```

- Expect: `201 Created`

### 3) Login and capture session cookie

- POST `http://localhost:8080/api/auth/login`
- Body JSON:

```json
{"username":"demo","password":"Passw0rd!"}
```

- Expect: `200 OK` and header `Set-Cookie: JSESSIONID=...`
- In Postman: verify Cookie Jar contains `JSESSIONID`

### 4) GET protected endpoint without cookie (optional quick check)

- GET `http://localhost:8080/api/shopping-lists/me` (Do not send cookie)
- Expect: `401 Unauthorized`

### 5) GET protected endpoint WITH cookie

- GET `http://localhost:8080/api/shopping-lists/me` (cookie present)
- Expect: `200 OK` + JSON: `{ "username": "demo", "lists": [...] }`

### 6) Create a new list

- POST `http://localhost:8080/api/shopping-lists`
- Body JSON:

```json
{"name": "Demo List"}
```

- Expect: `201 Created` + response DTO with mocked items

### 7) Verify persisted list references owner (H2 console or SQL)

- Open H2 console: `http://localhost:8080/h2-console` (if enabled)
- Run SQL:

```sql
SELECT id, name, owner_id FROM shopping_list WHERE owner_id = 1;
SELECT id, username, hashed_password FROM accounts WHERE username = 'demo';
```

- Evidence: `shopping_list.owner_id` matches account `id` and `accounts.hashed_password` is BCrypt hash

### 8) Delete the list (as owner)

- DELETE `http://localhost:8080/api/shopping-lists/{id}` (replace `{id}` with the list id from create or SELECT)
- Expect: `204 No Content`
- Confirm the list is removed from DB with another SELECT

### 9) Logout

- POST `http://localhost:8080/api/auth/logout` (with cookie)
- Expect: `200 OK`
- After logout, GET `/api/shopping-lists/me` with the old cookie → `401 Unauthorized`

---

## Example curl commands (for backup)

Login & save cookie jar:

```bash
curl -c cookies.txt -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"Passw0rd!"}'
```

Get lists using saved cookie:

```bash
curl -b cookies.txt http://localhost:8080/api/shopping-lists/me
```

Delete list id 3 using saved cookie:

```bash
curl -b cookies.txt -X DELETE http://localhost:8080/api/shopping-lists/3 -i
```

---

## SQL snippets to show during demo

- Show BCrypted password:

```sql
SELECT id, username, hashed_password FROM accounts WHERE username = 'demo';
```

- Show shopping lists owned by the account:

```sql
SELECT id, name, owner_id FROM shopping_list WHERE owner_id = <account_id>;
```

Replace `<account_id>` with the numeric id from the accounts query.

---

## Code snippets to highlight (copy-paste into slides)

- Session persistence in `AuthController.login`:

```java
SecurityContext context = SecurityContextHolder.createEmptyContext();
context.setAuthentication(authentication);
SecurityContextHolder.setContext(context);
securityContextRepository.saveContext(context, httpRequest, httpResponse);
session.setAttribute("userId", response.id());
session.setAttribute("username", response.username());
```

- `SecurityConfig` wiring (session-backed):

```java
HttpSessionSecurityContextRepository repository = new HttpSessionSecurityContextRepository();
repository.setAllowSessionCreation(true);
// wired into http.securityContext(...)
```

- Ownership check in `deleteShoppingList`:

```java
if (!list.owner().id().equals(userId)) {
    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this shopping list");
}
shoppingListRepository.delete(list);
```

---

## Questions you should prepare for (Q&A)

- Why sessions vs JWT? (simplicity, server control, demo focus)
- Are passwords stored securely? (BCrypt)
- How do you prevent cross-user deletes? (ownership check, 403)
- How would you scale sessions in production? (Redis/shared session store or move to stateless JWT)
- Why are items mocked? (separation of concerns and demo completeness; plan to implement items later)

---

## Evidence & backup materials to bring

- H2 console screenshots showing `accounts` and `shopping_list` tables
- Small recorded demo video (2–3 minutes) showing the same flows in case live demo fails
- Postman collection or the `test-auth.ps1` PowerShell script to quickly reproduce

---

## Next steps I can do for you (pick any)
- Generate a 2–3 minute recorded demo script or provide a small recorded video file
- Produce the Postman collection JSON and add it to `/postman/`
- Generate slide text and a minimal PowerPoint/Canva outline ready to present



*Prepared on: 2026-05-27*

