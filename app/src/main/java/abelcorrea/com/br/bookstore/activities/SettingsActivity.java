package abelcorrea.com.br.bookstore.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;

import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import abelcorrea.com.br.bookstore.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }


    public static class InventoryPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);
            /**
             *  finds the quantifier preference on create
             */
            Preference quantifier = findPreference(getString(R.string.settings_quantifier_key));
            bindPreference(quantifier);

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            /**
             * the code in this method takes care of updating the displayed preference
             * after it has been changed
             */
            preference.setSummary(newValue.toString());
            return true;
        }

        /**
         * binds the preference to a valid value to show in the UI
         * @param preference
         */
        private void bindPreference(Preference preference){

            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(),getString(R.string.settings_quantifier_default));
            onPreferenceChange(preference,preferenceString);
        }
    }

}
