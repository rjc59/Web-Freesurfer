package osgtesting.Model;

import osgtesting.Model.UserDTO;
public class JobsDTO{
	private String id;
	private UserDTO author;
	private String status;
	private String updated;	
	private String name;
	public JobsDTO(String id, UserDTO author, String status, String updated, String name){
		this.id=id;
		this.author=author;
		this.status=status;
		this.updated=updated;
		this.name=name;
	}

	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public UserDTO getAuthor() {
		return author;
	}


	public void setAuthor(UserDTO author) {
		this.author = author;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getUpdated() {
		return updated;
	}


	public void setUpdated(String updated) {
		this.updated = updated;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}	
}
