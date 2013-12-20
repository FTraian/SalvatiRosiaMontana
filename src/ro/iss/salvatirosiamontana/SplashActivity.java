/*****************************************************************
 * Licensed Materials - Property of IBM
 * Copyright IBM Corp. 2010  All Rights Reserved
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *****************************************************************/

package ro.iss.salvatirosiamontana;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

public class SplashActivity extends Activity {

    public static final int SPLASH_DISPLAY_TIME = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);


        Thread splashThread = new Thread() {
            int wait = 0;

            @Override
            public void run() {
                try {
                    super.run();
                    while (wait < SPLASH_DISPLAY_TIME) {
                        sleep(100);
                        wait += 100;
                    }
                }
                catch(InterruptedException iex) {
                }
                catch (Exception e) {
                }
                finally {
                    startActivity(new Intent(SplashActivity.this, SalvatiRosia.class));
                    finish();
                }
            }
        };

        splashThread.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.splash);
    }
}
