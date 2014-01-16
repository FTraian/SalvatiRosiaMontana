package ro.iss.salvatirosiamontana;

import java.util.Set;

import ro.iss.salvatirosiamontana.util.MainConstants;
import ro.iss.salvatirosiamontana.util.UIUtilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class SalvatiRosia extends Activity {

    private static final String TAG = SalvatiRosia.class.getSimpleName();
    private static final String EXTRA_CONTENT = "content";
    private static final String EXTRA_PAYLOAD = "payload";

    private ProgressBar progressBar;
    // PullToRefreshWebView mWebView;
    private WebView browser;
    private boolean doubleBackToExitPressedOnce;
    private SharedPreferences mPreferences;
    private SharedPreferences mDefaultPrefferences;

    public static Intent createIntent(Context context, String content,
            String payload) {
        Intent intent = new Intent(context, SalvatiRosia.class);
        intent.putExtra(EXTRA_CONTENT, content);
        intent.putExtra(EXTRA_PAYLOAD, payload);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salvati_rosia);
        initUI();
        initialiseParse();

        mPreferences = getSharedPreferences(MainConstants.PREFS_NAME,
                MODE_PRIVATE);
        mDefaultPrefferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        boolean isFirstRun = mPreferences.getBoolean(MainConstants.KEY_FIRST_RUN,
                true);

        if (isFirstRun) {
            Log.d(TAG, " isFirstRun !");
            startActivity(new Intent(SalvatiRosia.this, Settings.class));
            // register to defaults???
            // TODO register to user options
//            register(MainConstants.TEST_CHANNEL);
            register(MainConstants.BROADCAST_CHANNEL);
            register(MainConstants.DEFAULT_LANGUAGE);
            register(MainConstants.DEFAULT_CITY);
            load();
        } else {
        	Log.d(TAG, "Not first run");
        	Bundle bundle = getIntent().getExtras();
            if (bundle != null
                    && bundle.containsKey(EXTRA_PAYLOAD)) {
            	Log.d(TAG, "saved instance not null");
                String payload = bundle.getString(EXTRA_PAYLOAD);
                Log.d(TAG, "payload "+payload);
                if (!TextUtils.isEmpty(payload)) {
                    browser.loadUrl(payload);
                } else {
                    load();
                }
            } else {
                load();
            }
        }

        // mWebView = (PullToRefreshWebView) findViewById(R.id.webView1);
        // browser = mWebView.getRefreshableView();

        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean(MainConstants.KEY_FIRST_RUN, false);
        edit.commit();
    }

    private void initUI() {
        browser = (WebView) findViewById(R.id.webView1);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setWebViewClient(new CustomWebViewClient());
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
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
            Toast.makeText(this, R.string.exit_press_back_twice_message,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void unregister() {
        Set<String> subscriptions = PushService.getSubscriptions(this);

        for (String subscr : subscriptions) {
            PushService.unsubscribe(this, subscr);
        }

    }

    private void unregister(String channel) {
        Log.d(TAG, " unregister "+channel);
        PushService.unsubscribe(this, channel);
    }

    private void register(String channel) {
        Log.d(TAG, " register "+channel);
//		if (ParseInstallation.getCurrentInstallation()==null)
        PushService.subscribe(this, channel, SalvatiRosia.class);
    }

    private void initialiseParse() {
        Parse.initialize(this, MainConstants.APPLICATION_ID,
                MainConstants.CLIENT_KEY);
        PushService.setDefaultPushCallback(this, SalvatiRosia.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    private void load() {
        String lang = mDefaultPrefferences.getString(
                MainConstants.KEY_SAVED_LANGUAGE, MainConstants.DEFAULT_LANGUAGE);
        if (MainConstants.DEFAULT_LANGUAGE.equalsIgnoreCase(lang)) {
            browser.loadUrl(MainConstants.URL_ROM);
        } else if (MainConstants.INTERNATIONAL_LANGUAGE.equalsIgnoreCase(lang)) {
            browser.loadUrl(MainConstants.URL_INTERNATIONAL);
        } else {
            browser.loadUrl(MainConstants.URL_DIASPORA);
        }
    }

    @Override
    protected void onResume() {
        String url = "";
        String contentChannel = "";
        String cityChannel = "";

        if (mDefaultPrefferences == null) {
            mDefaultPrefferences = PreferenceManager
                    .getDefaultSharedPreferences(this);
        }

        if (browser != null)
            url = browser.getUrl();
        if (url != null && url.lastIndexOf("m.rosiamontana.net") != -1) {
            // load();
        }

        String savedCity = mDefaultPrefferences.getString(MainConstants.KEY_SAVED_CITY,
                MainConstants.DEFAULT_CITY);
        String savedLanguage = mDefaultPrefferences.getString(
                MainConstants.KEY_SAVED_LANGUAGE, MainConstants.DEFAULT_LANGUAGE);

        boolean broadcastUpToDate = false;
        Set<String> subscriptions = PushService.getSubscriptions(this);
        Log.d(TAG, " onResume Subscriptions : "+subscriptions);

        // go trough subscriptions
        for (String subscr : subscriptions) {
            // if subscription is one of the supported languages
            if (MainConstants.DEFAULT_LANGUAGE.equalsIgnoreCase(subscr)
                    || MainConstants.INTERNATIONAL_LANGUAGE
                            .equalsIgnoreCase(subscr)
                    || MainConstants.DIASPORA_LANGUAGE.equalsIgnoreCase(subscr)) {
                contentChannel = subscr;
            } else if (MainConstants.BROADCAST_CHANNEL.equalsIgnoreCase(subscr)) {
                // if subscription is of type Brodacast
                broadcastUpToDate = true;
            } else {
                // if nothing else then the subscription represents a city
                cityChannel = subscr;
            }
        }

        if (!contentChannel.equalsIgnoreCase(savedLanguage)) {
            unregister(contentChannel);
            register(savedLanguage);
        }

        if (!broadcastUpToDate) {
            register(MainConstants.BROADCAST_CHANNEL);
        }

		if (MainConstants.DEFAULT_LANGUAGE.equalsIgnoreCase(savedLanguage)) {
			if (!savedCity.equalsIgnoreCase(cityChannel)) {
				unregister(cityChannel);
				register(savedCity);
			}
		} else if (!cityChannel.equalsIgnoreCase("")) {
			unregister(cityChannel);
		}

        this.doubleBackToExitPressedOnce = false;
        super.onResume();
    }

    // ===================================================

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("fb://")) {
                String facebookScheme = url;
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(facebookScheme));
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
        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl) {
            view.loadData("<html><body></body></html>", "text/html", "utf-8");
            UIUtilities.createCommunicationErrorDialog(SalvatiRosia.this)
                    .show();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // createLoadingSpinner();

            progressBar.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);

        }

    }

    public void onSettingsItemClick(MenuItem item) {
        startActivity(new Intent(SalvatiRosia.this, Settings.class));
    }

    public void onHomeItemClick(MenuItem item) {
        load();
    }

    public void onDonateItemClick(MenuItem item) {
        browser.loadUrl("http://m.rosiamontana.net/doneaza");
    }

    public void onShareItemClick(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, browser.getUrl());
        intent.putExtra(android.content.Intent.EXTRA_STREAM,
                R.drawable.logo_frunza);
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
        // startActivity(new Intent(SalvatiRosia.this, TwitterFeed.class));
    }

    public void onFollowItemClick(MenuItem item) {
        String facebookScheme = "fb://profile/145342615479115";
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(facebookScheme));
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
