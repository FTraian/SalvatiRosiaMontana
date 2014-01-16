package ro.iss.salvatirosiamontana;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Receive notifications if app is updated
 * @author Administrator
 *
 */
public class OnUpgradeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//if upgraded re-register GCM
		((RosiaMontanaApplication)context.getApplicationContext()).getGCMHelper().registerInBackground();
	}
}
