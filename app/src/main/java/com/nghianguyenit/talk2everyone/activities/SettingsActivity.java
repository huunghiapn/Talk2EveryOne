package com.nghianguyenit.talk2everyone.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.nghianguyenit.talk2everyone.R;

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        getFragmentManager().beginTransaction().
                replace(R.id.container,
                        new MyPreferenceFragment()).commit();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        private CheckBoxPreference adsPref;

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);


            final SharedPreferences.Editor my_prefrence = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();

            adsPref = (CheckBoxPreference) findPreference("ads");
            adsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {

                    CheckBoxPreference chkboxPreference = (CheckBoxPreference) preference;

                    if (chkboxPreference.isChecked()) {
                        my_prefrence.putBoolean("ads", true);
                    } else {
                        my_prefrence.putBoolean("ads", false);
                    }
                    my_prefrence.apply();

                    return true;
                }
            });

            Preference liscence = findPreference("license");
            liscence.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), License.class));
                    return true;
                }
            });


            Preference share = findPreference("Share");
            share.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    String app_share_details = getResources().getString(R.string.app_share_link);
                    if (!(app_share_details.equals(null))) {
                        Intent myIntent = new Intent(Intent.ACTION_SEND);
                        myIntent.setType("text/plain");
                        myIntent.putExtra(Intent.EXTRA_TEXT, "Check out this awesome translator app.\n" + "*Talk2E1*\n" + app_share_details);
                        startActivity(Intent.createChooser(myIntent, "Share with"));
                    }
                    return true;
                }
            });


            Preference about = findPreference("About");
            about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), AboutActivity.class));
                    return true;
                }
            });

        }

        private void recreateActivity() {
            getActivity().recreate();
        }

    }

}