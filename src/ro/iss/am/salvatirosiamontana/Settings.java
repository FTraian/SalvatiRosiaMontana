package ro.iss.am.salvatirosiamontana;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Button;

public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	@SuppressWarnings("deprecation")
	ListPreference languageList;
	ListPreference cityList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		languageList = (ListPreference) findPreference("language");
		cityList = (ListPreference) findPreference("city");
		
		languageList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				final String val = newValue.toString();
				int index = languageList.findIndexOfValue(val);
				languageList.setSummary(val);
				if (index == 0) {
					cityList.setEnabled(true);
				} else {
					cityList.setEnabled(false);
				}
				return true;
			}
		});

		cityList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				final String val = newValue.toString();
				cityList.setSummary(val);

				return true;
			}
		});

		ListPreference listPreference = (ListPreference) findPreference("language");
		if (listPreference.getValue() == null) {
			// to ensure we don't get a null value
			// set first value by default
			listPreference.setValueIndex(0);
		} else {
			if ("International".equalsIgnoreCase(listPreference.getValue())) {
				cityList.setEnabled(false);
			}
		}
		listPreference.setSummary(listPreference.getEntry().toString());
		listPreference = (ListPreference) findPreference("city");
		if (listPreference.getValue() == null) {
			// to ensure we don't get a null value
			// set first value by default
			listPreference.setValueIndex(0);
		}
		listPreference.setSummary(listPreference.getEntry().toString());

		
		Button saveButton = new Button(this);
		saveButton.setText(R.string.save);

		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		getListView().addFooterView(saveButton);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference pref = findPreference(key);

		if (pref instanceof ListPreference) {
			ListPreference listPref = (ListPreference) pref;
			pref.setSummary(listPref.getEntry());
		}
	}

}
