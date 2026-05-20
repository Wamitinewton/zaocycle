INSERT INTO briquette_products (id, sku, name, description, weight_kg, unit_price, sort_order, active)
VALUES (gen_random_uuid(), 'BRIQ-50', '50kg Briquette Sack',
        'Smokeless eco-briquettes from rice husks and coffee pulp. Ideal for boarding schools.',
        50.00, 1500.00, 1, true),
       (gen_random_uuid(), 'BRIQ-25', '25kg Briquette Sack',
        'Smokeless eco-briquettes. Suited for institutional kitchens and large households.',
        25.00, 800.00, 2, true),
       (gen_random_uuid(), 'BRIQ-10', '10kg Briquette Sack',
        'Smokeless eco-briquettes. Household-friendly size.',
        10.00, 350.00, 3, true);
