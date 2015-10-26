package osgtesting.email;
import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.lang.NullPointerException;
import javax.mail.PasswordAuthentication;


public class Emailer {
	public String To;
	public String Subject = "default";
	public String Message = "Hello world!";
	private final String From = "opensciencegridtesting@gmail.com";
	private MimeMessage email;
	public Emailer()
	{

		Properties props = System.getProperties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		
		//TODO: change gmail smtp to actual smtp server
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

	    //TODO: change to real smtp authentication credentials
		Session session = Session.getInstance(props,
				  new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("opensciencegridtesting@gmail.com","osgtesting");
					}
		});
	    email = new MimeMessage(session);
	}
	public void send()
	{
		if (To == null)
		{
			throw new NullPointerException();
		}
		else
		{
			try
			{
				email.setFrom(From);
				email.addRecipient(RecipientType.TO, new InternetAddress(To));
				email.setSubject(Subject);
				email.setText(Message);
				Transport.send(email);
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
}
