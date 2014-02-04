package ro.iss.salvatirosiamontana;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.client.ClientProtocolException;

import ro.iss.salvatirosiamontana.networking.NetworkWorkerFactory;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class PostMessageActivity extends Activity implements OnClickListener {

	private Spinner mLanguageSpinner;
	private Spinner mCitySpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_data);

		mLanguageSpinner = (Spinner) findViewById(R.id.spinner1);
		String[] langArray = getResources().getStringArray(R.array.languages);
		ArrayList<String> languages = new ArrayList<String>(
				Arrays.asList(langArray));
		ArrayAdapter<String> lang_adp = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, languages);
		lang_adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		mLanguageSpinner.setAdapter(lang_adp);

		mCitySpinner = (Spinner) findViewById(R.id.spinner2);
		String[] citiesArray = getResources().getStringArray(R.array.cities);
		ArrayList<String> cities = new ArrayList<String>(
				Arrays.asList(citiesArray));
		ArrayAdapter<String> city_adp = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, cities);
		city_adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		mCitySpinner.setAdapter(city_adp);

		findViewById(R.id.send).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		final EditText editableTextField = (EditText) findViewById(R.id.editText1);
		final String textToSend = editableTextField.getText().toString();

		if (!TextUtils.isEmpty(textToSend)) {
			Toast.makeText(this, "Posting ... /n" + textToSend,
					Toast.LENGTH_LONG).show();

			(new AsyncTask<Void, Void, Exception>() {

				@Override
				protected Exception doInBackground(Void... params) {
					try {
						NetworkWorkerFactory.getNetworkWorker(
								NetworkWorkerFactory.WorkerType.HTTP).sendData(
								(String) mCitySpinner.getSelectedItem(),
								(String) mLanguageSpinner.getSelectedItem(),
								textToSend);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
						return e;
					} catch (IOException e) {
						e.printStackTrace();
						return e;
					}

					return null;
				}

				@Override
				protected void onPostExecute(Exception result) {
					if (result != null) {
						Toast.makeText(getApplicationContext(),
								result.getMessage(), Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getApplicationContext(), "Success",
								Toast.LENGTH_LONG).show();
						editableTextField.setText("");
					}
				}

			}).execute((Void) null);

		}

	}

}
