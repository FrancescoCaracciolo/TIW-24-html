USE tiw_db;

-- Drop all TABLEs if they already exist
DROP TABLE IF EXISTS image_album;
DROP TABLE IF EXISTS text_comment;
DROP TABLE IF EXISTS image;
DROP TABLE IF EXISTS album;
DROP TABLE IF EXISTS person;

-- CREATE all TABLEs
CREATE TABLE person (
	id INTEGER AUTO_INCREMENT NOT NULL,
	username VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    
    PRIMARY KEY (id)
);

CREATE TABLE image (
	id INTEGER NOT NULL,
	file_path VARCHAR(255) NOT NULL,
	title VARCHAR(30) NOT NULL,
    upload_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    uploader_id INTEGER NOT NULL,
    
    UNIQUE (file_path),
    FOREIGN KEY (uploader_id) REFERENCES person(id)
		ON UPDATE CASCADE
        ON DELETE CASCADE,
	PRIMARY KEY (id)
);

CREATE TABLE album (
	id INTEGER AUTO_INCREMENT NOT NULL,
	title VARCHAR(30) NOT NULL,
    creation_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    creator_id INTEGER NOT NULL,
    
   	CONSTRAINT album_unique UNIQUE (id, creator_id),
    FOREIGN KEY (creator_id) REFERENCES person(id)
		ON UPDATE CASCADE
        ON DELETE CASCADE,
    PRIMARY KEY (id)
);

CREATE TABLE image_album (
	image_id INTEGER,
    album_id INTEGER,
    order_position INTEGER,
    
    FOREIGN KEY (image_id) REFERENCES image(id)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
    FOREIGN KEY (album_id) REFERENCES album(id)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
    PRIMARY KEY (image_id, album_id)
);

CREATE TABLE text_comment (
	id INTEGER AUTO_INCREMENT NOT NULL,
    content TEXT NOT NULL,
    image_id INTEGER NOT NULL,
    author_id INTEGER,
    
    FOREIGN KEY (image_id) REFERENCES image(id)
		ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES person(id)
		ON UPDATE CASCADE
        ON DELETE SET NULL,
	PRIMARY KEY (id)
);