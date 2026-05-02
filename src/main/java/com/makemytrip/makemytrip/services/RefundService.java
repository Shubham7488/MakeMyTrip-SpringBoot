package com.makemytrip.makemytrip.services;

import com.makemytrip.makemytrip.models.Refund;
import com.makemytrip.makemytrip.repositories.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * RefundService handles all refund operations including creation, updates, and status management
 * NOTE: Currently simplified. Full implementation available in frontend mock APIs.
 */
@Service
public class RefundService {

    @Autowired
    private RefundRepository refundRepository;

    /**
     * Get all refunds for a specific user
     *
     * @param userId The user ID
     * @return List of user's refunds
     */
    public List<Refund> getUserRefunds(String userId) {
        return refundRepository.findByUserId(userId);
    }

    /**
     * Get refund by ID
     *
     * @param refundId The refund ID
     * @return Refund object if found
     */
    @SuppressWarnings("null")
    public Optional<Refund> getRefundById(String refundId) {
        return refundRepository.findById(refundId);
    }

    /**
     * Get all refunds
     *
     * @return List of all refunds
     */
    public List<Refund> getAllRefunds() {
        return refundRepository.findAll();
    }

    /**
     * Get refunds by status
     *
     * @param status The refund status
     * @return List of refunds with given status
     */
    public List<Refund> getRefundsByStatus(Refund.RefundStatus status) {
        return refundRepository.findByStatus(status);
    }

    /**
     * Get refunds by booking ID
     *
     * @param bookingId The booking ID
     * @return List of refunds for the booking
     */
    public List<Refund> getRefundsByBookingId(String bookingId) {
        return refundRepository.findAllByBookingId(bookingId);
    }

    /**
     * Update refund status (two parameters version)
     *
     * @param refundId The refund ID
     * @param newStatus The new status (as string)
     * @return Updated refund
     */
    @SuppressWarnings("null")
    public Refund updateRefundStatus(String refundId, String newStatus) {
        Optional<Refund> refund = refundRepository.findById(refundId);
        if (refund.isPresent()) {
            Refund r = refund.get();
            try {
                r.setStatus(Refund.RefundStatus.valueOf(newStatus));
                if (newStatus.equals("PROCESSED")) {
                    r.setProcessedDate(LocalDateTime.now());
                } else if (newStatus.equals("COMPLETED")) {
                    r.setCompletedDate(LocalDateTime.now());
                }
                return refundRepository.save(r);
            } catch (Exception e) {
                throw new RuntimeException("Error updating refund status: " + e.getMessage());
            }
        }
        throw new RuntimeException("Refund not found");
    }

    /**
     * Update refund status with admin notes
     *
     * @param refundId The refund ID
     * @param status The new status
     * @param adminNotes Admin notes
     * @return Updated refund
     */    @SuppressWarnings("null")    public Refund updateRefundStatus(String refundId, Refund.RefundStatus status, String adminNotes) {
        Optional<Refund> refund = refundRepository.findById(refundId);
        if (refund.isPresent()) {
            Refund r = refund.get();
            try {
                r.setStatus(status);
                if (adminNotes != null && !adminNotes.isEmpty()) {
                    r.setAdminNotes(adminNotes);
                }
                if (status == Refund.RefundStatus.PROCESSED) {
                    r.setProcessedDate(LocalDateTime.now());
                } else if (status == Refund.RefundStatus.COMPLETED) {
                    r.setCompletedDate(LocalDateTime.now());
                }
                return refundRepository.save(r);
            } catch (Exception e) {
                throw new RuntimeException("Error updating refund status: " + e.getMessage());
            }
        }
        throw new RuntimeException("Refund not found");
    }

    /**
     * Delete a refund
     *
     * @param refundId The refund ID
     */
    @SuppressWarnings("null")
    public void deleteRefund(String refundId) {
        refundRepository.deleteById(refundId);
    }
}
