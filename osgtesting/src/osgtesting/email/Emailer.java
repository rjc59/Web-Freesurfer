package osgtesting.email;
import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.lang.NullPointerException;
import javax.mail.PasswordAuthentication;

public class Emailer {
	private String to;
	private String subject = "default";
	private String message = "Hello world!";
	private final String from = "opensciencegridtesting@gmail.com";
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
	/**
	 * Initiates send on mail server
	 */
	public void send()
	{
		if (to == null)
		{
			throw new NullPointerException();
		}
		else
		{
			try
			{
				email.setFrom(from);
				email.addRecipient(RecipientType.TO, new InternetAddress(to));
				email.setSubject(subject);
				email.setText(message);
				Transport.send(email);
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void setTo(String to){
		to = to;
	}
	
	public String getTo(){
		return to;
	}
	
	public void setSubject(String subject){
		subject = subject;
	}
	
	public String getSubject(){
		return subject;
	}
	
	public void setMessage(String message){
		message = message;
	}
	
	public String getMessage(){
		return message;
	}
}
