package osgtesting.dao;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;
import osgtesting.dao.JobsDAO;
import osgtesting.Model.JobsDTO;
import osgtesting.Model.UserDTO;

import java.io.IOException;
import java.io.File;
import java.util.List;

import java.util.ArrayList;
/*
 * Requires freesurfer_test.py to be running on localhost
 * */
public class JobsDAOTest {
		
	
	/***************************/
	/**   WriteGoodParameterTest  **/
	/***************************/
	/*  Valid Write Good Param Test */
	@Test
	public void testWriteGoodParam() {
		UserDTO test_user = new UserDTO("1","username", "password","Bill", "Laboon",
									   "laboon@laboon.com","Univ of Pitt", 
									   "412-867-5309", "SodiumChloride", true );
		try{
			test_user.setId("12345678");
			JobsDAO tester = new JobsDAO(test_user);
			//Test file
			File test_file = File.createTempFile("test_file", ".mgz");
			//Test JobsDTO
			String time_stamp = Long.toString((System.currentTimeMillis() / 1000L));
			JobsDTO test_job = new JobsDTO("123123", test_user, "UPLOADED",time_stamp, "test_job");
			//Perform the Write
			int http_code = tester.Write(test_job, test_file);
			Assert.assertNotEquals(400, http_code);
		}
		catch(Exception e){
			e.printStackTrace();
			fail("Exception: "+ e.toString()+" was thrown.");
		}
	}
	/***************************/
	/**  GetJobsGoodParameterTest **/
	/**
	 * @throws IOException *****/
	/*  Valid Write Param Test */
	@Test
	public void testGetJobsGoodParam() throws IOException{
		UserDTO test_user = new UserDTO("1",
				   "username", "password","Bill", "Laboon",
				   "laboon@billlaboon.com","Univ of Pitt", 
				   "412-867-5309", "SodiumChloride" ,true);
		test_user.setId("12345678");
		List<JobsDTO> job_list = new ArrayList<JobsDTO>();
		
		try{
			JobsDAO tester = new JobsDAO(test_user);
			job_list = tester.GetJobs(test_user);
			Assert.assertNotNull(job_list);
		}
		catch(Exception e){
			e.printStackTrace();
			fail("Exception: "+ e.toString()+" was thown.");
		}
		
	}
	
}
