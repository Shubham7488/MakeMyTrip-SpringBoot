# Quick Setup & Testing Guide

## ⚡ Quick Start (5 minutes)

### Prerequisites
- Java 21+ installed
- Node.js 18+ installed
- MongoDB Atlas cluster configured
- Both frontend and backend built

---

## 🚀 Step 1: Start Backend (Port 8082)

### Option A: Using JAR (Recommended)
```bash
cd c:\Users\shubh\OneDrive\Desktop\Internship of makemytrip\make-my-trip-clone-springboot-main
java -jar target\makemytrip-0.0.1-SNAPSHOT.jar
```

**Expected Output:**
```
Started MakemytripApplication in XX.XXX seconds
Tomcat started on port 8082
```

### Option B: Using Maven
```bash
.\mvnw.cmd spring-boot:run
```

⚠️ **Note**: Maven may have issues with devtools. Use JAR method if you encounter problems.

---

## 🚀 Step 2: Start Frontend (Port 3000)

### In a new terminal:
```bash
cd makemytour
npm install  # Only if packages not installed
npm run dev
```

**Expected Output:**
```
> next dev
- ready started server on 0.0.0.0:3000
- event compiled client and server successfully
```

---

## 🧪 Step 3: Test Booking Cancellation

### Open Browser
```
http://localhost:3000
```

### Step-by-step Testing
1. **Log In**
   - Enter email/password
   - Click "Login"
   - Wait for profile page to load

2. **Navigate to Profile**
   - Click "Profile" in navigation (or go to `/profile`)
   - You should see "My Bookings" section

3. **Cancel a Booking**
   - Find a booking in the list
   - Click "Cancel Booking" button (red trash icon)
   - A dialog should appear

4. **Complete Cancellation**
   - Dialog shows booking details
   - Select a cancellation reason from dropdown
   - Optionally add notes in textarea
   - Review refund amount shown
   - Click "Confirm Cancellation"

5. **Verify Success**
   - Success message appears: "✓ Cancellation successful!"
   - Dialog closes after 2 seconds
   - Booking disappears from the list
   - Profit! 🎉

---

## 🔍 Troubleshooting

### Issue: 400 Bad Request Error

**Diagnosis:**
1. Open DevTools (F12) → Console tab
2. Look for "🔍 Cancellation validation" log
3. Check if `uniqueBookingId` is visible and is a UUID format

**Solution:**
- Verify booking object has `id` field (not just `bookingId`)
- Check Redux state shows correct `userId`
- Ensure backend is running on port 8082

---

### Issue: Booking Doesn't Disappear from List

**Diagnosis:**
1. Open DevTools → Console
2. Check for Redux dispatch logs
3. Verify response shows `"success": true`

**Solution:**
- Hard refresh page (Ctrl+Shift+R)
- Check that filter uses `b.id` not `b.bookingId`
- Verify user is still logged in

---

### Issue: Backend Won't Start

**Diagnosis:**
```bash
# Check if Java is running
tasklist | findstr java

# If yes, kill it
taskkill /F /IM java.exe
```

**Solution:**
1. Kill existing Java processes
2. Wait 3 seconds
3. Rebuild JAR: `mvnw.cmd clean package -DskipTests`
4. Start with: `java -jar target\makemytrip-0.0.1-SNAPSHOT.jar`

---

### Issue: "Port 3000 already in use"

**Solution:**
```bash
# Kill Node process using port 3000
lsof -ti:3000 | xargs kill -9  # Mac/Linux
netstat -ano | findstr :3000   # Windows (find PID)
taskkill /PID <PID> /F         # Windows (kill by PID)
```

---

## 📊 Check Backend Health

### Verify Backend is Running
```bash
# In terminal/PowerShell:
curl http://localhost:8082/api/booking/cancel/reasons/all
```

**Expected Response:**
```json
{
  "success": true,
  "count": 7,
  "reasons": [...]
}
```

### Check Logs in Terminal
Look for these key messages:
- ✅ "Tomcat started on port 8082"
- ✅ "Found 6 MongoDB repository interfaces"
- ✅ "Discovered replica set primary"

---

## 🔐 Verify Connectivity

### Frontend → Backend Connectivity
1. Open DevTools → Network tab
2. Click "Cancel Booking"
3. Look for request to `http://localhost:8082/api/booking/cancel/...`
4. Should return 200 (success) or 400 (with error details)

### Backend → MongoDB Connectivity
Look in backend console for:
```
MongoClient with metadata...
Discovered replica set primary...
```

---

## 📝 API Endpoints Reference

### Cancellation Endpoints

**Cancel Booking**
```
POST /api/booking/cancel/{bookingId}
?userId=<userId>
&cancellationReasonId=<reasonId>
&adminNotes=<notes>
```

**Get Cancellation Reasons**
```
GET /api/booking/cancel/reasons/all
```

**Check Refund Eligibility**
```
GET /api/booking/{bookingId}/refund-eligibility?userId=<userId>
```

---

## 🎯 Expected Results

### Success Scenario
```
1. User clicks "Cancel Booking"
2. Dialog opens showing:
   - Booking details (date, price, type)
   - Cancellation reason dropdown
   - Refund eligibility/amount
   - Notes textarea

3. User selects reason and submits
4. Backend processes:
   - Validates all parameters ✓
   - Finds booking by unique ID ✓
   - Calculates refund (50% if within 24h) ✓
   - Removes booking from user ✓
   - Restores flight seats or hotel rooms ✓

5. Frontend receives:
   - success: true
   - refundId: "REF-..."
   - refundAmount: 2500.00
   - refundPercentage: 50.0

6. Dialog shows success message
7. Booking removed from list after 2 seconds
```

### Error Scenarios
```
Missing ID Fields:
- Error: "Booking ID is missing"
- Error: "User ID is missing"
- Error: "Missing cancellationReasonId"

Booking Not Found:
- Returns: 400 with "Booking not found"
- Likely cause: Using wrong ID field

Server Error:
- Returns: 500 with "Internal server error"
- Check backend console for details
```

---

## 📞 Quick Reference Commands

```bash
# Build backend JAR
mvnw.cmd clean package -DskipTests

# Start backend
java -jar target\makemytrip-0.0.1-SNAPSHOT.jar

# Start frontend
cd makemytour && npm run dev

# Kill Java process
taskkill /F /IM java.exe

# Check backend health
curl http://localhost:8082/api/booking/cancel/reasons/all

# View file changes
git diff src/main/java/com/makemytrip/makemytrip/models/Users.java
```

---

## ✅ Final Checklist

- [ ] Backend JAR built successfully
- [ ] Backend running on http://localhost:8082
- [ ] Frontend running on http://localhost:3000
- [ ] MongoDB Atlas connected (check backend logs)
- [ ] Can log in successfully
- [ ] Profile page displays bookings
- [ ] Cancel booking dialog appears
- [ ] Cancellation processes without 400 error
- [ ] Refund amount displays correctly
- [ ] Booking removed from list after cancellation

---

**If everything works → 🎉 Feature is complete and ready!**

**If issues persist → Check BOOKING_CANCELLATION_FIX.md for detailed troubleshooting**

---

Last Updated: February 2, 2026
