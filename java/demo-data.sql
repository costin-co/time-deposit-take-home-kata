-- Optional demonstration data for the file-based H2 database.
-- Safe to rerun: inserts missing sample rows without updating or deleting existing data.
-- This file is not configured for automatic application startup.

INSERT INTO "timeDeposits" ("planType", "days", "balance")
SELECT 'basic', 30, 1000.00
WHERE NOT EXISTS (
    SELECT 1 FROM "timeDeposits"
    WHERE "planType" = 'basic' AND "days" = 30
);

INSERT INTO "timeDeposits" ("planType", "days", "balance")
SELECT 'basic', 31, 1000.00
WHERE NOT EXISTS (
    SELECT 1 FROM "timeDeposits"
    WHERE "planType" = 'basic' AND "days" = 31
);

INSERT INTO "timeDeposits" ("planType", "days", "balance")
SELECT 'premium', 45, 1000.00
WHERE NOT EXISTS (
    SELECT 1 FROM "timeDeposits"
    WHERE "planType" = 'premium' AND "days" = 45
);

INSERT INTO "timeDeposits" ("planType", "days", "balance")
SELECT 'premium', 46, 1000.00
WHERE NOT EXISTS (
    SELECT 1 FROM "timeDeposits"
    WHERE "planType" = 'premium' AND "days" = 46
);

INSERT INTO "timeDeposits" ("planType", "days", "balance")
SELECT 'student', 365, 1000.00
WHERE NOT EXISTS (
    SELECT 1 FROM "timeDeposits"
    WHERE "planType" = 'student' AND "days" = 365
);

INSERT INTO "timeDeposits" ("planType", "days", "balance")
SELECT 'student', 366, 1000.00
WHERE NOT EXISTS (
    SELECT 1 FROM "timeDeposits"
    WHERE "planType" = 'student' AND "days" = 366
);

INSERT INTO "withdrawals" ("timeDepositId", "amount", "date")
SELECT deposit."id", 100.00, DATE '2026-11-13'
FROM "timeDeposits" deposit
WHERE deposit."planType" = 'basic'
  AND deposit."days" = 31
  AND NOT EXISTS (
      SELECT 1 FROM "withdrawals" withdrawal
      WHERE withdrawal."timeDepositId" = deposit."id"
        AND withdrawal."amount" = 100.00
        AND withdrawal."date" = DATE '2026-11-13'
  );

INSERT INTO "withdrawals" ("timeDepositId", "amount", "date")
SELECT deposit."id", 250.00, DATE '2026-11-13'
FROM "timeDeposits" deposit
WHERE deposit."planType" = 'premium'
  AND deposit."days" = 46
  AND NOT EXISTS (
      SELECT 1 FROM "withdrawals" withdrawal
      WHERE withdrawal."timeDepositId" = deposit."id"
        AND withdrawal."amount" = 250.00
        AND withdrawal."date" = DATE '2026-11-13'
  );
