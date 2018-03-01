package com.codex.talkntrace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Rutviz Vyas on 25-03-2017.
 */

public class StartFirebaseAtBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,FirebaseBackgroundService.class));
    }
}
