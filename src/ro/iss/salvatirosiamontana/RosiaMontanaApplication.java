package ro.iss.salvatirosiamontana;

import ro.iss.salvatirosiamontana.common.GCMDelegate;
import ro.iss.salvatirosiamontana.common.MainConstants;
import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gcm.rosiamontana.GCMHelper;

public class RosiaMontanaApplication extends Application {

	private static final String TAG = RosiaMontanaApplication.class
			.getSimpleName();

	private GCMHelper mGCMHelper;

	/**
	 * A singleton instance of the application class for easy access in other
	 * places
	 */
	private static RosiaMontanaApplication sInstance;
	/**
	 * Global request queue for Volley
	 */
	private RequestQueue mRequestQueue;

	@Override
	public void onCreate() {
		super.onCreate();

		sInstance = this;

		initGCM();
	}

	/**
	 * @return RosiaMontanaApplication singleton instance
	 */
	public static synchronized RosiaMontanaApplication getInstance() {
		return sInstance;
	}

	public GCMHelper getGCMHelper() {
		return mGCMHelper;
	}

	/**
	 * @return The Volley Request queue, the queue will be created if it is null
	 */
	public RequestQueue getRequestQueue() {
		// lazy initialize the request queue, the queue instance will be
		// created when it is accessed for the first time
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	// TODO show dialog to update PlayStore if error!!
	private void initGCM() {
		mGCMHelper = new GCMDelegate(this, MainConstants.GCM_SENDER_ID);

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
