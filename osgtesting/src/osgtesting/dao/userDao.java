package osgtesting.dao;

import java.sql.*;
import java.util.ArrayList;

public class userDao {
	private Connection con;

	public userDao(){
		try{
		 con = DriverManager.getConnection("jdbc:postgresql://webfreesurferdb.cbiow68bwd0c.us-east-1.rds.amazonaws.com:5432/osgtestdb","administrator","osgtestdatabase");
		 System.out.println(con.equals(null));
		}
		catch(Exception e){
			System.out.println("uh-oh");
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> read(){
		try {
			PreparedStatement pstmt=con.prepareStatement("Select * from osgtestdb.users");
			ResultSet rs=pstmt.executeQuery();
			if(rs.equals(null)){
				System.out.println("It got something");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
		
	}



}

