package it.polimi.tiw.dao;

import it.polimi.tiw.utils.SignUtility;

public class Person {
	private int id;
	private String username;
	private String email;
	private String passwordHash;
	
	public Person(int id, String username, String email, String password) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.passwordHash = SignUtility.hashAndSalt(password);
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setPassword(String password) {
		this.passwordHash = SignUtility.hashAndSalt(password);
	}
	
	public int getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPasswordHash() {
		return passwordHash;
	}

}
