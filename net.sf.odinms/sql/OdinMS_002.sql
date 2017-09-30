Adds locked variable to items in database
ALTER TABLE inventoryequipment ADD COLUMN locked INTEGER NOT NULL DEFAULT 0 AFTER jump;