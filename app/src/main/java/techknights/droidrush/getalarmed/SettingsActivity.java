package techknights.droidrush.getalarmed;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;


public  class SettingsActivity extends AppCompatActivity {

    public static CheckBoxPreference Exitprefs,Snoozeprefs;
    public static SwitchPreference Vibrateprefs,SilentModeprefs,Shakeprefs;
    public static ListPreference SnoozeDurationprefs,MaxSnoozeprefs;
    public static SeekBarPreference Volumeprefs;
    static SharedPreferences settingsPrefs;
    static SharedPreferences.Editor settingsEditor;
    public static int v =50;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }


    //settings is managed by using PreferenceFragment
    public static  class SettingsFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_activity);


            // show the current value in the settings screen
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                pickPreferenceObject(getPreferenceScreen().getPreference(i));
            }
        }

        private void pickPreferenceObject(Preference p) {
            if (p instanceof PreferenceCategory) {
                PreferenceCategory cat = (PreferenceCategory) p;

                for (int i = 0; i < cat.getPreferenceCount(); i++) {
                    pickPreferenceObject(cat.getPreference(i));

                }
            }
            else
            {
                if (p instanceof CheckBoxPreference) {
                    Exitprefs = (CheckBoxPreference) findPreference("exit_preference");
                    Exitprefs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            boolean checked = Boolean.valueOf(newValue.toString());
                            settingsEditor = settingsPrefs.edit();
                            settingsEditor.putBoolean("exit",checked);
                            settingsEditor.apply();
                            return true;
                        }
                    });

                    Snoozeprefs = (CheckBoxPreference) findPreference("snooze_preference");
                    Snoozeprefs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            boolean Schecked = Boolean.valueOf(newValue.toString());
                            settingsEditor = settingsPrefs.edit();
                            settingsEditor.putBoolean("snooze",Schecked);
                            settingsEditor.apply();
                            return true;
                        }
                    });
                }
                else if (p instanceof SwitchPreference) {
                    Vibrateprefs = (SwitchPreference) findPreference("vibrate_preference");
                    Vibrateprefs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            boolean enabled = Boolean.valueOf(newValue.toString());
                            settingsEditor = settingsPrefs.edit();
                            settingsEditor.putBoolean("vibration",enabled);
                            settingsEditor.apply();
                            return true;
                        }
                    });

                    SilentModeprefs = (SwitchPreference) findPreference("silent_mode_preference");
                    SilentModeprefs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            boolean enabled = Boolean.valueOf(newValue.toString());
                            settingsEditor = settingsPrefs.edit();
                            settingsEditor.putBoolean("silentMode",enabled);
                            settingsEditor.apply();
                            return true;
                        }
                    });

                    Shakeprefs = (SwitchPreference) findPreference("shake_preference");
                    Shakeprefs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            boolean enabled = Boolean.valueOf(newValue.toString());
                            settingsEditor = settingsPrefs.edit();
                            settingsEditor.putBoolean("shake",enabled);
                            settingsEditor.apply();
                            return true;
                        }
                    });
                }

                else if(p instanceof ListPreference){
                    SnoozeDurationprefs = (ListPreference) findPreference("snooze_duration_preference");
                    SnoozeDurationprefs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object o) {
                            settingsEditor = settingsPrefs.edit();
                            settingsEditor.putInt("snoozeDuration",Integer.parseInt(o.toString()));
                            settingsEditor.apply();
                            return true;
                        }
                    });

                    MaxSnoozeprefs = (ListPreference) findPreference("snooze_times_preference");
                    MaxSnoozeprefs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object o) {
                            settingsEditor = settingsPrefs.edit();
                            settingsEditor.putInt("snoozeTimes",Integer.parseInt(o.toString()));
                            settingsEditor.apply();
                            return true;
                        }
                    });

                }
                else if(p instanceof SeekBarPreference){
                    Volumeprefs =(SeekBarPreference) findPreference("volume_preference") ;
                    Volumeprefs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object o) {
                            v =Integer.parseInt(o.toString());
                            settingsEditor = settingsPrefs.edit();
                            settingsEditor.putInt("volume",v);
                            settingsEditor.apply();
                            return true;
                        }
                    });
                }


            }

        }
    }

}
