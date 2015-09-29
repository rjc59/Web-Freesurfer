package osgtesting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import osgtesting.Model.JobsDTO;

public class base {
	private String message=new String("Intro message");
	private String name= new String("");
	private String surname=new String("");
	private String email=new String("");
	private String phone=new String("");
	private List<JobsDTO> jobList=new ArrayList<JobsDTO>();
	
	public base(){
		jobList.add(new JobsDTO("11eef764","Mr. Smith","Complete","9-28-2015 11:15"));
		jobList.add(new JobsDTO("129j3f64","Mrs. Smith","Error","9-26-2015 12:13"));
		jobList.add(new JobsDTO("3fj89932","Mr. Pitt","In progress","9-23-2015 07:35"));
		jobList.add(new JobsDTO("i344j90f","Mr. Stanford","Started","9-25-2015 18:25"));


	}
	
	
	public void newAccount(){
		System.out.println("Test");
		UIInput component = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("j_idt6:firstName");
		message= new String("Hello "+component.getValue());
		name= (String) component.getValue();
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


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<JobsDTO> getJobList() {
		return jobList;
	}


	public void setJobList(List<JobsDTO> jobList) {
		this.jobList = jobList;
	}


	public String getSurname() {
		return surname;
	}


	public void setSurname(String surname) {
		this.surname = surname;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}
	

}