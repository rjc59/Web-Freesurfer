package osgtesting.email;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmailerTest {
	
	/*******************/
	/** Creation Test **/
	/*******************/
	@Test
	public void testEmailerCreation() {
		Emailer test = new Emailer();
		
		assertNotNull( test );
	}

	/**************************/
	/**   Activation Test    **/
	/**************************/
	/*  Test for valid email  */
	@Test
	public void testEmailerSendValid() {
		boolean works = true;
		Emailer test = new Emailer();
		test.To = "opensciencegridtesting@gmail.com";
		
		try {
			test.send();
		} catch ( Exception e ) {
			works = false;
		}
		
		assertTrue( works );
	}
	/* Test for invalid email */
	@Test
	public void testEmailerSendInvalid() {
		boolean works = false;
		Emailer test = new Emailer();
		
		try {
			test.send();
		} catch ( Exception e ) {
			works = true;
		}
		
		assertTrue( works );
	}
}
