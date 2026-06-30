# Lab Sample and IP Tracking - Complete Migration Guide

**Migration Date:** June 30, 2026  
**Source:** Clinical-trial-and-Drug-Development (ctds backend)  
**Target:** Lab-Sample-and-IP-Tracking

---

## 📋 Migration Overview

This project has been successfully populated with all packages, resources, and configurations from the CTDS backend project. The application is a Spring Boot service for tracking lab samples and investigational product inventory in clinical trials.

---

## 🎯 What Was Migrated

### 1. Java Packages (24 Classes)

#### Domain Packages
- **adverseevent/** - 4 classes
  - `AdverseEventController` - REST endpoints for adverse event management
  - `AdverseEvent` - JPA entity model
  - `AdverseEventRepository` - Database repository interface
  - `AdverseEventService` - Business logic service

- **samplelog/** - 7 classes
  - `SamplePageController` - Primary REST controller (340+ lines)
  - `SampleService` - Sample management business logic
  - `SampleLog` - JPA entity for sample logs
  - `InvestigationalProductInventory` - Inventory model
  - `GlobalExceptionHandler` - Centralized exception handling
  - `SampleLogRepository` - Sample data access layer
  - `InvestigationalProductInventoryRepository` - Inventory data access

- **subjectenrollment/** - 4 classes
  - `SubjectEnrollmentController` - Enrollment management endpoints
  - `SubjectEnrollment` - JPA entity
  - `SubjectEnrollmentRepository` - Data repository
  - `SubjectEnrollmentService` - Service layer

- **trialprotocol/** - 4 classes
  - `TrialProtocolController` - Protocol management endpoints
  - `TrialProtocol` - JPA entity
  - `TrialProtocolRepository` - Data repository
  - `TrialProtocolService` - Service layer

- **visitscheduling/** - 4 classes
  - `VisitSchedulingController` - Visit scheduling endpoints
  - `VisitScheduling` - JPA entity
  - `VisitSchedulingRepository` - Data repository
  - `VisitSchedulingService` - Service layer

### 2. Resource Files (22 Files)

#### Database Migrations
- `src/main/resources/db/migration/V1__CleanupSampleLogTable.sql`
  - Flyway migration script for database initialization

#### Static Assets
- `src/main/resources/static/index.html` - Main landing page
- `src/main/resources/static/app.css` - Application styling

#### UI Pages (6 HTML Pages)
- `ui/pages/user-form.html` - User/sample registration form
- `ui/pages/sample-list.html` - View all sample logs
- `ui/pages/inventory.html` - Investigational product inventory dashboard
- `ui/pages/dispense-form.html` - Drug dispensing form
- `ui/pages/lab-result.html` - Lab result entry and tracking
- `ui/pages/search.html` - Advanced search interface

#### UI Snippets (12 Reusable Components)
- **Alert Components:**
  - `alert-success.html` - Success notification
  - `alert-error.html` - Error notification

- **Inventory Components:**
  - `inventory-row.html` - Detailed inventory item row
  - `inventory-row-short.html` - Compact inventory row
  - `inventory-stat-tile.html` - Inventory statistics tile

- **Lab Workflow Components:**
  - `lab-collected-section.html` - Sample collected state
  - `lab-in-transit-section.html` - Sample in transit state
  - `lab-final-section.html` - Lab final disposition
  - `lab-destroy-action.html` - Sample destruction action

- **Sample Management Components:**
  - `sample-row.html` - Sample list item
  - `search-row.html` - Search result item
  - `dispense-option.html` - Drug dispensing option

---

## ⚙️ Configuration Details

### pom.xml - Dependencies Added

```xml
<!-- Spring Boot Web & JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Database -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Database Migrations (Flyway) -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>

<!-- Service Discovery -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### application.properties - Configuration

```properties
# Application
spring.application.name=Lab-Sample-and-IP-Tracking
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/lab_sample_ip_tracking?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Flyway Database Migrations
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baselineOnMigrate=true
```

---

## 🚀 Getting Started

### Prerequisites

- Java 21 (configured in pom.xml)
- MySQL 8.0+ running on `localhost:3306`
- Maven 3.8.1+ (Maven wrapper included)

### Database Setup

1. Ensure MySQL is running:
```bash
# MySQL should be accessible at localhost:3306
mysql -u root -p
```

2. Create database (automatic on first run):
```sql
CREATE DATABASE IF NOT EXISTS lab_sample_ip_tracking;
```

### Build Instructions

```bash
# Navigate to project directory
cd "C:\Users\2503696\Downloads\Lab-Sample-and-IP-Tracking\Lab-Sample-and-IP-Tracking"

# Clean and build
.\mvnw.cmd clean install

# Run tests (if available)
.\mvnw.cmd test
```

### Running the Application

```bash
# Option 1: Using Maven Spring Boot plugin
.\mvnw.cmd spring-boot:run

# Option 2: Run the generated JAR
java -jar target/Lab-Sample-and-IP-Tracking-0.0.1-SNAPSHOT.jar
```

### Accessing the Application

Once running, the application is available at:

- **Root URL:** `http://localhost:8080`
- **Static Page:** `http://localhost:8080/index.html`
- **REST API Base:** `http://localhost:8080/api/*`

### API Endpoints

The migrated controllers expose the following REST endpoints:

**Sample Log Management**
- `GET /` - Home page
- `GET /UserForm` - Sample registration form
- `POST /saveUser` - Save sample data
- `GET /sample-list` - List all samples
- `POST /search` - Search samples

**Adverse Events**
- `GET /adverse-events` - List adverse events
- `POST /adverse-events` - Create adverse event

**Subject Enrollment**
- `GET /enrollments` - List enrollments
- `POST /enrollments` - Create enrollment

**Trial Protocol**
- `GET /protocols` - List protocols
- `POST /protocols` - Create protocol

**Visit Scheduling**
- `GET /visits` - List scheduled visits
- `POST /visits` - Schedule visit

---

## 📂 Project Structure

```
Lab-Sample-and-IP-Tracking/
├── src/
│   ├── main/
│   │   ├── java/com/genc/Lab_Sample_and_IP_Tracking/
│   │   │   ├── LabSampleAndIpTrackingApplication.java
│   │   │   ├── adverseevent/
│   │   │   │   ├── controller/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── samplelog/
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── exception/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── subjectenrollment/
│   │   │   ├── trialprotocol/
│   │   │   └── visitscheduling/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── db/migration/
│   │       │   └── V1__CleanupSampleLogTable.sql
│   │       ├── static/
│   │       │   ├── index.html
│   │       │   └── app.css
│   │       └── ui/
│   │           ├── pages/
│   │           └── snippets/
│   └── test/
│       └── java/com/genc/Lab_Sample_and_IP_Tracking/
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

---

## 🔧 Troubleshooting

### MySQL Connection Issues
```
Error: Can't create a database connection
Solution: 
- Verify MySQL is running: mysql -u root -p
- Check credentials in application.properties
- Ensure localhost:3306 is accessible
```

### Flyway Migration Errors
```
Error: Flyway migration failed
Solution:
- Check SQL syntax in db/migration/*.sql
- Ensure database user has CREATE/ALTER permissions
- Check application.properties flyway configuration
```

### Port Already in Use
```
Error: Bind exception on port 8080
Solution:
- Change server.port in application.properties
- Or kill the process using port 8080
```

---

## 📝 Important Notes

1. **Package Namespace:** All classes use `com.genc.Lab_Sample_and_IP_Tracking.*` package names (updated from original `com.genc.ctds.*`)

2. **Database:** The application expects a MySQL database named `lab_sample_ip_tracking`. This will be created automatically if it doesn't exist.

3. **Migrations:** Flyway will automatically run SQL migration scripts in `db/migration/` on startup.

4. **Static Files:** CSS and HTML files in `static/` folder are served as static resources.

5. **UI Pages:** HTML pages in `ui/` folder are served through the `SamplePageController` endpoints.

---

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Flyway Database Migrations](https://flywaydb.org/)
- [MySQL Connector/J](https://dev.mysql.com/products/connector/j/)

---

## ✅ Verification Checklist

- [x] All 5 domain packages copied and updated
- [x] 24 Java classes migrated with correct package names
- [x] 22 resource files copied (db migrations, static assets, UI pages/snippets)
- [x] pom.xml updated with all required dependencies
- [x] application.properties configured with database and Flyway settings
- [x] Spring Boot application class ready to scan packages
- [x] README and migration documentation created

**Project is ready for build and deployment!**

