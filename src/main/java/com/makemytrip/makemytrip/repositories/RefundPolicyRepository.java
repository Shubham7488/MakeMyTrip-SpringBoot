package com.makemytrip.makemytrip.repositories;

import com.makemytrip.makemytrip.models.RefundPolicy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundPolicyRepository extends MongoRepository<RefundPolicy, String> {
    List<RefundPolicy> findByActive(boolean active);
    RefundPolicy findByPolicyName(String policyName);
}
