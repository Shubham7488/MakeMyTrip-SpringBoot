# Complete Code Changes Reference

## 1. Frontend - Profile Page
**File**: `makemytour/src/pages/profile/index.tsx`

### Change 1: Fixed booking removal filter
**Location**: `handleCancellationSuccess` function

```typescript
// OLD CODE (INCORRECT):
const handleCancellationSuccess = () => {
  if (selectedBooking) {
    dispatch(setUser({
      ...user,
      bookings: user.bookings.filter((b: any) => b.bookingId !== selectedBooking.bookingId)
    }));
  }
  setRefreshBookings(refreshBookings + 1);
};

// NEW CODE (CORRECT):
const handleCancellationSuccess = () => {
  // Remove the canceled booking from the user's bookings (use unique ID field)
  if (selectedBooking) {
    dispatch(setUser({
      ...user,
      bookings: user.bookings.filter((b: any) => b.id !== selectedBooking.id)
    }));
  }
  setRefreshBookings(refreshBookings + 1);
};
```

---

## 2. Frontend - Cancellation Dialog Component
**File**: `makemytour/src/components/CancellationDialog.tsx`

### Change 1: Fixed booking ID extraction
**Location**: `handleCancellation` function

```typescript
// OLD CODE (INCORRECT):
const uniqueBookingId = booking?.id || booking?.bookingId || booking?._id;

// NEW CODE (CORRECT):
// CRITICAL: Use the unique booking ID field (booking.id)
// NOT the reference ID (booking.bookingId which is the flight/hotel ID)
const uniqueBookingId = booking?.id;
```

### Change 2: Improved error validation
**Location**: `handleCancellation` function, after ID extraction

```typescript
// OLD CODE:
if (!uniqueBookingId) {
  setError('Booking ID is missing. Please try again.');
  setLoading(false);
  return;
}

if (!userId) {
  setError('User ID is missing. Please log in again.');
  setLoading(false);
  return;
}

// NEW CODE:
if (!uniqueBookingId) {
  console.error('❌ Missing unique booking ID:', booking);
  setError('Booking ID is missing. Cannot cancel this booking.');
  setLoading(false);
  return;
}

if (!userId) {
  console.error('❌ Missing user ID');
  setError('User ID is missing. Please log in again.');
  setLoading(false);
  return;
}
```

### Change 3: Fixed URL construction with proper encoding
**Location**: `handleCancellation` function, URL construction

```typescript
// OLD CODE:
const cancelUrl = `/api/booking/cancel/${uniqueBookingId}?userId=${encodeURIComponent(userId)}&cancellationReasonId=${encodeURIComponent(selectedReason)}&adminNotes=${encodeURIComponent(additionalNotes || '')}`;

// NEW CODE:
const cancelUrl = `/api/booking/cancel/${encodeURIComponent(uniqueBookingId)}?userId=${encodeURIComponent(userId)}&cancellationReasonId=${encodeURIComponent(selectedReason)}&adminNotes=${encodeURIComponent(additionalNotes || '')}`;
```

---

## 3. Backend - Users.Booking Model
**File**: `src/main/java/com/makemytrip/makemytrip/models/Users.java`

### Status: ✅ NO CHANGES NEEDED
This is already correctly implemented:

```java
public static class Booking {
    private String id;  // Unique booking ID - AUTO-GENERATED UUID
    private String type;
    private String bookingId;  // Reference to Flight/Hotel
    private String date;
    private int quantity;
    private double totalPrice;

    // Constructor to generate unique ID
    public Booking() {
        this.id = java.util.UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    // ... other getters/setters
}
```

---

## 4. Backend - Cancellation Controller
**File**: `src/main/java/com/makemytrip/makemytrip/controllers/CancellationController.java`

### Status: ✅ NO CHANGES NEEDED
This is already correctly implemented:

```java
@PostMapping("/cancel/{bookingId}")
public ResponseEntity<?> cancelBooking(
        @PathVariable String bookingId,
        @RequestParam String userId,
        @RequestParam String cancellationReasonId,
        @RequestParam(required = false) String adminNotes) {
    try {
        // Validate required parameters
        if (bookingId == null || bookingId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("success", false, "error", "Missing bookingId")
            );
        }

        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("success", false, "error", "Missing userId")
            );
        }

        if (cancellationReasonId == null || cancellationReasonId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("success", false, "error", "Missing cancellationReasonId")
            );
        }

        // Call booking service with validated parameters
        Map<String, Object> response = bookingService.cancelBooking(
            bookingId, userId, cancellationReasonId, adminNotes
        );

        if ((Boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            Map.of("success", false, "error", "Internal server error: " + e.getMessage())
        );
    }
}
```

---

## 5. Backend - Booking Service
**File**: `src/main/java/com/makemytrip/makemytrip/services/BookingService.java`

### Status: ✅ NO CHANGES NEEDED
This is already correctly implemented:

```java
public Map<String, Object> cancelBooking(String bookingId, String userId, 
                                          String reasonId, String notes) {
    Optional<Users> usersOptional = userRepository.findById(userId);
    Map<String, Object> response = new HashMap<>();

    if (usersOptional.isPresent()) {
        Users user = usersOptional.get();
        
        // CRITICAL: Search by unique ID field (booking.id), NOT reference (booking.bookingId)
        Booking booking = user.getBookings().stream()
                .filter(b -> b.getId().equals(bookingId))  // ✅ Uses unique ID
                .findFirst()
                .orElse(null);

        if (booking != null) {
            // Calculate refund based on booking date
            LocalDate bookingDate = LocalDate.parse(booking.getDate());
            LocalDate today = LocalDate.now();
            long daysDiff = ChronoUnit.DAYS.between(bookingDate, today);
            
            double refundPercentage = daysDiff <= 1 ? 50.0 : 0.0;
            double refundAmount = booking.getTotalPrice() * (refundPercentage / 100.0);

            // Remove booking
            user.getBookings().remove(booking);
            userRepository.save(user);

            // Restore availability
            if ("Flight".equals(booking.getType())) {
                Optional<Flight> flight = flightRepository.findById(booking.getBookingId());
                flight.ifPresent(f -> {
                    f.setAvailableSeats(f.getAvailableSeats() + booking.getQuantity());
                    flightRepository.save(f);
                });
            } else if ("Hotel".equals(booking.getType())) {
                Optional<Hotel> hotel = hotelRepository.findById(booking.getBookingId());
                hotel.ifPresent(h -> {
                    h.setAvailableRooms(h.getAvailableRooms() + booking.getQuantity());
                    hotelRepository.save(h);
                });
            }

            response.put("success", true);
            response.put("refundId", "REF-" + System.currentTimeMillis());
            response.put("refundAmount", refundAmount);
            response.put("refundPercentage", refundPercentage);
            response.put("message", "Booking cancelled successfully");
            return response;
        }
    }

    response.put("success", false);
    response.put("error", "Booking not found");
    return response;
}
```

---

## 6. Backend - Security Configuration
**File**: `src/main/java/com/makemytrip/makemytrip/config/SecurityConfig.java`

### Status: ✅ NO CHANGES NEEDED
CORS and security are properly configured:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("http://localhost:3000", "http://localhost:3001", 
                                   "http://127.0.0.1:3000", "http://127.0.0.1:3001")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            );
        return http.build();
    }
}
```

---

## Summary of Changes

| File | Changes | Status |
|------|---------|--------|
| `profile/index.tsx` | Fixed booking filter to use unique ID | ✅ UPDATED |
| `CancellationDialog.tsx` | Fixed ID extraction and validation | ✅ UPDATED |
| `Users.java` (Booking class) | Verified UUID generation | ✅ CORRECT |
| `CancellationController.java` | Verified parameter validation | ✅ CORRECT |
| `BookingService.java` | Verified unique ID filter logic | ✅ CORRECT |
| `SecurityConfig.java` | Verified CORS and security | ✅ CORRECT |

---

## Testing Checklist

After applying these changes:

- [ ] Backend is running on port 8082
- [ ] Frontend is running on port 3000
- [ ] Can log in successfully
- [ ] Profile page loads with bookings
- [ ] Click "Cancel Booking" opens dialog
- [ ] Dialog shows all cancellation reasons
- [ ] Submit cancellation returns success response
- [ ] Booking disappears from profile list
- [ ] No 400 errors in browser console
- [ ] Backend logs show successful processing

---

**Implementation Date**: February 2, 2026
**Last Updated**: February 2, 2026
