package osgtesting;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import osgtesting.Model.JobsDTO;
import osgtesting.Model.UserDTO;
import osgtesting.Util.CryptoToolbox;
import osgtesting.dao.UserDao;

public class ServerApplication {
	//global declarations
	private DateFormat date    = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
	private ServerLogic site = new ServerLogic();
	
	//functions
	public ServerApplication () {
		
	}
	
	public void login() {
		UIInput username_component = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("mainform:username");
		UIInput password_component = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("mainform:password");
		if( site.login((String)username_component.getValue(), (String)password_component.getValue()) ) {
			System.out.println("Password matches");
			site.setMessage("Welcome "+ site.getUsername());
		}
	}
	
	public void upload(){
		Calendar c = Calendar.getInstance();
		Date date=c.getTime();
		String strDate=df.format(date);
		String status="Uploaded";
		String id=UUID.randomUUID().toString();
		//jobList.add(new JobsDTO(id,name,status,strDate));
	}
}
