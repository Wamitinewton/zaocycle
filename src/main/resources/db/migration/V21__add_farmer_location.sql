ALTER TABLE farmers
    ADD COLUMN trading_center VARCHAR(100),
    ADD COLUMN latitude       DOUBLE PRECISION,
    ADD COLUMN longitude      DOUBLE PRECISION;

COMMENT ON COLUMN farmers.trading_center IS 'Nearest trading centre selected during USSD registration';
COMMENT ON COLUMN farmers.latitude       IS 'WGS-84 latitude of the trading centre selected by the farmer';
COMMENT ON COLUMN farmers.longitude      IS 'WGS-84 longitude of the trading centre selected by the farmer';
