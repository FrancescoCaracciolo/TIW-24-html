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
import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.Person;
import it.polimi.tiw.utils.DAOUtility;
import it.polimi.tiw.utils.Pair;

public class ImageDAO implements DAO<Image, Integer> {
	private Connection dbConnection;
	
	private PreparedStatement saveStatement;
	private PreparedStatement updateStatement;
	private PreparedStatement deleteStatement;
	private PreparedStatement getStatement;
	private PreparedStatement getPersonImagesStatement;
	private PreparedStatement getImageFromPathStatement;
	private PreparedStatement getAlbumImagesStatement;
	private PreparedStatement getAlbumImagesWithCommentsStatement;
	
	public ImageDAO(Connection dbConnection) throws SQLException {
		this.dbConnection = dbConnection;
		
		saveStatement = dbConnection.prepareStatement("INSERT INTO image (file_path, title, description, uploader_id) VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
		updateStatement = dbConnection.prepareStatement("UPDATE image SET title=? WHERE id=?;");
		deleteStatement = dbConnection.prepareStatement("DELETE FROM image WHERE id=?;");
		getStatement = dbConnection.prepareStatement("SELECT * FROM image WHERE id=?;");
		getImageFromPathStatement = dbConnection.prepareStatement("SELECT * FROM image WHERE path = ?");
		getPersonImagesStatement = dbConnection.prepareStatement("SELECT * FROM image WHERE uploader_id = ?");
		getAlbumImagesStatement = dbConnection.prepareStatement("SELECT i.* FROM image i JOIN image_album ia ON i.id=ia.image_id WHERE ia.album_id = ? ORDER BY i.upload_date DESC, i.id DESC");
		getAlbumImagesWithCommentsStatement = dbConnection.prepareStatement("SELECT * "
				+ "FROM (image i JOIN image_album ia ) LEFT JOIN (text_comment c JOIN person uploader JOIN person author) "
				+ "ON i.id=ia.image_id AND i.uploader_id = uploader.id AND c.image_id = i.id AND c.author_id = author.id "
				+ "WHERE ia.album_id = ? ORDER BY i.upload_date DESC, i.id DESC; ");
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
	
	@Override
	public Optional<Image> save(String... params) throws SQLException {
		saveStatement.setString(1, params[0]);
		saveStatement.setString(2, params[1]);
		saveStatement.setString(3, params[2]);
		saveStatement.setInt(4, Integer.parseInt(params[3]));
		
		Optional<Image> newImage = DAOUtility.tryToSave(this, saveStatement);
		
		return newImage;
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
	
	public Optional<Image> getFromPath(String path) throws SQLException {
		getImageFromPathStatement.setString(1, path);
		ResultSet result = getImageFromPathStatement.executeQuery();
		
		List<Image> images = imagesFromResult(result);
		
		return images.isEmpty()
				? Optional.empty()
				: Optional.of(images.get(0));
	}

	@Override
	public void close() throws SQLException {
		getStatement.close();
		saveStatement.close();
		updateStatement.close();
		deleteStatement.close();
		getImageFromPathStatement.close();
		getPersonImagesStatement.close();
		getAlbumImagesStatement.close();
		getAlbumImagesWithCommentsStatement.close();
	}
	
	public List<Image> getPersonImages(Person person) throws SQLException {
		getPersonImagesStatement.setInt(1, person.getId());
		ResultSet result = getPersonImagesStatement.executeQuery();
		return this.imagesFromResult(result);
	}
	
	public List<Image> getAlbumImages(Album album) throws SQLException {
		getAlbumImagesStatement.setInt(1, album.getId());
		ResultSet result = getAlbumImagesStatement.executeQuery();
		return this.imagesFromResult(result);
	}
	
	public LinkedHashMap<Image, Pair<Person, List<Pair<Person, Comment>>>> getAlbumImagesWithComments(Album album) throws SQLException {
		LinkedHashMap<Image, Pair<Person, List<Pair<Person, Comment>>>> images = new LinkedHashMap<>();
		getAlbumImagesWithCommentsStatement.setInt(1, album.getId());
		ResultSet result = getAlbumImagesWithCommentsStatement.executeQuery();
		while(result.next()) {
			// Fetch values
			Image fetchedImage = imageFromResult(result, "i.");
			Person fetchedUploader = PersonDAO.fetchPersonFromResult(result, "uploader.");
			Person fetchedAuthor = PersonDAO.fetchPersonFromResult(result, "author.");
			Comment fetchedComment = CommentDAO.commentFromResult(result, "c.");
			
			List<Pair<Person, Comment>> commentList;
			if (images.containsKey(fetchedComment)) {
				commentList = images.get(fetchedImage).second();
			} else {
				commentList = new ArrayList<Pair<Person, Comment>>();
				images.put(fetchedImage, new Pair<>(fetchedUploader, commentList));
			}
			if (fetchedComment.getContent() != null) {
				commentList.add(new Pair<>(fetchedAuthor, fetchedComment));
			}
		}
		return images;
	}
	
	// Utility method
	private List<Image> imagesFromResult(ResultSet result) throws SQLException {
		List<Image> images = new ArrayList<>();
		
		// For each row found
		while(result.next()) {
			Image fetchedImage = imageFromResult(result, "");
			images.add(fetchedImage);
		}
		
		return images;
	}
	
	public static Image imageFromResult(ResultSet result, String alias) throws SQLException {
		// Fetch values
		int fetchedId = result.getInt(alias + "id");
		String fetchedPath = result.getString(alias + "file_path");
		String fetchedTitle = result.getString(alias + "title");
		String fetchedDescription = result.getString(alias + "description");
		int fetchedUploader = result.getInt(alias + "uploader_id");
		Date fetchedDate = result.getDate(alias + "upload_date");
		
		return new Image(fetchedId, fetchedPath, fetchedTitle, fetchedDescription, fetchedUploader, fetchedDate);
		
	}
}
