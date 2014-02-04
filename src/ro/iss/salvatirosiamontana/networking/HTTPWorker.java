package ro.iss.salvatirosiamontana.networking;

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

import android.util.Log;

public class HTTPWorker implements NetworkWorkerFactory.NetworkWorker {

	public static final String REGISTER_URL = "http://salvatirosiamontana-admin.herokuapp.com/register?client_id=";
	public static final String SEND_URL = "http://salvatirosiamontana-admin.herokuapp.com/send";
	private static final String TAG = HTTPWorker.class.getSimpleName();

	/**
	 * Send a registration POST request to the backend Usage example: POST:
	 * http://localhost:5000/register?client_id=45674 Body: {"location":"Cluj",
	 * "continent":"Europe"}
	 */
	@Override
	public void register(String clientID, String location, String continent)
			throws ClientProtocolException, IOException {
		// Create a new HttpClient and Post Header
		HttpPost httppost = new HttpPost(REGISTER_URL + clientID);

		// Add your data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("location", location));
		nameValuePairs.add(new BasicNameValuePair("continent", continent));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		executeCommand(httppost);
	}

	// see
	// http://androidsnippets.com/executing-a-http-post-request-with-httpclient

	/**
	 * Sends data, to the back-end server.
	 *
	 * Usage example: POST: http://localhost:5000/send Body: { "filter":
	 * [{"field": "location", "value": "Cluj"}, {"field": "continent", "value":
	 * "Romania"}], "message":"This is a test message"}
	 */
	@Override
	public void sendData(String location, String continent, String message)
			throws ClientProtocolException, IOException {
		// Create a new HttpClient and Post Header
		HttpPost httppost = new HttpPost(SEND_URL);

		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("[{\"field\":\"location\", \"value\": \"")
				.append(location)
				.append("\"}, {\"field\": \"continent\", \"value\":\"")
				.append(continent).append("\"}]");

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("filter", jsonBuilder
				.toString()));
		nameValuePairs.add(new BasicNameValuePair("message", message));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		// Execute HTTP Post Request
		executeCommand(httppost);

	}

	private static void executeCommand(final HttpPost httppost)
			throws ClientProtocolException, IOException {
		final HttpClient httpclient = new DefaultHttpClient();
		// Execute HTTP Post Request
		HttpResponse response = httpclient.execute(httppost);
		Log.d(TAG, " .executeCommand(): " + response.getStatusLine() + "  // "
				+ response);
	}
}
