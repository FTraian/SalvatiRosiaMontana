package ro.iss.salvatirosiamontana;

import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class SalvatiRosia extends Activity {
	private static final String PREFS_NAME = "settings";
	ProgressBar progressBar;
	// PullToRefreshWebView mWebView;
	WebView browser;
	private boolean doubleBackToExitPressedOnce;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_salvati_rosia);// webView1
		// mWebView = (PullToRefreshWebView) findViewById(R.id.webView1);

		// browser = mWebView.getRefreshableView();
		browser = (WebView) findViewById(R.id.webView1);
		browser.getSettings().setJavaScriptEnabled(true);
		browser.setWebViewClient(new CustomWebViewClient());
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		
		initialiseParse();
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		boolean isFirstRun = settings.getBoolean("isFirstRun", true);

		if (isFirstRun) {
			startActivity(new Intent(SalvatiRosia.this, Settings.class));
			register("Broadcast");
			register("Romana");
			register("BUCURESTI");
			load();
		} else {

			Bundle extras = getIntent().getExtras();
			if (extras != null) {

				String payload = extras.getString("payload");
				if (payload != null) {
					browser.loadUrl(payload);
				} else {
					load();
				}
			} else {
				load();
			}
		}

		SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor edit = preferences.edit();

		edit.putBoolean("isFirstRun", false);
		edit.commit();

	}

	@Override
	public void onBackPressed() {
		if (browser.canGoBack()) {
			browser.goBack();
		} else {
			if (doubleBackToExitPressedOnce) {
				super.onBackPressed();
				return;
			}
			this.doubleBackToExitPressedOnce = true;
			Toast.makeText(this, R.string.exit_press_back_twice_message, Toast.LENGTH_SHORT).show();
		}
		//
	}

	private void unregister(String channel) {
		PushService.unsubscribe(this, channel);
	}

	private void register(String channel) {
		PushService.subscribe(this, channel, SalvatiRosia.class);
	}

	private void initialiseParse() {
		Parse.initialize(getApplicationContext(), "rqVCq2D4FwTGyzJFg5ARrdPQm7U6hQ0PsZTpP9Jq", "ZBzofMaATJLPP6baG5RVUSUuFpwDv4hxnWfEqAja");
		ParseAnalytics.trackAppOpened(getIntent());
		PushService.setDefaultPushCallback(this, SalvatiRosia.class);
		ParseInstallation.getCurrentInstallation().saveInBackground();

	}

	private void loadMenu() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String lang = pref.getString("language", "Romana");
		String city = pref.getString("city", "BUCURESTI");
		if ("Romana".equalsIgnoreCase(lang)) {
			browser.loadUrl("http://m.rosiamontana.net/meniu/");
		} else if ("International".equalsIgnoreCase(lang)) {
			browser.loadUrl("http://m.rosiamontana.net/menu/");
		} else {
			browser.loadUrl("http://m.rosiamontana.net/meniu-disapora/");
		}
	}

	private void load() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String lang = pref.getString("language", "Romana");
		String city = pref.getString("city", "BUCURESTI");
		if ("Romana".equalsIgnoreCase(lang)) {
			browser.loadUrl("http://m.rosiamontana.net/meniu");
		} else if ("International".equalsIgnoreCase(lang)) {
			browser.loadUrl("http://m.rosiamontana.net/menu");
		} else {
			browser.loadUrl("http://m.rosiamontana.net/meniu-disapora");
		}
	}

	@Override
	protected void onResume() {
		initialiseParse();
		
		String url = "";
		if (browser != null)
			url = browser.getUrl();
		if (url != null && url.lastIndexOf("m.rosiamontana.net") != -1) {
			// load();
		}
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String city = pref.getString("city", "BUCURESTI");
		String lang = pref.getString("language", "Romana");

		Set<String> subscriptions = PushService.getSubscriptions(this);
		boolean cityUpToDate = false, langUpToDate = false, broadcastUpToDate = false;
		String contentChannel = "";
		String cityChannel = "";

		for (String subscr : subscriptions) {
			if ("Romana".equalsIgnoreCase(subscr) || "International".equalsIgnoreCase(subscr)
			        || "Diaspora".equalsIgnoreCase(subscr)) {
				contentChannel = subscr;
			} else if ("Broadcast".equalsIgnoreCase(subscr)) {
				broadcastUpToDate = true;
			} else {
				cityChannel = subscr;
			}
		}

		if (!contentChannel.equalsIgnoreCase(lang)) {
			unregister(contentChannel);
			register(lang);
		}

		if (!broadcastUpToDate) {
			register("Broadcast");
		}

		if ("Romana".equalsIgnoreCase(lang)) {
			if (!city.equalsIgnoreCase(cityChannel)) {
				unregister(cityChannel);
				register(city);
			}
		} else if (!cityChannel.equalsIgnoreCase("")) {
			unregister(cityChannel);
		}

		this.doubleBackToExitPressedOnce = false;
		super.onResume();
	}

	private class CustomWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith("fb://")) {
				String facebookScheme = url;
				Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookScheme));
				startActivity(facebookIntent);
			}
			if (url.startsWith("mailto:") || url.startsWith("tel:")) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(intent);
			}
			if (url.equalsIgnoreCase("http://m.rosiamontana.net/")) {
				load();
			}
			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {

			progressBar.setVisibility(View.INVISIBLE);

		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			view.loadData("<html><body></body></html>", "text/html", "utf-8");
			UIUtilities.createCommunicationErrorDialog(SalvatiRosia.this).show();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			progressBar.setVisibility(View.VISIBLE);
			super.onPageStarted(view, url, favicon);
		}

	}

	public void onSettingsItemClick(MenuItem item) {
		startActivity(new Intent(SalvatiRosia.this, Settings.class));
	}

	public void onHomeItemClick(MenuItem item) {
		loadMenu();
	}

	public void onDonateItemClick(MenuItem item) {
		browser.loadUrl("http://m.rosiamontana.net/doneaza");
	}

	public void onShareItemClick(MenuItem item) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(android.content.Intent.EXTRA_TEXT, browser.getUrl());
		intent.putExtra(android.content.Intent.EXTRA_STREAM, R.drawable.logo_frunza);
		startActivity(Intent.createChooser(intent, "Uniti salvam"));
	}

	public void onAboutItemClick(MenuItem item) {
		browser.loadUrl("http://m.rosiamontana.net/about/");
	}

	public void onExitItemClick(MenuItem item) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

	public void onFollowItemClick(MenuItem item) {
		String facebookScheme = "fb://profile/145342615479115";
		Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookScheme));
		startActivity(facebookIntent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.home:
			onHomeItemClick(item);
			break;
		case R.id.settings:
			onSettingsItemClick(item);
			break;
		case R.id.donate:
			onDonateItemClick(item);
			break;
		case R.id.share:
			onShareItemClick(item);
			break;
		case R.id.about:
			onAboutItemClick(item);
			break;
		case R.id.exit:
			onExitItemClick(item);
			break;
		case R.id.follow:
			onFollowItemClick(item);
			break;
		default:
			break;
		}

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.salvati_rosia, menu);
		return true;
	}

}
