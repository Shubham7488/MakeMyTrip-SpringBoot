# Cancellation & Refund System - Implementation Guide

## Overview
This guide provides step-by-step instructions for implementing the cancellation and refund system in the MakeMyTour project.

## Phase 1: Backend Implementation (Days 1-5)

### Step 1: Update Booking Model
Add these fields to `Users.java` Booking inner class:
```java
private String cancellationReason;
private LocalDateTime cancellationDate;
private double refundAmount;
private String bookingStatus; // ACTIVE, CANCELLED, REFUNDED
```

### Step 2: Create Repositories
Already created:
- ✅ RefundRepository.java
- ✅ CancellationReasonRepository.java
- ✅ RefundPolicyRepository.java

### Step 3: Create Services
Next to create:
1. RefundService - Main service for refund operations
2. CancellationService - Handle booking cancellations

### Step 4: Create Controllers
1. CancellationController - User cancellation endpoints
2. RefundController - Refund tracking endpoints
3. AdminRefundController - Admin management endpoints

### Step 5: Create Scheduled Tasks
1. RefundScheduler - Auto-update refund status

## Phase 2: Frontend Implementation (Days 6-8)

### Step 1: Create Components
1. CancellationDialog.tsx - Dialog for cancellation
2. ReasonSelector.tsx - Dropdown for reasons
3. RefundTracker.tsx - Status tracking dashboard
4. RefundCard.tsx - Individual refund display

### Step 2: Update Existing Components
1. Update BookingCard.tsx - Add cancel button
2. Update profile/index.tsx - Add refunds tab

### Step 3: API Integration
1. Create API functions in src/api/index.js
2. Add Redux slices for refund state
3. Implement error handling

### Step 4: Testing
1. End-to-end testing
2. Edge case testing
3. Performance testing

## Phase 3: Admin Dashboard (Day 9)

### Features to Add
1. Refund management dashboard
2. Refund processing workflow
3. Analytics and reporting
4. Export functionality

## Phase 4: Testing & Deployment (Day 10)

### Testing Checklist
- [ ] Unit tests for refund calculation
- [ ] Integration tests for API endpoints
- [ ] End-to-end user flow tests
- [ ] Admin workflow tests
- [ ] Edge case testing
- [ ] Performance testing

### Deployment Checklist
- [ ] Code review
- [ ] All tests passing
- [ ] Documentation complete
- [ ] Environment variables set
- [ ] Database backup created
- [ ] Monitoring configured

## Code Structure

```
src/main/java/com/makemytrip/makemytrip/
├── models/
│   ├── Refund.java ✅
│   ├── CancellationReason.java ✅
│   └── RefundPolicy.java ✅
├── repositories/
│   ├── RefundRepository.java ✅
│   ├── CancellationReasonRepository.java ✅
│   └── RefundPolicyRepository.java ✅
├── services/
│   ├── RefundPolicyService.java ✅
│   ├── RefundService.java (TODO)
│   └── CancellationService.java (TODO)
├── controllers/
│   ├── CancellationController.java (TODO)
│   ├── RefundController.java (TODO)
│   └── AdminRefundController.java (TODO)
└── scheduler/
    └── RefundScheduler.java (TODO)

makemytour/src/
├── api/
│   └── index.js (TODO: Add refund functions)
├── components/
│   ├── CancellationDialog.tsx (TODO)
│   ├── ReasonSelector.tsx (TODO)
│   ├── RefundTracker.tsx (TODO)
│   └── RefundCard.tsx (TODO)
├── pages/
│   └── refunds/
│       └── index.tsx (TODO)
└── store/
    └── refundSlice.ts (TODO)
```

## API Endpoints

### User Endpoints
```
POST   /booking/cancel/{bookingId}
GET    /booking/{bookingId}/refund-eligibility
GET    /refund-reasons
GET    /user/{userId}/refunds
GET    /refund/{refundId}
```

### Admin Endpoints
```
PUT    /admin/refund/{refundId}/process
PUT    /admin/refund/{refundId}/status
GET    /admin/refunds
GET    /admin/refunds/analytics
```

## Database Collections

### Refunds Collection
```json
{
  "_id": "ObjectId",
  "bookingId": "String",
  "userId": "String",
  "originalAmount": 15344,
  "refundAmount": 7672,
  "refundPercentage": 50,
  "status": "PENDING",
  "createdDate": "2026-01-24T10:30:00",
  "processedDate": null,
  "completedDate": null,
  "expectedRefundDate": "2026-01-29T10:30:00",
  "cancellationReason": "Change of Plans",
  "adminNotes": ""
}
```

### CancellationReasons Collection
```json
{
  "_id": "ObjectId",
  "reasonName": "Change of Plans",
  "description": "User changed their travel plans",
  "active": true
}
```

### RefundPolicies Collection
```json
{
  "_id": "ObjectId",
  "policyName": "Within 24 Hours",
  "timeWindowHours": 24,
  "refundPercentage": 50,
  "description": "Get 50% refund if cancelled within 24 hours",
  "active": true
}
```

## Testing Examples

### Test Case 1: Cancel Flight Within 24 Hours
```
1. User books flight for ₹15,344
2. User cancels within 24 hours
3. Expected refund: 50% = ₹7,672
4. Refund status: PENDING
5. After 24 hours: Status changes to PROCESSED
6. After 5 days: Status changes to COMPLETED
```

### Test Case 2: Cancel Hotel After 24 Hours
```
1. User books hotel for ₹489
2. User cancels after 24 hours
3. Expected refund: 0% = ₹0
4. Show message: "No refund applicable"
```

### Test Case 3: Admin Processing
```
1. Refund in PENDING status
2. Admin clicks "Process Refund"
3. Refund status changes to PROCESSED
4. User receives notification
5. Refund status auto-updates to COMPLETED after 5 days
```

## Important Dates to Remember

- Refund window: 24 hours from booking
- Processing time: Auto-processed after 24 hours
- Completion time: 5 business days (configurable)
- Refund percentage within 24 hours: 50%
- Refund percentage after 24 hours: 0%

## Troubleshooting

### Issue: Refund calculation incorrect
- Check booking date format (should be YYYY-MM-DD)
- Check LocalDateTime parsing
- Verify timezone handling

### Issue: Scheduled task not running
- Ensure @EnableScheduling is added to main application class
- Check cron expression syntax
- Verify database connection

### Issue: Frontend not showing refunds
- Check Redux state initialization
- Verify API endpoint returns correct data
- Check browser console for errors

## Next Steps

1. Complete backend service implementation (Day 3-4)
2. Create API controllers (Day 4)
3. Implement scheduled tasks (Day 5)
4. Build frontend components (Day 6-7)
5. Integration testing (Day 8)
6. Admin dashboard (Day 9)
7. Final testing and deployment (Day 10)

## References

- [Booking Model Structure](../models/Users.java)
- [Refund Model](../models/Refund.java)
- [Refund Policy Service](../services/RefundPolicyService.java)
