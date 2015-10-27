package osgtesting.dao;
import osgtesting.Model.JobsDTO;
import osgtesting.Model.UserDTO;
import osgtesting.Util.CryptoToolbox;
import java.sql.*;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
public class JobsDao {
	private Connection con;
	private String dbURL;
	private String osgConnectURL;
	private OkHttpClient client;
	private CryptoToolbox hasher;
	private String timeStamp;
	private String token;
	public JobsDao(UserDTO user){
		dbURL = "jdbc:postgresql://webfreesurferdb.cbiow68bwd0c.us-east-1.rds.amazonaws.com:5432/osgtestdb";
		client = new OkHttpClient();
		hasher = new CryptoToolbox();
		timeStamp = Long.toString((System.currentTimeMillis() / 1000L));
		
		byte[] tokenBytes = hasher.HashSHA256(user.getSalt().concat(timeStamp).getBytes());
		token = new String(tokenBytes);
		try{
			DriverManager.registerDriver(new org.postgresql.Driver());
			con = DriverManager.getConnection(dbURL,"administrator","osgtestdatabase");
			System.out.println(con.equals(null));
		}
		catch(SQLException e){
			e.printStackTrace();
			System.err.println("Could not access "+ dbURL);
		}
	}
	public void Write(JobsDTO job){
		//create request body
		RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
			.addFormDataPart("userid", userid)
			.addFormDataPart("token", token)
			.addFormDataPart("filename", filename)
			.addFormDataPart("singlecore", "1")
			.addFormDataPart("jobname", jobname)
			.addFormDataPart("jobfile", filename, RequestBody.create(MediaType.parse("application/plain", job.GetFile()))
			.build();
		//create post request
		Request request = new Request.Builder()
			.url(osgConnectURL+ "/freesurfer/jobs")
			.post(body)
			.build();
		//make RESTful calls to OSGConnect freesurfer_interface
		
	}
}


