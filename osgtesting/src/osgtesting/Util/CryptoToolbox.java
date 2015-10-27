package osgtesting.Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class CryptoToolbox {
	private String algorithm = "PBKDF2WithHmacSHA1";
	private int derivedKeyLength = 64;
	private SecretKeyFactory f;
	private MessageDigest digest;
	private int iterations = 1000;
	public CryptoToolbox()
	{
		try{
			digest = MessageDigest.getInstance("SHA-256");
			f=SecretKeyFactory.getInstance(algorithm);	
		}
		catch(NoSuchAlgorithmException e)
		{
			System.err.println("Bad Algorithm, Check your JDK version > 1.8");
			e.printStackTrace();
		}
		
	}
	public byte[] HashSHA256(byte[] toHash)
	{
		return digest.digest(toHash);
	}
	public byte[] PasswordHash(String passText, byte[]salt)
	{
		try{
			String passHashStr = new String(HashSHA256(passText.getBytes("UTF-8")));
			KeySpec spec = new PBEKeySpec(passHashStr.toCharArray(), salt, iterations, derivedKeyLength);
			return f.generateSecret(spec).getEncoded();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	public boolean CheckPassword(String retpass, String retsalt,String checkpass){
		byte[] oldsalt=null,oldpass=null,attemptToCheck=null;
		try{
			String attemptText=checkpass;
			oldsalt=retsalt.getBytes();
			oldpass=retpass.getBytes();
			attemptToCheck = PasswordHash(attemptText, oldsalt);

		}catch(Exception e){
			e.printStackTrace();
		}
		return Arrays.equals(oldpass, attemptToCheck);
	}

}
