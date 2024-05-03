package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ImageDAO implements DAO<Image, Integer> {
	private Connection dbConnection;
	
	private PreparedStatement saveStatement;
	private PreparedStatement updateStatement;
	private PreparedStatement deleteStatement;
	private PreparedStatement getStatement;
	private PreparedStatement getAlbumThumbnail;
	
	public ImageDAO(Connection dbConnection) throws SQLException {
		this.dbConnection = dbConnection;
		
		saveStatement = dbConnection.prepareStatement("INSERT INTO image (file_path, title, uploader_id) VALUES (?, ?, ?);");
		updateStatement = dbConnection.prepareStatement("UPDATE image SET title=? WHERE id=?;");
		deleteStatement = dbConnection.prepareStatement("DELETE FROM image WHERE id=?;");
		getStatement = dbConnection.prepareStatement("SELECT * FROM image WHERE id=?;");
		getAlbumThumbnail = dbConnection.prepareStatement("SELECT i.* FROM image i JOIN image_album ia ON i.id=ia.image_id AND ia.album_id=? ORDER BY upload_date;");
	}

	@Override
	public Optional<Image> get(Integer id) throws SQLException {
		getStatement.setInt(1, id);
		
		ResultSet result = getStatement.executeQuery();
		
		List<Image> images = imagesFromResult(result);
		
		return images.isEmpty()
				? Optional.empty()
				: Optional.of(images.get(0));
	}
	
	public Optional<Image> getAlbumThumbnail(Album album) throws SQLException {
		getAlbumThumbnail.setInt(1, album.getId());
		
		ResultSet result = getAlbumThumbnail.executeQuery();
		
		List<Image> images = imagesFromResult(result);
		
		return images.isEmpty()
				? Optional.empty()
				: Optional.of(images.get(0));
	}

	@Override
	public void save(String... params) throws SQLException {
		saveStatement.setString(1, params[0]);
		saveStatement.setString(2, params[1]);
		saveStatement.setInt(3, Integer.parseInt(params[2]));
		
		saveStatement.executeUpdate();
	}

	@Override
	public void update(Image image) throws SQLException {
		// Set new fields values
		updateStatement.setString(1, image.getTitle());
		
		// Set id field value
		updateStatement.setInt(2, image.getId());
		
		updateStatement.executeUpdate();
	}

	@Override
	public void delete(Image image) throws SQLException {
		// Set id field value
		deleteStatement.setInt(1, image.getId());
		
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
	private List<Image> imagesFromResult(ResultSet result) throws SQLException {
		List<Image> images = new ArrayList<>();
		
		// For each row found
		while(result.next()) {
			// Fetch values
			int fetchedId = result.getInt("id");
			String fetchedPath = result.getString("file_path");
			String fetchedTitle = result.getString("title");
			int fetchedUploader = result.getInt("uploader_id");
			Date fetchedDate = result.getDate("upload_date");
			
			Image fetchedImage = new Image(fetchedId, fetchedPath, fetchedTitle, fetchedUploader, fetchedDate);
			
			images.add(fetchedImage);
		}
		
		return images;
	}
}
