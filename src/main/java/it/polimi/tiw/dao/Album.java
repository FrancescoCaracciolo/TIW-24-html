package it.polimi.tiw.dao;

import java.util.Date;

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
}
