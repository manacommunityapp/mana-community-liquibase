# Mana Community — Enterprise Liquibase Migration Service

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen?logo=spring)](https://spring.io/projects/spring-boot)
[![Liquibase](https://img.shields.io/badge/Liquibase-4.27.0-blue)](https://www.liquibase.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16%2B-blue?logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue?logo=docker)](https://docker.com)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-Job-326CE5?logo=kubernetes)](https://kubernetes.io/)

> Enterprise-grade, multi-tenant database migration service for the Mana Community Operating System.
> Supports **schema-per-tenant** isolation, **zero-downtime deployments**, and full **CI/CD** integration.

---

## Architecture: Three-Layer Migration System

```
┌───────────────────────────────────────────────────────────────────┐
│  LAYER 3 — Release Layer                                          │
│  db/changelog/releases/  (v1.0, v2.0, ...)                       │
│  Versioned bundles, performance patches, release markers          │
├───────────────────────────────────────────────────────────────────┤
│  LAYER 2 — Domain Layer                                           │
│  db/changelog/domains/auth/      ← users, sessions, OTP, KYC     │
│  db/changelog/domains/sports/    ← venues, tournaments, auction   │
│  db/changelog/domains/events/    ← events, registrations, results │
├───────────────────────────────────────────────────────────────────┤
│  LAYER 1 — Platform Layer                                         │
│  db/changelog/platform/          ← schemas, enums, master data    │
│                                    audit, config, functions       │
└───────────────────────────────────────────────────────────────────┘
```

---

## Folder Structure

```
mana-community-liquibase/
├── pom.xml                                   Java 21 + Spring Boot 3.3.5 + Liquibase 4.27
├── docker/
│   ├── Dockerfile                            Multi-stage Java 21 build
│   ├── docker-compose.yml                    Local dev + PostgreSQL
│   └── docker-compose.prod.yml               Production override
├── k8s/
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── secret.yaml
│   └── job-liquibase-migration.yaml          Kubernetes Job for migrations
├── .github/workflows/
│   └── liquibase-ci.yml                      GitHub Actions CI/CD pipeline
└── src/main/
    ├── java/com/manacommunity/
    │   ├── LiquibaseApplication.java
    │   └── config/LiquibaseConfig.java
    └── resources/
        ├── application.yml                   Base config
        ├── application-local.yml             Developer workstation
        ├── application-dev.yml               Dev server
        ├── application-qa.yml                QA / test
        ├── application-uat.yml               UAT
        ├── application-prod.yml              AWS RDS / production
        └── db/changelog/
            ├── db.changelog-root.yaml        ROOT: orchestrates all 3 layers
            ├── platform/                     LAYER 1
            │   ├── db.changelog-platform.yaml
            │   ├── schema/
            │   │   ├── 202501010001-create-schemas.yaml
            │   │   └── 202501010002-create-enum-types.yaml
            │   ├── master-data/
            │   │   ├── 202501010010-countries.yaml
            │   │   ├── 202501010016-roles.yaml
            │   │   ├── 202501010017-permissions.yaml
            │   │   └── 202501010020-sports-categories.yaml
            │   ├── audit/
            │   │   └── 202501010030-audit-log-table.yaml
            │   ├── notification/
            │   │   └── 202501010040-notification-templates.yaml
            │   ├── configuration/
            │   │   ├── 202501010050-tenant-config.yaml
            │   │   ├── 202501010051-system-settings.yaml
            │   │   └── 202501010052-feature-flags-table.yaml
            │   ├── shared-functions/
            │   │   ├── 202501010060-fn-updated-at-trigger.yaml
            │   │   └── 202501010061-fn-soft-delete.yaml
            │   └── shared-views/
            │       └── 202501010070-vw-tenant-summary.yaml
            ├── domains/                      LAYER 2
            │   ├── auth/
            │   │   ├── db.changelog-auth.yaml
            │   │   ├── schema/, tables/, indexes/, functions/, seed/
            │   │   └── [15 migration files]
            │   ├── sports/
            │   │   ├── db.changelog-sports.yaml
            │   │   ├── schema/, tables/, indexes/, views/, seed/
            │   │   └── [18 migration files]
            │   └── events/
            │       ├── db.changelog-events.yaml
            │       ├── schema/, tables/, indexes/, views/, seed/
            │       └── [12 migration files]
            └── releases/                     LAYER 3
                ├── db.changelog-releases.yaml
                └── v1.0/
                    ├── 202507290001-v1.0-baseline.yaml
                    └── 202507290002-v1.0-performance.yaml
```

---

## Schemas Created

| Schema | Purpose | Owner |
|--------|---------|-------|
| `platform_master` | Countries, roles, permissions, tenants, sports categories | Platform |
| `platform_audit` | Enterprise audit log (immutable) | Platform |
| `platform_config` | System settings, feature flags | Platform |
| `platform_notification` | Notification templates & logs | Platform |
| `platform_workflow` | Workflow definitions & instances | Platform |
| `platform_document` | Document metadata & file refs | Platform |
| `platform_ai` | AI model configs & inference logs | Platform |
| `platform_analytics` | Analytics aggregations | Platform |
| `liquibase_meta` | Liquibase tracking tables | Liquibase |
| `auth` | Users, sessions, devices, OTP, KYC | Auth Domain |
| `sports` | Venues, teams, tournaments, auction | Sports Domain |
| `events` | Events, registrations, results | Events Domain |

---

## Quick Start

### 1. Local Development

```bash
# Start PostgreSQL
docker-compose -f docker/docker-compose.yml up db -d

# Run migrations (local profile)
mvn liquibase:update -Plocal

# Check migration status
mvn liquibase:status -Plocal
```

### 2. Docker Compose (full stack)

```bash
docker-compose -f docker/docker-compose.yml up --build
```

### 3. Validate Changelog

```bash
mvn liquibase:validate -Plocal
```

### 4. Rollback (last N changesets)

```bash
# Rollback last 3 changesets
mvn liquibase:rollback -Plocal -Dliquibase.rollbackCount=3

# Rollback to a specific tag
mvn liquibase:rollbackToTag -Plocal -Dliquibase.rollbackTag=v1.0
```

### 5. Generate Diff (compare local vs dev)

```bash
mvn liquibase:diff -Pdev
```

---

## Multi-Environment Usage

```bash
# Developer workstation
mvn liquibase:update -Plocal

# Development server
mvn liquibase:update -Pdev \
  -DDEV_DB_HOST=dev-db.internal \
  -DDEV_DB_USER=manacommunity \
  -DDEV_DB_PASS=secret

# QA
mvn liquibase:update -Pqa

# UAT
mvn liquibase:update -Puat

# Production (AWS RDS)
mvn liquibase:update -Pprod \
  -DRDS_ENDPOINT=mana-prod.rds.amazonaws.com \
  -DDB_USER=manacommunity \
  -DDB_PASS=${AWS_SECRET}
```

---

## Changeset Standards

Every changeset follows this structure:

```yaml
- changeSet:
    id: "YYYYMMDDNNNN-description"          # Timestamp-based unique ID
    author: "mana-community-dev"            # Author tag
    labels: "domain,category,version"       # Comma-separated labels
    context: "default"                      # default | dev | qa | prod
    comment: "Human-readable description"   # What this changeset does
    logicalFilePath: "db/changelog/..."     # Full path for tracking
    runInTransaction: false                 # false for DDL on PostgreSQL
    preConditions:
      - onFail: MARK_RAN                    # Skip if already done
        onError: CONTINUE
    changes:
      - ...                                 # The actual change
    rollback:
      - ...                                 # ALWAYS include rollback
```

---

## Table Standards

Every table includes these **11 standard audit columns**:

| Column | Type | Default | Purpose |
|--------|------|---------|---------|
| `status` | VARCHAR(20) | `ACTIVE` | Record lifecycle status |
| `created_at` | TIMESTAMP | `CURRENT_TIMESTAMP` | Creation time |
| `created_by` | BIGINT | `0` | Creator user ID |
| `updated_at` | TIMESTAMP | `CURRENT_TIMESTAMP` | Last update time |
| `updated_by` | BIGINT | `0` | Updater user ID |
| `deleted` | BOOLEAN | `false` | Soft delete flag |
| `deleted_at` | TIMESTAMP | NULL | Soft delete timestamp |
| `deleted_by` | BIGINT | NULL | Who deleted this record |
| `version` | INTEGER | `0` | Optimistic locking version |

---

## Context Strategy

| Context | When it runs |
|---------|-------------|
| `default` | Always — runs in every environment |
| `local` | Local developer workstation only |
| `dev` | Dev + local environments |
| `qa` | QA test environment only |
| `uat` | UAT environment only |
| `prod` | Production-only migrations |

---

## Kubernetes Deployment

```bash
# Apply all K8s resources
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml   # Edit secrets first!
kubectl apply -f k8s/job-liquibase-migration.yaml

# Watch migration job
kubectl logs -n manacommunity-db-migration -l app=liquibase-migration -f

# Delete job after completion
kubectl delete job liquibase-migration-job -n manacommunity-db-migration
```

---

## CI/CD Pipeline (GitHub Actions)

The workflow at `.github/workflows/liquibase-ci.yml` provides:

| Job | Trigger | Action |
|-----|---------|--------|
| `validate` | Every PR & push | Validates changelog syntax |
| `diff` | PR to develop | Shows pending changes |
| `migrate-dev` | Push to `develop` | Auto-applies to dev DB |
| `migrate-prod` | Push to `main` + manual approval | Applies to production |
| `rollback` | `workflow_dispatch` | Rolls back N changesets |

---

## Domain Tables Summary

### Auth Domain (12 tables)
`users` · `user_profiles` · `user_roles` · `user_sessions` · `user_devices` · `refresh_tokens` · `otp_verifications` · `password_history` · `login_attempts` · `kyc_documents`

### Sports Domain (15 tables)
`sports_meta` · `venues` · `courts` · `teams` · `players` · `tournaments` · `tournament_config` · `tournament_groups` · `tournament_group_teams` · `tournament_matches` · `match_standings` · `auction_config` · `auction_teams` · `auction_bids`

### Events Domain (8 tables)
`events` · `event_categories` · `event_registrations` · `event_sponsors` · `event_schedules` · `event_notifications` · `event_results`

### Platform Layer (10+ tables)
`tenants` · `system_settings` · `feature_flags` · `countries` · `roles` · `permissions` · `role_permissions` · `sports_categories` · `audit_log` · `notification_templates` · `notification_log`

---

## License

Proprietary — Mana Community Pvt Ltd © 2025
