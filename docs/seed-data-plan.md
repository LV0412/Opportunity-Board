# Demo seed data plan

## Overview

Add a new Flyway migration after V7 instead of editing previously applied migrations. Seed a verified admin account, a verified organization account/profile, and realistic opportunities for public discovery and admin moderation testing.

## Steps

1. Add `V8__seed_demo_users_and_opportunities.sql` with deterministic UUIDs and BCrypt passwords.
2. Insert eight opportunities across the existing reference categories, including approved and pending records with future deadlines.
3. Attach relevant existing tags to the seeded opportunities.
4. Run backend tests/compile and verify the migration against the local PostgreSQL container.

## Completion criteria

- Admin credentials authenticate through the existing BCrypt flow.
- Approved opportunities appear in public search and pending opportunities appear in admin moderation.
- Flyway applies V8 successfully without changing checksums for V1–V7.
