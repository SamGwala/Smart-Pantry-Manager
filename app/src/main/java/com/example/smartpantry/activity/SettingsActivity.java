package com.example.smartpantry.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.R;

/**
  Simple settings screen.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    private static final String KEY_METRIC_UNITS = "metric_units_enabled";

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        Switch switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);
        Switch switchUnitPreference = findViewById(R.id.switchUnitPreference);

        boolean expiryAlertsOn = preferences.getBoolean(KEY_EXPIRY_ALERTS, true);
        boolean metricOn = preferences.getBoolean(KEY_METRIC_UNITS, true);

        switchExpiryAlerts.setChecked(expiryAlertsOn);
        switchUnitPreference.setChecked(metricOn);

        switchExpiryAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_EXPIRY_ALERTS, isChecked);
            editor.apply();
        });

        switchUnitPreference.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_METRIC_UNITS, isChecked);
            editor.apply();
        });
    }
}
