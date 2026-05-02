package com.makemytrip.makemytrip.controllers;

import com.makemytrip.makemytrip.repositories.CancellationReasonRepository;
import com.makemytrip.makemytrip.services.BookingService;
import com.makemytrip.makemytrip.models.CancellationReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

/**
 * CancellationController handles all cancellation operations
 */
@RestController
@RequestMapping("/api/booking")
public class CancellationController {

    @Autowired
    private CancellationReasonRepository cancellationReasonRepository;

    @Autowired
    private BookingService bookingService;

    /**
     * Cancel a booking and initiate refund process
     */
    @PostMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(
            @PathVariable String bookingId,
            @RequestParam String userId,
            @RequestParam String cancellationReasonId,
            @RequestParam(required = false) String adminNotes) {
        try {
            // Log incoming parameters for diagnostics
            System.out.println("[CancellationController] Received cancel request -> bookingId: '" + bookingId + "', userId: '" + userId + "', cancellationReasonId: '" + cancellationReasonId + "', adminNotes: '" + adminNotes + "'");

            // Basic validation
            if (bookingId == null || bookingId.trim().isEmpty() || "undefined".equalsIgnoreCase(bookingId) || "null".equalsIgnoreCase(bookingId)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Missing or invalid bookingId");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            if (userId == null || userId.trim().isEmpty() || "undefined".equalsIgnoreCase(userId) || "null".equalsIgnoreCase(userId)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Missing or invalid userId");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            if (cancellationReasonId == null || cancellationReasonId.trim().isEmpty() || "undefined".equalsIgnoreCase(cancellationReasonId) || "null".equalsIgnoreCase(cancellationReasonId)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Missing or invalid cancellationReasonId");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Call the booking service
            Map<String, Object> response = bookingService.cancelBooking(bookingId, userId, cancellationReasonId, adminNotes);

            // Log result for diagnostics
            System.out.println("[CancellationController] cancelBooking result -> " + response);

            if ((Boolean) response.getOrDefault("success", false)) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            System.err.println("[CancellationController] Exception: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Check if a booking can be cancelled and get refund eligibility
     */
    @GetMapping("/{bookingId}/refund-eligibility")
    public ResponseEntity<?> checkRefundEligibility(@PathVariable String bookingId, @RequestParam String userId) {
        try {
            Map<String, Object> response = bookingService.getRefundEligibility(bookingId, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/cancel/reasons/all")
    public ResponseEntity<?> getCancellationReasons() {
        try {
            List<CancellationReason> reasons = cancellationReasonRepository.findAll();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", reasons.size());
            response.put("reasons", reasons);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/cancel/reasons/active")
    public ResponseEntity<?> getActiveCancellationReasons() {
        try {
            List<CancellationReason> reasons = cancellationReasonRepository.findAll();
            List<CancellationReason> activeReasons = reasons.stream()
                    .filter(CancellationReason::isActive)
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", activeReasons.size());
            response.put("reasons", activeReasons);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/cancel/reasons/{reasonId}")
    @SuppressWarnings("null")
    public ResponseEntity<?> getCancellationReason(@PathVariable String reasonId) {
        try {
            Optional<CancellationReason> reason = cancellationReasonRepository.findById(reasonId);

            if (reason.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("reason", reason.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Reason not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/cancel/reasons")
    @SuppressWarnings("null")
    public ResponseEntity<?> createCancellationReason(@RequestBody CancellationReason reason) {
        try {
            CancellationReason savedReason = cancellationReasonRepository.save(reason);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cancellation reason created");
            response.put("reason", savedReason);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

}
