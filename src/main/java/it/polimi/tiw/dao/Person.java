package it.polimi.tiw.dao;

public class Person {
	private String username;
	private String email;
	private String passwordHash;
	
	public Person(String username, String email, String passwordHash) {
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
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
