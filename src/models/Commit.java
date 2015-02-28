package models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Commit {
	private String hash;
	
	private String shortMessage;
	
	private String message;
	
	private String author;
	
	private String email;
	
	public String getHash() {
		return hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public String getShortMessage() {
		return shortMessage;
	}
	
	public void setShortMessage(String shortMessage) {
		this.shortMessage = shortMessage;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
}
