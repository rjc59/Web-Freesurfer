package osgtesting;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKeyFactory;

import osgtesting.Model.JobsDTO;
import osgtesting.Model.UserDTO;
import osgtesting.Util.CryptoToolbox;
import osgtesting.dao.UserDao;

public class ServerApplication {
	//global declarations
	private String message      = new String("Intro message");
	private String username     = new String("");
	private String password     = new String("");
	private String name         = new String("");
	private String surname      = new String("");
	private String institution  = new String("");
	private String email        = new String("");
	private String phone_number = new String("");
	
	private List<JobsDTO> job_list   = new ArrayList<JobsDTO>();
	private List<UserDTO> admin_list = new ArrayList<UserDTO>();
	
	private DateFormat date    = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
	private ServerLogic site = new ServerLogic();
	
	private boolean logged_out   = true;
	private boolean admin        = false;
	
	private UserDTO currentuser;
	
	//functions
	public ServerApplication () {
		
	}
	
	public void login() {
		if( site.loginLogic() ) {
			System.out.println("Password matches");	 
			message="Welcome "+uname;
			username=uname;
			if(username.equals("admin")){
				admin=true;
			}
		}
	}
	
}
