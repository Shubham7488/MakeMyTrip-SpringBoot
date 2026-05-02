package com.makemytrip.makemytrip.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * RefundPolicy Model - Defines refund percentages based on time windows
 * Example: 50% refund if cancelled within 24 hours
 */
@Document(collection = "refundPolicies")
public class RefundPolicy {
    @Id
    private String _id;
    private String policyName;
    private int timeWindowHours;  // e.g., 24 for 24 hours
    private int refundPercentage; // e.g., 50 for 50%
    private String description;
    private boolean active;

    // Constructors
    public RefundPolicy() {
        this.active = true;
    }

    public RefundPolicy(String policyName, int timeWindowHours, int refundPercentage) {
        this.policyName = policyName;
        this.timeWindowHours = timeWindowHours;
        this.refundPercentage = refundPercentage;
        this.active = true;
    }

    // Getters and Setters
    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public int getTimeWindowHours() {
        return timeWindowHours;
    }

    public void setTimeWindowHours(int timeWindowHours) {
        this.timeWindowHours = timeWindowHours;
    }

    public int getRefundPercentage() {
        return refundPercentage;
    }

    public void setRefundPercentage(int refundPercentage) {
        this.refundPercentage = refundPercentage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
