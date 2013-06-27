package flash.car.mobile.alarm;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import flash.car.mobile.FlashCarMobileActivity;
import flash.car.mobile.R;
import flash.car.mobile.log.Logger;

public class AlarmDialogKmReceiver extends BroadcastReceiver {
	private String value="";
/*
	public AlarmDialogKmReceiver() {
		
	}
*/
	public AlarmDialogKmReceiver(String value) {
        logMe("AlarmDialogKmReceiver", "Constructor START");
		this.value = value;
        logMe("AlarmDialogKmReceiver", "Constructor END");
	}

	@Override
    public void onReceive(final Context context, Intent intent) {
        logMe("AlarmDialogKmReceiver", "onReceive START");
		String title = context.getString(R.string.dlg_km_title);
		String message = context.getString(R.string.dlg_km_message);

        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(title);
        alert.setMessage(message);

        // Set an EditText view to get user input 
        final EditText input = new EditText(context);
        input.setText(value);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	value = input.getText().toString();

                logMe("AlarmDialogKmReceiver", "onClick Ok value:"+value);

                // Execute Action to return Km
                Intent indentChangeKM = new Intent();
                indentChangeKM.setAction(FlashCarMobileActivity.INDENT_ACTION_CHANGE_KM);
                indentChangeKM.putExtra("km", value);
                context.sendBroadcast(indentChangeKM);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                logMe("AlarmDialogKmReceiver", "onClickCancel");
            }
          });

        alert.show();
        logMe("AlarmDialogKmReceiver", "onReceive END");
    }
	
	public String getValue() {
		return value;
	}

	private static void logMe(String tag, String msg) {
		Logger.logMe(tag, msg);
//		//System.out.println(tag+" "+msg);
//        Log.i(tag, msg);
    }
};
