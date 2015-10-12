package osgtesting;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import osgtesting.Model.JobsDTO;
import osgtesting.dao.userDao;

public class base {
	private String message=new String("Intro message");
	private String username=new String("");
	private String pass=new String("");
	private String name= new String("");
	private String surname=new String("");
	private String inst=new String("");
	private String email=new String("");
	private String phone=new String("");
	private List<JobsDTO> jobList=new ArrayList<JobsDTO>();
	private DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
	private userDao userDao=new userDao();
	private boolean loggedout=true;
	
	public base(){
		jobList.add(new JobsDTO("11eef764","Mr. Smith","Complete","9-28-2015 11:15"));
		jobList.add(new JobsDTO("129j3f64","Mrs. Smith","Error","9-26-2015 12:13"));
		jobList.add(new JobsDTO("3fj89932","Mr. Pitt","In progress","9-23-2015 07:35"));
		jobList.add(new JobsDTO("i344j90f","Mr. Stanford","Started","9-25-2015 18:25"));


	}
	
	
	public void newAccount(){
		System.out.println("Test");
		UIInput component = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("accountform:firstName");
		message= new String("Hello "+component.getValue());
		name= (String) component.getValue();
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
			userDao.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	public void upload(){
		Calendar c = Calendar.getInstance();
		Date date=c.getTime();
		String strDate=df.format(date);
		String status="Uploaded";
		String id=UUID.randomUUID().toString();
		jobList.add(new JobsDTO(id,name,status,strDate));
	}
	
	public void login(){
		try {
			loggedout=false;
			FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void logout(){
		try {
			loggedout=true; 
			FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void validatePass(ComponentSystemEvent e){
		
		FacesContext fc=FacesContext.getCurrentInstance();
		UIComponent comp= e.getComponent();
		
		UIInput passIn=(UIInput) comp.findComponent("firstpass");
		String pass=passIn.getLocalValue() == null ? ""
				: passIn.getLocalValue().toString();
		String passID=passIn.getClientId();
		
		UIInput passIncon=(UIInput) comp.findComponent("secondpass");
		String conpass=passIncon.getLocalValue() == null ? ""
				: passIncon.getLocalValue().toString();
		
		if(pass.isEmpty() || conpass.isEmpty())
			return;
		
	if(!pass.equals(conpass)){
		FacesMessage msg = new FacesMessage("Passwords must match");
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		fc.addMessage(passID, msg);
		fc.renderResponse();
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


	public boolean isLoggedout() {
		return loggedout;
	}


	public void setLoggedout(boolean loggedout) {
		this.loggedout = loggedout;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPass() {
		return pass;
	}


	public void setPass(String pass) {
		this.pass = pass;
	}


	public String getInst() {
		return inst;
	}


	public void setInst(String inst) {
		this.inst = inst;
	}
	

}