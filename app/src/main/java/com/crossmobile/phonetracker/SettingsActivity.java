package com.crossmobile.phonetracker;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {

    public static final String KEY_GPRS_IP_ADDRESS = "pref_gprs_address";
    public static final String KEY_WIFI_IP_ADDRESS = "pref_wifi_address";
    public static final String KEY_UPDATE_INTERVAL = "pref_update_interval";
    public static final String KEY_MANUAL_MODE = "pref_manual_mode";
    public static final String KEY_DEBUG = "pref_debug_msg";
    public static final String KEY_DYNAMIC_LOCATIONS = "pref_dynamic_locations";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();
    }

}
