package com.topelec.smarthome;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;


import it.moondroid.coverflowdemo.R;
/**
 * Created by Amber on 2015/3/25.
 */
public class Settings extends PreferenceActivity implements Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {

    //定制变量
    static final String tempAutoSwitchKey = "auto_temp_switch";
    static final String tempSettingKey = "temp_settings";
//    static final String humSettingKey = "hum_settings";
    static final String brightAutoSwitchKey = "auto_bright_switch";

    CheckBoxPreference tempAutoSwitchPref;
    EditTextPreference tempSettingsPref;
//    EditTextPreference humSettingsPref;
    CheckBoxPreference brightAutoSwitchPref;

    boolean autoTemp;
    String Temperature;

//    String Humidity;
    boolean autoBright;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 从资源文件中添Preferences ，选择的值将会自动保存到SharePreferences
        addPreferencesFromResource(R.xml.settings_smarthome);

        tempAutoSwitchPref = (CheckBoxPreference) findPreference(tempAutoSwitchKey);

        tempSettingsPref = (EditTextPreference) findPreference(tempSettingKey);
        tempSettingsPref.setSummary(getString(R.string.smarthome_prefix_temp)+tempSettingsPref.getText()+getString(R.string.smarthome_sheshidu));

//        humSettingsPref = (EditTextPreference) findPreference(humSettingKey);
//        humSettingsPref.setSummary(getString(R.string.prefix_hum)+humSettingsPref.getText()+getString(R.string.percent));

        brightAutoSwitchPref = (CheckBoxPreference) findPreference(brightAutoSwitchKey);



        tempAutoSwitchPref.setOnPreferenceClickListener(this);
        tempAutoSwitchPref.setOnPreferenceChangeListener(this);

        tempSettingsPref.setOnPreferenceClickListener(this);
        tempSettingsPref.setOnPreferenceChangeListener(this);


//
//        humSettingsPref.setOnPreferenceClickListener(this);
//        humSettingsPref.setOnPreferenceChangeListener(this);

        brightAutoSwitchPref.setOnPreferenceClickListener(this);
        brightAutoSwitchPref.setOnPreferenceChangeListener(this);

        autoTemp = tempAutoSwitchPref.isChecked();

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        //判断是哪个Preference被点击了
        if (preference.getKey().equals(tempAutoSwitchKey)) {

            if ((Boolean)newValue) {
                Toast.makeText(this,getString(R.string.smarthome_temp_auto_on) , Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this,getString(R.string.smarthome_temp_auto_off) , Toast.LENGTH_LONG).show();
            }

            return true;
        }else if (preference.getKey().equals(tempSettingKey)) {
            Toast.makeText(this, getString(R.string.smarthome_prefix_temp) +  (String)newValue + getString(R.string.smarthome_sheshidu), Toast.LENGTH_LONG).show();
            tempSettingsPref.setSummary(getString(R.string.smarthome_prefix_temp)+(String)newValue+getString(R.string.smarthome_sheshidu));
            return true;
        }else if (preference.getKey().equals(brightAutoSwitchKey)) {

            if ((Boolean)newValue) {
                Toast.makeText(this,getString(R.string.smarthome_bright_auto_on) , Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this,getString(R.string.smarthome_bright_auto_off) , Toast.LENGTH_LONG).show();
            }
            return true;
        }else {
            return false;
        }

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        //判断是哪个Preference被点击了
        if (preference.getKey().equals(tempAutoSwitchKey)) {

            return true;
        }else if (preference.getKey().equals(tempSettingKey)) {
            return true;
        }else if (preference.getKey().equals(brightAutoSwitchKey)) {
            return true;
        }else {
            return false;
        }

    }

    private void resultDate(){
//        Intent intent = new Intent();
//        intent.putExtra(tempAutoSwitchKey,tempAutoSwitchPref.g);
    }
    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onStop() {
        super.onStop();
        //finish();
    }
}
