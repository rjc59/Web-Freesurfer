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
import com.squareup.okhttp.HttpUrl;
public class JobsDao {
	private String freesurfer_interface;
	private int port;
	private OkHttpClient client;
	private CryptoToolbox hasher;
	private String timeStamp;
	private String token;
	public JobsDao(UserDTO user){
		client = new OkHttpClient();
		hasher = new CryptoToolbox();
		//Get Unix Epoch time
		timeStamp = Long.toString((System.currentTimeMillis() / 1000L));
		//token = SHA256 of Salt + User's Password + Epoch time
		byte[] token_bytes = hasher.hashSHA256(user.getSalt().concat(user.getPass()).concat(timeStamp).getBytes());
		token = new String(token_bytes);
		freesurfer_interface = "localhost";
		port = 8081;
	}
	public void Write(JobsDTO job){
		//create request body
		RequestBody file_body = RequestBody.create(MediaType.parse("application/plain"), job.getJobFile());
		RequestBody request_body = new MultipartBuilder().type(MultipartBuilder.FORM)
				.addFormDataPart("jobfile", job.getJobFile().getName(), file_body)
				.build();
		//create url
		HttpUrl request_url = new HttpUrl.Builder()
				.scheme("http")
				.host(freesurfer_interface)
				.port(port)
				.addPathSegment("freesurfer")
				.addPathSegment("jobs")
				.addQueryParameter("userid", job.getAuthor().getId())
				.addQueryParameter("token", token)
				.addQueryParameter("singlecore", "true")
				.addQueryParameter("jobname", job.getJobFile().getName())
				.build();
		//create post request
		Request http_request = new Request.Builder()
			.url(request_url)
			.post(request_body)
			.build();
		//make RESTful calls to OSGConnect freesurfer_interface
		Response http_response;
		try{
			http_response = client.newCall(http_request).execute();	
			switch(http_response.code())
			{
				case 200:
					System.err.println("Jobfile:"+job.getJobFile().getName()+" was successfully submitted.");
					break;
				case 400:
					System.err.println("Job submission for file:"+ job.getJobFile().getName() + " had malformed parameters");
					break;
				case 500:
					System.err.println("Job submission for file:"+ job.getJobFile().getName() + " caused a server error");
					break;
			}
			//clean up jobfile
		}
		catch(Exception e)
		{
			//clean up job file
			e.printStackTrace();
		}
		
	}
}


