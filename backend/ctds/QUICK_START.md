# Adverse Event Module - Quick Start Guide

## Module Structure

```
adverseevent/
├── model/
│   ├── AdverseEvent.java          (JPA Entity with MedDRA fields)
│   ├── Severity.java              (MILD, MODERATE, SEVERE)
│   ├── EventStatus.java           (REPORTED, UNDER_REVIEW, RESOLVED, FATAL)
│   └── Seriousness.java           (SAE, NON_SAE)
├── controller/
│   └── AdverseEventController.java (HTML views + REST API endpoints)
├── service/
│   └── AdverseEventService.java    (Business logic, MedDRA coding, SAE workflow)
├── repository/
│   └── AdverseEventRepository.java (Custom JPQL queries)
└── dto/
    └── AdverseEventDTO.java        (API Data Transfer Object)
```

## Start Application

```powershell
cd "C:\Practice\Clinical-trial-and-Drug-Development\backend\ctds"
mvn clean spring-boot:run
```

Application starts on: **http://localhost:8082**

## Key Endpoints Summary

### Web Interface
- `GET /` → Redirects to adverse events form
- `GET /events` → Report adverse event form
- `POST /events/save` → Save event via HTML form
- `GET /dashboard` → Dashboard view

### REST API (JSON)
- `POST /api/adverse-events/report` → Report event with auto-classification
- `POST /api/adverse-events/{id}/classify` → Classify seriousness
- `POST /api/adverse-events/{id}/submit-safety-report` → Submit SAE report
- `GET /api/adverse-events/history/{subjectId}` → Get subject event history
- `GET /api/adverse-events/history/{subjectId}/{from}/{to}` → Date range query
- `GET /api/adverse-events/sae-pending-review` → Get pending SAE reviews
- `GET /api/adverse-events/{id}` → Get event details
- `GET /api/adverse-events/by-status/{status}` → Filter by status
- `PATCH /api/adverse-events/{id}/status/{newStatus}` → Update status
- `GET /api/adverse-events/statistics` → Get SAE statistics

## Workflow Example

### 1. Report an Adverse Event
```json
POST /api/adverse-events/report

{
  "subjectId": "SUBJ-001",
  "eventDescription": "Severe allergic reaction with hospitalization",
  "severity": "SEVERE",
  "eventStatus": "REPORTED",
  "eventOnsetDate": "2026-06-11"
}
```

**Auto-classifies as SAE due to:**
- Severity = SEVERE, OR
- Contains keyword "hospitalization"

### 2. Check Classification
```
POST /api/adverse-events/1/classify
```

Returns: `seriousness: SAE`, `requiresSAEReport: true`

### 3. Submit SAE Report
```
POST /api/adverse-events/1/submit-safety-report?reportingPhysician=Dr.Smith&reportingNotes=Patient%20admitted
```

- Sets status → UNDER_REVIEW
- Sets deadline → 24 hours from now
- Triggers safety workflows

### 4. Review Event History
```
GET /api/adverse-events/history/SUBJ-001
```

Returns all events for subject with MedDRA coding

### 5. Get Pending Reviews
```
GET /api/adverse-events/sae-pending-review
```

Returns all SAE events awaiting review

## Database Verification

Check saved data in MySQL:
```sql
-- View all adverse events
SELECT * FROM adverse_event;

-- View SAE events only
SELECT * FROM adverse_event WHERE seriousness = 'SAE';

-- View events for specific subject
SELECT * FROM adverse_event WHERE subject_id = 'SUBJ-001';

-- Count by severity
SELECT severity, COUNT(*) FROM adverse_event GROUP BY severity;

-- View MedDRA coded events
SELECT event_id, subject_id, event_description, meddra_preferred_term 
FROM adverse_event;
```

## MedDRA Coding Examples

Events are auto-coded based on keywords in description:

| Description Contains | MedDRA PT | Code |
|---------------------|-----------|------|
| rash, skin reaction | Rash | 10037844 |
| fever, high temp | Pyrexia | 10016273 |
| headache | Headache | 10019211 |
| nausea, vomiting | Nausea | 10028813 |
| dizziness, vertigo | Dizziness | 10013573 |
| tremor, shaking | Tremor | 10044565 |

## SAE Classification Logic

An event becomes a **Serious Adverse Event (SAE)** if:

1. **Severity = SEVERE**, OR
2. Description contains any of:
   - "hospitali..." (hospitalization)
   - "death"
   - "life-threatening"
   - "disability"  
   - "permanent"
   - "critical"

When SAE is triggered:
- ✅ Seriousness set to SAE
- ✅ 24-hour reporting deadline created
- ✅ Safety workflow initiated
- ✅ Notifications sent (TODO: email integration)
- ✅ Status set to UNDER_REVIEW

## Test with Postman

### Collection Setup

1. **Create new Collection:** "Adverse Events"
2. **Environment Variables:**
   ```
   {{base_url}} = http://localhost:8082
   {{subject_id}} = SUBJ-001
   ```

### Requests

**1. Report Event**
```
POST {{base_url}}/api/adverse-events/report
Content-Type: application/json

{
  "subjectId": "{{subject_id}}",
  "eventDescription": "Severe rash requiring hospitalization",
  "severity": "SEVERE",
  "eventStatus": "REPORTED",
  "eventOnsetDate": "2026-06-11"
}
```

**2. Get Event History**
```
GET {{base_url}}/api/adverse-events/history/{{subject_id}}
```

**3. Get Pending SAE Reviews**
```
GET {{base_url}}/api/adverse-events/sae-pending-review
```

**4. Submit Safety Report**
```
POST {{base_url}}/api/adverse-events/1/submit-safety-report?reportingPhysician=Dr.Smith&reportingNotes=Hospitalized%20for%203%20days
```

**5. Update Status**
```
PATCH {{base_url}}/api/adverse-events/1/status/RESOLVED
```

## Features Implemented

✅ **Adverse Event Reporting**
- Capture event details with structured form
- Automatic timestamp and metadata

✅ **MedDRA Coding**
- Automatic ontology mapping
- Preferred term assignment
- Version tracking

✅ **Seriousness Classification**
- Intelligent SAE detection
- Severity-based escalation
- Keyword pattern matching

✅ **SAE Workflow Management**
- 24-hour reporting deadline
- Status tracking (REPORTED → UNDER_REVIEW → RESOLVED)
- Physician assignment and notes

✅ **Event History & Auditing**
- Complete subject event history
- Date range filtering
- Event status filtering
- MedDRA code queries

✅ **Statistics & Reporting**
- Total SAE count
- Events by subject
- Events by status
- Pending review dashboard

## Integration Points (TODO)

- Email notifications for SAE alerts
- FDA e-submission format conversion
- Regulatory reporting integration
- Advanced MedDRA SNOMED-CT mapping
- Audit trail logging

## Performance Optimization

Repository includes optimized queries:
- `findBySubjectId()` - indexed query
- `findEventsBySubjectAndDateRange()` - date-optimized
- `findAllSAEEventsForReporting()` - named queries
- `countSAEEvents()` - aggregate functions

---

**Ready to test!** Start the app and navigate to http://localhost:8082/events

