-- Add rider name to farmer confirmation
UPDATE sms_templates
SET body = 'Pickup confirmed for {{date}}. Rider {{riderName}} will collect your waste. Have it ready by the road. - ZaoCycle'
WHERE code = 'PICKUP_SCHEDULED_FARMER';

-- New: farmer has no certified crops ready for pickup
INSERT INTO sms_templates (id, code, body) VALUES
  (gen_random_uuid(), 'NOT_HARVEST_READY',
   'Your crops are not yet certified for pickup. Ensure your pesticide waiting period is complete and your certificate is active. Dial *384*5193# for help. - ZaoCycle');

-- New: pickup accepted but no rider available in ward yet
INSERT INTO sms_templates (id, code, body) VALUES
  (gen_random_uuid(), 'PICKUP_PENDING_ASSIGNMENT',
   'Pickup request received for {{date}}. A rider will be assigned to you shortly and you will receive a confirmation SMS. - ZaoCycle');
