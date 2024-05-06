package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.utils.DAOUtility;

public class CommentDAO implements DAO<Comment, Integer> {
	Connection dbConnection;
	
	private PreparedStatement saveStatement;
	private PreparedStatement updateStatement;
	private PreparedStatement deleteStatement;
	private PreparedStatement getStatement;
	private PreparedStatement getFromImageStatement;
	
	public CommentDAO(Connection dbConnection) throws SQLException {
		this.dbConnection = dbConnection;
		
		saveStatement = dbConnection.prepareStatement("INSERT INTO text_comment (content, image_id, author_id) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
		updateStatement = dbConnection.prepareStatement("UPDATE text_comment SET content=? WHERE id=?;");
		deleteStatement = dbConnection.prepareStatement("DELETE FROM text_comment WHERE id=?;");
		getStatement = dbConnection.prepareStatement("SELECT * FROM text_comment WHERE id=?;");
		getFromImageStatement = dbConnection.prepareStatement("SELECT * FROM text_comment WHERE image_id=?;");
	}

	@Override
	public Optional<Comment> get(Integer id) throws SQLException {
		getStatement.setInt(1, id);
		
		ResultSet result = getStatement.executeQuery();
		
		List<Comment> comments = commentsFromResult(result);
		
		return comments.isEmpty()
				? Optional.empty()
				: Optional.of(comments.get(0));	
	}
	
	public List<Comment> getFromImage(Image image) throws SQLException {
		getFromImageStatement.setInt(1, image.getId());
		
		ResultSet result = getFromImageStatement.executeQuery();
		
		return commentsFromResult(result);	
	}

	@Override
	public Optional<Comment> save(String... params) throws SQLException {
		saveStatement.setString(1, params[0]);
		saveStatement.setInt(2, Integer.parseInt(params[1]));
		saveStatement.setInt(3, Integer.parseInt(params[2]));
		
		Optional<Comment> newComment = DAOUtility.tryToSave(this, saveStatement);
		
		return newComment;
	}

	@Override
	public void update(Comment comment) throws SQLException {
		// Set new fields values
		updateStatement.setString(1, comment.getContent());
		
		// Set id field value
		updateStatement.setInt(2, comment.getId());
		
		updateStatement.executeUpdate();
	}

	@Override
	public void delete(Comment comment) throws SQLException {
		// Set id field value
		deleteStatement.setInt(1, comment.getId());
		
		deleteStatement.executeUpdate();
	}

	@Override
	public void close() throws SQLException {
		getStatement.close();
		saveStatement.close();
		updateStatement.close();
		deleteStatement.close();
		getFromImageStatement.close();
	}

	// Utility method
	private List<Comment> commentsFromResult(ResultSet result) throws SQLException {
		List<Comment> comments = new ArrayList<>();
		
		// For each row found
		while(result.next()) {
			// Fetch values
			int fetchedId = result.getInt("id");
			String fetchedContent = result.getString("content");
			int fetchedImage = result.getInt("image_id");
			int fetchedAuthor = result.getInt("author_id");
			
			Comment fetchedComment = new Comment(fetchedId, fetchedContent, fetchedImage, fetchedAuthor);
			
			comments.add(fetchedComment);
		}
		
		return comments;
	}
}
