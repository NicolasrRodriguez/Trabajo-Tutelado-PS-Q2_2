package com.example.triptracks;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Locale;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String languageValue = prefs.getString("language_preference", null);
        if (languageValue == null) {
            languageValue = Locale.getDefault().getLanguage();
            saveLanguagePreference(this, languageValue);
        }
    }
    private void saveLanguagePreference(Context context, String lang) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language_preference", lang);
        editor.apply();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            ListPreference languagePreference = findPreference("language_preference");
            if (languagePreference != null) {
                languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    String languageValue = newValue.toString();
                    updateLocale(languageValue);
                    getActivity().recreate();
                    return true;
                });
            }
        }

        private void updateLocale(String languageTag) {
            Locale locale = new Locale(languageTag);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());
            saveLanguagePreference(getActivity(), languageTag);
        }

        private void saveLanguagePreference(Context context, String lang) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("language_preference", lang);
            editor.apply();
        }
    }
}

