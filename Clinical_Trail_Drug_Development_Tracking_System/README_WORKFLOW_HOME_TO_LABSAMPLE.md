# Internal Workflow: Home -> Login -> JWT/Security -> Lab Sample Module

This document explains the **actual internal flow in your current codebase** from the home page to the Lab Sample dashboard, including how Spring Security, JWT, API Gateway routing, and role checks work.

---

## 1) End-to-End Flow (High Level)

1. User opens `api-gateway` static home page: `\`home`/`index.html`.
2. User clicks **Sign In** and goes to `\`home`/`login.html`.
3. Frontend `\`home`/`home.js` sends `POST /auth/login` with username/password.
4. API Gateway routes `/auth/**` to `auth-service`.
5. `auth-service` validates credentials and returns JWT + role + username.
6. Frontend stores token/role in `localStorage` and redirects by role using `\`home`/`app.js` `DASHBOARDS` map.
7. User lands on module dashboard (for coordinator: `/LabSample/dashboard.html`).
8. Module JS calls `/api/samples`, `/api/inventory`, `/api/subjects` with Bearer token.
9. API Gateway JWT filter validates token and injects `X-User-Role` / `X-Username` headers.
10. Gateway routes request to target microservice (`labsampleandiptracking` for sample/inventory, `SubjectEnrollment` for subjects).
11. Backend controllers/services process request and return response.

---

## 2) Home and Login UI Flow

### Home page
- File: `api-gateway/src/main/resources/static/home/index.html`
- Purpose:
  - Landing page for project overview.
  - Entry point to login (`login.html`).

### Login page + client login logic
- UI file: `api-gateway/src/main/resources/static/home/login.html`
- Logic file: `api-gateway/src/main/resources/static/home/home.js`

`doLogin()` in `home.js`:
- Reads `username` and `password`.
- Calls:
  - `POST /auth/login`
  - Body: `{ "username": "...", "password": "..." }`
- On success:
  - Calls `saveAuth(data.token, data.role, data.username)` from `app.js`.
  - Redirects using role map `DASHBOARDS[data.role]`.
- On failure:
  - `401`: invalid credentials message.
  - Other status: server error message.

---

## 3) Role Mapping and Dashboard Redirection

File: `api-gateway/src/main/resources/static/home/app.js`

`DASHBOARDS` mapping currently:
- `ADMIN` -> `/TrialProtocol/admin.html`
- `INVESTIGATOR` -> `/subjectenrollment/dashboard.html`
- `DATA_MANAGER` -> `/VisitSchedule/dashboard.html`
- `PHARMACOVIGILANCE_OFFICER` -> `/AdverseEvent/dashboard.html`
- `COORDINATOR` -> `/LabSample/dashboard.html`

So for the Lab Sample workflow, user role must be:
- `COORDINATOR` (or `ADMIN`, because admin bypass is enabled in `requireRole()`).

---

## 4) Auth-Service Internal Flow (Spring Security + JWT)

### Login endpoint
- Controller: `auth-service/auth-service/src/main/java/com/example/auth_service/controller/AuthController.java`
- Endpoint: `POST /auth/login`

### Authentication logic
- Service: `auth-service/auth-service/src/main/java/com/example/auth_service/service/AuthService.java`
- Steps:
  1. Fetch user by username from DB (`UserRepository`).
  2. Validate raw password against stored BCrypt hash (`PasswordEncoder.matches`).
  3. Generate JWT with:
     - subject = username
     - claim `role` = user role
     - expiration from config
  4. Return `AuthResponse(token, role, username)`.

### JWT utility
- File: `auth-service/auth-service/src/main/java/com/example/auth_service/util/JwtUtil.java`
- Purpose:
  - `generateToken(username, role)` creates signed JWT.
  - `validateToken(token)` parses and verifies signature/expiration.

### Spring Security in auth-service
- File: `auth-service/auth-service/src/main/java/com/example/auth_service/config/SecurityConfig.java`
- Behavior:
  - Stateless session (`SessionCreationPolicy.STATELESS`).
  - `permitAll()` for `/auth/**` and `/users/**`.
  - Other paths require authentication (inside auth-service).

Why this setup:
- Auth-service focuses on credential validation + token issuance.
- Cross-service auth enforcement is centralized at API Gateway.

---

## 5) API Gateway Security and Routing

### JWT filter (global)
- File: `api-gateway/src/main/java/com/genc/api_gateway/filter/JwtAuthenticationFilter.java`

#### Route classes in filter
- Public prefixes (no token required):
  - `/auth/`
- Protected prefixes (token required):
  - `/api/`
  - `/users`

#### What filter does for protected requests
1. Read `Authorization: Bearer <token>`.
2. Validate token signature/claims using same `jwt.secret`.
3. Extract role and username from claims.
4. Add headers to downstream request:
   - `X-User-Role`
   - `X-Username`

Why these headers are used:
- Downstream services can enforce role-based operations without parsing JWT again.

### Gateway route mapping
- File: `api-gateway/src/main/java/com/genc/api_gateway/config/GatewayConfig.java`

Relevant routes:
- `/auth/**`, `/users/**` -> `lb://auth-service`
- `/api/subjects/**` -> `lb://SubjectEnrollment`
- `/api/samples`, `/api/samples/**`, `/api/inventory`, `/api/inventory/**` -> `lb://labsampleandiptracking`

---

## 6) Lab Sample Module Workflow (Coordinator Path)

### Frontend dashboard
- UI: `api-gateway/src/main/resources/static/LabSample/dashboard.html`
- Logic: `api-gateway/src/main/resources/static/LabSample/lab.js`

### Access guard
In `lab.js`:
- `requireRole("COORDINATOR")`
- In `app.js`, `ADMIN` bypass is enabled, so admin also has access.

### Initial data loading in lab.js
On page load:
- `loadSamples()` -> `GET /api/samples`
- `loadInventory()` -> `GET /api/inventory`
- `loadSubjects()` -> `GET /api/subjects` (from Subject Enrollment module)

### Collect Sample flow
1. User picks subject and sample type.
2. Frontend calls `POST /api/samples` with payload:
   - `subjectId`
   - `sampleType`
   - `collectionDate` (optional)
   - `labResult` (optional)
3. Gateway validates JWT and routes to `labsampleandiptracking`.
4. Backend endpoint:
   - `LabSampleApiController.collectSample()`
5. Service validation in `SampleService.collectSample()`:
   - `subjectId > 0`
   - sample type required
   - default date = today if missing
   - status set to `COLLECTED`
6. Sample saved in DB table `sample_log`.

### Lab lifecycle actions
- Mark In Transit:
  - `PUT /api/samples/{id}/transit`
- Record Lab Result:
  - `PUT /api/samples/{id}/result`
  - service enforces current status must be `IN_TRANSIT`
  - then transitions to `ANALYZED`
- Destroy Sample:
  - `PUT /api/samples/{id}/destroy`

Lifecycle enforced in service:
- `COLLECTED -> IN_TRANSIT -> ANALYZED -> DESTROYED`

### Inventory/IP flow
- View inventory:
  - `GET /api/inventory`
- Dispense IP:
  - `POST /api/inventory/{id}/dispense`
- Service enforces:
  - positive IDs/quantity
  - sufficient available stock
  - updates dispensed and available counts
  - returns dispense log object

---

## 7) How Subject IDs Reach Lab Sample

`lab.js` calls `/api/subjects` to populate subject dropdowns.

Source of truth:
- Subject Enrollment service (`SubjectController`) via gateway route `/api/subjects/**`.

So, for subject IDs to appear in Lab Sample:
- Subject Enrollment service must be running and reachable via gateway.
- User must have valid JWT.
- At least one subject should exist (preferably ENROLLED as per current UI filtering behavior).

---

## 8) Role and Authorization Summary

### Frontend layer
- `requireRole(role)` in `app.js`:
  - Redirects to login if token missing.
  - Allows ADMIN to pass any module check.

### Gateway layer
- `JwtAuthenticationFilter`:
  - Blocks protected paths without valid Bearer token.
  - Forwards role/username headers to services.

### Service layer (where implemented)
- Example: auth-service `/users` controller checks `X-User-Role == ADMIN`.
- Other services can also check forwarded role header for operation-level control.

---

## 9) Key Files Involved in This Workflow

### UI and shared JS
- `api-gateway/src/main/resources/static/home/index.html`
- `api-gateway/src/main/resources/static/home/login.html`
- `api-gateway/src/main/resources/static/home/home.js`
- `api-gateway/src/main/resources/static/home/app.js`
- `api-gateway/src/main/resources/static/LabSample/dashboard.html`
- `api-gateway/src/main/resources/static/LabSample/lab.js`

### Gateway backend
- `api-gateway/src/main/java/com/genc/api_gateway/filter/JwtAuthenticationFilter.java`
- `api-gateway/src/main/java/com/genc/api_gateway/config/GatewayConfig.java`

### Auth service backend
- `auth-service/auth-service/src/main/java/com/example/auth_service/controller/AuthController.java`
- `auth-service/auth-service/src/main/java/com/example/auth_service/service/AuthService.java`
- `auth-service/auth-service/src/main/java/com/example/auth_service/util/JwtUtil.java`
- `auth-service/auth-service/src/main/java/com/example/auth_service/config/SecurityConfig.java`

### Lab Sample backend
- `ctds/src/main/java/com/genc/ctds/samplelog/controller/LabSampleApiController.java`
- `ctds/src/main/java/com/genc/ctds/samplelog/service/SampleService.java`

---

## 10) Practical Request/Response Path Example

### Example: Collect Sample
1. Browser (`LabSample/lab.js`): `POST /api/samples` + `Authorization: Bearer ...`
2. Gateway filter:
   - Validates token
   - Adds `X-User-Role` and `X-Username`
3. Gateway router:
   - Matches `/api/samples`
   - Forwards to `lb://labsampleandiptracking`
4. `LabSampleApiController.collectSample()` receives request
5. `SampleService.collectSample()` validates + persists
6. JSON response returns to UI
7. UI refreshes table/stats

---

## 11) Notes for Troubleshooting This Flow

- `401` on `/api/**` usually means:
  - Missing/invalid JWT
  - Expired token
- `404` on `/api/samples` usually means:
  - Gateway route mismatch or service not registered
- Subject dropdown empty in Lab Sample usually means:
  - No subjects in SubjectEnrollment service
  - Subject service not reachable through gateway

---

## 12) Why This Architecture Is Used

- **API Gateway pattern**: one entry point for UI + APIs.
- **JWT stateless auth**: no server session storage; scalable.
- **Service discovery (Eureka)**: decouples route from fixed host/port.
- **Role-based workflows**: maps pharmaceutical responsibilities cleanly to module dashboards.
- **Modular microservices**: each domain (protocols, enrollment, visits, safety, labs) evolves independently while staying connected through common identity and routing.

