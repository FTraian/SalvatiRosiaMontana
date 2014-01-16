package ro.iss.salvatirosiamontana;

import ro.iss.salvatirosiamontana.util.MainConstants;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

@SuppressWarnings("deprecation")
public class Settings extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {
    ListPreference languageList;
    ListPreference cityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        languageList = (ListPreference) findPreference(MainConstants.KEY_SAVED_LANGUAGE);
        cityList = (ListPreference) findPreference(MainConstants.KEY_SAVED_CITY);

        languageList
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    public boolean onPreferenceChange(Preference preference,
                            Object newValue) {
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
            public boolean onPreferenceChange(Preference preference,
                    Object newValue) {
                final String val = newValue.toString();
                cityList.setSummary(val);

                return true;
            }
        });

        ListPreference listPreference = (ListPreference) findPreference(MainConstants.KEY_SAVED_LANGUAGE);

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
        listPreference = (ListPreference) findPreference(MainConstants.KEY_SAVED_CITY);
        if (listPreference.getValue() == null) {
            // to ensure we don't get a null value
            // set first value by default
            listPreference.setValueIndex(0);
        }

        listPreference.setSummary(listPreference.getEntry().toString());
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        Preference pref = findPreference(key);

        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }
    }

}
