package com.topelec.foodtraceability;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import it.moondroid.coverflowdemo.R;

/**
 * Created by Amber on 2015/4/14.
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {
    private static final String TAG = ".PreferenceActivity";
   // SwitchPreference author_switch;
    EditTextPreference id;
    EditTextPreference name;
    EditTextPreference time;
    EditTextPreference place;
    EditTextPreference material;
    Preference author;

    private String CurrentID = new String();

    private boolean isAuthor = false;

//    /**数据库相关**/
//    Context mContext;
//    DatabaseHelper mDatabaseHelper;
//    SQLiteDatabase mDatabase;
//
//    private final static String TABLE_NAME = "HFCard";
//    private final static String ID = "_id";
//    private final static String CARD_ID = "card_id";
//    private final static String NAME = "name";
//

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 从资源文件中添Preferences ，选择的值将会自动保存到SharePreferences
        addPreferencesFromResource(R.xml.settings_foodtraceability);
        CurrentID = this.getIntent().getStringExtra("CurrentId");


        id = (EditTextPreference)findPreference("id");

        if (id.getText() != null)
            id.setSummary(getString(R.string.foodtraceability_current_id)+id.getText());

//        id.setOnPreferenceChangeListener(this);
//        id.setOnPreferenceClickListener(this);
        name = (EditTextPreference)findPreference("name");
//        name.setSummary(getString(R.string.name) + name.getText());
        name.setOnPreferenceChangeListener(this);

        time = (EditTextPreference)findPreference("time");
//        time.setSummary(getString(R.string.time) + time.getText());
        time.setOnPreferenceChangeListener(this);

        place = (EditTextPreference)findPreference("place");
//        place.setSummary(getString(R.string.place) + place.getText());
        place.setOnPreferenceChangeListener(this);

        material = (EditTextPreference)findPreference("material");
//        material.setSummary(getString(R.string.material) + material.getText());
        material.setOnPreferenceChangeListener(this);

        author = (Preference)findPreference("author");
        author.setOnPreferenceClickListener(this);

        if (CurrentID.length() != 0) {
            if (CurrentID.equals(id.getText())) {
                isAuthor = true;
                author.setTitle(getString(R.string.foodtraceability_if_cancel_author));
                author.setSummary(getString(R.string.foodtraceability_cancel_author));
                name.setSummary(getString(R.string.foodtraceability_name)+name.getText());
                time.setSummary(getString(R.string.foodtraceability_time)+time.getText());
                place.setSummary(getString(R.string.foodtraceability_place)+place.getText());
                material.setSummary(getString(R.string.foodtraceability_material)+material.getText());

            } else {
                isAuthor = false;
                author.setTitle(getString(R.string.foodtraceability_if_author_current_id));
                author.setSummary(getString(R.string.foodtraceability_author_id));

            }

        }else {
            name.setEnabled(false);
            time.setEnabled(false);
            place.setEnabled(false);
            material.setEnabled(false);
            author.setEnabled(false);
        }
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
//        if (preference.getKey().equals("id")) {
//            if (newValue.equals("")) {
//                id.setSummary(getString(R.string.no_author_id));
//                name.setSummary(getString(R.string.name));
//                time.setSummary(getString(R.string.time));
//                place.setSummary(getString(R.string.place));
//                material.setSummary(getString(R.string.material));
//                author.setEnabled(false);
//            } else {
//                id.setSummary(getString(R.string.current_id)+newValue);
//                name.setEnabled(true);
//                time.setEnabled(true);
//                place.setEnabled(true);
//                material.setEnabled(true);
//                author.setEnabled(true);
//                author.setEnabled(true);
//            }
//            return true;
//        } else
        if (preference.getKey().equals("name")) {
            name.setSummary(getString(R.string.foodtraceability_name)+newValue);
            return true;
        }else if (preference.getKey().equals("time")) {
            time.setSummary(getString(R.string.foodtraceability_time)+newValue);
            return true;
        } else if (preference.getKey().equals("place")) {
            place.setSummary(getString(R.string.foodtraceability_place)+newValue);
            return true;
        } else if (preference.getKey().equals("material")) {
            material.setSummary(getString(R.string.foodtraceability_material)+newValue);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
//        if (preference.getKey().equals("id")) {
//            if (CurrentID.length() != 0) {
//                id.setDialogTitle(getResources().getString(R.string.if_author_current_id));
//                //id.setText(CurrentID);
//                id.setText(CurrentID);
//                Log.d(TAG,"CurrentID = " +CurrentID);
//            } else {
//                id.setDialogTitle(getResources().getString(R.string.enter_id));
//                id.setText("");
//            }
//            Log.d(TAG,"CurrentID = " +CurrentID);
//            return true;
//        } else
        if (preference.getKey().equals("author")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(author.getTitle());
            builder.setPositiveButton(getString(R.string.foodtraceability_commit), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //这里添加点击确定后的逻辑
                    //showDialog("commit");
//                    id.setText("");
                    if (isAuthor) {  //
                        id.setText("");
                        isAuthor = false;
                        id.setSummary(getString(R.string.foodtraceability_no_author_id));
                        author.setTitle(getString(R.string.foodtraceability_if_author_current_id));
                        author.setSummary(getString(R.string.foodtraceability_author_id));

                    }else {
                        id.setText(CurrentID);
                        isAuthor = true;
                        id.setSummary(getString(R.string.foodtraceability_current_id)+id.getText());
                        author.setTitle(getString(R.string.foodtraceability_if_cancel_author));
                        author.setSummary(getString(R.string.foodtraceability_cancel_author));
                    }
//                    id.setSummary(getString(R.string.no_author_id));
                    name.setSummary(getString(R.string.foodtraceability_name));
                    time.setSummary(getString(R.string.foodtraceability_time));
                    place.setSummary(getString(R.string.foodtraceability_place));
                    material.setSummary(getString(R.string.foodtraceability_material));
//                    author.setEnabled(false);
                }
            });
            builder.setNegativeButton(getString(R.string.foodtraceability_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //这里添加点击确定后的逻辑
                   //showDialog("cannel");
                }
            });
            builder.create().show();
            return true;
        } else {
                return false;
        }

    }
}
