package osgtesting.dao;

import static org.junit.Assert.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import org.junit.Test;
import osgtesting.Model.UserDTO;

public class UserDAOTest {

	/*******************/
	/** Creation Test **/
	/*******************/
	@Test
	public void testUserDaoCreation() {
		UserDAO tester = new UserDAO();
		
		assertNotNull( tester );
	}
	
	/***************************/
	/**      Login Test       **/
	/***************************/
	/*  Valid User Login Test  */
	@Test
	public void testUserDaoLoginValid() {
		UserDAO tester = new UserDAO();
		try {
			ResultSet rs = tester.login("test", "test123");
			Statement st;
			st = rs.getStatement();
			assertEquals("Statement in rs is what is expected",
					st.toString(), "Select username,password,salt from freesurfer_interface.users Where username='test'");
		} catch ( Exception e ) {
			fail();
		}
	}
	
	/* Invalid User Login Test */
	public void testUserDaoLoginInvalid() {
		UserDAO tester = new UserDAO();
		try {
			ResultSet rs = tester.login("thisusernamewillneverbeused", "test123");
			Statement st;
			st = rs.getStatement();
			fail();
		} catch ( Exception e ) {
			assertTrue( true );
		}
	}

	/***************/
	/** Edit Test **/
	/***************/
	@Test
	public void testUserDaoEdit() throws SQLException {
		UserDAO tester = new UserDAO();
		ResultSet rs = tester.edit("test2");
		Statement st;
		st = rs.getStatement();
		assertEquals("Statement in rs is what is expected",
				st.toString(), "Select * from freesurfer_interface.users where username='test2'");
	}
	
	/***************/
	/** Read Test **/
	/***************/
	@Test
	public void testUserDaoRead() {
		UserDAO tester = new UserDAO();
		List<UserDTO> users = tester.read();
		
		assertNotNull( users );
	}
	
	/****************************************************************/
	/** Write Test                                                 **/
	/*  To test this functionality remove "try {" and the entirety  */
	/*  of the "catch" block from the function and uncomment the    */
	/*  throw                                                       */
	/****************************************************************/
	@Test(timeout=500)
	public void testUserDaoWrite() {
		UserDAO tester = new UserDAO();
		UserDTO account = new UserDTO("1","test", "test123", "testfirst", "testlast", "test@test.com", "tester", "1231231234", "abcd", true);
		try {
			tester.write(account);	
		} catch ( Exception e ) {
			fail();
		}
	}
	
	/****************************************************************/
	/** Update Account Test                                        **/
	/*  To test this functionality remove "try {" and the entirety  */
	/*  of the "catch" block from the function and uncomment the    */
	/*  throw                                                       */
	/****************************************************************/
	@Test(timeout=500)
	public void testUserDaoUpdateAccount() {
		UserDAO tester = new UserDAO();
		UserDTO account = new UserDTO("1","test", "test123", "testfirst", "testlast", "test@test.com", "tester", "1231231234", "abcd", true);
		try {
			tester.update(account);	
		} catch ( Exception e ) {
			fail();
		}
	}
	
	/****************************************************************/
	/** Update Password Test                                       **/
	/*  To test this functionality remove "try {" and the entirety  */
	/*  of the "catch" block from the function and uncomment the    */
	/*  throw                                                       */
	/****************************************************************/
	@Test(timeout=500)
	public void testUserDaoUpdatePassword() {
		UserDAO tester = new UserDAO();
		try {
			tester.updatePassword( "test", "test123", "abcd" );	
		} catch ( Exception e ) {
			fail();
		}
	}
}
