package ro.iss.salvatirosiamontana;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class MyCustomReceiver extends BroadcastReceiver {
	private static final String TAG = "MyCustomReceiver";
	public static final String PREFS_NAME = "push";
	
	
	public void createNotification(Context context, String content, String url ,int id,String title) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(ro.iss.salvatirosiamontana.R.drawable.logo_frunza,
				content , System.currentTimeMillis());
		// Hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		
		Intent intent = new Intent(context, SalvatiRosia.class);
		intent.putExtra("content", content);
		intent.putExtra("payload", url);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		title = context.getResources().getString(R.string.app_name);
		notification.setLatestEventInfo(context, title,content, pendingIntent);
		notificationManager.notify(id, notification);

	}
	
	
	@Override
	public void onReceive(Context arg0, Intent intent) {
		try {
			String action = intent.getAction();
			String channel = intent.getExtras().getString("com.parse.Channel");
			JSONObject json = new JSONObject(intent.getExtras().getString(
					"com.parse.Data"));

			Log.d(TAG, "got action " + action + " on channel " + channel
					+ " with:");
			String url = "#";
			String title = "";
			String content="";
			Iterator itr = json.keys();
			while (itr.hasNext()) {
				
				String key = (String) itr.next();
				
				if (key.equalsIgnoreCase("url")){
					url = json.getString(key);
				}else if (key.equalsIgnoreCase("title")){
					title = json.getString(key);
				}else if (key.equalsIgnoreCase("content")){
					content = json.getString(key);
				}
				
				Log.d(TAG, "..." + key + " => " + json.getString(key));
			}
			
			 SharedPreferences settings = arg0.getSharedPreferences(PREFS_NAME, 0);
		     int id = settings.getInt("notificationId", 0);
		     
			createNotification(arg0, content,url, id++, title);
			
			SharedPreferences.Editor editor = settings.edit();
		     editor.putInt("notificationId", id++);
		     editor.commit();
		     
		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}

	}

}
