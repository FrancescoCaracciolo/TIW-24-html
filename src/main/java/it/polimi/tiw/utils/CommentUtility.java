package it.polimi.tiw.utils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Person;
import it.polimi.tiw.dao.PersonDAO;

public class CommentUtility {
	public static Map<Integer, Person> getCommentAuthorMap(List<Comment> comments, PersonDAO personDAO) throws SQLException {		
		Map<Integer, Person> commentAuthor = new HashMap<>();
		
		for (Comment comment : comments) {
			Optional<Person> author = personDAO.getCommentAuthor(comment);
			
			if (author.isPresent()) {
				commentAuthor.put(comment.getId(), author.get());
			}
		}
		
		return commentAuthor;
	}
}
