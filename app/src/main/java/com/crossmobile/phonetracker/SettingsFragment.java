package com.crossmobile.phonetracker;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by ervin on 9/15/14.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}