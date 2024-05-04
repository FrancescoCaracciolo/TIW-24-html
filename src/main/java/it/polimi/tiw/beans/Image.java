package it.polimi.tiw.beans;

import java.util.Date;
import java.util.Objects;

public class Image {
	private int id;
	private String filePath;
	private String title;
	private int uploaderId;
	private Date uploadDate;

	public Image(int id, String filePath, String title, int uploaderId, Date uploadDate) {
		this.id = id;
		this.title = title;
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
        if(!(obj instanceof Image)) {
        	return false;
        }
        
        Image other = (Image) obj;
        
        return other.getId() == id && 
        	   other.getTitle().equals(title) &&
        	   other.getFilePath().equals(filePath) &&
        	   other.getUploadDate().equals(uploadDate) &&
        	   other.getUploaderId() == uploaderId;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, title, filePath, uploadDate, uploaderId);
	}
}
