package osgtesting.Util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;

import osgtesting.Model.UserDTO;

public class CryptoToolbox {
	private String algorithm = "PBKDF2WithHmacSHA1";
	private int derived_key_length = 64;
	private SecretKeyFactory factory;
	private MessageDigest digest;
	private int iterations = 1000;
	
	
	public CryptoToolbox()
	{
		try{
			digest = MessageDigest.getInstance("SHA-256");
			factory=SecretKeyFactory.getInstance(algorithm);	
		}
		catch(NoSuchAlgorithmException e)
		{
			System.err.println("Bad Algorithm:\n\tMake sure you are using JDK version 1.7 or higher.");
			e.printStackTrace();
		}
		
	}
	
	public byte[] makeSalt () {
		Date creationTime = new Date();
		byte[] salt = null;
		try {
			salt = digest.digest( creationTime.toString().getBytes("UTF-8"));
		} catch (Exception e) {
			System.err.println("Unsupported Encoding");
		}
		return salt;
	}
	
	public String[] makeToken(UserDTO user){
		String[] tsToken=new String[2];
		byte[] token_hash=null;
		String timeStamp = Long.toString((System.currentTimeMillis() / 1000L));
		tsToken[0]=timeStamp;
		try{
		String shared_secret = new String(hashSHA256(user.getSalt().concat(user.getPass()).getBytes("UTF-8")));
		token_hash = hashSHA256(shared_secret.concat(timeStamp).getBytes("UTF-8"));
		}catch(Exception e){
			e.printStackTrace();
		}
		String token = DatatypeConverter.printBase64Binary(token_hash);
		tsToken[1]=token;
		
		return tsToken;
	}
	
	public byte[] hashSHA256(byte[] toHash) {
		return digest.digest(toHash);
	}
	
	public byte[] passwordHash(String password_text, byte[]salt) {
		try{
			String password_hash_str = new String(hashSHA256(password_text.getBytes("UTF-8")));
			KeySpec spec = new PBEKeySpec(password_hash_str.toCharArray(), salt, iterations, derived_key_length);
			return factory.generateSecret(spec).getEncoded();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	public boolean checkPassword(String retpass, String retsalt,String attempt_text){
		byte[] old_salt=null,oldpass=null,attempt_to_check=null;
		try{
			old_salt=retsalt.getBytes();
			oldpass=retpass.getBytes();
			attempt_to_check = passwordHash(attempt_text, old_salt);

		}catch(Exception e){
			e.printStackTrace();
		}
		return Arrays.equals(oldpass, attempt_to_check);
	}

}