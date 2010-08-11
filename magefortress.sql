DROP TABLE IF EXISTS races;
CREATE TABLE races (id INTEGER PRIMARY KEY, name TEXT NOT NULL, hold_behavior TEXT NOT NULL, move_behavior TEXT NOT NULL);
DROP TABLE IF EXISTS blueprints;
CREATE TABLE blueprints (id INTEGER PRIMARY KEY, name TEXT NOT NULL, placing_behavior TEXT NOT NULL);
DROP TABLE IF EXISTS grounds;
CREATE TABLE grounds (id INTEGER PRIMARY KEY, blueprint_id INTEGER NOT NULL, hardness INTEGER NOT NULL);
DROP TABLE IF EXISTS maps;
CREATE TABLE maps (id INTEGER PRIMARY KEY, width INTEGER NOT NULL, height INTEGER NOT NULL, depth INTEGER NOT NULL);
DROP TABLE IF EXISTS tiles;
CREATE TABLE tiles (id INTEGER PRIMARY KEY, map_id INTEGER NOT NULL, ground_id INTEGER NOT NULL, room_id INTEGER NOT NULL, object_id INTEGER NOT NULL, x INTEGER NOT NULL, y INTEGER NOT NULL, z INTEGER NOT NULL, underground INTEGER NOT NULL, dug_out INTEGER NOT NULL, wall_n INTEGER NOT NULL, wall_e INTEGER NOT NULL, wall_s INTEGER NOT NULL, wall_w INTEGER NOT NULL, floor INTEGER NOT NULL);