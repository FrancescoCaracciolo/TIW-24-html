package it.polimi.tiw.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import it.polimi.tiw.dao.DAO;

public class DAOUtility {
	public static <T> Optional<T> tryToSave (DAO<T, Integer> objDAO, PreparedStatement saveStatement) throws SQLException {
		int affectedRows = saveStatement.executeUpdate();
		
		if (affectedRows == 0) {
            return Optional.empty();
        } else {
        	try (ResultSet generatedKeys = saveStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                	int id = generatedKeys.getInt(1);
                	return objDAO.get(id);
                } else {
                    return Optional.empty();
                }
            }
        }
	}
}
