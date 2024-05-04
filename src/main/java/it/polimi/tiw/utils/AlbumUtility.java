package it.polimi.tiw.utils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.Person;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.dao.PersonDAO;

public class AlbumUtility {
	public static Map<Integer, Image> getAlbumThumbnailMap(List<Album> albums, ImageDAO imageDAO) throws SQLException {		
		Map<Integer, Image> albumThumbnail = new HashMap<>();
		
		for (Album album : albums) {
			Optional<Image> thumbnail = imageDAO.getAlbumThumbnail(album);
			
			if (thumbnail.isPresent()) {
				albumThumbnail.put(album.getId(), thumbnail.get());
			}
		}
		
		return albumThumbnail;
	}
	
	public static Map<Integer, Person> getAlbumAuthorMap(List<Album> albums, PersonDAO personDAO) throws SQLException {		
		Map<Integer, Person> albumAuthor = new HashMap<>();
		
		for (Album album : albums) {
			Optional<Person> author = personDAO.getAlbumAuthor(album);
			
			if (author.isPresent()) {
				albumAuthor.put(album.getId(), author.get());
			}
		}
		
		return albumAuthor;
	}
}
