# Visit Scheduling & Case Report Form Capture — Microservice

Module 3 of the **Clinical Trial & Drug Development Tracking System**, built as an
independent Spring Boot microservice. It owns **only** visit data — a subject is
referenced by `subjectId` only (no cross-module/database coupling).

## Tech stack
- Java 21, Spring Boot 3.3.4 (Spring MVC)
- Spring Data JPA + Hibernate
- MySQL
- Static HTML/CSS/JS frontend (Bootstrap 5)

## Project layout
```
visit-scheduling/
├─ src/main/java/com/genc/visit_scheduling/
│  ├─ VisitSchedulingApplication.java
│  ├─ config/CorsConfig.java
│  ├─ controller/VisitController.java
│  ├─ service/VisitService.java
│  ├─ repository/VisitRecordRepository.java
│  ├─ model/VisitRecord.java
│  └─ exception/{BusinessRuleException, ResourceNotFoundException, GlobalExceptionHandler}.java
├─ src/main/resources/application.properties
└─ frontend/
   ├─ VisitSchedule/{dashboard.html, visit.js, visit.css}
   ├─ home/{app.js, dashboard.js, theme.css, premium.css}
   └─ TrialProtocol/admin.css
```

## REST API (base: `http://localhost:8089/api`)
| Method | Path                         | Description                          |
|--------|------------------------------|--------------------------------------|
| GET    | `/visits`                    | List all visits                      |
| POST   | `/visits`                    | Schedule a visit                     |
| PUT    | `/visits/{id}/crf?queryCount=`| Record CRF data (→ COMPLETED)        |
| PUT    | `/visits/{id}/lock`          | Lock CRF (→ LOCKED)                  |
| GET    | `/visits/subject/{subjectId}`| Visit history for a subject          |

### Business rules
- New visits start as `crfStatus = PENDING`.
- A `LOCKED` CRF cannot be edited.
- Only a `COMPLETED` CRF can be locked.

## Database schema
```sql
CREATE TABLE visit_record (
  visit_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
  subject_id   BIGINT,
  visit_name   VARCHAR(100),
  visit_date   DATE,
  crf_status   VARCHAR(20),   -- PENDING, COMPLETED, LOCKED
  query_count  INT,
  visit_window VARCHAR(100)
);
```
> The table is auto-created by Hibernate (`ddl-auto=update`).

## Run
1. Start MySQL (the DB `visit_scheduling` is auto-created).
2. From the `visit-scheduling` folder:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
3. Open `frontend/VisitSchedule/dashboard.html` in a browser.

