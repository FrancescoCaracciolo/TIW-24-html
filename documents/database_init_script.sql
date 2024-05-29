USE tiw_db;

-- Drop all tables if they already exist
DROP TABLE IF EXISTS image_album;
DROP TABLE IF EXISTS text_comment;
DROP TABLE IF EXISTS image;
DROP TABLE IF EXISTS album;
DROP TABLE IF EXISTS person;

-- Create all tables
CREATE TABLE person (
	id INTEGER AUTO_INCREMENT NOT NULL,
	username VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    
    PRIMARY KEY (id)
);

CREATE TABLE image (
	id INTEGER AUTO_INCREMENT NOT NULL,
	file_path VARCHAR(255) NOT NULL,
	title VARCHAR(30) NOT NULL,
	description TEXT NOT NULL,
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
    
    FOREIGN KEY (creator_id) REFERENCES person(id)
		ON UPDATE CASCADE
        ON DELETE CASCADE,
    PRIMARY KEY (id)
);

CREATE TABLE image_album (
	image_id INTEGER,
    album_id INTEGER,
    
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

-- Insert test data in all tables
INSERT INTO person VALUES (1,'francesco','francesco@it.poil','$2a$12$zYdnNOvlyLEp/NVGCdutTOY1SZASl8VR5UINpIJubQ2SxDdSCv0Z.'),(2,'oingo','oingo@jjba.it','$2a$12$aXTMlOKx6FkLFMzBP/R4PeMq2aMXVkeECopcP6b9O30ashwePQmNy'),(3,'numbers','numbers@it.it','$2a$12$OcpYMHOpF61fwC6.m6J7FuTQlymGBY.3WdRp4sVyFzsQ9XmHTr.e.'),(4,'4kguy','4k@4k.it','$2a$12$HzSeV2/lHi6q.nMpzLiYUuU.731.YqzB8V2KoBThDB1vXHodPHUdy'),(5,'commenter','asdfa@fdafds','$2a$12$LkS5zZq9qOC5hmdNaw52UOHNTlXH1f7sY1vbKqyO2mwYKSJtMBW.W'),(6,'joseph','joseph@ifd.it','$2a$12$AKB9w4phjzSDmbumdi1AU.yUO5qPRL5PkV/ueMh/8fpinZ/1Xo5va');
INSERT INTO album VALUES (1,'Oingo and Boingo brothers','2022-05-28',2),(2,'Numbers','2021-05-28',3),(3,'Numbers 1-5','2024-05-25',3),(4,'Numbers 1-4','1990-05-28',3),(5,'Numbers 1-6','2024-05-28',3),(6,'Numbers 1-11','2024-05-26',3),(7,'Images','2024-05-28',4),(8,'Comments','2024-05-28',5);
INSERT INTO image VALUES (1, 'XDVfhgMuFr.webp', 'Oingo Boingo adventures', 'Oingo Boingo', '2022-05-20', 2), (2, 'oRkk1uvIk8.jpeg', 'Page 1', 'Oingo e Boingo incontrano dei brutti ceffi', '2024-05-19', 2), (3, 'S97hCIo8pv.jpeg', 'Page 2', 'Oingo e Boingo sono molto felici', '2024-05-18', 2), (4, 'bcvpkq5QF9.jpeg', 'Page 3', 'Oingo e Boingo vanno al bar', '2024-05-17', 2), (5, 'QD89pXy00N.jpeg', 'Page 4', 'What is Iggy doing', '2024-05-16', 2), (6, 'bu2o390v51.jpeg', 'Page 5', 'I brutti ceffi', '2024-05-15', 2), (7, 'RlHuhse6lu.jpeg', 'Page 6', 'I brutti ceffi scelgono il bar', '2024-05-14', 2), (8, 'XGIsOSf5Cc.jpeg', 'Page 7', 'I brutti ceffi bevono il the avvelenato. Hurra\' Oingo e Boingo hanno vinto', '2024-05-13', 2), (9, 'ow44K06TXC.jpeg', 'Page 8', 'Le previsioni di Boingo non sbagliano mai!', '2024-05-12', 2), (10, 'PfnQF4eKii.jpeg', 'Page 9', 'Hurra\' oingo e boingo hanno vinto', '2024-05-11', 2), (11, 'ifKPxINCY4.jpeg', 'Page 10', 'Oingo Boingo Brothers!', '2024-05-10', 2), (12, 'C6eZUZ5Li7.png', 'TBC', 'Ci rivediamo!', '2023-05-28', 2), (13, 'cN5iy0D7mN.jpg', '10', '10', '2024-05-28', 3), (14, 'jsCDRi1jBZ.jpg', '9', '9', '2024-05-28', 3), (15, 'psRlIBxeev.jpg', '8', '8', '2024-05-28', 3), (16, 'KmBLSiISAL.jpg', '7', '7', '2024-05-28', 3), (17, 'YB9RC7DhKk.jpg', '6', '6', '2024-05-28', 3), (18, 'I3ZpcM25Np.jpg', '5', '5', '2024-05-28', 3), (19, 'H3jIJWnGBJ.jpg', '4', '4', '2024-05-28', 3), (20, 'jzxbuitnht.jpeg', '3', '3', '2024-05-28', 3), (21, 'ACsak5ckpx.jpg', '2', '2', '2010-05-28', 3), (22, 'Ydt8sSlKul.jpg', '1', '1', '2024-05-28', 3), (23, 'IvZy1NcIAO.jpg', '11', '11', '2019-05-27', 3), (24, 'wCVNGOtynS.jpg', 'Big cat', 'meow', '2024-05-28', 4), (25, 'Z2c19eUDkt.jpg', 'fasd', 'afsd', '2022-05-28', 4), (26, 'Wjy0BDToQz.jpg', 'fads', 'afds', '2021-05-28', 4), (27, '0vgk2fQaEn.jpg', 'fasdfasdf', 'adsfdfasfdasfdasdfsasdfaasdfasdfasdfasdfasdf', '2024-02-02', 4), (28, 'JDJ3KNINAh.jpg', 'dfasdf', 'asdfsdfa', '2024-05-28', 4), (29, 'h7fHUGc4IP.jpg', 'asdfasdf', 'asdfasdfsd', '2024-05-28', 4), (30, 'N358pvtnCC.jpg', 'fasdfasdf', 'asdfasdfasdf', '2024-05-22', 4), (31, 'l2I8aPr8hz.jpg', 'fasdfasd', 'fasdfasdfasd', '2024-05-20', 4), (32, '9AQanAnCmH.jpg', 'asdfasdf', 'afff', '1999-05-28', 4), (33, 'ObcTehdRHi.jpg', 'Img', 'image', '2024-05-28', 5), (34, 'CL3NpM9obf.jpg', 'Image', 'You expected an Image, but it was me, DIO!', '1990-05-28', 5);
INSERT INTO image_album VALUES (1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(7,1),(8,1),(9,1),(10,1),(11,1),(12,1),(13,2),(13,6),(14,2),(14,6),(15,2),(15,6),(16,2),(16,6),(17,2),(17,5),(17,6),(18,2),(18,3),(18,5),(18,6),(19,2),(19,3),(19,4),(19,5),(19,6),(20,2),(20,3),(20,4),(20,5),(20,6),(21,2),(21,3),(21,4),(21,5),(21,6),(22,2),(22,3),(22,4),(22,5),(22,6),(23,6),(24,7),(25,7),(26,7),(27,7),(28,7),(29,7),(31,7),(32,7),(33,8),(34,8);
INSERT INTO text_comment VALUES (1,'Test Comment',34,5),(2,'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc, quis gravida magna mi a libero. Fusce vulputate eleifend sapien. Vestibulum purus quam, scelerisque ut, mollis sed, nonummy id, metus. Nullam accumsan lorem in dui. Cras ultricies mi eu turpis hendrerit fringilla. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; In ac dui quis mi consectetuer lacinia. Nam pretium turpis et arcu. Duis arcu tortor, suscipit eget, imperdiet nec, imperdiet iaculis, ipsum. Sed aliquam ultrices mauris. Integer ante arcu, accumsan a, consectetuer eget, posuere ut, mauris. Praesent adipiscing. Phasellus ullamcorper ipsum rutrum nunc. Nunc nonummy metus. Vestibulum volutpat pretium libero. Cras id dui. Aenean ut eros et nisl sagittis vestibulum. Nullam nulla eros, ultricies sit amet, nonummy id, imperdiet feugiat, pede. Sed lectus. Donec mollis hendrerit risus. Phasellus nec sem in justo pellentesque facilisis. Etiam imperdiet imperdiet orci. Nunc nec neque. Phasellus leo dolor, tempus non, auctor et, hendrerit quis, nisi. Curabitur ligula sapien, tincidunt non, euismod vitae, posuere imperdiet, leo. Maecenas malesuada. Praesent congue erat at massa. Sed cursus turpis vitae tortor. Donec posuere vulputate arcu. Phasellus accumsan cursus velit. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Sed aliquam, nisi quis porttitor congue, elit erat euismod orci, ac placerat dolor lectus quis orci. Phasellus consectetuer vestibulum elit. Aenean tellus metus, bibendum sed, posuere ac, mattis non, nunc. Vestibulum fringilla pede sit amet augue. In turpis. Pellentesque posuere. Praesent turpis. Aenean posuere, tortor sed cursus feugiat, nunc augue blandit nunc, eu sollicitudin urna dolor sagittis lacus. Donec elit libero, sodales nec, volutpat a, suscipit non, turpis. Nullam sagittis. Suspendisse pulvinar, augue ac venenatis condimentum, sem libero volutpat nibh, nec pellentesque velit pede quis nunc. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Fusce id purus. Ut varius tincidunt libero. Phasellus dolor. Maecenas vestibulum mollis diam. Pellentesque ut neque. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. In dui magna, posuere eget, vestibulum et, tempor auctor, justo. In ac felis quis tortor malesuada pretium. Pellentesque auctor neque nec urna. Proin sapien ipsum, porta a, auctor quis, euismod ut, mi. Aenean viverra rhoncus pede. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Ut non enim eleifend felis pretium feugiat. Vivamus quis mi. Phasellus a est. Phasellus magna. In hac habitasse platea dictumst. Curabitur at lacus ac velit ornare lobortis. Cura',33,5),(3,'Ok',33,1),(4,'Oh no!',34,6),(5,'Oh My God!',34,6);