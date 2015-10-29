package osgtesting;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import osgtesting.Model.JobsDTO;
import osgtesting.Model.UserDTO;
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
	private List<UserDTO> adminList=new ArrayList<UserDTO>();
	private DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
	private userDao userDao=new userDao();
	private boolean loggedout=true;
	private boolean admin=false;
	private MessageDigest digest;
	private UserDTO currentuser;
	String algorithm = "PBKDF2WithHmacSHA1";
	int derivedKeyLength = 64;
	SecretKeyFactory f;
	int iterations = 1000;

	public base() throws NoSuchAlgorithmException{
		digest = MessageDigest.getInstance("SHA-256");
		f=SecretKeyFactory.getInstance(algorithm);
		jobList.add(new JobsDTO("11eef764","Mr. Smith","Complete","9-28-2015 11:15"));
		jobList.add(new JobsDTO("129j3f64","Mrs. Smith","Error","9-26-2015 12:13"));
		jobList.add(new JobsDTO("3fj89932","Mr. Pitt","In progress","9-23-2015 07:35"));
		jobList.add(new JobsDTO("i344j90f","Mr. Stanford","Started","9-25-2015 18:25"));


	}




	public void upload(){
		Calendar c = Calendar.getInstance();
		Date date=c.getTime();
		String strDate=df.format(date);
		String status="Uploaded";
		String id=UUID.randomUUID().toString();
		jobList.add(new JobsDTO(id,name,status,strDate));
	}
	
	/**
	 * Creates a new userDTO containing the information from the form
	 * places new account into db
	 * 
	 */
	public void newAccount() throws SQLException{
		UIInput usercomp = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("accountform:userName");
		UIInput passcomp = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("accountform:firstpass");
		UIInput namecomp = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("accountform:firstName");
		UIInput surnamecomp = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("accountform:lastName");
		UIInput emailcomp = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("accountform:email");
		UIInput instcomp = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("accountform:inst");
		UIInput phonecomp = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("accountform:phoneNumber");

		String passText=(String)passcomp.getValue();
		String newPass=null,newSalt=null;



		try{

			byte[] passHash = digest.digest(passText.getBytes("UTF-8"));
			String passHashStr = new String(passHash, "UTF-8");
			//Create Salt
			//Get current time
			Date creationTime = new Date();
			byte[] salt = digest.digest(creationTime.toString().getBytes("UTF-8"));
			newSalt=new String(salt);
			//Hash password plus salt with pbkdf2

			KeySpec spec = new PBEKeySpec(passHashStr.toCharArray(), salt, iterations, derivedKeyLength);
			byte[] passwordToStore = f.generateSecret(spec).getEncoded();
			newPass= new String(passwordToStore);
		}catch(Exception e){
			e.printStackTrace();
		}

		System.out.println(newPass);
		System.out.println(newSalt);


		UserDTO newAcct = new UserDTO(((String) usercomp.getValue()),
				newPass,
				((String) namecomp.getValue()),
				((String) surnamecomp.getValue()),
				((String) emailcomp.getValue()),
				((String) instcomp.getValue()),
				((String) phonecomp.getValue()),
				newSalt);

		userDao.Write(newAcct);


		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
			userDao.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 

	
	/**
	 * Retreives values from login boxes.
	 * Queries DB for match of username.
	 * Performs password check.
	 * @throws SQLException
	 */
	public void login() throws SQLException{

		UIInput usercomp = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("mainform:username");
		UIInput passcomp = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("mainform:password");

		String uname= (String) usercomp.getValue();
		String pass= (String) passcomp.getValue();

		ResultSet result=userDao.login(uname, pass);

		if(!result.next()){
			System.out.println("NO USERNAME");
			return;
		}
		else {  

			if (checkPassword(result.getString(2).trim(), result.getString(3).trim(),pass))
			{ 
				System.out.println("Password matches");	 
				message="Welcome "+uname;
				username=uname;
				if(username.equals("admin")){
					admin=true;
				}
			}
			else
			{
				System.out.println("Password does not match");
				return;
			}
		}

		try {


			loggedout=false;
			FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Retreives account info for current user.
	 * @throws SQLException
	 */
	public void editAccount() throws SQLException{
		ResultSet account=userDao.edit(username);
		if(!account.next()){
			System.out.println("whoops     "); 
			return;
		}
		currentuser=new UserDTO(account.getString(2),account.getString(8),account.getString(3),account.getString(4),account.getString(5),account.getString(6),account.getString(7),account.getString(9));

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("account.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Retrieves updated information from form inputs.
	 * Creates new object based on such information.
	 * Updates row in db table with new information.
	 * 
	 * @throws SQLException
	 */
	public void updateAccount() throws SQLException{
		UIInput usercomp = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("accountupdateform:userName");
		UIInput namecomp = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("accountupdateform:firstName");
		UIInput surnamecomp = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("accountupdateform:lastName");
		UIInput emailcomp = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("accountupdateform:email");
		UIInput instcomp = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("accountupdateform:inst");
		UIInput phonecomp = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("accountupdateform:phoneNumber");

		ResultSet result=userDao.edit((String)usercomp.getValue());
		if(result.next()){
			UserDTO update = new UserDTO();
			update.setUserName((String)usercomp.getValue());
			update.setName((String)namecomp.getValue());
			update.setSurname((String)surnamecomp.getValue());
			update.setEmail((String)emailcomp.getValue());
			update.setInst((String)instcomp.getValue());
			update.setPhone((String)phonecomp.getValue());
			System.out.println(update.getName());

			userDao.update(update);
		}

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * sets current user based on username
	 * @throws SQLException
	 */
	public void editPass() throws SQLException{
		ResultSet account=userDao.edit(username);
		if(!account.next()){
			System.out.println("whoops     "); 
			return;
		}
		currentuser=new UserDTO(account.getString(2),account.getString(8),account.getString(3),account.getString(4),account.getString(5),account.getString(6),account.getString(7),account.getString(9));
		System.out.println(currentuser.getName());

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("password.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Obtains info from input.
	 * Creates new encoded password and salt.
	 * Updates values in db
	 */
	public void updatePassword(){

		UIInput passcomp = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("passwordupdateform:newPass");
		String passText=(String)passcomp.getValue();
		System.out.println(passText);
		String newPass=null,newSalt=null;



		try{

			byte[] passHash = digest.digest(passText.getBytes("UTF-8"));
			String passHashStr = new String(passHash, "UTF-8");
			//Create Salt
			//Get current time
			Date creationTime = new Date();
			byte[] salt = digest.digest(creationTime.toString().getBytes("UTF-8"));
			newSalt=new String(salt);
			//Hash password plus salt with pbkdf2

			KeySpec spec = new PBEKeySpec(passHashStr.toCharArray(), salt, iterations, derivedKeyLength);
			byte[] passwordToStore = f.generateSecret(spec).getEncoded();
			newPass= new String(passwordToStore);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(newPass);
		System.out.println(newSalt);


		userDao.updatePassword(currentuser.getUserName(),newPass,newSalt);

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Logs user out of system.
	 * Nullifys current user object.
	 * 
	 */
	public void logout(){
		try {
			currentuser=null;
			message="Intro message"; 
			loggedout=true;
			username="";
			FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Validates password for new accounts, Must be 7 or more characters.
	 * Must match confirm password entry.
	 * 
	 * @param e The event triggerd after initial validation of inputs
	 * @throws SQLException
	 */
	public void validatePass(ComponentSystemEvent e) throws SQLException{

		FacesContext fc=FacesContext.getCurrentInstance();
		UIComponent comp= e.getComponent();

		UIInput uname=(UIInput) comp.findComponent("userName");
		String username=(String) uname.getLocalValue();
		String unameId=uname.getClientId();

		UIInput passIn=(UIInput) comp.findComponent("firstpass");
		String pass=passIn.getLocalValue() == null ? ""
				: passIn.getLocalValue().toString();
		String passID=passIn.getClientId();

		UIInput passIncon=(UIInput) comp.findComponent("secondpass");
		String conpass=passIncon.getLocalValue() == null ? ""
				: passIncon.getLocalValue().toString();

		if(pass.isEmpty() || conpass.isEmpty())
			return;

		ResultSet rs = userDao.edit(username);
		if(rs.next()){
			FacesMessage msg = new FacesMessage("Username already exists");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			fc.addMessage(unameId, msg);
			fc.renderResponse();
		}

		if(pass.length()<6){
			FacesMessage msg = new FacesMessage("Password must be atleast 6 characters");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			fc.addMessage(passID, msg);
			fc.renderResponse();
		}

		if(!pass.equals(conpass)){
			FacesMessage msg = new FacesMessage("Passwords must match");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			fc.addMessage(passID, msg);
			fc.renderResponse();
		}

	}

	/**
	 * Form checking of reset password page. Confrims current password.
	 * Error checks new passwords
	 * 
	 * @param e Event triggered post validation of inputs.
	 */
	public void revalidatePass(ComponentSystemEvent e){

		FacesContext fc=FacesContext.getCurrentInstance();
		UIComponent comp= e.getComponent();

		UIInput passOld=(UIInput) comp.findComponent("oldPass");
		String oldPass=passOld.getLocalValue() == null ? ""
				: passOld.getLocalValue().toString();
		String oldpassID=passOld.getClientId();

		UIInput passIn=(UIInput) comp.findComponent("newPass");
		String pass=passIn.getLocalValue() == null ? ""
				: passIn.getLocalValue().toString();
		String passID=passIn.getClientId();

		UIInput passIncon=(UIInput) comp.findComponent("conPass");
		String conpass=passIncon.getLocalValue() == null ? ""
				: passIncon.getLocalValue().toString();

		if(pass.isEmpty() || conpass.isEmpty() ||oldPass.isEmpty())
			return;

		if(!checkPassword(currentuser.getPass().trim(),currentuser.getSalt().trim(),oldPass)){
			System.out.println("INCORRECT PASSWORD");
			FacesMessage msg = new FacesMessage("Incorrect Password");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			fc.addMessage(oldpassID, msg);
			fc.renderResponse();
		}

		if(pass.length()<6){
			FacesMessage msg = new FacesMessage("Password must be atleast 6 characters");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			fc.addMessage(passID, msg);
			fc.renderResponse();
		}

		if(!pass.equals(conpass)){
			FacesMessage msg = new FacesMessage("Passwords must match");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			fc.addMessage(passID, msg);
			fc.renderResponse();
		}

	}

	/**
	 * Validation of admin credentials
	 * @throws SQLException
	 */
	public void adminPower() throws SQLException{
		ResultSet account=userDao.edit(username);
		if(!account.next()){
			System.out.println("whoops     "); 
			return;
		}
		currentuser=new UserDTO(account.getString(2),account.getString(8),account.getString(3),account.getString(4),account.getString(5),account.getString(6),account.getString(7),account.getString(9));
		System.out.println(currentuser.getName());

		adminList=userDao.read();
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("admin.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Compares the entered password with the encoded one contained
	 * within the db
	 * 
	 * @param retpass Encoded password retrieved from database
	 * @param retsalt Salt retrieved from database
	 * @param checkpass Plain string of entered password
	 * @return true if passwords match, false otherwise
	 */
public boolean checkPassword(String retpass, String retsalt,String checkpass){
	byte[] oldsalt=null,oldpass=null,attemptToCheck=null;
	try{ 
		String attemptText=checkpass;
		oldsalt=retsalt.getBytes();
		oldpass=retpass.getBytes();
		byte[] attemptHash = digest.digest(attemptText.getBytes("UTF-8"));
		String attemptHashStr = new String(attemptHash, "UTF-8");
		KeySpec attemptSpec = new PBEKeySpec(attemptHashStr.toCharArray(), oldsalt, iterations, derivedKeyLength);
		attemptToCheck = f.generateSecret(attemptSpec).getEncoded();


	}catch(Exception e){
		e.printStackTrace();
	}
	return Arrays.equals(oldpass, attemptToCheck);
}

public UserDTO getCurrentuser() {
	return currentuser;
}


public void setCurrentuser(UserDTO currentuser) {
	this.currentuser = currentuser;
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




public boolean isAdmin() {
	return admin;
}




public void setAdmin(boolean admin) {
	this.admin = admin;
}




public List<UserDTO> getAdminList() {
	return adminList;
}




public void setAdminList(List<UserDTO> adminList) {
	this.adminList = adminList;
}


}