package osgtesting;

import java.security.spec.KeySpec;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.crypto.spec.PBEKeySpec;

import osgtesting.Model.UserDTO;
import osgtesting.Util.CryptoToolbox;
import osgtesting.dao.UserDao;

public class ServerLogic {
	//Global Declarations
	public String message      = new String("Intro message");
	public String username     = new String("");
	public String password     = new String("");
	public String name         = new String("");
	public String surname      = new String("");
	public String institution  = new String("");
	public String email        = new String("");
	public String phone_number = new String("");
	
	private CryptoToolbox crypto = new CryptoToolbox();
	private UserDao userDao      = new UserDao();
	
	public ServerLogic () {
		
	}
	
	public boolean newAccountLogic( UserDTO new_account, String password_text ) {
		String new_password=null, new_salt=null;
		try{
			byte[] salt = crypto.makeSalt();
			new_salt    = new String(salt);
			
			byte[] passwordToStore = crypto.passwordHash( password_text, salt );
			new_password           = new String(passwordToStore);
		}catch(Exception e){
			e.printStackTrace();
		}

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
	
	public boolean loginLogic( String form_username, String form_password ) throws SQLException {
		ResultSet result = userDao.login(form_username, form_password);

		if(!result.next()){
			System.out.println("NO USERNAME");
			return false;
		} else {  
			if (checkPassword(result.getString(2).trim(), result.getString(3).trim(), form_password)) { 
				return true;
			} else {
				System.out.println("Password does not match");
				return false;
			}
		}
	}
	
	public boolean updateAccountLogic ( String form_username, String form_name, String form_surname, String form_email, String form_institution, String form_phone ) {
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
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("");
		}
	}
	
}
