package ro.iss.salvatirosiamontana;

import ro.iss.salvatirosiamontana.common.GCMDelegate;
import ro.iss.salvatirosiamontana.common.MainConstants;
import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gcm.rosiamontana.GCMHelper;

public class RosiaMontanaApplication extends Application {

	private static final String TAG = RosiaMontanaApplication.class
			.getSimpleName();

	private GCMHelper mGCMHelper;

	@Override
	public void onCreate() {
		super.onCreate();
		mGCMHelper = new GCMDelegate(this, MainConstants.CLIENT_KEY);
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
