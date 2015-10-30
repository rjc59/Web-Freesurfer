package osgtesting.Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

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
			System.err.println("Bad Algorithm, Check your JDK version >= 1.7");
			e.printStackTrace();
		}
		
	}
	public byte[] hashSHA256(byte[] toHash)
	{
		return digest.digest(toHash);
	}
	public byte[] passwordHash(String password_text, byte[]salt)
	{
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
