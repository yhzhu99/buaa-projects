package com.topelec.smartagriculture;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import it.moondroid.coverflowdemo.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SmartAgricultureSettings extends PreferenceActivity implements Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */

    //定制变量
    static final String AutoSwitchKey = "auto_switch";
    static final String AutoListKey = "setting_list";
    static final String tempSettingKey = "temp_settings";
    static final String humSettingKey = "hum_settings";


    CheckBoxPreference AutoSwitchPref;
    ListPreference  AutoListPref;
    EditTextPreference tempSettingsPref;
    EditTextPreference humSettingsPref;


    boolean autoControl;
    String whichControl;
    String Temperature;

    //    String Humidity;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 从资源文件中添Preferences ，选择的值将会自动保存到SharePreferences
        addPreferencesFromResource(R.xml.settings_smartagriculture);

        AutoSwitchPref = (CheckBoxPreference) findPreference(AutoSwitchKey);
        AutoSwitchPref.setOnPreferenceChangeListener(this);

        AutoListPref = (ListPreference) findPreference(AutoListKey);
        AutoListPref.setOnPreferenceChangeListener(this);



        tempSettingsPref = (EditTextPreference) findPreference(tempSettingKey);
        tempSettingsPref.setSummary(getString(R.string.smartagriculture_prefix_temp)+tempSettingsPref.getText()+getString(R.string.smartagriculture_sheshidu));
        tempSettingsPref.setOnPreferenceChangeListener(this);

        humSettingsPref = (EditTextPreference) findPreference(humSettingKey);
        humSettingsPref.setSummary(getString(R.string.smartagriculture_prefix_hum)+humSettingsPref.getText()+getString(R.string.smartagriculture_percent));
        humSettingsPref.setOnPreferenceChangeListener(this);

        onToControl(AutoListPref.getValue());

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        //判断是哪个Preference被点击了
        if (preference.getKey().equals(AutoSwitchKey)) {

            if ((Boolean) newValue) {
                Toast.makeText(this, getString(R.string.smartagriculture_temp_auto_on), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.smartagriculture_temp_auto_off), Toast.LENGTH_LONG).show();
            }

            return true;
        } else if (preference.getKey().equals(AutoListKey)) {
            Toast.makeText(this,AutoListPref.getEntry() , Toast.LENGTH_LONG).show();

            onToControl((String)newValue);
            return true;
        } else if (preference.getKey().equals(tempSettingKey)) {
            Toast.makeText(this, getString(R.string.smartagriculture_prefix_temp) + (String) newValue + getString(R.string.smartagriculture_sheshidu), Toast.LENGTH_LONG).show();
            tempSettingsPref.setSummary(getString(R.string.smartagriculture_prefix_temp) + (String) newValue + getString(R.string.smartagriculture_sheshidu));
            return true;
        } else if (preference.getKey().equals(humSettingKey)) {
            Toast.makeText(this,getString(R.string.smartagriculture_prefix_hum)+ (String)newValue + getString(R.string.smartagriculture_percent),Toast.LENGTH_LONG).show();
            humSettingsPref.setSummary(getString(R.string.smartagriculture_prefix_hum)+(String)newValue+getString(R.string.smartagriculture_percent));
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        //判断是哪个Preference被点击了
        if (preference.getKey().equals(AutoSwitchKey)) {

            return true;
        }else if (preference.getKey().equals(tempSettingKey)) {
            return true;
        }else {
            return false;
        }

    }

    /**
     * 转换温湿度管理互斥作用,并显示结果
     * @param which
     */
    private void onToControl(String which) {
        whichControl = which;
        if (whichControl.equals("1")) {
            AutoListPref.setSummary(getString(R.string.smartagriculture_temp_control));
            tempSettingsPref.setEnabled(true);
            humSettingsPref.setEnabled(false);
        } else if (whichControl.equals("2")) {
            AutoListPref.setSummary(getString(R.string.smartagriculture_hum_control));
            tempSettingsPref.setEnabled(false);
            humSettingsPref.setEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onStop() {
        super.onStop();
    }
}
