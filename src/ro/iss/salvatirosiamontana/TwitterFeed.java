package ro.iss.salvatirosiamontana;

import ro.iss.salvatirosiamontana.common.UIUtilities;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class TwitterFeed extends Activity {
    //PullToRefreshWebView mWebView;
    WebView browser;
    private boolean doubleBackToExitPressedOnce;
    ProgressBar progressBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salvati_rosia);// webView1
        //mWebView = (PullToRefreshWebView) findViewById(R.id.webView1);

        browser =( WebView)findViewById(R.id.webView1);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setWebViewClient(new CustomWebViewClient());
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        load();
    }

    private void load() {
        browser.loadUrl("https://www.twitter.com/LiveProtesteRM");
    }

    @Override
    protected void onResume() {
        this.doubleBackToExitPressedOnce = false;
        super.onResume();
    }

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
            UIUtilities.createCommunicationErrorDialog(TwitterFeed.this)
                    .show();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // createLoadingSpinner();

            progressBar.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);

        }

    }


    public void onHomeItemClick(MenuItem item) {
        startActivity(new Intent(TwitterFeed.this, SalvatiRosia.class));
    }

    public void onShareItemClick(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, browser.getUrl());
        intent.putExtra(android.content.Intent.EXTRA_STREAM, R.drawable.logo_frunza);
        startActivity(Intent.createChooser(intent, "Uniti salvam"));
    }
    @Override
    public void onBackPressed() {
        if (browser.canGoBack()){
            browser.goBack();
        }else{
             if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, R.string.exit_press_back_twice_message, Toast.LENGTH_SHORT).show();
        }
        //
    }


    @Override
      public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.home:
            onHomeItemClick(item);
          break;
        case R.id.share:
            onShareItemClick(item);
          break;

        }

        return true;
      }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.twitter_menu, menu);
        return true;
    }
}
