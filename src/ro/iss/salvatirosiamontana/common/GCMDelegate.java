package ro.iss.salvatirosiamontana.common;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import ro.iss.salvatirosiamontana.networking.NetworkWorkerFactory;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gcm.rosiamontana.GCMHelper;

public class GCMDelegate extends GCMHelper {

	private static final String TAG = GCMDelegate.class.getSimpleName();

	public GCMDelegate(Context context, String appID) {
		super(context, appID);
	}

	/**
	 * This is called on an AsynkTask from the GCMHelper
	 */
	@Override
	public void sendRegistrationIdToBackend() {
		Log.i(TAG, " .sendRegistrationIdToBackend(): ");
		SharedPreferences mDefaultPrefferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		String savedCity = mDefaultPrefferences.getString(
				MainConstants.KEY_SAVED_CITY, MainConstants.DEFAULT_CITY);
		String savedLanguage = mDefaultPrefferences.getString(
				MainConstants.KEY_SAVED_LANGUAGE,
				MainConstants.DEFAULT_LANGUAGE);

		try {
			NetworkWorkerFactory.getNetworkWorker(NetworkWorkerFactory.WorkerType.HTTP).register(getRegistrationId(), savedCity,
					savedLanguage);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block, handle Network errors and
			// resend data
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}