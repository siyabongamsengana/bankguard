
-- ========== UPGRADING MY EXISTING DATABASE ==========

-- I add the status column to an older risks table.
-- I give existing risks an Open status by default.
-- My Java code checks whether the column exists first.

ALTER TABLE risks
ADD COLUMN status TEXT NOT NULL DEFAULT 'Open'
CHECK (
    status IN (
        'Open',
        'In Progress',
        'Mitigated',
        'Closed'
    )
);
