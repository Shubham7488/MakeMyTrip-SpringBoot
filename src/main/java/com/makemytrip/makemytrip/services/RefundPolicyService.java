package com.makemytrip.makemytrip.services;

import com.makemytrip.makemytrip.models.Refund;
import com.makemytrip.makemytrip.models.RefundPolicy;
import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.repositories.RefundPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * RefundPolicyService - Handles refund calculation and policy management
 * Logic: 50% refund if cancelled within 24 hours, 0% after 24 hours
 */
@Service
public class RefundPolicyService {
    
    @Autowired
    private RefundPolicyRepository refundPolicyRepository;

    /**
     * Calculate refund amount based on booking date and cancellation date
     * @param booking The booking to be cancelled
     * @param cancellationDate The date when cancellation is requested
     * @return Refund object with calculated amount
     */
    public Refund calculateRefund(Users.Booking booking, LocalDateTime cancellationDate) {
        // Parse booking date
        LocalDateTime bookingDateTime = LocalDateTime.parse(booking.getDate() + "T00:00:00");
        
        // Calculate hours between booking and cancellation
        long hoursElapsed = ChronoUnit.HOURS.between(bookingDateTime, cancellationDate);
        
        // Apply refund policy
        int refundPercentage = 0;
        if (hoursElapsed <= 24) {
            refundPercentage = 50; // 50% refund within 24 hours
        }
        // After 24 hours: 0% refund
        
        double refundAmount = (booking.getTotalPrice() * refundPercentage) / 100.0;
        
        Refund refund = new Refund();
        refund.setRefundPercentage(refundPercentage);
        refund.setRefundAmount(refundAmount);
        refund.setOriginalAmount(booking.getTotalPrice());
        
        return refund;
    }

    /**
     * Check if a booking is eligible for cancellation
     * @param booking The booking to check
     * @return true if can be cancelled, false otherwise
     */
    public boolean canBookingBeCancelled(Users.Booking booking) {
        // Check if booking is already cancelled
        if (booking.getType() == null) {
            return false;
        }
        
        // Check if booking date has not passed by more than 24 hours
        LocalDateTime bookingDateTime = LocalDateTime.parse(booking.getDate() + "T00:00:00");
        LocalDateTime now = LocalDateTime.now();
        
        long hoursBetween = ChronoUnit.HOURS.between(bookingDateTime, now);
        
        // Allow cancellation if within 7 days before booking (can adjust as needed)
        return hoursBetween < (7 * 24); // 7 days in hours
    }

    /**
     * Get all active refund policies
     * @return List of active refund policies
     */
    public List<RefundPolicy> getAllActivePolicies() {
        return refundPolicyRepository.findByActive(true);
    }

    /**
     * Initialize default refund policies if not exist
     */
    public void initializeDefaultPolicies() {
        // Check if policies already exist
        if (refundPolicyRepository.count() == 0) {
            // Create default policy: 50% within 24 hours
            RefundPolicy policy1 = new RefundPolicy(
                "Within 24 Hours",
                24,
                50
            );
            policy1.setDescription("Get 50% refund if cancelled within 24 hours of booking");
            
            // Create default policy: 0% after 24 hours
            RefundPolicy policy2 = new RefundPolicy(
                "After 24 Hours",
                Integer.MAX_VALUE,
                0
            );
            policy2.setDescription("No refund if cancelled after 24 hours of booking");
            
            refundPolicyRepository.save(policy1);
            refundPolicyRepository.save(policy2);
        }
    }

    /**
     * Calculate expected refund date (5 business days from now)
     * @return Expected refund date
     */
    public LocalDateTime calculateExpectedRefundDate() {
        // For now, return 5 business days from today
        // In production, calculate actual business days excluding weekends/holidays
        return LocalDateTime.now().plusDays(5);
    }
}
