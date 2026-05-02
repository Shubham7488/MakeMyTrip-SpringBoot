package com.makemytrip.makemytrip.repositories;

import com.makemytrip.makemytrip.models.Refund;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends MongoRepository<Refund, String> {
    List<Refund> findByUserId(String userId);
    Optional<Refund> findByBookingId(String bookingId);
    List<Refund> findByUserIdAndStatus(String userId, Refund.RefundStatus status);
    List<Refund> findByStatus(Refund.RefundStatus status);
    List<Refund> findAllByBookingId(String bookingId);
}
