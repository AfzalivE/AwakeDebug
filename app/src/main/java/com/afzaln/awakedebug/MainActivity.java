package com.afzaln.awakedebug;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SwitchCompat actionBarSwitch = (SwitchCompat) findViewById(R.id.toggle);

        boolean state = PowerConnectionReceiver.getPrefEnabled(getApplicationContext());
        actionBarSwitch.setChecked(state);
        if (state) {
            actionBarSwitch.setText("On");
        } else {
            actionBarSwitch.setText("Off");
        }

        actionBarSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
    }
}
