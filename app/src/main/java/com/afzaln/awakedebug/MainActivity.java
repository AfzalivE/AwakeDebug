package com.afzaln.awakedebug;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import java.util.Arrays;


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
        SwitchCompat acSwitch = (SwitchCompat) findViewById(R.id.toggle_ac);

        final boolean state = PowerConnectionReceiver.getPrefEnabled(this);
        mActionBarSwitch.setChecked(state);
        acSwitch.setChecked(Utils.getAcPowerOn(this));
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

                PowerConnectionReceiver.setPrefEnabled(MainActivity.this, isChecked);
                if (!isChecked) {
                    buttonView.setText("Off");
                } else {
                    buttonView.setText("On");
                }

                toggleAwake(isChecked);
                initShortcuts(isChecked);
            }
        });

        acSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.setAcPowerOn(MainActivity.this, isChecked);
                toggleAwake(mActionBarSwitch.isChecked());
                initShortcuts(state);
            }
        });

        mIconHelper.startListeningForChanges();

        initShortcuts(state);

        if (getIntent() != null && getIntent().getAction().equals(Intent.ACTION_RUN)) {
            if (getIntent().hasExtra(getString(R.string.awake_key))) {
                PowerConnectionReceiver.setPrefEnabled(MainActivity.this, !state);
                toggleAwake(!state);
                initShortcuts(!state);
            } else if (getIntent().hasExtra(getString(R.string.awake_ac_key))) {
                Utils.setAcPowerOn(MainActivity.this, !Utils.getAcPowerOn(this));
                toggleAwake(Utils.getAcPowerOn(this));
                initShortcuts(state);
            }
            finish();
        }

    }

    private void initShortcuts(boolean state) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(Intent.ACTION_RUN);
            intent.putExtra(getString(R.string.awake_key), state);

            ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "id1")
                    .setShortLabel(getString(R.string.awake_toggle_short))
                    .setLongLabel(getString(R.string.awake_toggle_long))
                    .setIcon(Icon.createWithResource(this, state ? R.drawable.ic_check_box : R.drawable.ic_check_box_blank))
                    .setIntent(intent)
                    .build();

            Intent intentAc = new Intent(this, MainActivity.class);
            intentAc.setAction(Intent.ACTION_RUN);
            intentAc.putExtra(getString(R.string.awake_ac_key), Utils.getAcPowerOn(this));

            ShortcutInfo shortcutAc = new ShortcutInfo.Builder(this, "id2")
                    .setShortLabel(getString(R.string.awake_ac_toggle_short))
                    .setLongLabel(getString(R.string.awake_ac_toggle_long))
                    .setIcon(Icon.createWithResource(this, Utils.getAcPowerOn(this) ? R.drawable.ic_check_box : R.drawable.ic_check_box_blank))
                    .setIntent(intentAc)
                    .build();

            shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut, shortcutAc));
        }
    }

    private void toggleAwake(boolean isChecked) {
        if (isChecked) {
            PowerConnectionReceiver.toggleStayAwake(getApplicationContext());
        } else {
            PowerConnectionReceiver.disableStayAwake(getApplicationContext());
        }
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
