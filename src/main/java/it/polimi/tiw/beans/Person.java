package it.polimi.tiw.beans;

public class Person {
	private int id;
	private String username;
	private String email;
	private String passwordHash;
	
	public Person(int id, String username, String email, String passwordHash) {
		this.id = id;
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
