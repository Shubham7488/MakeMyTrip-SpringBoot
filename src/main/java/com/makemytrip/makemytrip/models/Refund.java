package com.makemytrip.makemytrip.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/**
 * Refund Model - Tracks all refunds initiated by users
 * Supports status tracking: PENDING -> PROCESSED -> COMPLETED
 */
@Document(collection = "refunds")
public class Refund {
    @Id
    private String _id;
    private String bookingId;
    private String bookingType;
    private String refundId;
    private String userId;
    private double originalAmount;
    private double refundAmount;
    private double refundPercentage;
    private RefundStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime processedDate;
    private LocalDateTime completedDate;
    private LocalDateTime expectedRefundDate;
    private String cancellationReason;
    private String adminNotes;

    public enum RefundStatus {
        PENDING,      // Refund request created
        PROCESSED,    // Refund approved by admin
        COMPLETED     // Refund transferred to user
    }

    // Constructors
    public Refund() {
        this.status = RefundStatus.PENDING;
        this.createdDate = LocalDateTime.now();
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    // Getters and Setters
    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public int getRefundPercentage() {
        return (int)refundPercentage;
    }

    public void setRefundPercentage(double refundPercentage) {
        this.refundPercentage = refundPercentage;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(LocalDateTime processedDate) {
        this.processedDate = processedDate;
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }

    public LocalDateTime getExpectedRefundDate() {
        return expectedRefundDate;
    }

    public void setExpectedRefundDate(LocalDateTime expectedRefundDate) {
        this.expectedRefundDate = expectedRefundDate;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
}
