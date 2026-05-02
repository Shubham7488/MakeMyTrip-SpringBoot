package com.makemytrip.makemytrip.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewDTOs {
    public static class ReviewRequest {
        public String userId;
        // Accept either bookingId (legacy) or bookingUniqueId (preferred)
        public String bookingId;
        public String bookingUniqueId;
        public String targetType;
        public String targetId;
        public int rating;
        public String reviewText;
        public List<String> images;
    }

    public static class ReviewResponse {
        public String reviewId;
        public String userId;
        public String bookingId;
        public String bookingUniqueId;
        public String targetType;
        public String targetId;
        public int rating;
        public String reviewText;
        public List<String> images;
        public LocalDateTime createdDate;
        public LocalDateTime updatedDate;
        public int likes;
        public boolean flagged;
    }

    public static class ReplyRequest {
        public String reviewId;
        public String userId;
        public String text;
    }
}
