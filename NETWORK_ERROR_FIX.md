# 🔧 NETWORK ERROR FIX - Backend Connection Setup

## ❌ Problem: AxiosError: Network Error

The error you're seeing means the frontend **cannot connect to the backend API**. Here's the complete fix:

---

## ✅ SOLUTION

### Step 1: Ensure Backend is Running ⭐ MOST IMPORTANT

**Terminal 1: Start Java Backend**
```bash
mvn spring-boot:run
```

**Wait for these logs** (copy/paste to verify):
```
✓ The following profiles are active: 
✓ Tomcat started on port(s): 8080 (http) with context path ''
✓ ✅ Successfully initialized 7 cancellation reasons
✓ Started MakemytripApplication in X seconds
```

**If you see errors about MongoDB**:
```bash
# Make sure MongoDB is running in another terminal
mongod

# Or on Windows with MongoDB service:
net start MongoDB
```

---

### Step 2: Test Backend Connection

**Open New Browser Tab & Go To:**
```
http://localhost:8080/api/booking/cancel/reasons/active
```

**You should see JSON response**:
```json
{
  "success": true,
  "reasons": [
    {"_id": "1", "reasonName": "Change of Plans", ...},
    ...
  ]
}
```

**If you get "Cannot GET"**: Backend not running or endpoint not found
**If you get "Connection refused"**: Backend not running on port 8080
**If page blank/hangs**: Backend might be starting, wait 10 seconds

---

### Step 3: Start Frontend

**Terminal 2: Start Next.js Frontend**
```bash
cd makemytour
npm run dev
```

**Wait for:**
```
✓ Ready in 2.5s
```

---

### Step 4: Test in Browser

```
1. Go to: http://localhost:3000/profile
2. Click "Cancel Booking" on any booking
3. Dialog should open smoothly
4. Dropdown should show all 7 reasons
5. Open browser console (F12) to see logs
```

---

## 🔍 Troubleshooting: Step-by-Step

### Issue: Still Getting Network Error

**Step A: Check Backend**
```bash
# Terminal 1: Is backend running?
# Look for: "Started MakemytripApplication" message

# If not, restart:
# Press Ctrl+C to stop
# Then: mvn spring-boot:run
```

**Step B: Check MongoDB**
```bash
# Terminal 3: Is MongoDB running?
mongod

# Or check existing:
mongo
# If connected, type: exit
```

**Step C: Check Port 8080**
```bash
# Windows PowerShell:
netstat -ano | findstr :8080

# Should show process running
# If port in use, kill it or change Spring port
```

**Step D: Check Frontend Configuration**

Open file: `makemytour/next.config.ts`

Add this if not present:
```typescript
const nextConfig = {
  reactStrictMode: true,
  async rewrites() {
    return {
      fallback: [
        {
          source: '/api/:path*',
          destination: 'http://localhost:8080/api/:path*'
        }
      ]
    };
  }
};

export default nextConfig;
```

---

### Issue: Dropdown Shows But Says "No reasons available"

**This means**: API is running but not returning data

**Fix**:
```bash
# 1. Verify backend has data:
curl http://localhost:8080/api/booking/cancel/reasons/active

# 2. If empty, restart backend:
# Press Ctrl+C in Terminal 1
mvn spring-boot:run

# 3. Check logs for:
# "✅ Successfully initialized 7 cancellation reasons"
```

---

### Issue: Button Click Shows Error

**Check Browser Console (F12)**:
1. Go to browser DevTools
2. Click Console tab
3. Look for red error messages
4. Share the full error message

**Common errors**:
```
❌ "Cannot POST /api/booking/cancel/..."
   → Backend endpoint not implemented

❌ "Network Error"  
   → Backend not running

❌ "404 Not Found"
   → Wrong API path
```

---

## 📋 Complete Checklist

```
✅ MongoDB running?
   mongod (in terminal or service)

✅ Backend running?
   mvn spring-boot:run
   Check for: "Started MakemytripApplication"

✅ Backend has data?
   Visit: http://localhost:8080/api/booking/cancel/reasons/active
   Should show JSON with 7 reasons

✅ Frontend running?
   npm run dev (in makemytour folder)
   Check for: "Ready in Xs"

✅ Can access frontend?
   http://localhost:3000/profile
   Page loads without errors

✅ Can open dialog?
   Click "Cancel Booking" button
   Dialog appears

✅ Dropdown working?
   Click dropdown
   See all 7 reasons
   Can select one

✅ Can submit?
   Select reason
   Click "Confirm Cancellation"
   Should show loading spinner
   Then success message

✅ Console shows logs?
   F12 → Console tab
   See messages starting with "Fetching cancellation reasons..."
```

---

## 🚀 Quick Start Command

Run all three in separate terminals:

**Terminal 1**:
```bash
mongod
```

**Terminal 2**:
```bash
mvn spring-boot:run
```

**Terminal 3**:
```bash
cd makemytour && npm run dev
```

**Then in Browser**:
- `http://localhost:3000/profile`
- Click "Cancel Booking"
- Should work perfectly!

---

## 💡 Pro Tips

1. **Clear Browser Cache**: Ctrl+Shift+Delete, then Ctrl+F5
2. **Hard Refresh Frontend**: Ctrl+F5 (not just F5)
3. **Check Console Logs**: F12 → Console tab - shows detailed errors
4. **Check Network Tab**: F12 → Network tab - shows API requests
5. **Restart Everything**: If something weird happens, restart all 3 terminals

---

## 📊 How It Works

```
Browser                    Frontend (Next.js)           Backend (Spring Boot)
────────                   ──────────────────           ────────────────────
       │                          │                              │
       │─── Click Dialog ────────>│                              │
       │                          │                              │
       │                          │─── GET /api/booking/cancel/reasons/active ──>│
       │                          │                              │
       │                          │                              │ (MongoDB Query)
       │                          │                              │
       │                          │<─── JSON Response ──────────│
       │<─── Dialog with Reasons │                              │
       │                          │                              │
       │─── Select & Submit ────>│                              │
       │                          │─── POST /api/booking/cancel/{id} ──>│
       │                          │                              │
       │                          │                              │ (Create Refund)
       │                          │                              │ (Update Booking)
       │                          │<─── Success Response ─────────│
       │<─── Success Message ────│                              │
```

---

## ✅ Success Indicators

You know it's working when:

```
✅ No Network Error in browser
✅ Dropdown shows 7 reasons
✅ Can select a reason
✅ Button works (shows loading)
✅ Success message appears
✅ No red errors in console
✅ Network tab shows 200 responses (not 500)
```

---

## 🆘 Still Having Issues?

1. **Screenshot the error** (F12 → Console)
2. **Check all three terminals** are running
3. **Verify port 8080 is free** (netstat -ano | findstr :8080)
4. **Restart everything** - kill all terminals and restart
5. **Check MongoDB** - make sure it's running

---

**Everything should work now!** 🎉
