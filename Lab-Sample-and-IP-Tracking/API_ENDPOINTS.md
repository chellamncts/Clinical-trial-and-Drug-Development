# Lab Sample and IP Tracking - API Endpoints & Testing URLs

**Base URL:** `http://localhost:8080`  
**Server Port:** `8080`  
**Application Name:** Lab-Sample-and-IP-Tracking

---

## 🚀 Quick Start

### 1. Run the Application

```powershell
cd "C:\Users\2503696\Downloads\Lab-Sample-and-IP-Tracking\Lab-Sample-and-IP-Tracking"
.\mvnw.cmd spring-boot:run
```

### 2. Wait for Startup Message

Look for: `Started LabSampleAndIpTrackingApplication in X.XXX seconds`

### 3. Test the URLs Below

---

## 📍 Core Application URLs

### **Home Page / Dashboard**
```
GET http://localhost:8080/
```
- Landing page of the application
- Renders HTML home view

---

## 🧬 Sample Log Management

### **Sample Registration Form**
```
GET http://localhost:8080/UserForm
```
- Displays the sample registration form
- Returns HTML with form fields

### **With Success Message**
```
GET http://localhost:8080/UserForm?success=Sample%20saved%20successfully
```

### **With Error Message**
```
GET http://localhost:8080/UserForm?error=Failed%20to%20save%20sample
```

### **Save Sample Data**
```
POST http://localhost:8080/saveUser
```
**Parameters:**
- `subjectId` (required) - Subject ID (integer)
- `collectionDate` (optional) - Sample collection date (YYYY-MM-DD)
- `sampleType` (required) - Type of sample (text)
- `labResult` (optional) - Lab test results (text)

**Example cURL:**
```bash
curl -X POST "http://localhost:8080/saveUser" \
  -d "subjectId=101&sampleType=Blood&collectionDate=2026-06-30&labResult=Normal"
```

### **Sample List View**
```
GET http://localhost:8080/sample-list
```
- Displays all saved samples
- Returns HTML table view

### **Search Samples**
```
GET http://localhost:8080/search
```
- Opens search interface

### **Post Search Query**
```
POST http://localhost:8080/search
```
**Parameters:**
- `query` - Search query (text)

---

## 🧪 Adverse Event Management

### **List All Adverse Events**
```
GET http://localhost:8080/adverse-events
```

### **Create New Adverse Event**
```
POST http://localhost:8080/adverse-events
```
**Body (JSON):**
```json
{
  "eventDescription": "Patient experienced fever",
  "eventDate": "2026-06-30",
  "severity": "MILD",
  "relatedToStudy": true
}
```

---

## 👥 Subject Enrollment

### **List All Enrollments**
```
GET http://localhost:8080/enrollments
```

### **Create New Enrollment**
```
POST http://localhost:8080/enrollments
```
**Body (JSON):**
```json
{
  "subjectId": 101,
  "subjectName": "John Doe",
  "enrollmentDate": "2026-06-30",
  "status": "ACTIVE"
}
```

---

## 📋 Trial Protocol

### **List All Protocols**
```
GET http://localhost:8080/protocols
```

### **Create New Protocol**
```
POST http://localhost:8080/protocols
```
**Body (JSON):**
```json
{
  "protocolNumber": "CTDS-2026-001",
  "protocolTitle": "Clinical Trial for New Treatment",
  "status": "ACTIVE"
}
```

---

## 📅 Visit Scheduling

### **List All Scheduled Visits**
```
GET http://localhost:8080/visits
```

### **Schedule New Visit**
```
POST http://localhost:8080/visits
```
**Body (JSON):**
```json
{
  "subjectId": 101,
  "visitDate": "2026-07-15",
  "visitType": "Follow-up",
  "location": "Clinical Center A"
}
```

---

## 🎨 Static Resources

### **Main HTML Index**
```
GET http://localhost:8080/index.html
```

### **Application Stylesheet**
```
GET http://localhost:8080/app.css
```

---

## 📄 UI Pages

### **Inventory Dashboard**
```
GET http://localhost:8080/inventory.html
```

### **Lab Result Entry**
```
GET http://localhost:8080/lab-result.html
```

### **Dispense Form**
```
GET http://localhost:8080/dispense-form.html
```

### **Sample List**
```
GET http://localhost:8080/sample-list.html
```

---

## 🧩 UI Components (Snippets)

These are reusable HTML snippets served from:
```
http://localhost:8080/ui/snippets/[filename].html
```

**Available snippets:**
- `alert-success.html` - Success notification
- `alert-error.html` - Error notification
- `inventory-row.html` - Inventory row template
- `sample-row.html` - Sample row template
- `lab-collected-section.html` - Sample collected state
- `lab-in-transit-section.html` - Sample in transit state
- `lab-final-section.html` - Final lab disposition
- And more...

---

## 🧪 Testing Checklist

### **Basic Application Health**
- [ ] Home page loads: `GET http://localhost:8080/`
- [ ] User form displays: `GET http://localhost:8080/UserForm`
- [ ] CSS loads: `GET http://localhost:8080/app.css`

### **Sample Management**
- [ ] Save sample: `POST http://localhost:8080/saveUser`
- [ ] View samples: `GET http://localhost:8080/sample-list`
- [ ] Search samples: `GET http://localhost:8080/search`

### **Adverse Events**
- [ ] List events: `GET http://localhost:8080/adverse-events`
- [ ] Create event: `POST http://localhost:8080/adverse-events`

### **Subject Management**
- [ ] List enrollments: `GET http://localhost:8080/enrollments`
- [ ] Create enrollment: `POST http://localhost:8080/enrollments`

### **Trial Management**
- [ ] List protocols: `GET http://localhost:8080/protocols`
- [ ] Create protocol: `POST http://localhost:8080/protocols`

### **Visit Scheduling**
- [ ] List visits: `GET http://localhost:8080/visits`
- [ ] Schedule visit: `POST http://localhost:8080/visits`

---

## 🔍 API Testing Tools

### **Option 1: Using curl (Command Line)**
```powershell
# Test home page
curl http://localhost:8080/

# Test user form
curl http://localhost:8080/UserForm

# Post sample data
curl -X POST "http://localhost:8080/saveUser" `
  -d "subjectId=101&sampleType=Blood&collectionDate=2026-06-30"
```

### **Option 2: Using Postman**
1. Download Postman from https://www.postman.com/downloads/
2. Create new HTTP request
3. Set method and URL
4. Add parameters/body
5. Click Send

### **Option 3: Using Browser**
- Open browser and navigate to `http://localhost:8080/`
- For POST requests, use browser developer tools (F12) or form submission

### **Option 4: Using VS Code REST Client Extension**
Create a file `test.http`:
```http
### Home Page
GET http://localhost:8080/

### User Form
GET http://localhost:8080/UserForm

### Save Sample
POST http://localhost:8080/saveUser
Content-Type: application/x-www-form-urlencoded

subjectId=101&sampleType=Blood&collectionDate=2026-06-30
```

---

## 🐛 Troubleshooting URLs

### **Application Not Responding**
```powershell
# Check if port 8080 is in use
netstat -ano | findstr :8080

# Check if application is running
curl http://localhost:8080/
```

### **Connection Refused**
```
Error: Connection refused
Solution: Make sure application is running with .\mvnw.cmd spring-boot:run
```

### **404 Not Found**
```
Error: 404 - Endpoint not found
Solution: Check exact URL spelling and method (GET vs POST)
```

### **500 Internal Server Error**
```
Error: 500 - Internal Server Error
Solution: Check application logs for specific error message
```

---

## 📊 Expected Responses

### **Successful GET (HTML Page)**
```
HTTP/1.1 200 OK
Content-Type: text/html

<!DOCTYPE html>
<html>
...
</html>
```

### **Successful POST (Form Submit)**
```
HTTP/1.1 302 Found
Location: /UserForm?success=Sample+saved+successfully

Redirects back to form with success message
```

### **Error Response**
```
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
  "error": "Invalid input",
  "message": "Subject ID is required"
}
```

---

## 🔗 Complete Test Flow

### **Sample Registration Workflow:**

1. **Start Application**
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

2. **Open Home Page**
   ```
   http://localhost:8080/
   ```

3. **Go to Registration Form**
   ```
   http://localhost:8080/UserForm
   ```

4. **Fill and Submit Form**
   ```
   POST http://localhost:8080/saveUser
   - subjectId: 101
   - sampleType: Blood
   - collectionDate: 2026-06-30
   - labResult: Normal
   ```

5. **View Sample List**
   ```
   http://localhost:8080/sample-list
   ```

6. **Search Samples**
   ```
   POST http://localhost:8080/search
   - query: 101
   ```

---

## 💾 Database

### **Database Name:** `lab_sample_ip_tracking`

**Auto-created tables:**
- `sample_log` - Sample records
- `investigational_product_inventory` - Product inventory
- `adverse_event` - Adverse event records
- `subject_enrollment` - Subject enrollment data
- `trial_protocol` - Trial protocol information
- `visit_scheduling` - Visit schedule records

### **Connect to Database**
```bash
mysql -u root -p lab_sample_ip_tracking
```

---

## 📞 Support

If URLs don't work:
1. Ensure application is running (check console for "Started" message)
2. Verify port 8080 is correct
3. Check browser console (F12) for error details
4. Check application logs for detailed error messages

---

## ✅ Completion Status

- ✅ All 24 Java classes ready
- ✅ All endpoints implemented
- ✅ Static resources configured
- ✅ Database auto-configuration ready
- ✅ Ready for testing!

