package osgtesting;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import osgtesting.Model.UserDTO;


public class ServerApplication {
	//global declarations
	private DateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
	private ServerLogic site      = new ServerLogic();
	
	//functions
	public ServerApplication () {
		
	}
	
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
	
	private String getFormValue( String form_id ) {
		return (String)( ( (UIInput)( FacesContext.getCurrentInstance().getViewRoot().findComponent( form_id ) ) ).getValue() );
	}
	
	public void login() {
		if( site.login(getFormValue("mainform:username"), getFormValue("mainform:password") ) ) {
			System.out.println("Password matches");
			site.setMessage("Welcome "+ site.getUsername());
		}
	}
	
	public void logout() {
		site.logout();
	}
	
	public void upload(){
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		String strDate= dateformat.format(date);
		String status="Uploaded";
		String id=UUID.randomUUID().toString();
		//jobList.add(new JobsDTO(id,name,status,strDate));
	}
	
	public void newAccount() {
		UserDTO new_account = new UserDTO( getFormValue("accountform:userName"), 
										   "",
										   getFormValue("accountform:fistName"),
										   getFormValue("accountform:lastName"),
										   getFormValue("accountform:email"),
										   getFormValue("accountform:inst"),
										   getFormValue("accountform:phoneNumber"),
									   	   "" );
		
		if( !( site.newAccount( new_account, getFormValue("accountform:firstpass") ) ) ) {
			System.err.println( "ERROR:\n\tCould not create account." );
		}

		redirect( "index.xhtml" );
	}
	
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
	
	public void editAccount() {
		site.editAccount();
		redirect( "account.xhtml" );
	}
	
	public void updatePassword() {
		site.updatePassword( getFormValue("passwordupdateform:newPass") );
		
		redirect( "index.xhtml" );
	}
	
	public void editPassword() {
		site.editPassword();
		
		redirect( "passwrod.xhtml" );
	}
	
	public void adminPower() {
		if( site.adminPower() ) {
			redirect( "admin.xhtml" );
		}
	}
	
	
	/**********************************************************************************/
	/**                            Questionable Contents                             **/
	/**********************************************************************************/
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
}