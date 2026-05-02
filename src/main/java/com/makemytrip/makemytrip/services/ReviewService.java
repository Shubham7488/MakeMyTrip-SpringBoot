package com.makemytrip.makemytrip.services;

import com.makemytrip.makemytrip.models.Review;
import com.makemytrip.makemytrip.models.Reply;
import com.makemytrip.makemytrip.repositories.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.makemytrip.makemytrip.repositories.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.DoubleStream;

@Service
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReplyRepository replyRepository;

    public Review addReview(Review review) {
        // prevent duplicate review for same booking by same user (use unique booking id)
        String bookingUniqueId = review.getBookingUniqueId();
        logger.info("Attempting to add review - userId={}, bookingUniqueId={}", review.getUserId(), bookingUniqueId);
        Optional<Review> existing = reviewRepository.findByUserIdAndBookingUniqueId(review.getUserId(), bookingUniqueId);
        if (existing.isPresent()) {
            logger.warn("Duplicate review detected for user {} and bookingUniqueId {}", review.getUserId(), bookingUniqueId);
            throw new RuntimeException("User has already reviewed this booking");
        }
        review.setReviewId("RV-" + UUID.randomUUID().toString());
        review.setCreatedDate(LocalDateTime.now());
        review.setUpdatedDate(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByTarget(String targetType, String targetId) {
        return reviewRepository.findByTargetTypeAndTargetId(targetType, targetId);
    }

    public List<Review> getReviewsByUser(String userId) {
        return reviewRepository.findByUserId(userId);
    }

    public Reply addReply(Reply reply) {
        reply.setReplyId("RP-" + UUID.randomUUID().toString());
        reply.setCreatedDate(LocalDateTime.now());
        return replyRepository.save(reply);
    }

    public List<Reply> getRepliesForReview(String reviewId) {
        return replyRepository.findByReviewId(reviewId);
    }

    public Review flagReview(String reviewId) {
        Review r = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
        r.setFlagged(true);
        return reviewRepository.save(r);
    }

    public List<Review> getFlaggedReviews() {
        return reviewRepository.findByFlaggedTrue();
    }

    public void deleteReview(String reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public Review likeReview(String reviewId) {
        Review r = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
        r.setLikes(r.getLikes() + 1);
        return reviewRepository.save(r);
    }

    // compute average rating for a target
    public double getAverageRating(String targetType, String targetId) {
        List<Review> list = reviewRepository.findByTargetTypeAndTargetId(targetType, targetId);
        if (list.isEmpty()) return 0.0;
        double sum = list.stream().mapToInt(Review::getRating).sum();
        return sum / list.size();
    }
}
