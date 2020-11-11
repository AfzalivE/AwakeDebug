package com.afzaln.awakedebug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

/**
 * Created by afzal on 2017-07-16.
 */

public class AppShortcutActivity extends Activity {

    public static final String EXTRA_TOGGLE_DEBUG_AWAKE = "toggle_debug_awake";
    public static final String EXTRA_TOGGLE_AC_AWAKE = "toggle_ac_awake";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent != null && intent.getAction().equals(Intent.ACTION_RUN)) {
            if (intent.hasExtra(EXTRA_TOGGLE_DEBUG_AWAKE)) {
                boolean newState = !PrefUtils.isAwakeDebugEnabled(this);
                PrefUtils.setAwakeDebugEnabled(this, newState);
                PowerConnectionReceiver.toggleAwake(this, newState);
                ShortcutUtils.updateShortcuts(this);
            } else if (intent.hasExtra(EXTRA_TOGGLE_AC_AWAKE)) {
                boolean newState = !PrefUtils.isAwakeAcEnabled(this);
                PrefUtils.setAwakeAc(this, newState);
                PowerConnectionReceiver.toggleAwake(this, newState);
                ShortcutUtils.updateShortcuts(this);
            }
        }

        finish();
    }
}
