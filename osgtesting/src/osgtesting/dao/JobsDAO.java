package osgtesting.dao;
import osgtesting.Model.JobsDTO;
import osgtesting.Model.UserDTO;
import osgtesting.Util.CryptoToolbox;
import java.io.File;
import java.util.ArrayList;
import java.sql.SQLException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.bind.DatatypeConverter;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.HttpUrl;
public class JobsDAO {
	private String freesurfer_interface;
	private int port;
	private OkHttpClient client;
	private CryptoToolbox hasher;
	private String timeStamp;
	private String token;
	
	
	public JobsDAO(UserDTO user){
		client = new OkHttpClient();
		hasher = new CryptoToolbox();
		//Get Unix Epoch time
		timeStamp = Long.toString((System.currentTimeMillis() / 1000L));
		//token = SHA256(SHA256(Salt + User's Password) + Epoch time)
		String shared_secret;
		try {
			shared_secret = new String(hasher.hashSHA256(user.getSalt().concat(user.getPass()).getBytes("UTF-8")));
			byte[] token_hash = hasher.hashSHA256(shared_secret.concat(timeStamp).getBytes("UTF-8"));
			token = DatatypeConverter.printBase64Binary(token_hash);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		freesurfer_interface = "localhost";
		port = 8085;
	}
	/** GetJobs
	 *  Takes a UserDTO and returns the a list of all jobs
	 *  
	 *  @param user -    A UserDTO to query a jobs list for
	 *   
	 */
	public int GetJobs(UserDTO user, ArrayList<JobsDTO> job_list) throws IOException
	{
		//create url
		HttpUrl request_url = new HttpUrl.Builder()
				.scheme("http")
				.host(freesurfer_interface)
				.port(port)
				.addPathSegment("freesurfer")
				.addPathSegment("jobs")
				.addQueryParameter("userid", user.getId())
				.addQueryParameter("token", token)
				.build();
		//create post request
		System.err.println("URL: "+ request_url);
		Request http_request = new Request.Builder()
			.url(request_url)
			.get()
			.build();
				//make RESTful calls to OSGConnect freesurfer_interface		
		
		Response http_response;
		http_response = client.newCall(http_request).execute();	
			switch(http_response.code())
			{
				case 200:
					System.err.println("Sucessfully queried for running jobs Code:200");
					break;
				case 400:
					System.err.println("Malformed Http parameters for jobs query Code:400");
					break;
				case 500:
					System.err.println("Query for jobs caused server error Code:500");
					break;
			}
		//dummy call right now	
		job_list = new ArrayList<JobsDTO>();
		return http_response.code();
	}
	/** Write
	 *  Takes a JobsDTO and and an Image file and submits it to be processed
	 *  
	 *  @param user -    A JobsDTO containing the information for the new job
	 *  @param job_file - A java File object to reference the image sequence file 
	 */
	public int Write(JobsDTO job, File job_file) throws IOException
	{
		//create request body
		RequestBody file_body = RequestBody.create(MediaType.parse("application/plain"), job_file);
		RequestBody request_body = new MultipartBuilder().type(MultipartBuilder.FORM)
				.addFormDataPart("jobfile", job_file.getName(), file_body)
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
				.addQueryParameter("jobname", job_file.getName())
				.build();
		//create post request
		System.err.println("URL: "+ request_url);
		Request http_request = new Request.Builder()
			.url(request_url)
			.post(request_body)
			.build();
		//make RESTful calls to OSGConnect freesurfer_interface
		Response http_response;
		http_response = client.newCall(http_request).execute();	
		switch(http_response.code())
		{
			case 200:
				System.err.println("Jobfile:"+job_file.getName()+" was successfully submitted. Code:200");
				break;
			case 400:
				System.err.println("Job submission for file:"+ job_file.getName() + " had malformed parameters. Code 400");
				break;
			case 500:
				System.err.println("Job submission for file:"+ job_file.getName() + " caused a server error. Code 500");
				break;
		}
		return http_response.code();
	}
}


