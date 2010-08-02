// races
DELETE FROM races;
INSERT INTO races (name, hold_behavior, move_behavior) VALUES ("Martian", "magefortress.creatures.behavior.MFNullHoldable", "magefortress.creatures.behavior.MFNullMovable");
INSERT INTO races (name, hold_behavior, move_behavior) VALUES ("Venusian", "magefortress.creatures.behavior.MFNullHoldable", "magefortress.creatures.behavior.MFNullMovable");
DELETE FROM maps;
INSERT INTO maps (width, height, depth) VALUES (13, 14, 15);
INSERT INTO maps (width, height, depth) VALUES (4, 4, 1);
DELETE FROM tiles;
INSERT INTO tiles (map_id, room_id, object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) VALUES (2, -1, -1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO tiles (map_id, room_id, object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) VALUES (2, -1, -1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO tiles (map_id, room_id, object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) VALUES (2, -1, -1, 0, 2, 0, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO tiles (map_id, room_id, object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) VALUES (2, -1, -1, 0, 3, 0, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO tiles (map_id, room_id, object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) VALUES (2, -1, -1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO tiles (map_id, room_id, object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) VALUES (2, -1, -1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO tiles (map_id, room_id, object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) VALUES (2, -1, -1, 1, 2, 0, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO tiles (map_id, room_id, object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) VALUES (2, -1, -1, 1, 3, 0, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO tiles (map_id, room_id, object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) VALUES (2, -1, -1, 2, 0, 0, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO tiles (map_id, room_id, object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) VALUES (2, -1, -1, 2, 1, 0, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO tiles (map_id, room_id, object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) VALUES (2, -1, -1, 2, 2, 0, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO tiles (map_id, room_id, object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) VALUES (2, -1, -1, 2, 3, 0, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO tiles (map_id, room_id, object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) VALUES (2, -1, -1, 3, 0, 0, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO tiles (map_id, room_id, object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) VALUES (2, -1, -1, 3, 1, 0, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO tiles (map_id, room_id, object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) VALUES (2, -1, -1, 3, 2, 0, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO tiles (map_id, room_id, object_id, x, y, z, underground, dug_out, wall_n, wall_e, wall_s, wall_w, floor) VALUES (2, -1, -1, 3, 3, 0, 1, 1, 1, 1, 1, 1, 1);