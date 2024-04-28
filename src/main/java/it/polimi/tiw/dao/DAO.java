package it.polimi.tiw.dao;

import java.sql.SQLException;
import java.util.Optional;

public interface DAO<T, IdOfT> {
	Optional<T> get(IdOfT id) throws SQLException;

	void save(T t) throws SQLException;

	void update(T t, String[] params) throws SQLException;

	void delete(T t) throws SQLException;
	
	void close() throws SQLException;
}
