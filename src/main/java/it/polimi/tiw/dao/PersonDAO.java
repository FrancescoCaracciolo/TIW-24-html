package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.Person;
import it.polimi.tiw.utils.DAOUtility;
import it.polimi.tiw.utils.SignUtility;

public class PersonDAO implements DAO<Person, Integer> {
	private Connection dbConnection;
	
	private PreparedStatement saveStatement;
	private PreparedStatement updateStatement;
	private PreparedStatement deleteStatement;
	private PreparedStatement getFromIdStatement;
	private PreparedStatement getFromUsernameStatement;
	private PreparedStatement getFromEmailStatement;
	private PreparedStatement getFromEmailOrUsernameStatement;
	private PreparedStatement getCommentAuthorStatement;
	
	public PersonDAO(Connection dbConnection) throws SQLException {
		this.dbConnection = dbConnection;
		
		saveStatement = dbConnection.prepareStatement("INSERT INTO person (username, email, password_hash) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
		updateStatement = dbConnection.prepareStatement("UPDATE person SET username=?, email=?, password_hash=? WHERE id=?;");
		deleteStatement = dbConnection.prepareStatement("DELETE FROM person WHERE id=?;");
		getFromIdStatement = dbConnection.prepareStatement("SELECT * FROM person WHERE id=?;");
		getFromUsernameStatement = dbConnection.prepareStatement("SELECT * FROM person WHERE username=?;");
		getFromEmailStatement = dbConnection.prepareStatement("SELECT * FROM person WHERE email=?;");
		getFromEmailOrUsernameStatement = dbConnection.prepareStatement("SELECT * FROM person WHERE username = ? or email = ?");
		getCommentAuthorStatement = dbConnection.prepareStatement("SELECT p.* FROM text_comment c JOIN person p ON c.author_id=p.id WHERE c.id=?;");
	}
	
	@Override
	public Optional<Person> get(Integer id) throws SQLException {
		getFromIdStatement.setInt(1, id);
		
		ResultSet result = getFromIdStatement.executeQuery();
		
		return personFromResult(result);
	}

	public Optional<Person> getCommentAuthor(Comment comment) throws SQLException {
		getCommentAuthorStatement.setInt(1, comment.getId());
		
		ResultSet result = getCommentAuthorStatement.executeQuery();
		
		return personFromResult(result);
	}
	
	public Optional<Person> getFromEmailOrUsername(String emailOrUsername) throws SQLException {
		getFromEmailOrUsernameStatement.setString(1, emailOrUsername);
		getFromEmailOrUsernameStatement.setString(2, emailOrUsername);
		
		ResultSet result = getFromEmailOrUsernameStatement.executeQuery();
		
		return personFromResult(result);
	}
	
	public Optional<Person> getFromUsername(String username) throws SQLException {
		getFromUsernameStatement.setString(1, username);
		
		ResultSet result = getFromUsernameStatement.executeQuery();
		
		return personFromResult(result);
	}
	
	public Optional<Person> getFromEmail(String email) throws SQLException {
		getFromEmailStatement.setString(1, email);
		
		ResultSet result = getFromEmailStatement.executeQuery();
		
		return personFromResult(result);
	}

	@Override
	public Optional<Person> save(String... params) throws SQLException {
		String hashedSaltedPassword = SignUtility.hashAndSalt(params[2]);
		
		saveStatement.setString(1, params[0]);
		saveStatement.setString(2, params[1]);
		saveStatement.setString(3, hashedSaltedPassword);
		
		Optional<Person> newPerson = DAOUtility.tryToSave(this, saveStatement);
		
		return newPerson;
	}

	@Override
	public void update(Person person) throws SQLException {
		// Set new fields values
		updateStatement.setString(1, person.getUsername());
		updateStatement.setString(2, person.getEmail());
		
		// Set id field value
		updateStatement.setInt(4, person.getId());
		
		updateStatement.executeUpdate();
	}

	@Override
	public void delete(Person person) throws SQLException {
		// Set id field value
		deleteStatement.setInt(1, person.getId());
		
		deleteStatement.executeUpdate();
	}

	@Override
	public void close() throws SQLException {
		getFromIdStatement.close();
		getFromUsernameStatement.close();
		getFromEmailStatement.close();
		saveStatement.close();
		updateStatement.close();
		deleteStatement.close();
		getFromEmailOrUsernameStatement.close();
		getCommentAuthorStatement.close();
	}

	private Optional<Person> personFromResult (ResultSet result) throws SQLException {
		return personFromResult(result, "");
	}
	// Utility method
	public static Optional<Person> personFromResult(ResultSet result, String alias) throws SQLException {
		// If the query fetched no rows
		if (!result.isBeforeFirst())
			return Optional.empty();
		// If the query fetched some rows (should be one)
		else {
			// Move the result index to the first row
			result.next();
			
			Person fetchedPerson = fetchPersonFromResult(result, alias);
			
			return Optional.ofNullable(fetchedPerson);
		}
	}
	
	public static Person fetchPersonFromResult(ResultSet result, String alias) throws SQLException {
		int fetchedId = result.getInt(alias + "id");
		String fetchedUsername = result.getString(alias + "username");
		String fetchedEmail = result.getString(alias + "email");
		String fetchedPasswordHash = result.getString(alias + "password_hash");
		
		return new Person(fetchedId, fetchedUsername, fetchedEmail, fetchedPasswordHash);
	}
}
