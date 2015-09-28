package osg;

import java.io.IOException;

import javax.faces.context.FacesContext;

public class Main {
	private String message=new String("TESTIsdfNG");

	public static void main(String[] args){
		
	}
	
	
	public void newAccount(){
		message=new String("Account Created");
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	

}
