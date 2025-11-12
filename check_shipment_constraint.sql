-- Run this query to see the exact CHECK constraint on shipment_lines
SELECT
    conname AS constraint_name,
    pg_get_constraintdef(c.oid) AS constraint_definition
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'shipment_lines'
  AND contype = 'c';

-- If the constraint is blocking valid rows, we can drop it:
-- ALTER TABLE shipment_lines DROP CONSTRAINT IF EXISTS shipment_lines_qty_check;

-- Then optionally add a correct one if needed:
-- ALTER TABLE shipment_lines ADD CONSTRAINT shipment_lines_qty_check
--   CHECK (quantity_shipped >= 0 AND quantity_shipped <= quantity);

