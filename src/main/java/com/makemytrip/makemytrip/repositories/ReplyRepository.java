package com.makemytrip.makemytrip.repositories;

import com.makemytrip.makemytrip.models.Reply;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends MongoRepository<Reply, String> {
    List<Reply> findByReviewId(String reviewId);
}
