package ro.iss.salvatirosiamontana;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import ro.iss.salvatirosiamontana.util.HTTPHelper;
import ro.iss.salvatirosiamontana.util.MainConstants;

import com.google.android.gcm.rosiamontana.GCMHelper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class RosiaMontanaApplication extends Application {

	private static class MyGCMHelper extends GCMHelper {

		public MyGCMHelper(Context context, String appID) {
			super(context, appID);
		}

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
				HTTPHelper.register(getRegistrationId(), savedCity,
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
	};

	private static final String TAG = RosiaMontanaApplication.class
			.getSimpleName();

	private GCMHelper mGCMHelper;

	@Override
	public void onCreate() {
		super.onCreate();
		mGCMHelper = new MyGCMHelper(this, MainConstants.CLIENT_KEY);
		checkGCMRegistered();
	}

	public GCMHelper getGCMHelper() {
		return mGCMHelper;
	}

	// TODO show dialog to update PlayStore if error!!
	private void checkGCMRegistered() {
		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.
		if (mGCMHelper.checkPlayServices()) {
			String regid = mGCMHelper.getRegistrationId();

			if (TextUtils.isEmpty(regid)) {
				Log.i(TAG, "Registering Application");
				mGCMHelper.registerInBackground();
			}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
	}

}
