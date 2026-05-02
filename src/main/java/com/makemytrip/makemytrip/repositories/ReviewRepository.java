package com.makemytrip.makemytrip.repositories;

import com.makemytrip.makemytrip.models.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByTargetTypeAndTargetId(String targetType, String targetId);
    List<Review> findByUserId(String userId);
    Optional<Review> findByUserIdAndBookingUniqueId(String userId, String bookingUniqueId);
    List<Review> findByFlaggedTrue();
}
