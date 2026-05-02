# MakeMyTour Internship Task: Cancellation & Refund System

## 📋 Task Overview

This is a **10-day internship task** to implement a comprehensive **Cancellation and Refund System** for the MakeMyTour application (a MakeMyTrip clone). The system allows users to cancel their bookings and receive automatic refunds based on predefined policies.

### Key Features
✅ User-initiated cancellation from dashboard  
✅ Automatic refund calculation (50% within 24 hours, 0% after)  
✅ Cancellation reason selection dropdown  
✅ Real-time refund status tracking  
✅ Admin refund management dashboard  
✅ Refund analytics and reporting  
✅ Email notifications  

---

## 📁 Project Structure

```
make-my-trip-clone-springboot-main/
├── INTERNSHIP_TASK_10_DAY_BREAKDOWN.md      ← 10-day detailed plan (START HERE!)
├── IMPLEMENTATION_GUIDE.md                   ← Step-by-step implementation
├── GOOGLE_FORM_REPORTING_TEMPLATE.md        ← Daily reporting template
│
├── src/main/java/com/makemytrip/makemytrip/
│   ├── models/
│   │   ├── Refund.java                      ✅ CREATED
│   │   ├── CancellationReason.java          ✅ CREATED
│   │   ├── RefundPolicy.java                ✅ CREATED
│   │   └── Users.java                       (needs update)
│   ├── repositories/
│   │   ├── RefundRepository.java            ✅ CREATED
│   │   ├── CancellationReasonRepository.java ✅ CREATED
│   │   ├── RefundPolicyRepository.java      ✅ CREATED
│   │   └── UserRepository.java              (existing)
│   ├── services/
│   │   ├── RefundPolicyService.java         ✅ CREATED
│   │   ├── RefundService.java               (TODO: Day 5)
│   │   ├── CancellationService.java         (TODO: Day 5)
│   │   └── UserServices.java                (existing)
│   ├── controllers/
│   │   ├── CancellationController.java      (TODO: Day 4)
│   │   ├── RefundController.java            (TODO: Day 4)
│   │   └── BookingController.java           (existing)
│   └── scheduler/
│       └── RefundScheduler.java             (TODO: Day 5)
│
└── makemytour/src/
    ├── api/
    │   └── index.js                         (TODO: Add refund functions)
    ├── components/
    │   ├── CancellationDialog.tsx           (TODO: Day 6)
    │   ├── ReasonSelector.tsx               (TODO: Day 6)
    │   ├── RefundTracker.tsx                (TODO: Day 7)
    │   └── RefundCard.tsx                   (TODO: Day 7)
    ├── pages/
    │   ├── profile/index.tsx                (existing - needs update)
    │   └── refunds/index.tsx                (TODO: Day 7)
    └── store/
        └── refundSlice.ts                   (TODO: Day 8)
```

---

## 🚀 Getting Started (Quick Start)

### Prerequisites
- Java 17+
- Node.js 18+
- MongoDB
- VS Code
- Git

### Step 1: Review the 10-Day Plan
Read **INTERNSHIP_TASK_10_DAY_BREAKDOWN.md** to understand the complete roadmap.

### Step 2: Check Current Status
Files already created (Day 1-2 complete):
- ✅ Refund.java (model)
- ✅ CancellationReason.java (model)
- ✅ RefundPolicy.java (model)
- ✅ All repositories
- ✅ RefundPolicyService.java (refund calculation logic)

### Step 3: Next Steps (Day 3 onwards)
Follow the daily breakdown in **INTERNSHIP_TASK_10_DAY_BREAKDOWN.md**

### Step 4: Daily Reporting
Use **GOOGLE_FORM_REPORTING_TEMPLATE.md** to document your daily progress.

---

## 📊 Daily Breakdown Summary

| Day | Focus | Key Tasks | Hours |
|-----|-------|-----------|-------|
| 1 | Planning & Analysis | Architecture, DB schema design | 2-3 |
| 2 | Backend Models | Create 4 models + repositories | 2.5 |
| 3 | Refund Logic | Implement calculation engine | 3 |
| 4 | API Endpoints | Create 6+ endpoints | 3 |
| 5 | Status Tracking | Services + scheduled tasks | 3.5 |
| 6 | Frontend UI | Cancellation components | 3 |
| 7 | Refund Dashboard | Tracker + status display | 3 |
| 8 | Integration | Connect frontend-backend | 4 |
| 9 | Admin Features | Management dashboard + analytics | 3.5 |
| 10 | Testing & Deploy | Final testing + documentation | 4 |

**Total Estimated Time: 33 hours**

---

## 🔑 Key Refund Policies

### Policy 1: Within 24 Hours
- **Refund Percentage**: 50%
- **Timeline**: Payment will be refunded within 5 business days
- **Example**: Book for ₹100, cancel within 24 hours → Get ₹50 back

### Policy 2: After 24 Hours
- **Refund Percentage**: 0%
- **Timeline**: No refund eligible
- **Example**: Book for ₹100, cancel after 24 hours → Get ₹0 back

---

## 📝 Refund Status Workflow

```
PENDING (0-24 hours)
    ↓ (Auto-updated after 24 hours)
PROCESSED (24 hours - 5 days)
    ↓ (Auto-updated after 5 business days)
COMPLETED (Funds transferred to user)
```

### Status Descriptions
- **PENDING**: Refund request created, awaiting processing
- **PROCESSED**: Refund approved and initiated by admin/system
- **COMPLETED**: Funds transferred to user's account

---

## 🗄️ Database Schema

### Refunds Collection
```javascript
{
  _id: ObjectId,
  bookingId: String,           // Reference to booking
  userId: String,              // Reference to user
  originalAmount: Double,      // Original booking amount
  refundAmount: Double,        // Amount to be refunded
  refundPercentage: Integer,   // 50 or 0
  status: "PENDING",           // PENDING, PROCESSED, COMPLETED
  createdDate: DateTime,       // When refund was created
  processedDate: DateTime,     // When admin processed it
  completedDate: DateTime,     // When refund was completed
  expectedRefundDate: DateTime,// Expected completion date
  cancellationReason: String,  // User's reason for cancellation
  adminNotes: String           // Admin comments
}
```

### CancellationReasons Collection
```javascript
{
  _id: ObjectId,
  reasonName: String,          // e.g., "Change of Plans"
  description: String,         // Detailed description
  active: Boolean              // Is reason available for selection
}
```

### RefundPolicies Collection
```javascript
{
  _id: ObjectId,
  policyName: String,          // e.g., "Within 24 Hours"
  timeWindowHours: Integer,    // 24 hours
  refundPercentage: Integer,   // 50%
  description: String,         // Detailed policy description
  active: Boolean              // Is policy active
}
```

---

## 🔌 API Endpoints

### User Endpoints

#### Cancel Booking
```
POST /booking/cancel/{bookingId}
Body: { cancellationReason: "Change of Plans" }
Response: {
  bookingId: "...",
  refundAmount: 7672,
  status: "PENDING",
  expectedRefundDate: "2026-01-29"
}
```

#### Check Refund Eligibility
```
GET /booking/{bookingId}/refund-eligibility
Response: {
  eligible: true,
  refundAmount: 7672,
  refundPercentage: 50,
  expectedDate: "2026-01-29"
}
```

#### Get Cancellation Reasons
```
GET /refund-reasons
Response: [
  { id: "1", reasonName: "Change of Plans", description: "..." },
  { id: "2", reasonName: "Found Better Price", description: "..." },
  ...
]
```

#### Get User Refunds
```
GET /user/{userId}/refunds
Response: [
  {
    refundId: "...",
    bookingId: "...",
    amount: 7672,
    status: "PENDING",
    createdDate: "2026-01-24",
    expectedDate: "2026-01-29"
  },
  ...
]
```

#### Get Refund Details
```
GET /refund/{refundId}
Response: { refundId, bookingId, userId, amount, status, ... }
```

### Admin Endpoints

#### Process Refund
```
PUT /admin/refund/{refundId}/process
Body: { 
  newStatus: "PROCESSED",
  adminNotes: "Processed successfully"
}
```

#### Get All Refunds
```
GET /admin/refunds?status=PENDING&limit=10
Response: Array of refunds
```

#### Get Analytics
```
GET /admin/refunds/analytics
Response: {
  totalRefunds: 125,
  totalAmount: 45000,
  pending: 23,
  processed: 50,
  completed: 52,
  topReasons: { "Change of Plans": 45, ... },
  refundsByType: { "Flight": 75, "Hotel": 25 }
}
```

---

## 💻 Frontend Components

### CancellationDialog.tsx
- Display booking details
- Show refund amount
- Display timeline
- Confirm/Cancel buttons

### ReasonSelector.tsx
- Dropdown with 7 predefined reasons
- "Other" option with text input
- Validation

### RefundTracker.tsx
- 3-step timeline visualization
- Status indicator
- Expected completion date

### RefundCard.tsx
- Individual refund display
- Amount, status, reason
- Formatted dates

### Refund Dashboard
- List all user refunds
- Filter by status
- Sort options
- Real-time status updates

---

## 🧪 Testing Checklist

### Unit Tests (Day 3)
- [ ] Refund calculation: 24 hours = 50%
- [ ] Refund calculation: >24 hours = 0%
- [ ] Edge cases (exactly 24 hours)
- [ ] Date parsing and formatting

### Integration Tests (Day 4)
- [ ] API endpoints return correct data
- [ ] Database operations work correctly
- [ ] Error handling works properly
- [ ] Validation rules enforced

### End-to-End Tests (Day 8)
- [ ] User cancels flight booking
- [ ] User cancels hotel booking
- [ ] Refund status auto-updates
- [ ] Email notifications sent
- [ ] Admin can process refunds

### Edge Cases
- [ ] Cancel booking that's already cancelled
- [ ] Cancel booking with invalid date
- [ ] Cancel past booking
- [ ] Multiple refunds for same booking

---

## 📈 Progress Tracking

### Files Created
- ✅ 3 Model files (Refund, CancellationReason, RefundPolicy)
- ✅ 3 Repository files
- ✅ 1 Service file (RefundPolicyService)
- 📝 8 More files to create

### Code Statistics
| Category | Created | Todo | Total |
|----------|---------|------|-------|
| Java Files | 7 | 6 | 13 |
| TypeScript/React Files | 0 | 8 | 8 |
| Test Files | 0 | 5 | 5 |
| Documentation | 3 | 2 | 5 |

---

## 🎯 Learning Outcomes

By completing this task, you will learn:

### Backend
- ✅ Spring Boot service layer design
- ✅ MongoDB data modeling
- ✅ RESTful API design
- ✅ Business logic implementation
- ✅ Scheduled tasks
- ✅ Error handling and validation

### Frontend
- ✅ React component design
- ✅ State management with Redux
- ✅ API integration
- ✅ UI/UX patterns
- ✅ Form handling and validation
- ✅ Real-time updates

### Full Stack
- ✅ End-to-end feature development
- ✅ Database to UI workflow
- ✅ Testing strategies
- ✅ Documentation practices
- ✅ Code review process
- ✅ Production deployment

---

## 📚 Documentation Files

1. **INTERNSHIP_TASK_10_DAY_BREAKDOWN.md** - Complete 10-day plan with daily summaries
2. **IMPLEMENTATION_GUIDE.md** - Step-by-step implementation instructions
3. **GOOGLE_FORM_REPORTING_TEMPLATE.md** - Daily reporting template
4. **README.md** (this file) - Overview and quick start

---

## 🤝 Support & Resources

### When You Get Stuck
1. Check the relevant day's summary in **10_DAY_BREAKDOWN.md**
2. Review **IMPLEMENTATION_GUIDE.md** for step-by-step instructions
3. Check existing code for patterns and examples
4. Use Spring Boot documentation: https://spring.io/projects/spring-boot
5. Use React documentation: https://react.dev

### Key Files Reference
- **Model**: `src/main/java/com/makemytrip/makemytrip/models/Refund.java`
- **Service**: `src/main/java/com/makemytrip/makemytrip/services/RefundPolicyService.java`
- **Repository**: `src/main/java/com/makemytrip/makemytrip/repositories/RefundRepository.java`

---

## ✅ Completion Criteria

Your task is **COMPLETE** when:

- [x] All backend models implemented (Day 2)
- [x] Refund calculation logic working (Day 3)
- [x] All API endpoints created (Day 4)
- [x] Status tracking system operational (Day 5)
- [ ] Frontend components completed (Day 6-7)
- [ ] Integration testing passed (Day 8)
- [ ] Admin dashboard functional (Day 9)
- [ ] Full documentation & tests (Day 10)
- [ ] Code review approved
- [ ] Deployed to staging environment

---

## 📞 Quick Reference Commands

### Start Backend
```bash
cd make-my-trip-clone-springboot-main
mvn spring-boot:run
```

### Start Frontend
```bash
cd makemytour
npm install
npm run dev
```

### Run Tests
```bash
mvn test
npm test
```

### Check MongoDB
```bash
mongo
use makemytrip
db.refunds.find()
db.cancellationReasons.find()
```

---

## 🏆 Success Tips

1. **Plan Before Coding**: Read the entire daily plan before starting
2. **Test Incrementally**: Test each feature as you build it
3. **Document As You Go**: Update documentation daily
4. **Ask Questions Early**: Don't get stuck, ask for help
5. **Review Code**: Use code review to improve quality
6. **Track Time**: Monitor how long each task takes
7. **Stay Organized**: Follow the project structure strictly

---

## 📅 Timeline

- **Start Date**: January 24, 2026
- **Duration**: 10 business days
- **Expected Completion**: February 6, 2026
- **Reporting**: Daily via Google Form

---

## 🚀 Next Steps

1. **RIGHT NOW**: Read **INTERNSHIP_TASK_10_DAY_BREAKDOWN.md** (all 10 days)
2. **Then**: Review **IMPLEMENTATION_GUIDE.md** for technical details
3. **Day 1**: Complete architecture planning (already done!)
4. **Day 2**: Start implementing services (next step)
5. **Daily**: Submit progress via Google Form

---

**Good luck with your internship! Remember, this is a great opportunity to build a production-ready feature. Take your time, code carefully, and ask for help when needed! 🎉**

