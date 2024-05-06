package it.polimi.tiw.beans;

import java.util.Objects;

public class Comment {
	private int id;
	private String content;
	private int imageId;
	private int authorId;
	
	public Comment(int id, String content, int imageId, int authorId) {
		this.id = id;
		this.content = content;
		this.imageId = imageId;
		this.authorId = authorId;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getId() {
		return id;
	}
	
	public String getContent() {
		return content;
	}
	
	public int getImageId() {
		return imageId;
	}
	
	public int getAuthorId() {
		return authorId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
        if(!(obj instanceof Comment)) {
        	return false;
        }
        
        Comment other = (Comment) obj;
        
        return other.getId() == id && 
        	   other.getContent().equals(content) &&
        	   other.getImageId() == imageId &&
        	   other.getAuthorId() == authorId;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, content, imageId, authorId);
	}
}
