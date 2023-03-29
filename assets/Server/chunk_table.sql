CREATE TABLE IF NOT EXISTS chunk (
	chunk_id INTEGER PRIMARY KEY,
	games_played INTEGER NOT NULL,
	rank REAL NOT NULL,
	user_id INTEGER NOT NULL,

	FOREIGN KEY (user_id)
		REFERENCES user (user_id)
		ON UPDATE CASCADE
		ON DELETE CASCADE
);
