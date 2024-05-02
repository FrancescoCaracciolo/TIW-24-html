package it.polimi.tiw.utils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.dao.Person;
import it.polimi.tiw.dao.PersonDAO;

public class SessionUtility {
	HttpSession session;

	public SessionUtility (HttpSession session) {
		this.session = session;
	}
	
	public Optional<Person> getPerson(PersonDAO personDAO) throws SQLException {
		Integer id = (Integer) this.session.getAttribute("Id");
		Optional<Person> result = Optional.empty();
		
		if (id == null) {
			return result;
		} else {
			return personDAO.get(id);
		}
	}
	
	public Optional<Person> redirectOnInvalidSession(HttpServletResponse response, PersonDAO personDAO) throws SQLException, IOException {
		Optional<Person> person = this.getPerson(personDAO);
		
		if (person.isEmpty()) {
			response.sendRedirect("signin");
		}
		
		return person;
	}
	
}
