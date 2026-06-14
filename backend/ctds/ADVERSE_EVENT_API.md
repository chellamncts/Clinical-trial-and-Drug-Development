# Adverse Event Reporting & Safety Monitoring Module - API Documentation

## Overview
Complete module for capturing, classifying, and managing adverse events with automated MedDRA coding and SAE (Serious Adverse Event) workflow management.

---

## HTML Views

### 1. Report Adverse Event Form
**URL:** `http://localhost:8082/events`  
**Method:** `GET`  
**Description:** Displays the adverse event reporting form

**Form Fields:**
- **Subject ID** (Required): Clinical trial subject identifier
- **Event Description** (Required): Detailed description of the adverse event
- **Onset Date** (Required): Date when the event occurred
- **Severity** (Required): Select from MILD, MODERATE, SEVERE
- **Status** (Required): Select from REPORTED, UNDER_REVIEW, RESOLVED, FATAL

**Form Submission:** `POST /events/save`

### 2. Dashboard
**URL:** `http://localhost:8082/dashboard`  
**Method:** `GET`  
**Description:** Main dashboard view

---

## REST API Endpoints

### Base URL: `http://localhost:8082/api/adverse-events`

### 1. Report Adverse Event
**Endpoint:** `POST /api/adverse-events/report`  
**Description:** Report a new adverse event with automatic:
- MedDRA coding
- Seriousness classification (SAE/NON_SAE)
- SAE workflow triggering

**Request Body:**
```json
{
  "subjectId": "SUBJ001",
  "eventDescription": "Patient experienced severe rash after medication",
  "severity": "SEVERE",
  "eventStatus": "REPORTED",
  "eventOnsetDate": "2026-06-11"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Adverse event reported successfully",
  "eventId": 1,
  "seriousness": "SAE",
  "meddraPreferredTerm": "Rash",
  "requiresSAEReport": true
}
```

**Status Codes:**
- `200 OK`: Event reported successfully
- `400 Bad Request`: Invalid input data

---

### 2. Classify Seriousness
**Endpoint:** `POST /api/adverse-events/{eventId}/classify`  
**Description:** Classify event seriousness and determine if SAE

**Example:** `POST /api/adverse-events/1/classify`

**Response:**
```json
{
  "success": true,
  "eventId": 1,
  "seriousness": "SAE",
  "severity": "SEVERE",
  "requiresSAEReport": true,
  "meddraCode": "10037844",
  "meddraPreferredTerm": "Rash"
}
```

---

### 3. Submit Safety Report (SAE Workflow)
**Endpoint:** `POST /api/adverse-events/{eventId}/submit-safety-report`  
**Description:** Submit official safety report for a SAE event

**Parameters:**
- `reportingPhysician` (Query Param): Name/ID of reporting physician
- `reportingNotes` (Query Param): Additional notes/observations

**Example:**
```
POST /api/adverse-events/1/submit-safety-report?reportingPhysician=Dr.Smith&reportingNotes=Resolved%20after%20treatment
```

**Response:**
```json
{
  "success": true,
  "message": "Safety report submitted for SAE",
  "eventId": 1,
  "eventStatus": "UNDER_REVIEW",
  "saereportingDeadline": "2026-06-12",
  "reportingPhysician": "Dr.Smith"
}
```

---

### 4. Get Event History for Subject
**Endpoint:** `GET /api/adverse-events/history/{subjectId}`  
**Description:** Retrieve complete event history for a subject

**Example:** `GET /api/adverse-events/history/SUBJ001`

**Response:**
```json
{
  "success": true,
  "subjectId": "SUBJ001",
  "totalEvents": 3,
  "events": [
    {
      "eventId": 1,
      "subjectId": "SUBJ001",
      "eventDescription": "Severe rash",
      "severity": "SEVERE",
      "eventStatus": "REPORTED",
      "seriousness": "SAE",
      "eventOnsetDate": "2026-06-11",
      "reportingDate": "2026-06-11T10:30:00",
      "meddraCode": "10037844",
      "meddraPreferredTerm": "Rash",
      "requiresSAEReport": true
    }
  ]
}
```

---

### 5. Get Event History by Date Range
**Endpoint:** `GET /api/adverse-events/history/{subjectId}/{startDate}/{endDate}`  
**Description:** Retrieve events within a specific date range (ISO format: YYYY-MM-DD)

**Example:** `GET /api/adverse-events/history/SUBJ001/2026-06-01/2026-06-30`

**Response:**
```json
{
  "success": true,
  "subjectId": "SUBJ001",
  "dateRange": "2026-06-01 to 2026-06-30",
  "eventCount": 2,
  "events": [...]
}
```

---

### 6. Get All SAE Events Pending Review
**Endpoint:** `GET /api/adverse-events/sae-pending-review`  
**Description:** Retrieve all SAE events requiring review/reporting

**Response:**
```json
{
  "success": true,
  "totalSAECount": 5,
  "pendingReviewCount": 2,
  "saeEvents": [...]
}
```

---

### 7. Get Event by ID
**Endpoint:** `GET /api/adverse-events/{eventId}`  
**Description:** Retrieve details of a specific event

**Example:** `GET /api/adverse-events/1`

**Response:**
```json
{
  "success": true,
  "event": {
    "eventId": 1,
    "subjectId": "SUBJ001",
    "eventDescription": "Severe rash",
    "severity": "SEVERE",
    "eventStatus": "REPORTED",
    "seriousness": "SAE",
    "meddraCode": "10037844",
    "meddraPreferredTerm": "Rash"
  }
}
```

---

### 8. Get Events by Status
**Endpoint:** `GET /api/adverse-events/by-status/{status}`  
**Description:** Retrieve events filtered by status

**Valid Status Values:** `REPORTED`, `UNDER_REVIEW`, `RESOLVED`, `FATAL`

**Example:** `GET /api/adverse-events/by-status/UNDER_REVIEW`

**Response:**
```json
{
  "success": true,
  "status": "UNDER_REVIEW",
  "eventCount": 2,
  "events": [...]
}
```

---

### 9. Update Event Status
**Endpoint:** `PATCH /api/adverse-events/{eventId}/status/{newStatus}`  
**Description:** Update the status of an event

**Valid Status Values:** `REPORTED`, `UNDER_REVIEW`, `RESOLVED`, `FATAL`

**Example:** `PATCH /api/adverse-events/1/status/RESOLVED`

**Response:**
```json
{
  "success": true,
  "eventId": 1,
  "newStatus": "RESOLVED",
  "message": "Event status updated successfully"
}
```

---

### 10. Get Statistics
**Endpoint:** `GET /api/adverse-events/statistics`  
**Description:** Retrieve overall SAE statistics

**Response:**
```json
{
  "success": true,
  "totalSAECount": 5,
  "timestamp": "2026-06-11T11:02:30"
}
```

---

## Data Models

### AdverseEvent Entity
```java
{
  "eventId": Integer,                    // Auto-generated Primary Key
  "subjectId": String,                   // Foreign Key to Subject
  "eventDescription": String,            // Event details
  "severity": "MILD|MODERATE|SEVERE",    // Severity level
  "eventStatus": "REPORTED|UNDER_REVIEW|RESOLVED|FATAL",
  "seriousness": "SAE|NON_SAE",          // Classification
  "eventOnsetDate": LocalDate,           // Date of event onset
  "reportingDate": LocalDateTime,        // When event was reported
  "meddraCode": String,                  // MedDRA preferred term code
  "meddraPreferredTerm": String,         // MedDRA PT name
  "meddraVersion": String,               // MedDRA version used
  "requiresSAEReport": Boolean,          // SAE reporting required?
  "saereportingDeadline": LocalDate,     // 24-hour deadline for SAE
  "reportingPhysician": String,          // Reporting clinician
  "reportingNotes": String               // Additional notes
}
```

---

## MedDRA Coding Examples

The system automatically applies MedDRA coding based on event description keywords:

| Keyword | MedDRA Code | Preferred Term |
|---------|-------------|-----------------|
| rash, skin | 10037844 | Rash |
| fever, temperature | 10016273 | Pyrexia |
| headache, head pain | 10019211 | Headache |
| nausea | 10028813 | Nausea |
| dizziness, vertigo | 10013573 | Dizziness |
| tremor | 10044565 | Tremor |

---

## SAE Workflow Logic

### Automatic SAE Classification
An event is classified as **SAE** if:
1. Severity is **SEVERE**, OR
2. Event description contains keywords:
   - hospitali... (hospitalization)
   - death
   - life-threatening
   - disability
   - permanent
   - critical

### SAE Reporting Workflow
1. Event classified as SAE → `requiresSAEReport = true`
2. 24-hour reporting deadline set
3. `/submit-safety-report` endpoint initiates workflow
4. Event status changed to `UNDER_REVIEW`
5. Safety alerts triggered to responsible parties

---

## Testing Examples

### Using cURL

**Report Event:**
```bash
curl -X POST http://localhost:8082/api/adverse-events/report \
  -H "Content-Type: application/json" \
  -d '{
    "subjectId": "SUBJ001",
    "eventDescription": "Patient experienced severe rash",
    "severity": "SEVERE",
    "eventStatus": "REPORTED",
    "eventOnsetDate": "2026-06-11"
  }'
```

**Get Event History:**
```bash
curl http://localhost:8082/api/adverse-events/history/SUBJ001
```

**Submit Safety Report:**
```bash
curl -X POST "http://localhost:8082/api/adverse-events/1/submit-safety-report?reportingPhysician=Dr.Smith&reportingNotes=Following%20up"
```

**Update Event Status:**
```bash
curl -X PATCH http://localhost:8082/api/adverse-events/1/status/RESOLVED
```

---

## Error Handling

All endpoints return standardized error responses:

```json
{
  "success": false,
  "message": "Descriptive error message"
}
```

**Common HTTP Status Codes:**
- `200 OK`: Successful operation
- `400 Bad Request`: Invalid input or business logic error
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server-side exception

---

## Database Schema

The following tables are automatically created:

### adverse_event
| Column | Type | Notes |
|--------|------|-------|
| event_id | INT | Primary Key, Auto-increment |
| subject_id | VARCHAR | Foreign Key |
| event_description | VARCHAR | Event details |
| severity | ENUM | MILD, MODERATE, SEVERE |
| event_status | ENUM | REPORTED, UNDER_REVIEW, RESOLVED, FATAL |
| seriousness | ENUM | SAE, NON_SAE |
| event_onset_date | DATE | |
| reporting_date | DATETIME | |
| meddra_code | VARCHAR | MedDRA code |
| meddra_preferred_term | VARCHAR | |
| meddra_version | VARCHAR | |
| requires_sae_report | BOOLEAN | |
| sae_reporting_deadline | DATE | |
| reporting_physician | VARCHAR | |
| reporting_notes | VARCHAR | |

---

## Running the Application

```bash
cd backend/ctds
mvn spring-boot:run
```

Access the application at: `http://localhost:8082/events`

---

## Key Features

✅ **Automated MedDRA Coding** - Events are automatically mapped to MedDRA terms  
✅ **SAE Classification** - Intelligent seriousness determination  
✅ **SAE Workflows** - Integrated safety reporting with 24-hour deadlines  
✅ **Event History** - Complete audit trail for each subject  
✅ **Status Tracking** - Track events through the complete lifecycle  
✅ **Date Range Queries** - Analyze events over time periods  
✅ **RESTful API** - Full API for integration with other systems  
✅ **HTML Form Interface** - User-friendly adverse event reporting form  

---

## Support
For issues or questions, refer to the module source code in:
`backend/ctds/src/main/java/com/genc/ctds/adverseevent/`

