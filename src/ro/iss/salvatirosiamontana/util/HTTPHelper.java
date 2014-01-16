package ro.iss.salvatirosiamontana.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.google.android.gcm.rosiamontana.GCMHelper;

public class HTTPHelper {

	public static final String URL = "http://localhost:5000/register?client_id=";

	/**
	 * Send a registration POST request to the backend
	 * Usage example: POST: http://localhost:5000/register?client_id=45674 Body:
	 * {"location":"Cluj", "continent":"Europe"}
	 */
	public static void postData(String clientID, String location, String continent) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(URL+clientID);

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("location", location));
	        nameValuePairs.add(new BasicNameValuePair("continent", continent));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);

	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	}
	// see http://androidsnippets.com/executing-a-http-post-request-with-httpclient



}
