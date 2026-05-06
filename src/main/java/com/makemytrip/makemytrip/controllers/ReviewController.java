package com.makemytrip.makemytrip.controllers;

import com.makemytrip.makemytrip.models.Review;
import com.makemytrip.makemytrip.models.Reply;
import com.makemytrip.makemytrip.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
@CrossOrigin(origins = {"http://localhost:3000", "https://makemytrip-frontend-newest.onrender.com"})
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addReview(@RequestBody Review review) {
        Map<String, Object> response = new HashMap<>();
        try {
            Review saved = reviewService.addReview(review);
            response.put("success", true);
            response.put("reviewId", saved.getReviewId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/target")
    public ResponseEntity<List<Review>> getReviewsByTarget(
            @RequestParam String type, 
            @RequestParam String id) {
        List<Review> reviews = reviewService.getReviewsByTarget(type, id);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    @PostMapping("/reply")
    public ResponseEntity<Reply> addReply(@RequestBody Reply reply) {
        return ResponseEntity.ok(reviewService.addReply(reply));
    }

    @GetMapping("/replies/{reviewId}")
    public ResponseEntity<List<Reply>> getReplies(@PathVariable String reviewId) {
        return ResponseEntity.ok(reviewService.getRepliesForReview(reviewId));
    }

    @PutMapping("/flag/{reviewId}")
    public ResponseEntity<Review> flagReview(@PathVariable String reviewId) {
        return ResponseEntity.ok(reviewService.flagReview(reviewId));
    }

    @GetMapping("/flagged")
    public ResponseEntity<List<Review>> getFlaggedReviews() {
        return ResponseEntity.ok(reviewService.getFlaggedReviews());
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable String reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok("Review deleted");
    }

    @PutMapping("/like/{reviewId}")
    public ResponseEntity<Review> likeReview(@PathVariable String reviewId) {
        return ResponseEntity.ok(reviewService.likeReview(reviewId));
    }

    @GetMapping("/average")
    public ResponseEntity<Double> getAverageRating(
            @RequestParam String type, 
            @RequestParam String id) {
        return ResponseEntity.ok(reviewService.getAverageRating(type, id));
    }
}

