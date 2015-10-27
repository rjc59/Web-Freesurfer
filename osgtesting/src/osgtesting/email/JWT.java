/* This class creates JSON Web Tokens to be used in creating email verification links for user accounts
 */

package osgtesting.email;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JWT {
	private SecretKey secretKey;
	
	//Creates the key to be used in creating the token
	public void createKey(KeySpec spec){
		secretKey = generateKey(spec);
	}
	
	//Creates and returns token based on user id, issuer, subject, and the expiration time of the token in milliseconds
	public String getJWT(String id, String issuer, String subject, long ttlMillis){
		return createJWT(id, issuer, subject, ttlMillis);
	}
	
	//Decodes and verifies the token, throwing an exception if it's not valid/signed
	public void verifyJWT(String jwt){
		parseJWT(jwt);
	}
	
	private SecretKey generateKey(KeySpec spec){
		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			SecretKey secretKey = factory.generateSecret(spec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return secretKey;
	}
	
	private String createJWT(String id, String issuer, String subject, long ttlMillis) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		
		Key signingKey = new SecretKeySpec(secretKey.getEncoded(), signatureAlgorithm.getJcaName());
		
		JwtBuilder builder = Jwts.builder().setId(id).setIssuedAt(now).setSubject(subject).setIssuer(issuer).signWith(signatureAlgorithm, signingKey);
		
		if (ttlMillis >= 0) {
			long expMillis = nowMillis + ttlMillis;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
		}
		
		return builder.compact();
	}
	
	private void parseJWT(String jwt) {
		//This line will throw an exception if it is not a signed JWS
		Claims claims = Jwts.parser()         
		   .setSigningKey(secretKey.getEncoded())
		   .parseClaimsJws(jwt).getBody();
		
		System.out.println("ID: " + claims.getId());
		System.out.println("Subject: " + claims.getSubject());
		System.out.println("Issuer: " + claims.getIssuer());
		System.out.println("Expiration: " + claims.getExpiration());
	}
}
