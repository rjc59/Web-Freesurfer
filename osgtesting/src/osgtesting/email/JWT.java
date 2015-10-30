/* This class creates JSON Web Tokens to be used in creating email verification links for user accounts
 * It needs to be initialized with a byte array of a secret key (e.g. passwordToStore in newAccount() base.java)
 */

package osgtesting.email;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JWT {
	private byte[] secret;
	
	public JWT(byte[] secretBytes){
		secret = secretBytes;
	}
	
	//Creates and returns token based on user id, issuer, subject, and the expiration time of the token in milliseconds
	public String getJWT(String id, String issuer, String subject, long ttlMillis){
		return createJWT(id, issuer, subject, ttlMillis);
	}
	
	//Decodes and verifies the token, throwing an exception if it's not valid/signed
	public void verifyJWT(String jwt){
		parseJWT(jwt);
	}
	
	private String createJWT(String id, String issuer, String subject, long ttlMillis) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		
		Key signingKey = new SecretKeySpec(secret, signatureAlgorithm.getJcaName());
		
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
		   .setSigningKey(secret)
		   .parseClaimsJws(jwt).getBody();
		
		System.out.println("ID: " + claims.getId());
		System.out.println("Subject: " + claims.getSubject());
		System.out.println("Issuer: " + claims.getIssuer());
		System.out.println("Expiration: " + claims.getExpiration());
	}
}
