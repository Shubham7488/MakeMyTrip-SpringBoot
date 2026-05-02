package com.makemytrip.makemytrip.repositories;

import com.makemytrip.makemytrip.models.CancellationReason;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CancellationReasonRepository extends MongoRepository<CancellationReason, String> {
    List<CancellationReason> findByActive(boolean active);
    CancellationReason findByReasonName(String reasonName);
}
