-- ============================================
-- Fix inventory_movements type_check constraint
-- Add RESERVED to allowed movement types
-- ============================================

-- For PostgreSQL
-- Drop the old constraint
ALTER TABLE inventory_movements DROP CONSTRAINT IF EXISTS inventory_movements_type_check;

-- Recreate the constraint with RESERVED included
ALTER TABLE inventory_movements
ADD CONSTRAINT inventory_movements_type_check
CHECK (movement_type IN ('INBOUND', 'OUTBOUND', 'RESERVED', 'ADJUSTMENT'));

-- Verify the constraint
SELECT conname, pg_get_constraintdef(oid)
FROM pg_constraint
WHERE conname = 'inventory_movements_type_check';

-- Success message
SELECT 'Constraint updated successfully! RESERVED movement type is now allowed.' AS status;
