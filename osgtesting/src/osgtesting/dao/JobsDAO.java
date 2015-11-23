package osgtesting.dao;
import osgtesting.Model.JobsDTO;
import osgtesting.Model.UserDTO;
import osgtesting.Util.CryptoToolbox;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
import org.json.JSONObject;
import org.json.JSONArray;

public class JobsDAO {
	private String freesurfer_interface;
	private int port;
	private OkHttpClient client;
	private CryptoToolbox hasher;
	private String timeStamp;
	private String token;
	private UserDTO user;

	public JobsDAO(UserDTO user){
		client = new OkHttpClient();
		hasher = new CryptoToolbox();
		this.user = user;
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
		
		freesurfer_interface = "Scott-PC";
		port = 8085;
	}
	
	/** getUrllRequest
	 * creates a get request url with the user's query parameters
	 */
	private HttpUrl getUrlRequest()
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
		return request_url;
	}
	
	
	/** getHTTPRequest
	 * takes a get http request url and forms a get HTTP request
	 * @param request_url - a request url with query parameters added 
	 */
	private Request getHTTPRequest(HttpUrl request_url)
	{
		Request http_request = new Request.Builder()
				.url(request_url)
				.get()
				.build();
		return http_request;
	}
	
	
	/** writeUrlRequest
	 * takes a JobsDTO and a the Job file name to create a request url with query parameters
	 * @param job - containing data about the job being submitted
	 * @param job_file_name - String of the filename of the job file 
	 */
	private HttpUrl writeUrlRequest(JobsDTO job, String job_file_name)
	{
		HttpUrl request_url = new HttpUrl.Builder()
				.scheme("http")
				.host(freesurfer_interface)
				.port(port)
				.addPathSegment("freesurfer")
				.addPathSegment("jobs")
				.addQueryParameter("userid", job.getAuthor().getId())
				.addQueryParameter("token", token)
				.addQueryParameter("filename", job_file_name)
				.addQueryParameter("singlecore", "true")
				.addQueryParameter("jobname", job_file_name)
				.build();
		return request_url;
	}
	/** writeHTTPRequest
	 * takes a write request url and a post request body to generate a write(POST) HttpRequest
	 * 
	 * @param request_url - a HttpUrl object with the server end point and query parameters
	 * @param request_body - a post request body containing the job file
	 *
	 */
	private Request writeHTTPRequest(HttpUrl request_url, RequestBody request_body)
	{
		Request http_request = new Request.Builder()
				.url(request_url)
				.post(request_body)
				.build();
		return http_request;
	}
	
	/** createFileBody
	 * takes a new job file object and returns a request body containing the file
	 * 
	 * @param job_file -  a java File object containing the new job file
	 * 
	 */
	private RequestBody createFileBody(File job_file)
	{
		RequestBody file_body = RequestBody.create(MediaType.parse("application/plain"), job_file);
		RequestBody request_body = new MultipartBuilder().type(MultipartBuilder.FORM)
				.addFormDataPart("jobfile", job_file.getName(), file_body)
				.build();
		return request_body;
	}
	
	/** handleHttpResponse
	 *  Takes a htt_response and throws exceptions for bad responses
	 *  
	 *  @param http_response - 	A http_response from freesurfer_interface
	 *  
	*/
	private void handleHttpResponse(Response http_response) throws IOException
	{
		switch(http_response.code())
		{
			case 200:
				System.err.println("Rest api call was successful Code:200");
				break;
			case 400:
				System.err.println("Rest api called had malformed parameters. Code 400");
				throw new IOException("Error Code:400 Malformed Parameters");
			case 500:
				System.err.println("Rest api call caused server error. Code 500");
				throw new IOException("Error Code:500 Internal Server Error");
			default:
				System.err.println("Rest api returned unknown");
				break;
		}
	}
	
	/** GetJobs
	 *  Takes a UserDTO and returns the a list of all jobs
	 *  
	 *  @param user -    A UserDTO to query a jobs list for
	 *  @param jobs_list	- A list to store the jobs for this user 
	 */
	public List<JobsDTO> GetJobs(UserDTO user) throws IOException
	{
		HttpUrl request_url = getUrlRequest();
		//create post request
		System.err.println("URL: "+ request_url);
		Request http_request = getHTTPRequest(request_url);
		//make RESTful calls to OSGConnect freesurfer_interface		
		Response http_response;
		http_response = client.newCall(http_request).execute();	
		handleHttpResponse(http_response);
		JSONObject json_obj;
		List<JobsDTO> job_list = new ArrayList<JobsDTO>();
		try {
			json_obj= new JSONObject(http_response.body().string());
			JSONArray json_arr = json_obj.getJSONArray("jobs");
			for(int i = 0; i < json_arr.length(); i++)
			{
			
				JSONObject job_json = json_arr.getJSONObject(i);
			
				JobsDTO new_job = new JobsDTO(Integer.toString(job_json.getInt("id")),
											  user,
											  job_json.getString("status"),
											  "",
											  job_json.getString("name"));
				new_job.setOutput(job_json.getString("output"));
				job_list.add(new_job);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Bad JSON");
		}
		//{"jobs":[{"id":"1","input":"subj_1.mgz","job_name":"job_name1","url":"PROCESSING"},{"id":"23","input":"subj_182.mgz","job_name":"my_job2","url":"COMPLETED"}]}
		
		//dummy call right now	
		return job_list;
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
		
		RequestBody request_body = createFileBody(job_file);
		//create url
		HttpUrl request_url = writeUrlRequest(job, job_file.getName());
		//create post request
		System.err.println("URL: "+ request_url);
		Request http_request = writeHTTPRequest(request_url, request_body);
		//make RESTful calls to OSGConnect freesurfer_interface
		Response http_response;
		http_response = client.newCall(http_request).execute();	
		handleHttpResponse(http_response);
		return http_response.code();
	}
	
	public int delete(UserDTO user, String job_id) throws IOException
	{
		HttpUrl request_url = new HttpUrl.Builder()
				.scheme("http")
				.host(freesurfer_interface)
				.port(port)
				.addPathSegment("freesurfer")
				.addPathSegment("jobs")
				.addQueryParameter("userid", user.getId())
				.addQueryParameter("token", token)
				.addQueryParameter("jobid", job_id)
				.build();
		
		Request http_request = new Request.Builder()
				.url(request_url)
				.delete()
				.build();
		Response http_response;
		http_response = client.newCall(http_request).execute();	
		handleHttpResponse(http_response);
		return http_response.code();
	}
}


