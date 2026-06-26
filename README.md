# Mana Community Liquibase

Database migration management for the **Mana Community** platform. Uses [Liquibase](https://www.liquibase.org/) with Spring Boot to version-control the PostgreSQL schema.

## Schema Overview

| Domain | Tables |
|---|---|
| **Users & Community** | `app_user`, `community`, `user_profile`, `roles`, `role_permissions` |
| **Sports Events** | `sports_event`, `sports_event_registration`, `tournament`, `sports_meta`, `player_category`, `event_category` |
| **Venues** | `venue`, `court` |
| **Auction System** | `auction_config`, `auction_team`, `auction_player`, `auction_bid`, `auction_config_category`, `auction_dispute_committee`, `auction_session_log` |
| **Tournament Scheduler** | `tournament_config`, `tournament_group`, `group_team_standing`, `tournament_match`, `schedule_generation_log` |
| **Notifications** | `notification`, `sports_notification_scheduler`, `sports_event_sponsor` |
| **Feed** | `post`, `post_like`, `post_comment` |
| **Chat** | `chat_conversation`, `chat_participant`, `chat_message` |
| **Auth & Audit** | `email_otp`, `user_sessions`, `audit_log` |

## Changelog Structure

```
src/main/resources/db/changelog/
  db.changelog-master.xml          # Master index
  v1.0.0/
    01_initial_schema.xml          # Core tables, FKs, indexes, constraints
    02_tournament_scheduler.xml    # Tournament config, groups, standings, matches
    03_audit_log.xml               # Audit trail table + indexes
    04_user_sessions.xml           # Login session tracking + indexes
    05_sports_event_auction_enabled.xml  # auction_enabled column
    06_auction_optimistic_locking.xml    # @Version columns on auction tables
```

## Quick Start

### Run migrations via Spring Boot
```bash
# Using environment variables
DB_HOST=localhost DB_PORT=5432 DB_NAME=postgres DB_SCHEMA=manacommunity \
DB_USER=postgres DB_PASS=changeme \
mvn spring-boot:run

# Using local profile
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Run migrations via Maven plugin
```bash
mvn liquibase:update -Plocal

# Dry-run (preview SQL)
mvn liquibase:updateSQL -Plocal

# Check pending changesets
mvn liquibase:status -Plocal

# Validate all changesets
mvn liquibase:validate -Plocal
```

## Requirements

- Java 17+
- PostgreSQL 14+
- Maven 3.8+
