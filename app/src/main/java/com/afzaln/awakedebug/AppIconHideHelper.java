package com.afzaln.awakedebug;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by Dan Mercer (Github: drmercer) on 6/11/16.
 */
public class AppIconHideHelper implements CompoundButton.OnCheckedChangeListener {
    private final Activity mActivity;
    private CheckBox mCheckBox;
    private boolean mDialogHasBeenSeen = false;

    public AppIconHideHelper(Activity activity) {
        mActivity = activity;
    }

    public void setCheckBox(CheckBox checkBox) {
        mCheckBox = checkBox;

        // Setup checkBox state
        boolean currentSetting = PreferenceManager.getDefaultSharedPreferences(mActivity)
                .getBoolean("AppIconHidden", false);
        checkBox.setChecked(currentSetting);
    }

    public void setCheckBoxEnabled(boolean enabled) {
        mCheckBox.setEnabled(enabled);
        if (!enabled) {
            mCheckBox.setChecked(false);
        }
    }

    public void startListeningForChanges() {
        mCheckBox.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        // Show warning dialog on first time
        if (!mDialogHasBeenSeen && isChecked) {
            mDialogHasBeenSeen = true;

            AlertDialog.Builder db = new AlertDialog.Builder(mActivity);
            db.setMessage(R.string.icon_warning);

            // "Yes" button
            db.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    changeIconVisibility(true); // Hide icon
                }
            });

            // "No" button
            db.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCheckBox.setChecked(false); // Un-check the CheckBox
                    mDialogHasBeenSeen = false; // Clear flag.
                }
            });

            // Show the dialog
            db.show();

        } else {
            // The warning has already been seen, or they are disabling the setting (so no warning
            // is needed).
            changeIconVisibility(isChecked);
        }
    }

    private void changeIconVisibility(boolean hidden) {
        PackageManager pm = mActivity.getPackageManager();
        final int flags = PackageManager.DONT_KILL_APP;
        final String packageName = BuildConfig.APPLICATION_ID;

        // The component name of the activitiy-alias component
        final ComponentName alias = new ComponentName(packageName,
                packageName + ".MainActivityAlias");

        if (hidden) { // Hide launcher icon
            final int stateDisabled = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            pm.setComponentEnabledSetting(alias, stateDisabled, flags);

        } else { // Show launcher icon
            final int stateEnabled = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
            pm.setComponentEnabledSetting(alias, stateEnabled, flags);
        }

        // Save user setting
        PreferenceManager.getDefaultSharedPreferences(mActivity)
                .edit()
                .putBoolean("AppIconHidden", hidden)
                .apply();
    }
}
