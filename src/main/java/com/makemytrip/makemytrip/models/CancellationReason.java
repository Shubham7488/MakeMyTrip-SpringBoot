package com.makemytrip.makemytrip.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * CancellationReason Model - Predefined cancellation reasons for tracking
 */
@Document(collection = "cancellationReasons")
public class CancellationReason {
    @Id
    private String _id;
    private String reasonName;
    private String description;
    private boolean active;

    // Constructors
    public CancellationReason() {
        this.active = true;
    }

    public CancellationReason(String reasonName, String description) {
        this.reasonName = reasonName;
        this.description = description;
        this.active = true;
    }

    public CancellationReason(String _id, String reasonName, String description, boolean active) {
        this._id = _id;
        this.reasonName = reasonName;
        this.description = description;
        this.active = active;
    }

    // Getters and Setters
    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getReasonName() {
        return reasonName;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
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
