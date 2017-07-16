package com.afzaln.awakedebug;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import java.util.Arrays;
import java.util.List;

/**
 * Created by afzal on 2017-07-15.
 */

public class ShortcutUtils {
    private static final String AC_POWER_SHORTCUT_ID = "ac_power";
    private static final String DEBUG_SHORTCUT_ID = "debug_power";

    static void updateShortcuts(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            List<ShortcutInfo> existingShortcuts = shortcutManager.getDynamicShortcuts();

            // create and set dynamic shortcuts
            ShortcutInfo debugShortcut = createShortcut(context,
                    AppShortcutActivity.EXTRA_TOGGLE_DEBUG_AWAKE,
                    PrefUtils.isAwakeDebugEnabled(context),
                    DEBUG_SHORTCUT_ID,
                    context.getString(R.string.awake_debug_toggle_short),
                    context.getString(R.string.awake_debug_toggle_long),
                    1);

            ShortcutInfo acShortcut = createShortcut(context,
                    AppShortcutActivity.EXTRA_TOGGLE_AC_AWAKE,
                    PrefUtils.isAwakeAcEnabled(context),
                    AC_POWER_SHORTCUT_ID,
                    context.getString(R.string.awake_ac_toggle_short),
                    context.getString(R.string.awake_ac_toggle_long),
                    0);

            List<ShortcutInfo> shortcuts = Arrays.asList(debugShortcut, acShortcut);

            if (existingShortcuts.isEmpty()) {
                shortcutManager.setDynamicShortcuts(shortcuts);
            } else {
                shortcutManager.updateShortcuts(shortcuts);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @NonNull
    private static ShortcutInfo createShortcut(Context context,
                                               String extraName,
                                               boolean enabled,
                                               String shortcutId,
                                               String shortLabel,
                                               String longLabel,
                                               int rank) {

        Intent intent = new Intent(context, AppShortcutActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_RUN);
        intent.putExtra(extraName, enabled);

        Icon acIcon = Icon.createWithResource(context, enabled ? R.drawable.ic_check_box : R.drawable.ic_check_box_blank);
        return new ShortcutInfo.Builder(context.getApplicationContext(), shortcutId)
                .setShortLabel(shortLabel)
                .setLongLabel(longLabel)
                .setIcon(acIcon)
                .setRank(rank)
                .setIntent(intent)
                .build();
    }
}
