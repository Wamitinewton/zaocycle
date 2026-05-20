-- =============================================================================
-- Seed: Staff Users
-- Default password for all accounts: ZaoCycle123!
-- (BCrypt cost-12 hashes — compatible with Spring Security BCryptPasswordEncoder)
-- =============================================================================

INSERT INTO staff_users (id, email, password_hash, full_name, role, active, created_at, updated_at)
VALUES
  (
    '11111111-0000-0000-0000-000000000001',
    'admin@zaocycle.co.ke',
    '$2b$12$I9DpliGGRq/OBSy6sE61t.nq0OP4s4Nx/Hl7C4eHTVOqeI0T6s56u',
    'Alice Njeri Kamau',
    'ADMIN',
    true,
    NOW() - INTERVAL '14 months',
    NOW() - INTERVAL '2 months'
  ),
  (
    '11111111-0000-0000-0000-000000000002',
    'james.kariuki@zaocycle.co.ke',
    '$2b$12$fV1HnMGRNoN7tviXLFEe8.osWh0clxsSBq1ZnO9EC8XiieCcEI.Cq',
    'James Kariuki Mwangi',
    'COOP_MANAGER',
    true,
    NOW() - INTERVAL '13 months',
    NOW() - INTERVAL '1 month'
  ),
  (
    '11111111-0000-0000-0000-000000000003',
    'grace.wairimu@zaocycle.co.ke',
    '$2b$12$tGyImVKq5x2EFqT8wb5QgeBc/Cs117fjgGAciJFzjrzJYfA7rVFzK',
    'Grace Wairimu Njuguna',
    'COOP_MANAGER',
    true,
    NOW() - INTERVAL '12 months',
    NOW() - INTERVAL '3 weeks'
  );
