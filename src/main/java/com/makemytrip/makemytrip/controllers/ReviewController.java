package com.makemytrip.makemytrip.controllers;

import com.makemytrip.makemytrip.dto.ReviewDTOs.ReplyRequest;
import com.makemytrip.makemytrip.dto.ReviewDTOs.ReviewRequest;
import com.makemytrip.makemytrip.models.Reply;
import com.makemytrip.makemytrip.models.Review;
import com.makemytrip.makemytrip.services.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody ReviewRequest req) {
        try {
            if (req.rating < 1 || req.rating > 5) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "error", "Rating must be between 1 and 5"));
            }
            Review r = new Review();
            r.setUserId(req.userId);
            // Accept either bookingUniqueId (preferred) or bookingId (legacy). Prefer bookingUniqueId when provided.
            String incomingBookingUnique = req.bookingUniqueId != null && !req.bookingUniqueId.isBlank() ? req.bookingUniqueId : req.bookingId;
            r.setBookingUniqueId(incomingBookingUnique);
            logger.info("Received addReview request - userId={}, bookingId(request)={}, bookingUniqueId(request)={}", req.userId, req.bookingId, req.bookingUniqueId);
            r.setTargetType(req.targetType);
            r.setTargetId(req.targetId);
            r.setRating(req.rating);
            r.setReviewText(req.reviewText);
            r.setImages(req.images != null ? req.images : List.of());

            Review saved = reviewService.addReview(r);
            return ResponseEntity.ok(Map.of("success", true, "review", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/target/{type}/{id}")
    public ResponseEntity<?> getReviewsByTarget(@PathVariable String type, @PathVariable String id) {
        List<Review> list = reviewService.getReviewsByTarget(type.toUpperCase(), id);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("count", list.size());
        res.put("reviews", list);
        res.put("averageRating", reviewService.getAverageRating(type.toUpperCase(), id));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/reply/add")
    public ResponseEntity<?> addReply(@RequestBody ReplyRequest req) {
        try {
            Reply rep = new Reply();
            rep.setReviewId(req.reviewId);
            rep.setUserId(req.userId);
            rep.setText(req.text);
            Reply saved = reviewService.addReply(rep);
            return ResponseEntity.ok(Map.of("success", true, "reply", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/flag/{reviewId}")
    public ResponseEntity<?> flagReview(@PathVariable String reviewId) {
        try {
            Review r = reviewService.flagReview(reviewId);
            return ResponseEntity.ok(Map.of("success", true, "review", r));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/admin/flagged")
    public ResponseEntity<?> getFlagged() {
        List<Review> list = reviewService.getFlaggedReviews();
        return ResponseEntity.ok(Map.of("success", true, "count", list.size(), "reviews", list));
    }

    @DeleteMapping("/admin/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable String reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/like/{reviewId}")
    public ResponseEntity<?> likeReview(@PathVariable String reviewId) {
        try {
            Review r = reviewService.likeReview(reviewId);
            return ResponseEntity.ok(Map.of("success", true, "review", r));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
