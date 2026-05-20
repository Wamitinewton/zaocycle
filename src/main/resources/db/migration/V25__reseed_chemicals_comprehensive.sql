-- Remove any previously seeded chemicals that are not referenced by pesticide applications.
-- This clears partial or duplicate data from V5 while preserving any farmer-linked records.
DELETE FROM chemicals
WHERE id NOT IN (
    SELECT DISTINCT chemical_id FROM pesticide_applications
);

-- Insert comprehensive chemical data with deterministic UUIDs.
-- Uses ON CONFLICT (id) DO NOTHING so the migration is safe to run even if some rows survived above.

INSERT INTO chemicals (id, name, active_ingredient, category, half_life_days, phi_days, common_crops, active)
VALUES

-- ── FUNGICIDES ────────────────────────────────────────────────────────────────

-- Broad-spectrum contact + systemic mixes
('c1000001-0000-4000-8000-000000000001', 'Ridomil Gold MZ 68 WP',   'Metalaxyl-M + Mancozeb',        'FUNGICIDE', 7,  7,  'Tomatoes,Potatoes,French Beans', true),
('c1000002-0000-4000-8000-000000000002', 'Milraz 72 WP',             'Cymoxanil + Propineb',           'FUNGICIDE', 7,  14, 'Tomatoes,Potatoes', true),
('c1000003-0000-4000-8000-000000000003', 'Dithane M-45',             'Mancozeb',                       'FUNGICIDE', 3,  7,  'Tomatoes,Potatoes,Cabbage,Onions,French Beans', true),
('c1000004-0000-4000-8000-000000000004', 'Curzate M8',               'Cymoxanil + Mancozeb',           'FUNGICIDE', 7,  7,  'Tomatoes,Potatoes', true),
('c1000005-0000-4000-8000-000000000005', 'Acrobat MZ 69 WP',         'Dimethomorph + Mancozeb',        'FUNGICIDE', 7,  7,  'Tomatoes,Potatoes,French Beans', true),
('c1000006-0000-4000-8000-000000000006', 'Sulcop 50 WP',             'Copper Oxychloride',             'FUNGICIDE', 3,  7,  'Tomatoes,Potatoes,Cabbage,French Beans', true),
('c1000007-0000-4000-8000-000000000007', 'Funguran OH 50 WP',        'Copper Hydroxide',               'FUNGICIDE', 3,  7,  'Tomatoes,Potatoes,Cabbage,French Beans', true),
('c1000008-0000-4000-8000-000000000008', 'Cupravit OB 21',           'Copper Oxychloride',             'FUNGICIDE', 3,  7,  'Tomatoes,Cabbage,Onions,French Beans', true),
('c1000009-0000-4000-8000-000000000009', 'Recoil 72 WP',             'Metalaxyl + Cymoxanil',          'FUNGICIDE', 7,  7,  'Potatoes,Tomatoes', true),
('c100000a-0000-4000-8000-000000000010', 'Equation Pro',             'Famoxadone + Cymoxanil',         'FUNGICIDE', 7,  3,  'Tomatoes,Potatoes', true),

-- Systemic strobilurins / triazoles / SDHI
('c100000b-0000-4000-8000-000000000011', 'Ortiva 250 SC',            'Azoxystrobin',                   'FUNGICIDE', 7,  7,  'Tomatoes,Potatoes,Onions,French Beans', true),
('c100000c-0000-4000-8000-000000000012', 'Score 250 EC',             'Difenoconazole',                 'FUNGICIDE', 14, 14, 'Tomatoes,Potatoes,French Beans', true),
('c100000d-0000-4000-8000-000000000013', 'Folicur 250 EW',           'Tebuconazole',                   'FUNGICIDE', 14, 21, 'Onions,Tomatoes,French Beans', true),
('c100000e-0000-4000-8000-000000000014', 'Ranman 400 SC',            'Cyazofamid',                     'FUNGICIDE', 7,  7,  'Potatoes,Tomatoes', true),
('c100000f-0000-4000-8000-000000000015', 'Cabrio Top 75 WG',         'Pyraclostrobin + Metiram',       'FUNGICIDE', 7,  14, 'Tomatoes,Potatoes', true),
('c1000010-0000-4000-8000-000000000016', 'Amistar Top 325 SC',       'Azoxystrobin + Difenoconazole',  'FUNGICIDE', 14, 7,  'Onions,Tomatoes,French Beans', true),
('c1000011-0000-4000-8000-000000000017', 'Tivano 375 SC',            'Azoxystrobin + Tebuconazole',    'FUNGICIDE', 14, 14, 'French Beans,Tomatoes,Onions', true),
('c1000012-0000-4000-8000-000000000018', 'Signum 33 WG',             'Pyraclostrobin + Boscalid',      'FUNGICIDE', 7,  7,  'French Beans,Tomatoes,Cabbage', true),

-- Short PHI — suited for export French Beans
('c1000013-0000-4000-8000-000000000019', 'Switch 62.5 WG',           'Cyprodinil + Fludioxonil',       'FUNGICIDE', 5,  3,  'French Beans,Tomatoes', true),
('c1000014-0000-4000-8000-000000000020', 'Luna Sensation 500 SC',    'Fluopyram + Trifloxystrobin',    'FUNGICIDE', 10, 3,  'French Beans,Tomatoes', true),
('c1000015-0000-4000-8000-000000000021', 'Serenade ASO',             'Bacillus subtilis QST 713',      'FUNGICIDE', 1,  0,  'French Beans,Tomatoes,Cabbage,Onions,Potatoes', true),
('c1000016-0000-4000-8000-000000000022', 'Kumulus DF 80 WG',         'Sulphur',                        'FUNGICIDE', 1,  7,  'French Beans,Tomatoes,Onions', true),

-- ── INSECTICIDES ─────────────────────────────────────────────────────────────

-- Pyrethroids
('c2000001-0000-4000-8000-000000000001', 'Karate 2.5 EC',            'Lambda-cyhalothrin',             'INSECTICIDE', 5,  7,  'Tomatoes,French Beans,Cabbage', true),
('c2000002-0000-4000-8000-000000000002', 'Decis 2.5 EC',             'Deltamethrin',                   'INSECTICIDE', 5,  7,  'Tomatoes,French Beans,Cabbage', true),
('c2000003-0000-4000-8000-000000000003', 'Kingcode Elite 50 EC',     'Cypermethrin + Chlorpyrifos',    'INSECTICIDE', 14, 21, 'Tomatoes,Cabbage,Potatoes', true),
('c2000004-0000-4000-8000-000000000004', 'Ekondoplus',               'Profenofos + Cypermethrin',      'INSECTICIDE', 7,  14, 'Tomatoes,French Beans,Cabbage', true),

-- Neonicotinoids
('c2000005-0000-4000-8000-000000000005', 'Actara 25 WG',             'Thiamethoxam',                   'INSECTICIDE', 14, 14, 'Tomatoes,French Beans', true),
('c2000006-0000-4000-8000-000000000006', 'Confidor 70 WG',           'Imidacloprid',                   'INSECTICIDE', 21, 21, 'Tomatoes,Cabbage,Onions', true),
('c2000007-0000-4000-8000-000000000007', 'Mospilan 20 SP',           'Acetamiprid',                    'INSECTICIDE', 7,  14, 'Tomatoes,French Beans,Cabbage', true),
('c2000008-0000-4000-8000-000000000008', 'Dudu Acelegold',           'Acetamiprid + Lambda-cyhalothrin','INSECTICIDE', 7, 14, 'Tomatoes,Onions,French Beans', true),
('c2000009-0000-4000-8000-000000000009', 'Movento 100 SC',           'Spirotetramat',                  'INSECTICIDE', 14, 7,  'French Beans,Onions,Tomatoes', true),

-- Diamides (short PHI, export-safe)
('c200000a-0000-4000-8000-000000000010', 'Coragen 20 SC',            'Chlorantraniliprole',            'INSECTICIDE', 14, 1,  'Tomatoes,French Beans,Cabbage', true),
('c200000b-0000-4000-8000-000000000011', 'Ampligo 150 ZC',           'Chlorantraniliprole + Lambda-cyhalothrin', 'INSECTICIDE', 14, 3,  'Tomatoes,French Beans,Cabbage', true),
('c200000c-0000-4000-8000-000000000012', 'Voliam Flexi 300 SC',      'Thiamethoxam + Chlorantraniliprole', 'INSECTICIDE', 14, 7,  'Tomatoes,French Beans', true),
('c200000d-0000-4000-8000-000000000013', 'Belt Expert 480 SC',       'Flubendiamide + Spirotetramat',  'INSECTICIDE', 14, 7,  'French Beans,Tomatoes', true),

-- Indoxacarb
('c200000e-0000-4000-8000-000000000014', 'Avaunt 150 SC',            'Indoxacarb',                     'INSECTICIDE', 7,  14, 'Tomatoes,Cabbage,French Beans', true),
('c200000f-0000-4000-8000-000000000015', 'Biodine 7.5 WDG',          'Indoxacarb',                     'INSECTICIDE', 7,  7,  'Tomatoes,French Beans', true),

-- Spinosyns (biological, short PHI)
('c2000010-0000-4000-8000-000000000016', 'Spintor 480 SC',           'Spinosad',                       'INSECTICIDE', 3,  3,  'French Beans,Tomatoes,Cabbage', true),
('c2000011-0000-4000-8000-000000000017', 'Success Neo 60 SC',        'Spinetoram',                     'INSECTICIDE', 3,  7,  'French Beans,Tomatoes,Cabbage', true),
('c2000012-0000-4000-8000-000000000018', 'Radiant SC',               'Spinetoram',                     'INSECTICIDE', 3,  7,  'Tomatoes,French Beans', true),

-- Macrocyclic lactones / avermectins
('c2000013-0000-4000-8000-000000000019', 'Proclaim 5 SG',            'Emamectin benzoate',             'INSECTICIDE', 7,  3,  'French Beans,Tomatoes,Cabbage', true),
('c2000014-0000-4000-8000-000000000020', 'Sunfire 240 SC',           'Chlorfenapyr',                   'INSECTICIDE', 14, 7,  'Tomatoes,French Beans,Cabbage', true),

-- Others
('c2000015-0000-4000-8000-000000000021', 'Pegasus 500 SC',           'Diafenthiuron',                  'INSECTICIDE', 7,  7,  'French Beans,Tomatoes', true),
('c2000016-0000-4000-8000-000000000022', 'Clorpyrifos 480 EC',       'Chlorpyrifos',                   'INSECTICIDE', 21, 21, 'Potatoes,Cabbage', true),

-- ── HERBICIDES ───────────────────────────────────────────────────────────────

('c3000001-0000-4000-8000-000000000001', 'Roundup 360 SL',           'Glyphosate',                     'HERBICIDE', 14, 30, 'Pre-planting', true),
('c3000002-0000-4000-8000-000000000002', 'Lexus 247 SC',             'Fluazifop-P-butyl',              'HERBICIDE', 7,  14, 'Tomatoes,French Beans', true),
('c3000003-0000-4000-8000-000000000003', 'Fusilade Forte 150 EC',    'Fluazifop-P-butyl',              'HERBICIDE', 7,  7,  'French Beans,Tomatoes,Onions', true),
('c3000004-0000-4000-8000-000000000004', 'Galant Plus 125 EC',       'Haloxyfop-R-methyl',             'HERBICIDE', 7,  7,  'French Beans,Onions,Cabbage', true),
('c3000005-0000-4000-8000-000000000005', 'Stomp Aqua SC',            'Pendimethalin',                  'HERBICIDE', 14, 21, 'Onions,Potatoes,Cabbage', true),
('c3000006-0000-4000-8000-000000000006', 'Trophy 480 EC',            'Acetochlor',                     'HERBICIDE', 14, 21, 'Pre-emergence', true)

ON CONFLICT (id) DO NOTHING;
