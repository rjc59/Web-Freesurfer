package osgtesting.dao;

import java.sql.*;
import java.util.ArrayList;

import osgtesting.Model.UserDTO;

public class userDao {
	private Connection con;

	public userDao(){
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
	
	public ResultSet login(String username, String Password){
		PreparedStatement pst;
		ResultSet rs=null;
		try{
			pst=con.prepareStatement("Select username,password,salt from freesurfer_interface.users Where username=?");
			pst.setString(1, username);
			rs=pst.executeQuery();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return rs;
		
		
	}
	
	public ResultSet edit(String username){
		PreparedStatement pst;
		ResultSet rs=null;
		try{
			pst=con.prepareStatement("Select * from freesurfer_interface.users where username=?");
			pst.setString(1,username);
			rs=pst.executeQuery();
		}catch(SQLException e){
		e.printStackTrace();}
		
		return rs;
	}

	public void Write(UserDTO account){
		PreparedStatement pst;
		try {
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String> read(){
		try {
			PreparedStatement pstmt=con.prepareStatement("Select * from freesurfer_interface.users");
			ResultSet rs=pstmt.executeQuery();
			//ResultSet rs = con.getMetaData().getTables(null, null, "%", null);
			//while (rs.next()) {
			// System.out.println(rs.getString(3));
			//}
			if(!rs.equals(null)){
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return null;

	}



}

