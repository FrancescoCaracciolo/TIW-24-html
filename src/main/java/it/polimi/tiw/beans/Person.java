package it.polimi.tiw.beans;

import java.util.Objects;

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

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
        if(!(obj instanceof Image)) {
        	return false;
        }
        
        Person other = (Person) obj;
        
        return other.getId() == id && 
        	   other.getEmail().equals(email) &&
        	   other.getUsername().equals(username) &&
        	   other.getPasswordHash().equals(passwordHash);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, username, email, passwordHash);
	}
}
