package com.manacommunity.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Enterprise Liquibase Configuration.
 *
 * <p>Configures the Liquibase bean with environment-aware settings.
 * Supports multi-context execution (default, dev, qa, prod) and
 * schema-per-tenant migrations via Liquibase properties binding.</p>
 *
 * <p>Key Features:
 * <ul>
 *   <li>Liquibase metadata stored in dedicated {@code liquibase_meta} schema</li>
 *   <li>Context-driven changeset filtering per environment</li>
 *   <li>Structured changelog parameters injected at runtime</li>
 *   <li>Configurable via {@code application.yml} + environment variables</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
public class LiquibaseConfig {

    @Value("${spring.liquibase.change-log:classpath:db/changelog/db.changelog-root.yaml}")
    private String changeLogFile;

    @Value("${spring.liquibase.contexts:default}")
    private String contexts;

    @Value("${spring.liquibase.labels:}")
    private String labels;

    @Value("${spring.liquibase.liquibase-schema:liquibase_meta}")
    private String liquibaseSchema;

    @Value("${spring.liquibase.default-schema:platform_master}")
    private String defaultSchema;

    @Value("${spring.liquibase.enabled:true}")
    private boolean liquibaseEnabled;

    /**
     * Primary Liquibase bean that runs all three layers of migrations.
     *
     * @param dataSource injected by Spring Boot auto-configuration
     * @return configured SpringLiquibase bean
     */
    @Bean
    @Primary
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLogFile);
        liquibase.setContexts(contexts);
        liquibase.setLabels(labels);
        liquibase.setLiquibaseSchema(liquibaseSchema);
        liquibase.setDefaultSchema(defaultSchema);
        liquibase.setShouldRun(liquibaseEnabled);
        liquibase.setDropFirst(false);

        // Runtime changelog parameters — available in YAML as ${param}
        liquibase.setChangeLogParameters(Map.of(
            "platform.schema",      "platform_master",
            "audit.schema",         "platform_audit",
            "config.schema",        "platform_config",
            "notification.schema",  "platform_notification",
            "workflow.schema",      "platform_workflow",
            "auth.schema",          "auth",
            "sports.schema",        "sports",
            "events.schema",        "events"
        ));

        return liquibase;
    }
}
