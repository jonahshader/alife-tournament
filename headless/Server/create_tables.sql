CREATE TABLE IF NOT EXISTS user (
	user_id INTEGER PRIMARY KEY,
	username TEXT NOT NULL UNIQUE,
	password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS creature (
	creature_id INTEGER PRIMARY KEY,
	wins INTEGER NOT NULL,
	losses INTEGER NOT NULL,
	user_id INTEGER NOT NULL,

	FOREIGN KEY (user_id)
		REFERENCES user (user_id)
		ON UPDATE CASCADE
		ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS environment(
	environment_id INTEGER PRIMARY KEY,
	user_id INTEGER NOT NULL,

	FOREIGN KEY (user_id)
		REFERENCES user (user_id)
		ON UPDATE CASCADE
		ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tournament(
	tournament_id INTEGER PRIMARY KEY,
	tournament_date TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS creature_tourney(
	creature_id INTEGER NOT NULL,
	tournament_id INTEGER NOT NULL,
	is_winner INTEGER NOT NULL,

	PRIMARY KEY(creature_id, tournament_id),
	FOREIGN KEY (creature_id)
		REFERENCES creature (creature_id)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	FOREIGN KEY (tournament_id)
		REFERENCES tournament (tournament_id)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);

CREATE VIEW leaderboard
AS
SELECT creature_file_link, wins, losses
FROM creature
ORDER BY
	wins DEC,
	losses ASC,
LIMIT 20;
