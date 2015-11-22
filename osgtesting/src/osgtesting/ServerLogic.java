package osgtesting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.squareup.okhttp.HttpUrl;

import osgtesting.Model.JobsDTO;
import osgtesting.Model.UserDTO;
import osgtesting.Util.CryptoToolbox;
import osgtesting.dao.UserDAO;
import osgtesting.email.Emailer;

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
	private Emailer mailer=new Emailer();
	private UserDAO userDAO      = new UserDAO();
	
	private List<JobsDTO> job_list   = new ArrayList<JobsDTO>();
	private List<UserDTO> admin_list = new ArrayList<UserDTO>();
	
	//functions
	
	/**
	 * ServerLogic
	 * Constructor function for the ServerLogic Class.
	 */
	public ServerLogic () {
		
	}
	
	/**
	 * login
	 * Creates a ResultSet from the form supplied username and password, then checks to see if:
	 * 		> the username exists
	 * 		> the suppled password is correct
	 * And then returns a boolean indicating the success of the login attempt
	 * 
	 * @param form_username - A String containing the username accepted from the form
	 * @param form_password - A String containing the password accepted from the form
	 * @return              - A boolean indicating the success of the login attempt
	 */
	public boolean login( String form_username, String form_password ) {
		ResultSet result = userDAO.login(form_username, form_password);
		try {
			System.out.println("Username : "+form_username);
			if(!result.next()){
				System.out.println("NO USERNAME");
				return false;
			} else {
				System.out.println("Got into correct login block");
				boolean pass_check = checkPassword(result.getString(2).trim(), result.getString(3).trim(), form_password);
				if (pass_check) {
					setUsername(form_username);
					setLoggedOut(false);
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
	
	/**
	 * logout
	 * Resets the server's current user information and then sets the loggedOut Status to false
	 */
	public void logout () {
		current_user=null;
		setMessage("Intro message"); 
		username = "";
		
		setLoggedOut(true);
		
	}
	
	/**
	 * adminPower
	 * Checks the current user against the list of valid administrator accounts.
	 * 
	 * @return - returns a boolean indicating the veracity of the user's adminsitrator status
	 */
	public boolean adminPower() {
		ResultSet account=userDAO.edit(username);
		try {
			if(!account.next()){
				System.out.println("whoops     "); 
				return false;
			}
			current_user=new UserDTO(account.getString(2),account.getString(8),account.getString(3),account.getString(4),account.getString(5),account.getString(6),account.getString(7),account.getString(9));
			System.out.println(current_user.getName());
	
			setAdminList(userDAO.read());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * newAccount
	 * Attempts to make a new account by:
	 * 		> Creating Salt
	 * 		> Hashing the password with salt
	 * 		> Writing out hashed password and created salt
	 * 		> Reading the userDAO back in to ensure that the Database connection is valid and users exist
	 * And then returns a boolean indicating the success of the attempt.   
	 * 
	 * @param new_account   - A UserDTO for the new account being created
	 * @param password_text - A String containing the text password obtained from the form
	 * @return              - A boolean indicating the success of the operation
	 */
	public boolean newAccount( UserDTO new_account, String password_text ) {
		
		byte[] salt     = crypto.makeSalt();
		String new_salt = crypto.base64Encode(salt);
		
		byte[] password_to_store = crypto.passwordHash( password_text, salt );
		String new_password      = crypto.base64Encode(password_to_store);
		
		System.out.println("New password: "+ new_password);
		System.out.println("New Salt: "+ new_salt);

		new_account.setPass(new_password);
		new_account.setSalt(new_salt);
		
		try {
			userDAO.write(new_account);
			userDAO.read();
			
		} catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		
		//generate token
		String[] tsToken=crypto.makeToken(new_account);
		String Ts=tsToken[0];
		String token= tsToken[1];
		String id=new_account.getUserName();
		
		//generate url?
		int port = 8085;
		HttpUrl request_url = new HttpUrl.Builder()
		.scheme("http")
		.host("localhost")
		.port(port)
		.addPathSegment("verify")
		.addQueryParameter("userid", id)
		.addQueryParameter("token", token)
		.addQueryParameter("timestamp",Ts)
		.build();
		mailer.setTo(new_account.getEmail());
		message=request_url.toString();
		mailer.sendToken(message);
			
		
		
		return true;
		
	}
	
	/**
	 * updateAccount
	 * Takes in the form information and creates a UserDTO that it then attempts to submit to the userDAO for updating.
	 * 
	 * @param form_username    - A String containing the username gathered from the form.
	 * @param form_name        - A String containing the user's given name gathered from the form.
	 * @param form_surname     - A String containing the user's family name gathered from the form.
	 * @param form_email       - A String containing the email address gathered from the form.
	 * @param form_institution - A String containing the institution name gathered from the form.
	 * @param form_phone       - A String containing the phone number gathered from the form.
	 * @return                 - A boolean indicating the success of the operation.
	 */
	public boolean updateAccount( String form_username, String form_name, String form_surname, String form_email, String form_institution, String form_phone ) {
		ResultSet result=userDAO.edit(form_username);
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
	
				userDAO.update(update);
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("");
			return false;
		}
	}
	
	/**
	 * editAccount
	 * Attempts to retrieve the non-password account information for the form auto-fill on the account.xhtml page
	 * 
	 * @return - A boolean indicating the success of the opeeration.
	 */
	public boolean editAccount() {
		ResultSet account=userDAO.edit(username);
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
	
	/**
	 * updatePassword
	 * Attempts to update the user's password by:
	 * 		> Creating Salt
	 * 		> Hashing the plaintext password with Salt
	 * 		> Writing out the user's password to the UserDAO
	 * 
	 * @param password_text - A String containing the new password ( in plain text ) to be stored to the database.
	 * @return              - This currently always returns true.
	 */  //we may want to see if there's a way to address the failure of this as it happens
	public boolean updatePassword(String password_text) {
		byte[] salt     = crypto.makeSalt();
		String new_salt = crypto.base64Encode(salt);
		
		byte[] password_to_store = crypto.passwordHash( password_text, salt );
		String new_password    = crypto.base64Encode(password_to_store);

		System.out.println(new_password);
		System.out.println(new_salt);

		userDAO.updatePassword(current_user.getUserName(), new_password, new_salt);
		
		return true;
	}
	
	/**
	 * editPassword
	 * Attempts to retrieve account information to ready the server for the password editing page.
	 * 
	 * @return - A boolean indicating the success of the operation.
	 */
	public boolean editPassword() {
		ResultSet account=userDAO.edit(username);
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
	
	/**
	 * checkPassword
	 * Assesses the accuracy of the entered password by hashing it with the salt from the database and comparing it to the hashed password already in the database
	 * 
	 * @param return_password - A String containing the hashed password from the server.
	 * @param return_salt     - A String containing the bytes in salt.
	 * @param check_password  - A String containing the plain text of the password that needs to be checked.
	 * @return                - A boolean indicating whether or not the passwords match.
	 */
	public boolean checkPassword(String return_password, String return_salt,String check_password){
		//byte[] password_to_check = crypto.passwordHash( check_password, return_salt.getBytes() );
		//System.out.println(return_password);
		//System.out.println(password_to_check.toString());
		//return Arrays.equals(return_password.getBytes(), password_to_check);
		return crypto.checkPassword(return_password, return_salt, check_password);
	}
	
	/**
	 * validatePassword
	 * Checks that all requirements have been met for a password in a new account:
	 * 		> The username is available
	 * 		> The password is at least 6 characters long
	 * 		> The password and the re-entered password match
	 * Writes warnings out to the system's error stream if any of the test's fail.
	 * 
	 * @param faces_context - The FacesContext that is running on the page.
	 * @param username      - A String containing the plain text user name.
	 * @param username_id   - A String containing the id for the username field in the form.
	 * @param password      - A String containing the plain text password.
	 * @param password_2    - A String containing the plain text for the re-entered password.
	 * @param password_id   - A String containing the id for the password field in the form.
	 * @return              - A boolean indicating success.
	 */ //on these warning write-outs we may want to attach user or session information for sysadmins to look at
	public boolean validatePassword( FacesContext faces_context, String username, String username_id, String password, String password_2, String password_id ) {
		boolean return_value = true;
		
		if( !( checkUsernameAvailability( username, username_id, faces_context ) ) ) {
			return_value = false;
			System.err.println( "WARNING:\n\tUsername is taken." );
		}
		
		if( !(	checkPasswordLength( password, password_id, faces_context ) ) ) {
			return_value = false;
			System.err.println( "WARNING:\n\tPassword is too short." );
		}

		if( !( checkPasswordMatch( password, password_2, password_id, faces_context ) ) ) {
			return_value = false;
			System.err.println( "WARNING:\n\tPasswords must match." );
		}
		
		return return_value;
	}
	
	/**
	 * revalidatePassworrd
	 * Checks that all requirements have been met for a password change:
	 * 		> The old password is valid
	 * 		> The password is at least 6 characters long
	 * 		> The password and the re-entered password match
	 * Writes warnings out to the system's error stream if any of the test's fail.
	 * 
	 * @param faces_context   - The FacesContext that is running on the page.
	 * @param old_password    - A String containing the plain text for the old password.
	 * @param old_password_id - A String containing the id for the old password field in the form.
	 * @param password        - A String containing the plain text password.
	 * @param password_2      - A String containing the plain text for the re-entered password.
	 * @param password_id     - A String containing the id for the password field in the form.
	 * @return                - A boolean indicating success.
	 */ //on these warning write-outs we may want to attach user or session information for sysadmins to look at
	public boolean revalidatePassword( FacesContext faces_context, String old_password, String old_password_id, String password, String password_2, String password_id ) {
		boolean return_value = true;
		
		if( !( checkPasswordAncestor( old_password, old_password_id, faces_context ) ) ) {
			return_value = false;
			System.err.println( "WARNING:\n\tPassword doesn't match the stored information." );
		}
		
		if( !(	checkPasswordLength( password, password_id, faces_context ) ) ) {
			return_value = false;
			System.err.println( "WARNING:\n\tPassword is too short." );
		}

		if( !( checkPasswordMatch( password, password_2, password_id, faces_context ) ) ) {
			return_value = false;
			System.err.println( "WARNING:\n\tPasswords must match." );
		}
		
		return return_value;
	}
	
	/**
	 * checkUsernameAvailability
	 * Takes in the username entered by the user in the account creation form and checks to make sure that the name is free in the database.
	 * Writes out a warning to the faces context if the function fails in its attempt before returning false.
	 * 
	 * @param username      - A String containing the plain text of the entered username.
	 * @param username_id   - A String containing the id of the username field in the form.
	 * @param faces_context - The FacesContext for the current page.
	 * @return              - A boolean indicating whether or not the usernname is available. (True = available)
	 */
	private boolean  checkUsernameAvailability( String username, String username_id, FacesContext faces_context ) {
		boolean return_value = validateUser( username );
		
		if( return_value ) {
			FacesMessage message = new FacesMessage("Username already exists");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			faces_context.addMessage(username_id, message);
			faces_context.renderResponse();
		}
		
		return !( return_value );
	}
	
	/**
	 * checkPasswordLength
	 * Checks to ensure that the password is of proper length.
	 * Writes out a warning to the faces context if the function fails before returning a false.
	 * 
	 * @param password      - A String containing the plain text password from the page form.
	 * @param password_id   - A String containing the ID for the form field for the password on the page.
	 * @param faces_context - The FacesContext for the current page.
	 * @return              - A boolean indicating whether or not the password length is at least the proper length.
	 */
	private boolean checkPasswordLength( String password, String password_id, FacesContext faces_context ) {
		boolean return_value = password.length() < 6;
		
		if( return_value ) {
			FacesMessage message = new FacesMessage("Password must be atleast 6 characters");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			faces_context.addMessage(password_id, message);
			faces_context.renderResponse();
		}
		
		return !( return_value );
	}
	
	/**
	 * checkPasswordMatch
	 * Compares the entered and re-entered passwords to make sure that they are the same.
	 * Writes out a warning message to the faces context before returning false if it fails the test.
	 * 
	 * @param password      - A String containing the plain-text form of the password gathered from the form.
	 * @param password_2    - A String containing the plain-text form of the re-entered password gathered from the form. 
	 * @param password_id   - A String containing the ID for the form field for password entry on the page.
	 * @param faces_context - The FacesContext for the current page.
	 * @return              - A boolean indicating whether or not the passowrds match.
	 */
	private boolean checkPasswordMatch( String password, String password_2, String password_id, FacesContext faces_context ) {
		boolean return_value = password.equals( password_2 );
		
		if( !( return_value ) ) {
			FacesMessage message = new FacesMessage("Passwords must match");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			faces_context.addMessage(password_id, message);
			faces_context.renderResponse();
		}
		
		return return_value;
	}
	
	/**
	 * checkPasswordAncestor
	 * Checks to ensure that the user has entered a password that matches the original one they stored in the database.
	 * Writes a warning out to the faces context before returning false if it fails.
	 * 
	 * @param old_password    - A String containing the plain-text form of the password currently stored in the database.
	 * @param old_password_id - A String containing the ID for the form field for the currently stored password on the page.
	 * @param faces_context   - A FacesContext for the current page.
	 * @return                - A boolean indicating the whether or not the password matches the one stored in the server.
	 */
	private boolean checkPasswordAncestor ( String old_password, String old_password_id, FacesContext faces_context ) {
		boolean return_value = checkPassword( current_user.getPass().trim(), current_user.getSalt().trim(), old_password );
		
		if( !( return_value ) ) {
			System.out.println( "INCORRECT PASSWORD" );
			FacesMessage message = new FacesMessage( "Incorrect Password" );
			message.setSeverity( FacesMessage.SEVERITY_ERROR );
			faces_context.addMessage( old_password_id, message );
			faces_context.renderResponse();
		}
		
		return return_value;
	}
	
	/**
	 * validateUser
	 * Checks the UserDAO to see if the given username already exists.
	 * 
	 * @param form_username - A String containing the username entered by the user.
	 * @return              - A boolean indicating whether or not the username already exists.
	 */
	public boolean validateUser( String form_username ) {
		ResultSet edit = userDAO.edit( form_username );
		try {
			if ( edit.next() ) {
				return true;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return false;
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

	public UserDTO getCurrent_user() {
		return current_user;
	}

	public void setCurrent_user(UserDTO current_user) {
		this.current_user = current_user;
	}
	
}
