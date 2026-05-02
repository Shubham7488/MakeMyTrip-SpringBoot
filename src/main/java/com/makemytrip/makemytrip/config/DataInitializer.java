package com.makemytrip.makemytrip.config;

import com.makemytrip.makemytrip.models.CancellationReason;
import com.makemytrip.makemytrip.repositories.CancellationReasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

/**
 * DataInitializer initializes database with default cancellation reasons
 * Runs on application startup
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CancellationReasonRepository cancellationReasonRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeCancellationReasons();
    }

    /**
     * Initialize default cancellation reasons if they don't exist
     */
    @SuppressWarnings("null")
    private void initializeCancellationReasons() {
        try {
            // Check if reasons already exist
            long count = cancellationReasonRepository.count();
            if (count > 0) {
                System.out.println("✅ Cancellation reasons already exist in database");
                return;
            }

            // Create default reasons
            List<CancellationReason> reasons = Arrays.asList(
                new CancellationReason(
                    null,
                    "Change of Plans",
                    "My travel plans have changed",
                    true
                ),
                new CancellationReason(
                    null,
                    "Found Better Price",
                    "Found a better deal elsewhere",
                    true
                ),
                new CancellationReason(
                    null,
                    "Schedule Conflict",
                    "There's a conflict with my schedule",
                    true
                ),
                new CancellationReason(
                    null,
                    "Medical Emergency",
                    "Medical emergency or health reasons",
                    true
                ),
                new CancellationReason(
                    null,
                    "Family Issue",
                    "Family issues or urgent matters",
                    true
                ),
                new CancellationReason(
                    null,
                    "Financial Reasons",
                    "Financial constraints",
                    true
                ),
                new CancellationReason(
                    null,
                    "Other",
                    "Other reason not listed above",
                    true
                )
            );

            // Save all reasons
            cancellationReasonRepository.saveAll(reasons);
            System.out.println("✅ Successfully initialized " + reasons.size() + " cancellation reasons");

        } catch (Exception e) {
            System.err.println("❌ Error initializing cancellation reasons: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
