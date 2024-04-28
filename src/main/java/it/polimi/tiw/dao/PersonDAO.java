package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PersonDAO implements DAO<Person, String> {
	private Connection dbConnection;
	private PreparedStatement saveStatement;
	private PreparedStatement updateStatement;
	private PreparedStatement deleteStatement;
	private PreparedStatement getStatement;
	private PreparedStatement getFromEmailStatement;
	
	public PersonDAO(Connection dbConnection) throws SQLException {
		this.dbConnection = dbConnection;
		
		saveStatement = dbConnection.prepareStatement("INSERT INTO person (username, email, password_hash) VALUES (?, ?, ?);");
		updateStatement = dbConnection.prepareStatement("UPDATE person SET username=?, email=?, password_hash=? WHERE username=?;");
		deleteStatement = dbConnection.prepareStatement("DELETE FROM person WHERE username=?;");
		getStatement = dbConnection.prepareStatement("SELECT * FROM person WHERE username=?;");
		getFromEmailStatement = dbConnection.prepareStatement("SELECT * FROM person WHERE email=?;");
	}
	
	public Optional<Person> get(String username) throws SQLException {
		getStatement.setString(1, username);
		
		ResultSet result = getStatement.executeQuery();
		
		// Check if the query fetched no rows
		if (!result.isBeforeFirst())
			return Optional.empty();
		else {
			Person fetchedPerson = personFromResult(result);
			return Optional.ofNullable(fetchedPerson);
		}
	}
	
	public Optional<Person> getFromEmail(String email) throws SQLException {
		getFromEmailStatement.setString(1, email);
		
		ResultSet result = getFromEmailStatement.executeQuery();
		
		// Check if the query fetched no rows
		if (!result.isBeforeFirst())
			return Optional.empty();
		else {
			Person fetchedPerson = personFromResult(result);
			return Optional.ofNullable(fetchedPerson);
		}
	}

	public void save(Person person) throws SQLException {
		saveStatement.setString(1, person.getUsername());
		saveStatement.setString(2, person.getEmail());
		saveStatement.setString(3, person.getPasswordHash());
		
		saveStatement.executeUpdate();
	}

	public void update(Person person, String[] params) throws SQLException {
		updateStatement.setString(1, params[0]);
		updateStatement.setString(2, params[1]);
		updateStatement.setString(3, params[2]);
		updateStatement.setString(4, person.getUsername());
		
		updateStatement.executeUpdate();
		
	}

	public void delete(Person person) throws SQLException {
		deleteStatement.setString(1, person.getUsername());
		
		deleteStatement.executeUpdate();
		
	}

	public void close() throws SQLException {
		saveStatement.close();
		updateStatement.close();
		deleteStatement.close();
	}

	// Utility methods
	private Person personFromResult(ResultSet resultSet) throws SQLException {
		resultSet.next();
		
		String fetchedUsername = resultSet.getString("username");
		String fetchedEmail = resultSet.getString("email");
		String fetchedPasswordHash = resultSet.getString("password_hash");
		
		return new Person(fetchedUsername, fetchedEmail, fetchedPasswordHash);
	}
}
