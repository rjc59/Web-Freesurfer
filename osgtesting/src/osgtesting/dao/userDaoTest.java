package osgtesting.dao;

import static org.junit.Assert.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import org.junit.Test;
import osgtesting.Model.UserDTO;

public class UserDaoTest {

	/*******************/
	/** Creation Test **/
	/*******************/
	@Test
	public void testUserDaoCreation() {
		UserDao tester = new UserDao();
		
		assertNotNull( tester );
	}
	
	/***************************/
	/**      Login Test       **/
	/***************************/
	/*  Valid User Login Test  */
	@Test
	public void testUserDaoLoginValid() {
		UserDao tester = new UserDao();
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
		UserDao tester = new UserDao();
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
		UserDao tester = new UserDao();
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
		UserDao tester = new UserDao();
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
		UserDao tester = new UserDao();
		UserDTO account = new UserDTO("test", "test123", "testfirst", "testlast", "test@test.com", "tester", "1231231234", "abcd");
		try {
			tester.Write(account);	
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
		UserDao tester = new UserDao();
		UserDTO account = new UserDTO("test", "test123", "testfirst", "testlast", "test@test.com", "tester", "1231231234", "abcd");
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
		UserDao tester = new UserDao();
		try {
			tester.updatePassword( "test", "test123", "abcd" );	
		} catch ( Exception e ) {
			fail();
		}
	}
}
