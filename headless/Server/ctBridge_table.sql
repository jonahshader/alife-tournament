CREATE TABLE IF NOT EXISTS chunk_tourney(
	chunk_id INTEGER NOT NULL,
	tournament_id INTEGER NOT NULL,

	PRIMARY KEY(chunk_id, tournament_id),
	FOREIGN KEY (chunk_id)
		REFERENCES chunk (chunk_id)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	FOREIGN KEY (tournament_id)
		REFERENCES tournament (tournament_id)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);
