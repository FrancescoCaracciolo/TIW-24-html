USE tiw_db;

-- Drop all TABLEs if they already exist
DROP TABLE IF EXISTS image_album;
DROP TABLE IF EXISTS text_comment;
DROP TABLE IF EXISTS image;
DROP TABLE IF EXISTS album;
DROP TABLE IF EXISTS person;

-- CREATE all TABLEs
CREATE TABLE person (
	username CHAR(20),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(30) NOT NULL,
    
    PRIMARY KEY (username)
);

CREATE TABLE image (
	file_path VARCHAR(255),
	title VARCHAR(30) NOT NULL,
    upload_DATE DATE NOT NULL,
    uploader_username CHAR(20),
    
    foreign KEY (uploader_username) REFERENCES person(username)
		ON UPDATE CASCADE
        ON DELETE CASCADE,
	PRIMARY KEY (file_path)
);

CREATE TABLE album (
	title VARCHAR(30) NOT NULL,
    creation_DATE DATE NOT NULL,
    creator_username CHAR(20),
    
    foreign KEY (creator_username) REFERENCES person(username)
		ON UPDATE CASCADE
        ON DELETE CASCADE,
    PRIMARY KEY (title, creator_username)
);

CREATE TABLE image_album (
	image_path VARCHAR(255),
    album_title VARCHAR(30),
    album_creator_username CHAR(30),
    order_position integer,
    
    foreign KEY (image_path) REFERENCES image(file_path)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
    foreign KEY (album_title, album_creator_username) REFERENCES album(title, creator_username)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
    PRIMARY KEY (image_path, album_title, album_creator_username)
);

CREATE TABLE text_comment (
	id INTEGER AUTO_INCREMENT,
    content text NOT NULL,
    image_path VARCHAR(255) NOT NULL,
    author_useraname CHAR(20),
    
    foreign KEY (image_path) REFERENCES person(username)
		ON UPDATE CASCADE
        ON DELETE CASCADE,
    foreign KEY (author_useraname) REFERENCES person(username)
		ON UPDATE CASCADE
        ON DELETE SET NULL,
	PRIMARY KEY (id)
);