package com.afzaln.awakedebug;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import com.crashlytics.android.Crashlytics;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG) {
            Crashlytics.start(this);
        }

        setContentView(R.layout.activity_main);


        Switch actionBarSwitch = new Switch(this);

        final int padding = getResources().getDimensionPixelSize(
                R.dimen.action_bar_switch_padding);
        actionBarSwitch.setPadding(0, 0, padding, 0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(actionBarSwitch, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL | Gravity.RIGHT));

        actionBarSwitch.setChecked(PowerConnectionReceiver.getPrefEnabled(getApplicationContext()));

        actionBarSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PowerConnectionReceiver.setPrefEnabled(getApplicationContext(), isChecked);
                if (!isChecked) {
                    PowerConnectionReceiver.disableStayAwake(getApplicationContext());
                } else {
                    PowerConnectionReceiver.toggleStayAwake(getApplicationContext());
                }
            }
        });
    }


        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
            return super.onOptionsItemSelected(item);
        }
    }
