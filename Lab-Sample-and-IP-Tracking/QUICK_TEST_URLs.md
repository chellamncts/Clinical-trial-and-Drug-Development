# 🚀 QUICK TEST URLs - Copy & Paste Ready

## Primary Test URLs

### 1. **Home Page** (Start Here!)
```
http://localhost:8080/
```

### 2. **Sample Registration Form**
```
http://localhost:8080/UserForm
```

### 3. **Sample List View**
```
http://localhost:8080/sample-list.html
```

### 4. **Inventory Dashboard**
```
http://localhost:8080/inventory.html
```

### 5. **Lab Result Entry**
```
http://localhost:8080/lab-result.html
```

### 6. **Search Samples**
```
http://localhost:8080/search
```

### 7. **Adverse Events**
```
http://localhost:8080/adverse-events
```

### 8. **Subject Enrollments**
```
http://localhost:8080/enrollments
```

### 9. **Trial Protocols**
```
http://localhost:8080/protocols
```

### 10. **Visit Scheduling**
```
http://localhost:8080/visits
```

---

## 🛠️ Run Application Command

```powershell
cd "C:\Users\2503696\Downloads\Lab-Sample-and-IP-Tracking\Lab-Sample-and-IP-Tracking"
.\mvnw.cmd spring-boot:run
```

**Wait for message:** `Started LabSampleAndIpTrackingApplication`

---

## 📝 POST Request Examples

### Save Sample (Form Submit)
```
URL: http://localhost:8080/saveUser
Method: POST
Parameters:
  - subjectId: 101
  - sampleType: Blood
  - collectionDate: 2026-06-30
  - labResult: Normal
```

### Search Samples
```
URL: http://localhost:8080/search
Method: POST
Parameters:
  - query: 101
```

---

## ⚡ Quickest Test (15 seconds)

1. Run app:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

2. Open browser and go to:
   ```
   http://localhost:8080/
   ```

3. If you see the page, application is **WORKING** ✅

---

## 📱 Browser Console Testing (F12)

```javascript
// Test API endpoint availability
fetch('http://localhost:8080/')
  .then(r => r.text())
  .then(t => console.log(t.substring(0, 100)))
```

---

## 🔍 Database Check

Once application is running:

```sql
-- Connect to database
mysql -u root -p lab_sample_ip_tracking

-- Check created tables
SHOW TABLES;

-- View sample logs
SELECT * FROM sample_log;
```

---

## 📞 If URLs Don't Work

| Issue | Solution |
|-------|----------|
| Connection Refused | App not running - Run: `.\mvnw.cmd spring-boot:run` |
| 404 Not Found | Check URL spelling (case-sensitive) |
| Port 8080 in Use | Kill Java: `Get-Process -Name java \| Stop-Process` |
| 500 Error | Check application logs in console |

---

## ✅ Success Indicators

- [ ] Home page loads at `http://localhost:8080/`
- [ ] User form displays
- [ ] CSS styling applied (not plain HTML)
- [ ] Can submit sample data
- [ ] Sample list shows saved data
- [ ] No errors in browser console

---

## 📄 Full Documentation

For detailed API documentation with curl examples and JSON payloads, see:
```
API_ENDPOINTS.md
```

---

**Last Updated:** June 30, 2026  
**Status:** Ready for Testing ✅

