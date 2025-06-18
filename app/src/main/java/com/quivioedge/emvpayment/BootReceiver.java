package com.quivioedge.emvpayment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        // When BOOT_COMPLETED event is received
        if (action.equals("android.intent.action.QUICKBOOT_POWERON")
                || action.equals(Intent.ACTION_BOOT_COMPLETED)
                || action.equals("paydroid.intent.action.BOOT_COMPLETED"))
        {
            Intent serviceIntent = new Intent(context, MainActivity.class);
            serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(serviceIntent);
        }
    }
}