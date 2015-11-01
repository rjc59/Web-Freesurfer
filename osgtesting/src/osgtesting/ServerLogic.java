package osgtesting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import osgtesting.Model.JobsDTO;
import osgtesting.Model.UserDTO;
import osgtesting.Util.CryptoToolbox;
import osgtesting.dao.UserDao;

public class ServerLogic {
	//Global Declarations
	private String message      = new String("Intro message");
	private String username     = new String("");
	private String password     = new String("");
	private String name         = new String("");
	private String surname      = new String("");
	private String institution  = new String("");
	private String email        = new String("");
	private String phone_number = new String("");
	
	private boolean logged_out = true;
	private boolean admin      = false;
	
	private UserDTO current_user;
	
	private CryptoToolbox crypto = new CryptoToolbox();
	private UserDao userDao      = new UserDao();
	
	private List<JobsDTO> job_list   = new ArrayList<JobsDTO>();
	private List<UserDTO> admin_list = new ArrayList<UserDTO>();
	
	public ServerLogic () {
		
	}
	
	public boolean login( String form_username, String form_password ) {0
		ResultSet result = userDao.login(form_username, form_password);
		try {
			if(!result.next()){
				System.out.println("NO USERNAME");
				return false;
			} else {  
				if (checkPassword(result.getString(2).trim(), result.getString(3).trim(), form_password)) {
					setUsername(form_username);
					if(form_username.equals("admin")){
						setAdmin(true);
					}
					return true;
				} else {
					System.out.println("Password does not match");
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void logout () {
		current_user=null;
		setMessage("Intro message"); 
		setLoggedOut(true);
		username="";
	}
	
	public boolean adminPower() {
		ResultSet account=userDao.edit(username);
		try {
			if(!account.next()){
				System.out.println("whoops     "); 
				return false;
			}
			current_user=new UserDTO(account.getString(2),account.getString(8),account.getString(3),account.getString(4),account.getString(5),account.getString(6),account.getString(7),account.getString(9));
			System.out.println(current_user.getName());
	
			setAdminList(userDao.read());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean newAccount( UserDTO new_account, String password_text ) {
		byte[] salt     = crypto.makeSalt();
		String new_salt = new String(salt);
		
		byte[] password_to_store = crypto.passwordHash( password_text, salt );
		String new_password    = new String(password_to_store);

		System.out.println(new_password);
		System.out.println(new_salt);

		new_account.setPass(new_password);
		new_account.setSalt(new_password);
		
		try {
			userDao.Write(new_account);
			return true;
		} catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean updateAccount( String form_username, String form_name, String form_surname, String form_email, String form_institution, String form_phone ) {
		ResultSet result=userDao.edit(form_username);
		try {
			if(result.next()){
				UserDTO update = new UserDTO();
				
				update.setUserName(form_username);
				update.setName(form_name);
				update.setSurname(form_surname);
				update.setEmail(form_email);
				update.setInst(form_institution);
				update.setPhone(form_phone);
				
				System.out.println(update.getName());
	
				userDao.update(update);
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("");
			return false;
		}
	}
	
	public boolean editAccount() {
		ResultSet account=userDao.edit(username);
		try {
			if(!account.next()){
				System.out.println("whoops     "); 
				return false;
			}
			current_user = new UserDTO(account.getString(2),account.getString(8),account.getString(3),account.getString(4),account.getString(5),account.getString(6),account.getString(7),account.getString(9));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean updatePassword(String password_text) {
		byte[] salt     = crypto.makeSalt();
		String new_salt = new String(salt);
		
		byte[] password_to_store = crypto.passwordHash( password_text, salt );
		String new_password    = new String(password_to_store);

		System.out.println(new_password);
		System.out.println(new_salt);

		userDao.updatePassword(current_user.getUserName(), new_password, new_salt);
		
		return true;
	}
	
	public boolean editPassword() {
		ResultSet account=userDao.edit(username);
		try {
			if(!account.next()){
				System.out.println("whoops     "); 
				return false;
			}
			current_user = new UserDTO(account.getString(2),account.getString(8),account.getString(3),account.getString(4),account.getString(5),account.getString(6),account.getString(7),account.getString(9));
			System.out.println(current_user.getName());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean checkPassword(String return_password, String return_salt,String check_password){
		byte[] password_to_check = crypto.passwordHash( check_password, return_salt.getBytes() );

		System.out.println(return_password);
		System.out.println(password_to_check.toString());
		return Arrays.equals(return_password.getBytes(), password_to_check);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phone_number;
	}

	public void setPhoneNumber(String phone_number) {
		this.phone_number = phone_number;
	}

	public boolean isLoggedOut() {
		return logged_out;
	}

	public void setLoggedOut(boolean logged_out) {
		this.logged_out = logged_out;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public List<JobsDTO> getJobList() {
		return job_list;
	}

	public void setJobList(List<JobsDTO> job_list) {
		this.job_list = job_list;
	}

	public List<UserDTO> getAdminList() {
		return admin_list;
	}

	public void setAdminList(List<UserDTO> admin_list) {
		this.admin_list = admin_list;
	}
	
}
