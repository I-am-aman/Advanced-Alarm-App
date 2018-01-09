package techknights.droidrush.getalarmed;


import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.view.WindowManager;

import static android.content.Context.KEYGUARD_SERVICE;


public class AlarmReceiver extends WakefulBroadcastReceiver{



    @Override
    public void onReceive(final Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        int reqCode = bundle.getInt("reqCode");

        Intent i = new Intent(context,AlarmAlertActivity.class);
        i.putExtra("reqCode",reqCode);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(i);

    }
}