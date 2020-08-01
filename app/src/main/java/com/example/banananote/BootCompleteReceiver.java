package com.example.banananote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent in = new Intent(context, AlarmReceiver.class);
                context.startForegroundService(in);
            } else {
                Intent in = new Intent(context, FloatingViewService.class);
                context.startService(in);
            }
        }
    }
}
