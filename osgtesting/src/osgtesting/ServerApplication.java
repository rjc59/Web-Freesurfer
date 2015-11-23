package osgtesting;

import java.util.HashMap;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import osgtesting.Model.JobsDTO;
import osgtesting.Model.UserDTO;
import osgtesting.Util.CryptoToolbox;
import osgtesting.email.*;


public class ServerApplication {
	//global declarations
	private DateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
	private ServerLogic site      = new ServerLogic();
	private CryptoToolbox hasher = new CryptoToolbox();
	//functions
	
	/**
	 * ServerApplication
	 * Constructor function for the ServerApplication class.
	 */
	public ServerApplication () {
		
	}
	
	/**
	 * redirect
	 * A convenience function to handle redirection attempts and failures
	 * 
	 * @param page - A String holding the name of the page that is desired to be redirected
	 */
	private void redirect( String page ) {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect( page );
		} catch (IOException e) {
			e.printStackTrace();
			System.err.print("ERROR:\n\tCould not redirect to ");
			System.err.print(page);
			System.err.print("\n");
		}
	}
	
	/**
	 * getFormValue
	 * Returns the String value contained within a form field given the field id.
	 * 
	 * @param form_id - The ID of a form field given in string format
	 * @return        - A string containing the value within form_id
	 */
	private String getFormValue( String form_id ) {
		String ret_string;	
		try {
			byte[] ui_bytes =  ((String)( (UIInput)( FacesContext.getCurrentInstance().getViewRoot().findComponent( form_id ) ) ).getValue()).getBytes("UTF-8");
			ret_string = new String(ui_bytes, "UTF-8").replaceAll("(\\r|\\n)", "");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			ret_string = "ERROR";
		}
		return ret_string;
	}
	
	/**
	 * login
	 * Attempts to log a user in to the server.
	 */
	public void login() {
		if( site.login(getFormValue("mainform:username"), getFormValue("mainform:password") ) ) {
			System.out.println("Password matches");
			site.setMessage("Welcome "+ site.getUsername());
		}
	}
	
	/**
	 * logout
	 * Sets the user to be logged out of the server.
	 */
	public void logout() {
		site.logout();
		redirect("index.xhtml");
	}
	
	/**
	 * upload
	 * I didn't make this and I don't know what id does aside from adding a job to the joblist
	 */
	public void upload(){
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		String strDate= dateformat.format(date);
		String status="Uploaded";
		String id=UUID.randomUUID().toString();
		//jobList.add(new JobsDTO(id,name,status,strDate));
	}
	
	/**
	 * newAccount
	 * Attempts to create a new account from information gathered from a form.
	 */
	public void newAccount() {
		UserDTO new_account = new UserDTO( "",getFormValue("accountform:userName"), 
										   "",
										   getFormValue("accountform:firstName"),
										   getFormValue("accountform:lastName"),
										   getFormValue("accountform:email"),
										   getFormValue("accountform:inst"),
										   getFormValue("accountform:phoneNumber"),
									   	   "",true );
		
		if( !( site.newAccount( new_account, getFormValue("accountform:firstpass") ) ) ) {
			System.err.println( "ERROR:\n\tCould not create account." );
		}
		
		
		
		redirect( "index.xhtml" );
		
	}
	
	/**
	 * updateAccount
	 * Attempts to update an account from information gathered from a form
	 */
	public void updateAccount() {
				if( !(site.updateAccount( getFormValue("accountupdateform:userName"),
										  getFormValue("accountupdateform:firstName"),
										  getFormValue("accountupdateform:lastName"),
										  getFormValue("accountupdateform:email"),
										  getFormValue("accountupdateform:inst"),
										  getFormValue("accountupdateform:phoneNumber") ) ) ){
					System.err.println( "ERROR:\n\tCould not update account." );
				}
				
				redirect( "index.xhtml" );
	}
	
	/**
	 * editAccount
	 * Makes the ServerLogic class get ready for the account editing page
	 */
	public void editAccount() {
		site.editAccount();
		
		redirect( "account.xhtml" );
	}
	
	/**
	 * updatePassword
	 * Attempts to update the password from the information gathered from a form
	 */
	public void updatePassword() {
		site.updatePassword( getFormValue("passwordupdateform:newPass") );
		
		redirect( "index.xhtml" );
	}
	
	/**
	 * editPassword
	 * Makes the ServerLogic class get ready for the password editing page
	 */
	public void editPassword() {
		site.editPassword();
		
		redirect( "password.xhtml" );
	}
	
	/**
	 * adminRedirect
	 * Checks to see if the user has administrator privilege and redirects if so.
	 */
	public void adminRedirect() {
		if( site.adminPower() ) {
			redirect( "admin.xhtml" );
		}
	}
	
	/**
	 * validatePassword
	 * Ensures that the passwords entered into the account creation form are valid and congruent
	 * 
	 * @param component_event - A ComponentSystemEvent that is triggered by modifications to the
	 *                          password fields in the account creation form
	 */
	public void validatePassword( ComponentSystemEvent component_event ) {

		FacesContext faces_context = FacesContext.getCurrentInstance();
		UIComponent page_component = component_event.getComponent();

		UIInput username_input     = (UIInput) page_component.findComponent("userName");
		String username            = (String) username_input.getLocalValue();
		String username_id         = username_input.getClientId();

		UIInput password_input     = (UIInput) page_component.findComponent("firstpass");
		String password            = password_input.getLocalValue() == null ? ""
					               : password_input.getLocalValue().toString();
		String password_id         = password_input.getClientId();
		
		UIInput password_input_2   = (UIInput) page_component.findComponent("secondpass");
		String password_2		   = password_input_2.getLocalValue() == null ? ""
								   : password_input_2.getLocalValue().toString();

		if(password.isEmpty() || password_2.isEmpty())
			return;

		if( !( site.validatePassword( faces_context, username,   username_id,
									  password,      password_2, password_id ) ) ) {
			System.err.println( "ERROR:\n\tCould not validate account password." );
		}
	}
	
	
	/**
	 * revalidatePassword
	 * Ensures that the passwords entered into the password change form are valid and congruent.
	 * 
	 * @param component_event - A ComponentSystemEvent that is triggered by modifications to the
	 *                          password fields in the account creation form
	 */
	public void revalidatePass( ComponentSystemEvent component_event ){

		FacesContext faces_context = FacesContext.getCurrentInstance();
		UIComponent page_component = component_event.getComponent();

		UIInput old_password_input = (UIInput) page_component.findComponent("oldPass");
		String  old_password       = old_password_input.getLocalValue() == null ? ""
				                   : old_password_input.getLocalValue().toString();
		String  old_password_id    = old_password_input.getClientId();
		
		UIInput password_input     = (UIInput) page_component.findComponent("newPass");
		String  password           = password_input.getLocalValue() == null ? ""
					               : password_input.getLocalValue().toString();
		String  password_id        = password_input.getClientId();
		
		UIInput password_input_2   = (UIInput) page_component.findComponent("conPass");
		String  password_2         = password_input_2.getLocalValue() == null ? ""
								   : password_input_2.getLocalValue().toString();

		if( password.isEmpty() || password_2.isEmpty() || old_password.isEmpty() )
			return;

		if( !( site.revalidatePassword( faces_context, old_password, old_password_id,
										password,      password_2,   password_id) ) ) {
			System.err.println( "ERROR:\n\tCould not validate new account password." );
		}
	}
	/**
	 * loadJobs
	 * loads the user's jobs into the job_list then redirects to the status page
	 */
	public void loadJobs()
	{
		site.updateJobsList();
		redirect("status.xhtml");
	}
	
	/**
	 * toggleUserID
	 * Gets the ID from the form submission and calls ServerLogic to enable/disable their account
	 * @return - Returns a String with the user's ID
	 */
	public String toggleUserId(){
		HashMap<String, String> values = new HashMap<String,String>(FacesContext.getCurrentInstance().
				getExternalContext().getRequestParameterMap());
		String id = values.get("idToToggle");
		site.toggleUserId(id);
		return id;
	}
	/**
	 * deleteUserId
	 * Gets the selected user ID from the form submission and calls ServerLogic to delete that user.
	 * @return - Returns a String with the deleted user's ID.
	 */
	public String deleteUserId(){
		
		HashMap<String, String> values = new HashMap<String,String>(FacesContext.getCurrentInstance().
				getExternalContext().getRequestParameterMap());
		String id = values.get("idToDelete");
		site.deleteUserID(id);
		return id;
	}
	public String deleteJobId(){
		
		HashMap<String, String> values = new HashMap<String,String>(FacesContext.getCurrentInstance().
				getExternalContext().getRequestParameterMap());
		String id = values.get("idToDelete");
		site.deleteJobId(id);
		return id;
	}
	public boolean isLoggedout(){
		return site.isLoggedOut();
	}
	public boolean isAdmin(){
		return site.isAdmin();
	}
	public UserDTO getCurrentUser(){
		return site.getCurrent_user();
	}
	public List<UserDTO> getAdminList() {
		return site.getAdminList();
	}
	public String getMessage(){
		return site.getMessage();
	}
	public List<JobsDTO> getJobList() {
		return site.getJobList();
	}
}
