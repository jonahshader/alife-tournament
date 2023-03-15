CREATE TABLE IF NOT EXISTS creature_tourney(
	creature_id INTEGER NOT NULL,
	tournament_id INTEGER NOT NULL,

	PRIMARY KEY(creature_id, tournament_id),
	FOREIGN KEY (creature_id)
		REFERENCES chunk (chunk_id)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	FOREIGN KEY (tournament_id)
		REFERENCES tournament (tournament_id)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);
