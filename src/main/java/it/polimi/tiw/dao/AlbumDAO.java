package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.Person;
import it.polimi.tiw.utils.DAOUtility;
import it.polimi.tiw.utils.Pair;

public class AlbumDAO implements DAO<Album, Integer> {
	private Connection dbConnection;
	
	private PreparedStatement saveStatement;
	private PreparedStatement updateStatement;
	private PreparedStatement deleteStatement;
	private PreparedStatement getStatement;
	private PreparedStatement getAllStatement;
	private PreparedStatement getFromCreatorStatement;
	private PreparedStatement addImageStatement;
	private PreparedStatement getAlbumAuthors;
	private PreparedStatement getAlbumThumbnails;
	private PreparedStatement getAlbumThumbnailsAndCreators;
	private PreparedStatement getOtherAlbumsAndUsers;
	private PreparedStatement getAlbumsAndUserByUser;
	private PreparedStatement deleteEmptyAlbums;
	
	public AlbumDAO(Connection dbConnection) throws SQLException {
		this.dbConnection = dbConnection;
		
		saveStatement = dbConnection.prepareStatement("INSERT INTO album (title, creator_id) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);
		updateStatement = dbConnection.prepareStatement("UPDATE album SET title=? WHERE id=?;");
		deleteStatement = dbConnection.prepareStatement("DELETE FROM album WHERE id=?;");
		getStatement = dbConnection.prepareStatement("SELECT * FROM album WHERE id=?;");
		getAllStatement = dbConnection.prepareStatement("SELECT * FROM album ORDER BY creation_date DESC, id DESC;");
		getFromCreatorStatement = dbConnection.prepareStatement("SELECT * FROM album WHERE creator_id=? ORDER BY creation_date DESC, id DESC;");
		addImageStatement = dbConnection.prepareStatement("INSERT INTO image_album (image_id, album_id) VALUES (?, ?)");
		getAlbumAuthors = dbConnection.prepareStatement("SELECT * FROM album a JOIN person p ON a.creator_id = p.id");
		getAlbumThumbnails = dbConnection.prepareStatement("SELECT *"
				+ " FROM image_album ap JOIN album a JOIN image i ON (ap.album_id = a.id AND ap.image_id = i.id)" 
				+ " WHERE i.id <= (SELECT MIN(i2.id) FROM image_album ia2 JOIN image i2 ON i2.id = ia2.image_id  WHERE album_id = a.id) ORDER BY a.creation_date DESC, a.id DESC;");
		getAlbumThumbnailsAndCreators = dbConnection.prepareStatement("SELECT * \n"
				+ "FROM image_album ap JOIN album a JOIN image i JOIN person p ON (ap.album_id = a.id AND ap.image_id = i.id AND a.creator_id = p.id) \n"
				+ "WHERE i.id <= (SELECT MIN(i2.id) FROM image_album ia2 JOIN image i2 ON i2.id = ia2.image_id  WHERE album_id = a.id)"
				+ "ORDER BY a.creation_date DESC, a.id DESC;");
		getOtherAlbumsAndUsers = dbConnection.prepareStatement("SELECT * FROM album a JOIN person p ON a.creator_id = p.id WHERE a.creator_id = ?");
		getAlbumsAndUserByUser = dbConnection.prepareStatement("SELECT * FROM album a JOIN person p ON a.creator_id = p.id WHERE a.creator_id <> ?");
		deleteEmptyAlbums = dbConnection.prepareStatement("DELETE FROM album a WHERE 0 = (SELECT COUNT(*) FROM image_album ap WHERE ap.album_id=a.id);");	
	}
	
	@Override
	public Optional<Album> get(Integer id) throws SQLException {
		getStatement.setInt(1, id);
		
		ResultSet result = getStatement.executeQuery();
		
		List<Album> albums = albumsFromResult(result);
		
		return albums.isEmpty()
				? Optional.empty()
				: Optional.of(albums.get(0));
	}
	
	public List<Album> getAll() throws SQLException {
		ResultSet result = getAllStatement.executeQuery();
		
		return albumsFromResult(result);
	}
	
	public List<Album> getFromCreator(Person person) throws SQLException {
		getFromCreatorStatement.setInt(1, person.getId());
		
		ResultSet result = getFromCreatorStatement.executeQuery();
		
		return albumsFromResult(result);
	}

	@Override
	public Optional<Album> save(String... params) throws SQLException {
		saveStatement.setString(1, params[0]);
		saveStatement.setInt(2, Integer.parseInt(params[1]));
		
		Optional<Album> newAlbum = DAOUtility.tryToSave(this, saveStatement);
		
		return newAlbum;
	}

	@Override
	public void update(Album album) throws SQLException {
		// Set new fields values
		updateStatement.setString(1, album.getTitle());
		
		// Set id field value
		updateStatement.setInt(2, album.getId());
		
		updateStatement.executeUpdate();
		
	}

	@Override
	public void delete(Album album) throws SQLException {
		// Set id field value
		deleteStatement.setInt(1, album.getId());
		
		deleteStatement.executeUpdate();
	}

	@Override 
	public void close() throws SQLException {
		getStatement.close();
		saveStatement.close();
		updateStatement.close();
		deleteStatement.close();
		getAllStatement.close();
		getFromCreatorStatement.close();
		addImageStatement.close();
		getAlbumAuthors.close();
		getAlbumThumbnails.close();
		getAlbumThumbnailsAndCreators.close();
		getOtherAlbumsAndUsers.close();
		getAlbumsAndUserByUser.close();
		deleteEmptyAlbums.close();
	}
	
	public void addImage(int albumId, int imageId) throws SQLException {
		addImageStatement.setInt(1, imageId);
		addImageStatement.setInt(2, albumId);
		
		addImageStatement.executeUpdate();
	}
	
	public void deleteEmptyAlbums() throws SQLException{
		deleteEmptyAlbums.executeUpdate();
	}
	
	public LinkedHashMap<Album, Person> getAlbumAuthorMap() throws SQLException {
		LinkedHashMap<Album, Person> map = new LinkedHashMap<>();
		ResultSet result = getAlbumAuthors.executeQuery();
		
		while (result.next()) {			
			Album fetchedAlbum = albumFromResult(result, "a.");
			Person fetchedPerson = PersonDAO.fetchPersonFromResult(result, "p.");
			map.put(fetchedAlbum, fetchedPerson);
		}
		return map;
	}
	
	public LinkedHashMap<Album, Image> getAlbumThumbnailMap() throws SQLException {
		LinkedHashMap<Album, Image> map = new LinkedHashMap<>();
		ResultSet result = getAlbumThumbnailsAndCreators.executeQuery();
		
		while (result.next()) {
			// Fetch values with alias
			Album fetchedAlbum = albumFromResult(result, "a.");
			Image fetchedImage = ImageDAO.imageFromResult(result, "i.");
			map.put(fetchedAlbum, fetchedImage);
		}
		return map;
	}
	
	public LinkedHashMap<Album, Pair<Person, Image>> getAlbumThumbnailAndPersonMap() throws SQLException {
		LinkedHashMap<Album, Pair<Person, Image>> map = new LinkedHashMap<>();
		ResultSet result = getAlbumThumbnailsAndCreators.executeQuery();
		
		while (result.next()) {
			// Fetch elements with aliases
			Album fetchedAlbum = albumFromResult(result, "a.");
			Image fetchedImage = ImageDAO.imageFromResult(result, "i.");
			Person fetchedPerson = PersonDAO.fetchPersonFromResult(result, "p.");
			
			Pair<Person, Image> pair = new Pair<>(fetchedPerson, fetchedImage);
			map.put(fetchedAlbum, pair);
		}
		return map;
	}
	
	
	// Utility method
	private List<Album> albumsFromResult(ResultSet result) throws SQLException {
		List<Album> albums = new ArrayList<>();
		
		// For each row found
		while(result.next()) {
			
			Album fetchedAlbum = albumFromResult(result, "");
			albums.add(fetchedAlbum);
		}
		
		return albums;
	}
	
	public static Album albumFromResult(ResultSet result, String alias) throws SQLException {
		// Fetch values
		int fetchedId = result.getInt(alias + "id");
		String fetchedTitle = result.getString(alias + "title");
		int fetchedCreator = result.getInt(alias + "creator_id");
		Date fetchedDate = result.getDate(alias + "creation_date");
		
		return new Album(fetchedId, fetchedTitle, fetchedCreator, fetchedDate);
	}
}
