package osgtesting.dao;

import static org.junit.Assert.*;
import java.sql.*;

import org.junit.Test;

import osgtesting.Model.UserDTO;

public class userDaoTest {

	@Test
	public void testLogin() throws SQLException {
		userDao tester = new userDao();
		ResultSet rs = tester.login("test", "test123");
		Statement st;
		st = rs.getStatement();
		assertEquals("Statement in rs is what is expected",
				st.toString(), "Select username,password,salt from freesurfer_interface.users Where username='test'");
	}

	@Test
	public void testEdit() throws SQLException {
		userDao tester = new userDao();
		ResultSet rs = tester.edit("test2");
		Statement st;
		st = rs.getStatement();
		assertEquals("Statement in rs is what is expected",
				st.toString(), "Select * from freesurfer_interface.users where username='test2'");
	}

	@Test(timeout=500)
	public void testWrite() {
		userDao tester = new userDao();
		UserDTO account = new UserDTO("test", "test123", "testfirst", "testlast", "test@test.com", "tester", "1231231234", "abcd");
		tester.Write(account);	
	}

}
