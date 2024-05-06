package it.polimi.tiw.dao;

import java.sql.SQLException;
import java.util.Optional;

public interface DAO<T, ID> {
	Optional<T> get(ID id) throws SQLException;
	
	Optional<T> save(String... params) throws SQLException;

	void update(T t) throws SQLException;

	void delete(T t) throws SQLException;
	
	void close() throws SQLException;
}
