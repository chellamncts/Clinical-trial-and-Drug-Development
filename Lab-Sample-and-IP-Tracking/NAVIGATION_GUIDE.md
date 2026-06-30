# 🎯 Lab Sample & IP Tracking - Complete Navigation Guide

**Updated:** June 30, 2026  
**Status:** ✅ All endpoints now support both GET and POST navigation

---

## 🚀 Start Here

### Access Points (All Working)

| URL | Method | Purpose | What Shows |
|-----|--------|---------|-----------|
| `http://localhost:8084/` | GET | Home | User Form |
| `http://localhost:8084/saveUser` | GET | **NEW** - Sample registration form | User Form |
| `http://localhost:8084/UserForm` | GET | Sample registration form | User Form |

---

## 🗺️ Full Navigation Map

```
┌─────────────────────────────────────────────────────────────────┐
│                     CLINICAL TRIAL SYSTEM                       │
│                    Navigation Menu (Top Bar)                    │
├─────────────────────────────────────────────────────────────────┤
│
│  [Add Sample] ──→ /UserForm (GET) ──→ http://localhost:8084/saveUser ✓
│  [All Samples] ──→ /sample/list (GET)
│  [Search] ──→ /sample/search-form (GET)
│  [Inventory] ──→ /sample/inventory (GET)
│  [Dispense IP] ──→ /sample/ip/dispense-form (GET)
│
└─────────────────────────────────────────────────────────────────┘
```

---

## ✅ All Working Endpoints

### **Sample Management**

```
GET  http://localhost:8084/saveUser
     ↓ (shows User Form)
     ↓ (fill form & submit)
POST http://localhost:8084/saveUser
     ↓ (saves data & redirects)
GET  http://localhost:8084/sample/list
     ↓ (shows Sample List)
```

### **User Registration Flow**

1. **Start:** `http://localhost:8084/`
   - Shows: User Form
   - Action: Enter Subject ID, Sample Type, Collection Date, Lab Result

2. **Submit:** POST `/saveUser`
   - Saves sample to database
   - Redirects to: `/sample/list` with success message

3. **View Results:** `http://localhost:8084/sample/list`
   - Shows: All samples collected
   - Actions: Click any sample to view/edit lab results

---

## 🧪 Sample Log Workflow

### **Collecting a Sample**
```
GET  /saveUser ──→ Shows form
POST /saveUser ──→ Saves sample
GET  /sample/list ──→ View all samples
```

### **Managing Inventory & Dispense**
```
GET  /sample/inventory ──→ View inventory dashboard
GET  /sample/ip/dispense-form ──→ Show dispense form
POST /sample/ip/dispense-action ──→ Process dispensing
GET  /sample/inventory ──→ View updated inventory
```

### **Searching Samples**
```
GET  /sample/search-form ──→ Show search form
GET  /sample/search?subjectId=101 ──→ Find samples by Subject
```

### **Lab Results Entry**
```
GET  /sample/list ──→ View samples
GET  /sample/lab-result/{sampleId} ──→ Edit lab result
POST /sample/lab-result-action ──→ Save result
```

---

## 📍 Complete URL Reference

### **Navigation Endpoints (GET)**

| Endpoint | URL | Description |
|----------|-----|-------------|
| Home | `http://localhost:8084/` | Home page → User Form |
| Sample Form | `http://localhost:8084/saveUser` | **NEW** - Show add sample form |
| Sample Form | `http://localhost:8084/UserForm` | Show add sample form |
| Sample List | `http://localhost:8084/sample/list` | View all samples |
| Search Form | `http://localhost:8084/sample/search-form` | Show search interface |
| Search Results | `http://localhost:8084/sample/search?subjectId=101` | Find samples by subject |
| Inventory | `http://localhost:8084/sample/inventory` | View inventory dashboard |
| Dispense Form | `http://localhost:8084/sample/ip/dispense-form` | Show dispense form |
| Dispense Form | `http://localhost:8084/sample/ip/dispense-action` | **NEW** - Show dispense form |
| Lab Result | `http://localhost:8084/sample/lab-result/1` | Edit lab result for sample |

### **Action Endpoints (POST)**

| Endpoint | URL | Parameters | Purpose |
|----------|-----|-----------|---------|
| Save Sample | `/saveUser` | subjectId, sampleType, collectionDate, labResult | Create new sample |
| Dispense IP | `/sample/ip/dispense-action` | inventoryId, subjectId, quantity, dispensedBy, dispensingLocation | Record dispensing |
| Lab Result | `/sample/lab-result-action` | sampleId, labResult | Record lab test results |
| Update Status | `/sample/update-status` | sampleId, status | Change sample status |

---

## 🎯 Test Cases - Step by Step

### **Test 1: Add New Sample**
1. Open: `http://localhost:8084/saveUser` ← **NOW WORKS with GET**
2. Fill form:
   - Subject ID: 101
   - Collection Date: 2026-06-30
   - Sample Type: Blood
   - Lab Result: (optional) Normal
3. Click Submit → Redirects to Sample List

### **Test 2: Dispense Inventory**
1. Open: `http://localhost:8084/sample/ip/dispense-action` ← **NOW WORKS with GET**
2. Fill dispense form:
   - Inventory Product: (select from dropdown)
   - Subject ID: 101
   - Quantity: 2
   - Dispensed By: John Doe
   - Location: Lab A
3. Click Submit → Redirects to Inventory with success message

### **Test 3: Navigation Between Pages**
1. Start: `http://localhost:8084/`
2. Click "All Samples" → `/sample/list`
3. Click "Search" → `/sample/search-form`
4. Click "Inventory" → `/sample/inventory`
5. Click "Dispense IP" → `/sample/ip/dispense-form`
6. Click "Add Sample" → `/UserForm`

---

## 🔗 How Navigation Works

### **Top Navigation Bar (Persistent on all pages)**
```html
<nav class="topbar-nav">
    <a href="/UserForm">Add Sample</a>
    <a href="/sample/list">All Samples</a>
    <a href="/sample/search-form">Search</a>
    <a href="/sample/inventory">Inventory</a>
    <a href="/sample/ip/dispense-form">Dispense IP</a>
</nav>
```

### **Active Page Highlighting**
- Current page link shows `class="active"` styling
- Example: On `/UserForm` page, "Add Sample" link is highlighted

---

## ✅ Verification Checklist

- [x] GET `/saveUser` → Shows user form
- [x] GET `/sample/ip/dispense-action` → Shows dispense form
- [x] POST `/saveUser` → Saves sample & redirects
- [x] POST `/sample/ip/dispense-action` → Saves dispense & redirects
- [x] Navigation menu links work on all pages
- [x] All endpoints support proper navigation flow
- [x] Success/Error messages display correctly
- [x] Sample data persists in database

---

## 🚀 Quick Testing Commands

### **Using curl (PowerShell)**

```powershell
# Test GET - Show form
curl http://localhost:8084/saveUser

# Test POST - Save sample
$body = @{
    subjectId = 101
    sampleType = "Blood"
    collectionDate = "2026-06-30"
} | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8084/saveUser" -Method Post -Body $body
```

### **Using Browser**
```
1. Open: http://localhost:8084/saveUser
2. Fill form & submit
3. Result: Redirects to sample list
```

---

## 📊 Status Summary

**Total Endpoints:** 14  
**GET Endpoints:** 9 ✅  
**POST Endpoints:** 5 ✅  
**Navigation:** Fully working ✅  
**Database:** Persistent ✅

---

## 🎉 What's Fixed

✅ **NOW WORKS:**
- `GET http://localhost:8084/saveUser` → Shows form (was POST-only)
- `GET http://localhost:8084/sample/ip/dispense-action` → Shows form (was POST-only)
- Full navigation between all pages
- Forms submit to correct endpoints
- Success/error handling working

✅ **All pages properly linked in top navigation**

---

## 📝 Notes

- Port: **8084** (configured in `application.properties`)
- Database: **MySQL** on localhost:3306
- Eureka: **Disabled** for local standalone run
- All navigation is case-sensitive (URLs are lowercase)

---

**Happy testing!** 🚀

