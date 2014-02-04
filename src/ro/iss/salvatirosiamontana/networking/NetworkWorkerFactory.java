package ro.iss.salvatirosiamontana.networking;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

/**
 * Create Network communication workers
 * @author Administrator
 *
 */
public class NetworkWorkerFactory {

	public enum WorkerType {VOLLEY, HTTP}

	public interface NetworkWorker {
		public void sendData(String location, String continent, String message)
				throws ClientProtocolException, IOException;

		public void register(String clientID, String location, String continent)
				throws ClientProtocolException, IOException;
	}

	public static NetworkWorker getNetworkWorker(WorkerType type) {
		switch (type) {
		case VOLLEY:
			return new VolleyWorker();
		default:
			return new HTTPWorker();
		}
	}

}
