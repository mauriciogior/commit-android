package com.mauriciogiordano.commit;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class CommitAlarmReceiver extends WakefulBroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
        		CommitAlarmIntent.class.getName());
        // Start the service, keeping the device awake while it is launching.
        intent.putExtra("requestCode", Constants.INTENT_COMMIT_NOTIFICATION);
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}