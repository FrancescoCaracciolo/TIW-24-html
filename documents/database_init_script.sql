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
	file_path VARCHAR(255) NOT NULL,
	title VARCHAR(30) NOT NULL,
    upload_date DATE NOT NULL,
    uploader_id INTEGER NOT NULL,
    
    FOREIGN KEY (uploader_id) REFERENCES person(id)
		ON UPDATE CASCADE
        ON DELETE CASCADE,
	PRIMARY KEY (file_path)
);

CREATE TABLE album (
	title VARCHAR(30) NOT NULL,
    creation_date DATE NOT NULL,
    creator_id INTEGER NOT NULL,
    
    foreign KEY (creator_id) REFERENCES person(id)
		ON UPDATE CASCADE
        ON DELETE CASCADE,
    PRIMARY KEY (title, creator_id)
);

CREATE TABLE image_album (
	image_path VARCHAR(255),
    album_title VARCHAR(30),
    album_creator_id INTEGER NOT NULL,
    order_position INTEGER,
    
    FOREIGN KEY (image_path) REFERENCES image(file_path)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
    FOREIGN KEY (album_title, album_creator_id) REFERENCES album(title, creator_id)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
    PRIMARY KEY (image_path, album_title, album_creator_id)
);

CREATE TABLE text_comment (
	id INTEGER AUTO_INCREMENT NOT NULL,
    content TEXT NOT NULL,
    image_path VARCHAR(255) NOT NULL,
    author_id INTEGER,
    
    foreign KEY (image_path) REFERENCES image(file_path)
		ON UPDATE CASCADE
        ON DELETE CASCADE,
    foreign KEY (author_id) REFERENCES person(id)
		ON UPDATE CASCADE
        ON DELETE SET NULL,
	PRIMARY KEY (id)
);