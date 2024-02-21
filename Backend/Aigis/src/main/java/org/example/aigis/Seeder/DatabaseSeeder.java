package org.example.aigis.Seeder;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder {
    private final AdminSeeder adminSeeder;

    private final Logger logger;


    private boolean alreadySeeded = false;

    @EventListener
    public void seed(ContextRefreshedEvent ignored) {
        if (alreadySeeded) {
            return;
        }

        logger.info("Starting database seed.");

        this.adminSeeder.seed();

        this.alreadySeeded = true;
    }
}
