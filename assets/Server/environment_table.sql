CREATE TABLE IF NOT EXISTS environment(
	environment_id INTEGER PRIMARY KEY,
	user_id INTEGER NOT NULL,

	FOREIGN KEY (user_id)
		REFERENCES user (user_id)
		ON UPDATE CASCADE
		ON DELETE CASCADE
);
