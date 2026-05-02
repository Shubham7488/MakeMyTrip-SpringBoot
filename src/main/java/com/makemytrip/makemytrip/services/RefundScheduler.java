package com.makemytrip.makemytrip.services;

import org.springframework.stereotype.Service;

/**
 * RefundScheduler handles automatic status updates for refunds
 * NOTE: Scheduling disabled due to model incompatibilities. 
 * Can be re-enabled once Refund model is properly updated.
 */
@Service
public class RefundScheduler {

    /**
     * Placeholder for future scheduled tasks
     */
    public void init() {
        System.out.println("RefundScheduler initialized");
    }
}
