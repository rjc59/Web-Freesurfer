package osgtesting.Util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
<<<<<<< HEAD
import javax.xml.bind.DatatypeConverter;

import osgtesting.Model.UserDTO;

=======
/**
 * CryptoToolBox
 * Utility class for securely creating and checking passwords.
 */
>>>>>>> master
public class CryptoToolbox {
	private String algorithm = "PBKDF2WithHmacSHA1";
	private int derived_key_length = 64;
	private SecretKeyFactory factory;
	private MessageDigest digest;
	private int iterations = 1000;
	
	/**
	 * CryptoToolbox
	 * Initializes CryptoToolbox.
	 */
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
	
	/**
	 * makeSalt
	 * Creates a salt using the current time.
	 */
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
	
	/**
	 * hashSHA256
	 * Applies a SHA-256 hash to a byte array.
	 * @param toHash The byte array to be hashed.
	 * @return The hashed byte array.
	 */
	public byte[] hashSHA256(byte[] toHash) {
		return digest.digest(toHash);
	}
	
	/**
	 * passwordHash
	 * sha256 Hashes a plaintext password and salts it with pbkdf2
	 * @param password_text The plaintext password
	 * @param salt The salt to use in the hash.
	 * @return A hashed byte array.
	 */
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
	
	/**
	 * checkPassword
	 * Checks the password that the user entered and compares it to the
	 * password stored on the server.
	 * @param retpass The hashed password retrieved from the server.
	 * @param retsalt The salt retrieved from the server.
	 * @param attempt_text The password that the user entered.
	 * @return True if the passwords match, false otherwise.
	 */
	public boolean checkPassword(String retpass, String retsalt,String attempt_text){
		byte[] old_salt=null,oldpass=null,attempt_to_check=null;
		try{
			old_salt=retsalt.getBytes("UTF-8");
			oldpass=retpass.getBytes("UTF-8");
			attempt_to_check = passwordHash(attempt_text, old_salt);

		}catch(Exception e){
			e.printStackTrace();
		}
		return Arrays.equals(oldpass, attempt_to_check);
	}
	/**
	 * base64Encode
	 * Base64 encodes a byte array.
	 * @param message Byte array to be encoded.
	 * @return Base64 encoded String
	 */
	public String base64Encode(byte[] message)
	{
		return DatatypeConverter.printBase64Binary(message);
	}
	/**
	 * base64Decode
	 * Base64 decodes a String.
	 * @param message String to be decoded.
	 * @return Decoded Base64 byte array
	 */
	public byte[] base64Decode(String message)
	{
		return DatatypeConverter.parseBase64Binary(message);
	}

}
