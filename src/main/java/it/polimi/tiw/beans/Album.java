package it.polimi.tiw.beans;

import java.util.Date;
import java.util.Objects;

public class Album {
	private int id;
	private String title;
	private int creatorId;
	private Date creationDate; 

	public Album(int id, String title, int creatorId, Date creationDate) {
		this.id = id;
		this.title = title;
		this.creatorId = creatorId;
		this.creationDate = creationDate;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getCreatorId() {
		return creatorId;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
        if(!(obj instanceof Album)) {
        	return false;
        }
        
        Album other = (Album) obj;
        
        return other.getId() == id && 
        	   other.getTitle().equals(title) &&
        	   other.getCreatorId() == creatorId &&
        	   other.getCreationDate().equals(creationDate);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, title, creationDate, creatorId);
	}
}
