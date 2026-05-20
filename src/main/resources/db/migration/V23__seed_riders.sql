-- =============================================================================
-- Seed: Riders
-- 8 riders — 2 assigned per ward (MWEA, GICHUGU, KIRINYAGA_CENTRAL, NDIA)
-- Default password: ZaoCycle123!
-- =============================================================================

INSERT INTO riders (id, phone, full_name, password_hash, assigned_ward, active, created_at, updated_at)
VALUES
  -- MWEA ward riders
  (
    '22222222-0000-0000-0000-000000000001',
    '+254720000001',
    'John Njuguna Karanja',
    '$2b$12$KCY/CVSssBzg.tesYBsBL.AU6/9L20J9xzGKDcMtjw8hUk3d3O/9.',
    'MWEA', true,
    NOW() - INTERVAL '13 months',
    NOW() - INTERVAL '1 week'
  ),
  (
    '22222222-0000-0000-0000-000000000002',
    '+254720000002',
    'Peter Mwangi Gitau',
    '$2b$12$bSD9WsY/WpF9RFRSCb8KhukuORegt0VnGe4QgVUFV9XmmIHTv6QG6',
    'MWEA', true,
    NOW() - INTERVAL '13 months',
    NOW() - INTERVAL '2 weeks'
  ),

  -- GICHUGU ward riders
  (
    '22222222-0000-0000-0000-000000000003',
    '+254720000003',
    'Samuel Kamau Njogu',
    '$2b$12$APx5qxVcfTlPYfAQwMgNc.pANq2A5uej4iMLB9FjHBttMKdAzmr9K',
    'GICHUGU', true,
    NOW() - INTERVAL '12 months',
    NOW() - INTERVAL '1 month'
  ),
  (
    '22222222-0000-0000-0000-000000000004',
    '+254720000004',
    'David Muriithi Gitonga',
    '$2b$12$oEWR41fktdvMYOg4IzayAuM2ENhNOdO21u7klOEAmHWFIiL/B5ib.',
    'GICHUGU', true,
    NOW() - INTERVAL '12 months',
    NOW() - INTERVAL '3 weeks'
  ),

  -- KIRINYAGA_CENTRAL ward riders
  (
    '22222222-0000-0000-0000-000000000005',
    '+254720000005',
    'Francis Njeru Wainaina',
    '$2b$12$DcwHLHFz6XwkkzK0SLDVZu9yh/KjZkf2pDmIR.3yLV6lkOaF..Lkq',
    'KIRINYAGA_CENTRAL', true,
    NOW() - INTERVAL '11 months',
    NOW() - INTERVAL '2 weeks'
  ),
  (
    '22222222-0000-0000-0000-000000000006',
    '+254720000006',
    'Joseph Kamande Mwangi',
    '$2b$12$hDET4m6alOaftVdFVgu.AudhKUVTraFJR7/UxCkHPeXuICuPaWFuq',
    'KIRINYAGA_CENTRAL', true,
    NOW() - INTERVAL '11 months',
    NOW() - INTERVAL '1 month'
  ),

  -- NDIA ward riders
  (
    '22222222-0000-0000-0000-000000000007',
    '+254720000007',
    'Thomas Gitau Mugo',
    '$2b$12$AdhQoenEuGcck6LUBqYf5Ontc.uaFICvY5xoPL66r/JMoDdUcUGfW',
    'NDIA', true,
    NOW() - INTERVAL '10 months',
    NOW() - INTERVAL '2 weeks'
  ),
  (
    '22222222-0000-0000-0000-000000000008',
    '+254720000008',
    'Paul Wanjiku Njoroge',
    '$2b$12$PMfhzyvObSsG0L8i/lAzDOkKacm2L7YYEscZnJsnCHPCeUGdLsKAG',
    'NDIA', true,
    NOW() - INTERVAL '10 months',
    NOW() - INTERVAL '3 weeks'
  );
