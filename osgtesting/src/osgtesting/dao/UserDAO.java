package osgtesting.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import osgtesting.Model.UserDTO;

public class UserDAO {
	private Connection con;

	/**
	 * Constructor: creates connection to application db
	 */
	public UserDAO(){
		try{
			DriverManager.registerDriver(new org.postgresql.Driver());
			con = DriverManager.getConnection("jdbc:postgresql://webfreesurferdb.cbiow68bwd0c.us-east-1.rds.amazonaws.com:5432/osgtestdb","administrator","osgtestdatabase");
			System.out.println(con.equals(null));
			
		}
		catch(Exception e){
			System.out.println("uh-oh");
			e.printStackTrace();
		}
	}
	
	/**
	 * Queries DB for account based on username
	 * 
	 * @param username Username entered by user
	 * @param Password Password entered by user
	 * @return ResultSet containing single entry, matched to username of username, password and salt
	 */
	public ResultSet login(String username, String Password){
		PreparedStatement pst;
		ResultSet rs=null;
		try{
			pst=con.prepareStatement("Select username,password,salt from freesurfer_interface.users Where username=?");
			pst.setString(1, username);
			rs=pst.executeQuery();
			//pst.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return rs;
		
		
	}
	/**
	 * Queries account based on username
	 * 
	 * @param username Username provided by user
	 * @return Single entry containing all account info matching username provided
	 */
	public ResultSet edit(String username){
		PreparedStatement pst=null;
		ResultSet rs=null;
		try{
			pst=con.prepareStatement("Select * from freesurfer_interface.users Where username=?");
			pst.setString(1,username);
			rs=pst.executeQuery();
		}catch(SQLException e){
		e.printStackTrace();
		}
			
		
		return rs;
	}

	/**
	 * Writes new account DTO to db
	 * 
	 * @param account DTO created by obtaining information from input boxes
	 */
	public void write(UserDTO account) throws SQLException {
		PreparedStatement pst;
		pst = con.prepareStatement("INSERT INTO freesurfer_interface.users (username,first_name,last_name,email,institution,phone,password,salt)"
				+ " Values(?,?,?,?,?,?,?,?)");
		pst.setString(1, account.getUserName());
		pst.setString(2, account.getName());
		pst.setString(3, account.getSurname());
		pst.setString(4, account.getEmail());
		pst.setString(5, account.getInst());
		pst.setString(6, account.getPhone());
		pst.setString(7, account.getPass());
		pst.setString(8, account.getSalt());
		
		pst.execute();
	}

	/**
	 * Method to ensure db connection and users exist
	 * 
	 * @return All values from users db
	 */
	public List<UserDTO> read(){
		List<UserDTO> results= new ArrayList<UserDTO>();
		try {
			PreparedStatement pstmt=con.prepareStatement("Select * from freesurfer_interface.users");
			ResultSet rs=pstmt.executeQuery();
			while (rs.next()) {
			results.add(new UserDTO(rs.getString(2),rs.getString(8),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(9)));
			}
			if(!rs.equals(null)){
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return results;

	}
	
	/**
	 * Takes updated DTO, finds corresponding row and updates that row.
	 * 
	 * @param update DTO with updated infro based on user input
	 */

	public void update(UserDTO update) /*throws SQLException */{
		try {
			PreparedStatement pstmt=con.prepareStatement("update freesurfer_interface.users set first_name=?,last_name=?,email=?,institution=?,phone=?"
					+ " where username=?");
			pstmt.setString(1, update.getName());
			pstmt.setString(2, update.getSurname());
			pstmt.setString(3, update.getEmail());
			pstmt.setString(4, update.getInst());
			pstmt.setString(5, update.getPhone());
			pstmt.setString(6, update.getUserName());

			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	/**
	 * Finds corresponding account based on username and updates login information
	 * 
	 * @param userName Username of account
	 * @param newPass New encoded password
	 * @param newSalt New salt
	 */
	public void updatePassword(String userName, String newPass, String newSalt) /*throws SQLException */{
		try {
			PreparedStatement pstmt=con.prepareStatement("update freesurfer_interface.users set password=?, salt=? "
					+ " where username=?");
			pstmt.setString(1, newPass);
			pstmt.setString(2, newSalt);
			pstmt.setString(3, userName);

			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



}

