-- Remove the restrictive quantity check constraint on purchase_order_lines
-- This allows for over-delivery scenarios where received_quantity can exceed ordered_quantity

-- Drop the existing quantity check constraint
ALTER TABLE purchase_order_lines DROP CONSTRAINT IF EXISTS purchase_order_lines_qty_check;

-- Optionally, add a more reasonable constraint: quantities should be non-negative
ALTER TABLE purchase_order_lines
ADD CONSTRAINT purchase_order_lines_qty_positive_check
CHECK (ordered_quantity > 0 AND received_quantity >= 0);

-- Verify the constraint was updated
SELECT conname, pg_get_constraintdef(oid)
FROM pg_constraint
WHERE conrelid = 'purchase_order_lines'::regclass
AND contype = 'c';

