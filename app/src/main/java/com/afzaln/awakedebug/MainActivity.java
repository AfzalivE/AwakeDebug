package com.afzaln.awakedebug;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final int WRITE_SETTINGS_REQUEST = 1;
    private SwitchCompat mActionBarSwitch;
    private AppIconHideHelper mIconHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // set up the "Hide app icon" checkbox
        CheckBox checkBox = (CheckBox) findViewById(R.id.app_icon_checkbox);
        mIconHelper = new AppIconHideHelper(this);
        mIconHelper.setCheckBox(checkBox);

        mActionBarSwitch = (SwitchCompat) findViewById(R.id.toggle);

        boolean state = PowerConnectionReceiver.getPrefEnabled(getApplicationContext());
        mActionBarSwitch.setChecked(state);
        mIconHelper.setCheckBoxEnabled(state); // Only enable "Hide app icon" if state is turned on.
        if (state) {
            mActionBarSwitch.setText("On");
        } else {
            mActionBarSwitch.setText("Off");
        }

        mActionBarSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!PowerConnectionReceiver.hasPermission(buttonView.getContext())) {
                    showPermissionRequestDialog();
                    mActionBarSwitch.setChecked(false);
                    return;
                }
                mIconHelper.setCheckBoxEnabled(isChecked);

                PowerConnectionReceiver.setPrefEnabled(getApplicationContext(), isChecked);
                if (!isChecked) {
                    buttonView.setText("Off");
                    PowerConnectionReceiver.disableStayAwake(getApplicationContext());
                } else {
                    buttonView.setText("On");
                    PowerConnectionReceiver.toggleStayAwake(getApplicationContext());
                }
            }
        });
        mIconHelper.startListeningForChanges();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_SETTINGS_REQUEST) {
            if (!PowerConnectionReceiver.hasPermission(this)) {
                mActionBarSwitch.setChecked(false);
                new AlertDialog.Builder(this)
                        .setMessage("Awake for Debug will not function without it.")
                        .setTitle("Permission not granted")
                        .setPositiveButton("OK", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            } else {
                mActionBarSwitch.setChecked(true);
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showPermissionRequestDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Awake for Debug needs permission to modify system settings to change the screen timeout.")
                .setTitle("Permission request")
                .setPositiveButton("Continue", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showPermissionActivity();
                    }
                })
                .setNegativeButton("Cancel", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mActionBarSwitch.setChecked(false);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void showPermissionActivity() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivityForResult(intent, WRITE_SETTINGS_REQUEST);
    }
}
