package it.polimi.tiw.beans;

import java.util.Date;

public class Image {
	private int id;
	private String filePath;
	private String title;
	private int uploaderId;
	private Date uploadDate;

	public Image(int id, String filePath, String title, int uploaderId, Date uploadDate) {
		this.id = id;
		this.filePath = filePath;
		this.uploaderId = uploaderId;
		this.uploadDate = uploadDate;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getId() {
		return id;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getUploaderId() {
		return uploaderId;
	}
	
	public Date getUploadDate() {
		return uploadDate;
	}
}
