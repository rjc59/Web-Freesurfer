package osg.Model;

public class JobsDTO{
	private String id;
	private String author;
	private String status;
	private String updated;
	
	
	
	
	public JobsDTO(String id, String author, String status, String updated){
		this.id=id;
		this.author=author;
		this.status=status;
		this.updated=updated;
	}




	public String getId() {
		return id;
	}




	public void setId(String id) {
		this.id = id;
	}




	public String getAuthor() {
		return author;
	}




	public void setAuthor(String author) {
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
	
	
	
	
}