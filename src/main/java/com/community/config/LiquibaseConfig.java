// ================================================================
//  CommUnity App — Liquibase Spring Boot Configuration
// ================================================================

// ── LiquibaseConfig.java ─────────────────────────────────────────
package com.community.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Value("${spring.liquibase.change-log}")
    private String changeLog;

    @Value("${spring.liquibase.contexts:default}")
    private String contexts;

    /**
     * Production bean — auto-run migrations on startup.
     * ddl-auto = validate ensures Hibernate only checks,
     * never modifies, the schema that Liquibase owns.
     */
    @Bean
    @Profile("!test")
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setContexts(contexts);
        liquibase.setShouldRun(true);
        liquibase.setDropFirst(false);
        liquibase.setDefaultSchema("public");
        return liquibase;
    }

    /**
     * Test bean — disable Liquibase when using H2 or test slices.
     * Tests manage their own schema via @Sql or TestContainers.
     */
    @Bean
    @Profile("test")
    public SpringLiquibase liquibaseTest(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setShouldRun(false);
        return liquibase;
    }
}


// ================================================================
//  pom.xml — Required Maven Dependencies
// ================================================================

/*
<dependencies>

    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Liquibase Core — version managed by Spring Boot BOM -->
    <dependency>
        <groupId>org.liquibase</groupId>
        <artifactId>liquibase-core</artifactId>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Testcontainers (PostgreSQL) for integration tests -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>

</dependencies>

<!-- Liquibase Maven Plugin — run migrations from CLI -->
<plugin>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-maven-plugin</artifactId>
    <version>4.27.0</version>
    <configuration>
        <changeLogFile>
            src/main/resources/db/changelog/db.changelog-master.xml
        </changeLogFile>
        <url>jdbc:postgresql://localhost:5432/community_db</url>
        <username>${db.username}</username>
        <password>${db.password}</password>
        <driver>org.postgresql.Driver</driver>
    </configuration>
</plugin>
*/


// ================================================================
//  USEFUL LIQUIBASE CLI COMMANDS
// ================================================================

/*
  -- Run all pending migrations
  mvn liquibase:update

  -- Check what migrations would run (dry-run)
  mvn liquibase:updateSQL

  -- Roll back last N changesets
  mvn liquibase:rollback -Dliquibase.rollbackCount=1

  -- Tag the current DB state (useful before a release)
  mvn liquibase:tag -Dliquibase.tag=v1.0.0

  -- Roll back to a tag
  mvn liquibase:rollback -Dliquibase.rollbackTag=v1.0.0

  -- View pending changesets
  mvn liquibase:status

  -- Generate changelog from existing DB (useful for legacy migration)
  mvn liquibase:generateChangeLog

  -- Validate all changesets
  mvn liquibase:validate

  -- Check db diff
  mvn liquibase:diff

  -- Clear all checksums (if changeset content was changed accidentally)
  mvn liquibase:clearCheckSums
*/



