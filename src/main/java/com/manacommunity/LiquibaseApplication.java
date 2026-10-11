package com.manacommunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Mana Community — Enterprise Database Migration Service
 *
 * <p>Three-layer Liquibase migration runner for a multi-tenant Community Operating System.</p>
 *
 * <p>Layer 1 — Platform: schemas, master data, audit, config, notifications</p>
 * <p>Layer 2 — Domain:   auth, sports, events</p>
 * <p>Layer 3 — Releases: versioned migration bundles</p>
 *
 * <p>Run with Maven profiles:</p>
 * <pre>
 *   mvn liquibase:update -Plocal
 *   mvn liquibase:update -Pdev
 *   mvn liquibase:update -Pprod -Ddb.password=${SECRET}
 * </pre>
 */
@SpringBootApplication
public class LiquibaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiquibaseApplication.class, args);
    }
}
