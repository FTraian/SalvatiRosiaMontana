package ro.iss.am.salvatirosiamontana;

import com.parse.Parse;

import android.app.Application;
import android.util.Log;

public class RosiaMontanaApplication extends Application {

	private static final String TAG = RosiaMontanaApplication.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, ".onCreate() ");
		Parse.initialize(this, "rqVCq2D4FwTGyzJFg5ARrdPQm7U6hQ0PsZTpP9Jq", "ZBzofMaATJLPP6baG5RVUSUuFpwDv4hxnWfEqAja");
	}


}
