package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class AlbumDAO implements DAO<Album, Integer> {
	private Connection dbConnection;
	
	private PreparedStatement saveStatement;
	private PreparedStatement updateStatement;
	private PreparedStatement deleteStatement;
	private PreparedStatement getStatement;
	private PreparedStatement getAllStatement;
	private PreparedStatement getFromCreatorStatement;
	
	public AlbumDAO(Connection dbConnection) throws SQLException {
		this.dbConnection = dbConnection;
		
		saveStatement = dbConnection.prepareStatement("INSERT INTO album (title, creator_id) VALUES (?, ?);");
		updateStatement = dbConnection.prepareStatement("UPDATE album SET title=? WHERE id=?;");
		deleteStatement = dbConnection.prepareStatement("DELETE FROM album WHERE id=?;");
		getStatement = dbConnection.prepareStatement("SELECT * FROM album WHERE id=?;");
		getAllStatement = dbConnection.prepareStatement("SELECT * FROM album;");
		getFromCreatorStatement = dbConnection.prepareStatement("SELECT * FROM album WHERE creator_id=?;");
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
	public void save(String... params) throws SQLException {
		saveStatement.setString(1, params[0]);
		saveStatement.setInt(2, Integer.parseInt(params[1]));
		
		saveStatement.executeUpdate();
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
	}
	
	// Utility method
	private List<Album> albumsFromResult(ResultSet result) throws SQLException {
		List<Album> albums = new ArrayList<>();
		
		// For each row found
		while(result.next()) {
			// Fetch values
			int fetchedId = result.getInt("id");
			String fetchedTitle = result.getString("title");
			int fetchedCreator = result.getInt("creator_id");
			Date fetchedDate = result.getDate("creation_date");
			
			Album fetchedAlbum = new Album(fetchedId, fetchedTitle, fetchedCreator, fetchedDate);
			
			albums.add(fetchedAlbum);
		}
		
		return albums;
	}
}
