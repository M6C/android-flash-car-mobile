package flash.car.mobile.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Vibrator;
import android.util.Log;
import flash.car.mobile.FlashCarMobileActivity;
import flash.car.mobile.log.Logger;

public class AlarmWakeUpKmReceiver extends BroadcastReceiver {

	@Override
    public void onReceive(Context context, Intent intent) {
        logMe("AlarmWakeUpKmReceiver", "onReceive START");

		// Message
        //Toast.makeText(context, "Wake Up AlarmWakeUpKmReceiver", Toast.LENGTH_SHORT).show();

        // Vigration
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(2000);

		// Execute Action
        Intent indentChangeKM = new Intent();
        indentChangeKM.setAction(FlashCarMobileActivity.INDENT_ACTION_DIALOG_KM);
        context.sendBroadcast(indentChangeKM);

        logMe("AlarmWakeUpKmReceiver", "onReceive END");
    }

	private static void logMe(String tag, String msg) {
		Logger.logMe(tag, msg);
//		//System.out.println(tag+" "+msg);
//        Log.i(tag, msg);
    }
}
