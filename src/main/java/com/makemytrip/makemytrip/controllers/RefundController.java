package com.makemytrip.makemytrip.controllers;

import com.makemytrip.makemytrip.models.Refund;
import com.makemytrip.makemytrip.services.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

/**
 * RefundController handles all refund-related operations
 */
@RestController
@RequestMapping("/api/refund")
public class RefundController {

    @Autowired
    private RefundService refundService;

    /**
     * Get all refunds for a user
     *
     * @param userId The user ID
     * @return List of user's refunds
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserRefunds(@PathVariable String userId) {
        try {
            List<Refund> refunds = refundService.getUserRefunds(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", userId);
            response.put("count", refunds.size());
            response.put("refunds", refunds);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get a specific refund by ID
     *
     * @param refundId The refund ID
     * @return Refund details
     */
    @GetMapping("/{refundId}")
    public ResponseEntity<?> getRefundById(@PathVariable String refundId) {
        try {
            Optional<Refund> refund = refundService.getRefundById(refundId);

            if (refund.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("refund", refund.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Refund not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get all refunds with a specific status
     *
     * @param status The refund status (PENDING, PROCESSED, COMPLETED)
     * @return List of refunds with given status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getRefundsByStatus(@PathVariable String status) {
        try {
            Refund.RefundStatus refundStatus = Refund.RefundStatus.valueOf(status.toUpperCase());
            List<Refund> refunds = refundService.getRefundsByStatus(refundStatus);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("status", status);
            response.put("count", refunds.size());
            response.put("refunds", refunds);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Invalid status: " + status);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get all refunds (admin endpoint)
     *
     * @return List of all refunds
     */
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllRefunds() {
        try {
            List<Refund> refunds = refundService.getAllRefunds();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalCount", refunds.size());
            response.put("refunds", refunds);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Update refund status (admin endpoint)
     *
     * @param refundId The refund ID
     * @param newStatus The new status
     * @param adminNotes Optional admin notes
     * @return Updated refund
     */
    @PutMapping("/{refundId}/admin/process")
    public ResponseEntity<?> updateRefundStatus(
            @PathVariable String refundId,
            @RequestParam String newStatus,
            @RequestParam(required = false) String adminNotes) {
        try {
            Refund.RefundStatus status = Refund.RefundStatus.valueOf(newStatus.toUpperCase());
            Refund updatedRefund = refundService.updateRefundStatus(refundId, status, adminNotes);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Refund status updated");
            response.put("refund", updatedRefund);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Invalid status: " + newStatus);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get refunds for a specific booking
     *
     * @param bookingId The booking ID
     * @return List of refunds for the booking
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getRefundsByBookingId(@PathVariable String bookingId) {
        try {
            List<Refund> refunds = refundService.getRefundsByBookingId(bookingId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("bookingId", bookingId);
            response.put("count", refunds.size());
            response.put("refunds", refunds);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get refund statistics (admin endpoint)
     *
     * @return Refund statistics
     */
    @GetMapping("/admin/statistics")
    public ResponseEntity<?> getRefundStatistics() {
        try {
            List<Refund> allRefunds = refundService.getAllRefunds();

            long pendingCount = allRefunds.stream()
                    .filter(r -> r.getStatus() == Refund.RefundStatus.PENDING)
                    .count();

            long processedCount = allRefunds.stream()
                    .filter(r -> r.getStatus() == Refund.RefundStatus.PROCESSED)
                    .count();

            long completedCount = allRefunds.stream()
                    .filter(r -> r.getStatus() == Refund.RefundStatus.COMPLETED)
                    .count();

            double totalRefundAmount = allRefunds.stream()
                    .mapToDouble(Refund::getRefundAmount)
                    .sum();

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("total", allRefunds.size());
            statistics.put("pending", pendingCount);
            statistics.put("processed", processedCount);
            statistics.put("completed", completedCount);
            statistics.put("totalRefundAmount", totalRefundAmount);
            statistics.put("averageRefundAmount", allRefunds.isEmpty() ? 0 : totalRefundAmount / allRefunds.size());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("statistics", statistics);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Delete a refund (admin only)
     *
     * @param refundId The refund ID
     * @return Success response
     */
    @DeleteMapping("/{refundId}/admin/delete")
    public ResponseEntity<?> deleteRefund(@PathVariable String refundId) {
        try {
            refundService.deleteRefund(refundId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Refund deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
