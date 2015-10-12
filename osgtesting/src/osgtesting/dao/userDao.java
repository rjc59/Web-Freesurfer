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
			PreparedStatement pstmt=con.prepareStatement("Select * from freesurfer_interface.users");
			ResultSet rs=pstmt.executeQuery();
			//ResultSet rs = con.getMetaData().getTables(null, null, "%", null);
			//while (rs.next()) {
			 // System.out.println(rs.getString(3));
			//}
			if(!rs.equals(null)){
				System.out.println("It got something");
				System.out.println(rs.getFetchSize());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
		
	}



}

