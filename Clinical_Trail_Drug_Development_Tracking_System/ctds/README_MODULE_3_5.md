# Module 3.5 - Run Guide

This module exposes REST APIs for Lab Sample and Investigational Product Tracking and provides a simple static UI.

## URLs

- UI: `http://localhost:8081/module-3-5.html`
- API base: `http://localhost:8081/sample`

## Endpoints

- `POST /sample/collect`
- `POST /sample/lab-result`
- `POST /sample/ip/dispense`
- `GET /sample/ip/inventory`
- `GET /sample/subject/{subjectId}`

## Run (PowerShell)

```powershell
Set-Location "C:\Users\2503696\Ctddts\Clinical-trial-and-Drug-Development\backend\ctds"
.\mvnw.cmd -DskipTests clean compile
.\mvnw.cmd -DskipTests spring-boot:run
```

## Quick API check

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8081/sample/collect" -ContentType "application/json" -Body '{"subjectId":1,"sampleType":"Blood","collectionDate":"2026-06-23"}'
Invoke-RestMethod -Method Get -Uri "http://localhost:8081/sample/ip/inventory"
```

