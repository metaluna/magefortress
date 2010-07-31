// races
DELETE FROM races;
INSERT INTO races (name, hold_behavior, move_behavior) VALUES ("Martian", "magefortress.core.MFNullHoldable", "magefortress.core.MFNullMovable");
INSERT INTO races (name, hold_behavior, move_behavior) VALUES ("Venusian", "magefortress.core.MFNullHoldable", "magefortress.core.MFNullMovable");

