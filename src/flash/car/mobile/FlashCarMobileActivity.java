package flash.car.mobile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import flash.car.mobile.alarm.AlarmDialogKmReceiver;
import flash.car.mobile.alarm.AlarmWakeUpKmReceiver;
import flash.car.mobile.domain.Alarm;
import flash.car.mobile.domain.FlashCar;
import flash.car.mobile.gdocument.auth.AuthenticatorActivity;
import flash.car.mobile.gdocument.thread.ThreadGDocumentReceiver;
import flash.car.mobile.log.Logger;

public class FlashCarMobileActivity extends Activity implements OnDateSetListener, OnTimeSetListener {

//	private static final boolean LOG_WRITE_SYSOUT = false;
//	private static final boolean LOG_WRITE_SD = true;
	private static final int CAMERA_PIC_REQUEST = 1337;
	private static final int AUTH_TOKEN_REQUEST = 1437;
	private static final int FLASH_REQUEST_ENTER = IntentIntegrator.REQUEST_CODE;
	private static final int FLASH_REQUEST_EXIT = IntentIntegrator.REQUEST_CODE + 100;

	private static final String REQUEST_FLASH_ENTER = "FLASH_ENTER";
	private static final String REQUEST_FLASH_EXIT = "FLASH_EXIT";

	private static final int ALARM_ID = 1234567;
	private static Alarm alarm;

	private static String requestFlashType = null;

	private Bundle authBundle = new Bundle();
	private FlashCar flashCar = new FlashCar();

	private static final String ALARM_DATETIME_FORMAT = "yyyy-MM-dd 'a' HH:mm";
	private static final String BTN_DATE_FORMAT = "%d-%m-%Y";
	private static final String BTN_HEURE_FORMAT = "%H:%M";
	private static final String DATETIME_FORMAT = "yyyyMMdd-HHmmss";

	public static final String INDENT_ACTION_CHANGE_KM = "flash.car.mobile.KmChange";
	public static final String INDENT_ACTION_DIALOG_KM = "flash.car.mobile.KmDialog";
	public static final String INDENT_ACTION_SEND_REPORT = "flash.car.mobile.gdocument.thread.SendReport";
	public static final String INDENT_ACTION_SENDED_REPORT = "flash.car.mobile.gdocument.thread.SendedReport";

	/** Keep track of the progress dialog so we can dismiss it */
    private ProgressDialog mProgressDialog = null;
//	private ThreadGDocumentReceiver sendReportReceiver;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState==null) {
	        initLogSD();
	        logMe("FlashCarMobileActivity", "onCreate AFTER CALL initLogSD");
        }

        logMe("FlashCarMobileActivity", "onCreate savedInstanceState:"+savedInstanceState+" START");
        super.onCreate(savedInstanceState);

        logMe("FlashCarMobileActivity", "onCreate Setting setContentView");
        setContentView(R.layout.main);

        logMe("FlashCarMobileActivity", "onCreate Setting setTitle");
        setTitle("["+getString(R.string.app_version)+"] "+getString(R.string.app_title));

        logMe("FlashCarMobileActivity", "onCreate Setting Component 'btn_flash_enter_car'");
        findViewById(R.id.btn_flash_enter_car).setOnClickListener(flashEnterCar);
        logMe("FlashCarMobileActivity", "onCreate Setting Component 'btn_flash_exit_car'");
        findViewById(R.id.btn_flash_exit_car).setOnClickListener(flashExitCar);
        //logMe("FlashCarMobileActivity", "onCreate Setting Component 'btn_picture'");
        //findViewById(R.id.btn_picture).setOnClickListener(takePicture);
        logMe("FlashCarMobileActivity", "onCreate Setting Component 'btn_report'");
        findViewById(R.id.btn_report).setOnClickListener(sendReport);

        if (savedInstanceState==null) {
            logMe("FlashCarMobileActivity", "onCreate Initialisation");

            logMe("FlashCarMobileActivity", "onCreate Setting flash_enter_car");
            findViewById(R.id.btn_flash_enter_car).setVisibility(android.view.View.VISIBLE);
            logMe("FlashCarMobileActivity", "onCreate Setting flash_exit_car_Visibility");
            findViewById(R.id.btn_flash_exit_car).setVisibility(android.view.View.INVISIBLE);
            logMe("FlashCarMobileActivity", "onCreate Setting btn_report");
            findViewById(R.id.btn_report).setVisibility(android.view.View.INVISIBLE);

	        logMe("FlashCarMobileActivity", "onCreate start Activity AuthenticatorActivity");
	        Intent intent = new Intent(FlashCarMobileActivity.this, AuthenticatorActivity.class);
			startActivityForResult(intent, AUTH_TOKEN_REQUEST);
        }
        else {
	        logMe("FlashCarMobileActivity", "onCreate Reinitialisation");
			alarm = new Alarm(savedInstanceState);
			flashCar = new FlashCar(savedInstanceState);
			requestFlashType = savedInstanceState.getString("requestFlashType");
	        authBundle = savedInstanceState.getBundle("authBundle");

	        affichageAlarm();

	        int btn_flash_enter_cart_Visibility = savedInstanceState.getInt("btn_flash_enter_cart_Visibility", android.view.View.VISIBLE);
	        logMe("FlashCarMobileActivity", "onCreate Setting btn_flash_enter_cart_Visibility:"+btn_flash_enter_cart_Visibility);
	        findViewById(R.id.btn_flash_enter_car).setVisibility(btn_flash_enter_cart_Visibility);
	        int btn_flash_exit_car_Visibility = savedInstanceState.getInt("btn_flash_exit_car_Visibility", android.view.View.INVISIBLE);
	        logMe("FlashCarMobileActivity", "onCreate Setting btn_flash_exit_car_Visibility:"+btn_flash_exit_car_Visibility);
	        findViewById(R.id.btn_flash_exit_car).setVisibility(btn_flash_exit_car_Visibility);
	        int btn_report_Visibility = savedInstanceState.getInt("btn_report_Visibility", android.view.View.INVISIBLE);
	        logMe("FlashCarMobileActivity", "onCreate Setting btn_report_Visibility:"+btn_report_Visibility);
	        findViewById(R.id.btn_report).setVisibility(btn_report_Visibility);

	        logMe("FlashCarMobileActivity", "onCreate No Action");
        }

        logMe("FlashCarMobileActivity", "onCreate savedInstanceState:"+savedInstanceState+" END");
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		Log.i("FlashCarMobileActivity", "savedInstanceState Bundle:"+savedInstanceState+" START");
		if (savedInstanceState!=null) {
			Log.i("FlashCarMobileActivity", "savedInstanceState alarm.savedInstanceState");
			if (alarm!=null)
				alarm.savedInstanceState(savedInstanceState);

			Log.i("FlashCarMobileActivity", "savedInstanceState flashCar.savedInstanceState");
			if (flashCar!=null)
				flashCar.savedInstanceState(savedInstanceState);

			Log.i("FlashCar", "savedInstanceState putString requestFlashType:"+requestFlashType);
			savedInstanceState.putString("requestFlashType", requestFlashType);

			int btn_flash_enter_cart_Visibility = findViewById(R.id.btn_flash_enter_car).getVisibility();
			Log.i("FlashCar", "savedInstanceState putInt btn_flash_enter_cart_Visibility:"+btn_flash_enter_cart_Visibility);
			savedInstanceState.putInt("btn_flash_enter_cart_Visibility", btn_flash_enter_cart_Visibility);

			int btn_flash_exit_car_Visibility = findViewById(R.id.btn_flash_exit_car).getVisibility();
			Log.i("FlashCar", "savedInstanceState putInt btn_flash_exit_car_Visibility:"+btn_flash_exit_car_Visibility);
			savedInstanceState.putInt("btn_flash_exit_car_Visibility", btn_flash_exit_car_Visibility);

			int btn_report_Visibility = findViewById(R.id.btn_report).getVisibility();
			Log.i("FlashCar", "savedInstanceState putInt btn_report_Visibility:"+btn_report_Visibility);
			savedInstanceState.putInt("btn_report_Visibility", btn_report_Visibility);

			Log.i("FlashCar", "savedInstanceState putBundle authBundle:"+authBundle);
			savedInstanceState.putBundle("authBundle", authBundle);
		}
		super.onSaveInstanceState(savedInstanceState);
		Log.i("FlashCarMobileActivity", "savedInstanceState END");
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
        logMe("FlashCarMobileActivity", "onResume START");
        super.onResume();
        logMe("FlashCarMobileActivity", "onResume BEFORE CALL initIndentAlarmDialogKm");
        initIndentAlarmDialogKm();
        logMe("FlashCarMobileActivity", "onResume BEFORE CALL initIndentAlarmChangeKm");
        initIndentAlarmChangeKm();
        logMe("FlashCarMobileActivity", "onResume BEFORE CALL initIndentSendReport");
        initIndentSendReport();
        logMe("FlashCarMobileActivity", "onResume END");
	}

    /* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
        logMe("FlashCarMobileActivity", "onPause START");
		super.onPause();
		if (alarmDialogKmReceiver!=null) {
			Log.i("FlashCar", "savedInstanceState unregisterReceiver alarmDialogKmReceiver:"+alarmDialogKmReceiver);
	        unregisterReceiver(alarmDialogKmReceiver);
	        alarmDialogKmReceiver=null;
		}

		if (alarmChangeKmReceiver!=null) {
			Log.i("FlashCar", "savedInstanceState unregisterReceiver alarmChangeKmReceiver:"+alarmChangeKmReceiver);
	        unregisterReceiver(alarmChangeKmReceiver);
	        alarmChangeKmReceiver=null;
		}
//
//		if (sendReportReceiver!=null) {
//			Log.i("FlashCar", "savedInstanceState unregisterReceiver sendReportReceiver:"+sendReportReceiver);
//	        unregisterReceiver(sendReportReceiver);
//	        sendReportReceiver=null;
//		}
//		
		logMe("FlashCarMobileActivity", "onPause END");
	}

	private void initIndentAlarmDialogKm() {
		Log.i("FlashCarMobileActivity", "initIndentAlarmDialogKm START");
		if (alarmDialogKmReceiver==null) {
	    	alarmDialogKmReceiver = new AlarmDialogKmReceiver(/*this, "Kilometrage", "Saisie du nouveau kilometrage",*/ flashCar.getKm());
	        IntentFilter alarmDialogKmFilter = new IntentFilter();
	        alarmDialogKmFilter.addAction(INDENT_ACTION_DIALOG_KM);
			Log.i("FlashCarMobileActivity", "initIndentAlarmDialogKm registerReceiver BEFORE");
	        registerReceiver(alarmDialogKmReceiver, alarmDialogKmFilter);
			Log.i("FlashCarMobileActivity", "initIndentAlarmDialogKm registerReceiver AFTER");
		}
		Log.i("FlashCarMobileActivity", "initIndentAlarmDialogKm END");
    }

	private void initIndentAlarmChangeKm() {
		Log.i("FlashCarMobileActivity", "initIndentAlarmChangeKm START");
		if (alarmChangeKmReceiver==null) {
			alarmChangeKmReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					logMe("KmCarAlarmReceiver", "initIndentAlarmChangeKm START");
					String km = (String)intent.getStringExtra("km");
				    flashCar.setKm(km);

					String msg = getString(R.string.msg_alarm_km_changed) + " " + km;
			    	Toast.makeText(FlashCarMobileActivity.this, msg, Toast.LENGTH_SHORT).show();
	
			    	// Schedule wake up from now more 1 month
					logMe("KmCarAlarmReceiver", "initIndentAlarmChangeKm Schedule wake up from now more 1 month");
			    	Calendar cal = Calendar.getInstance();
			    	cal.add(Calendar.MONTH, 1);
			    	alarm.setTimeInMillis(cal.getTimeInMillis());
					planifierAlarm();
	
			    	logMe("KmCarAlarmReceiver", "initIndentAlarmChangeKm km:"+km+" END");
				}
			};
	        IntentFilter alarmChangeKmFilter = new IntentFilter();
	        alarmChangeKmFilter.addAction(INDENT_ACTION_CHANGE_KM);
			Log.i("FlashCarMobileActivity", "initIndentAlarmChangeKm registerReceiver BEFORE");
	        registerReceiver(alarmChangeKmReceiver, alarmChangeKmFilter);
			Log.i("FlashCarMobileActivity", "initIndentAlarmChangeKm registerReceiver AFTER");
		}
		Log.i("FlashCarMobileActivity", "initIndentAlarmChangeKm END");
    }

	private void initIndentSendReport() {
		Log.i("FlashCarMobileActivity", "initIndentSendReport START");
//		if (sendReportReceiver==null) {
//	    	sendReportReceiver = new ThreadGDocumentReceiver();
//	        IntentFilter sendReportFilter = new IntentFilter();
//	        sendReportFilter.addAction(INDENT_ACTION_SEND_REPORT);
//			Log.i("FlashCarMobileActivity", "initIndentSendReport registerReceiver BEFORE");
//	        registerReceiver(sendReportReceiver, sendReportFilter);
//			Log.i("FlashCarMobileActivity", "initIndentSendReport registerReceiver AFTER");
//		}
		Log.i("FlashCarMobileActivity", "initIndentSendReport END");
    }

    private void initAlam() {
        logMe("FlashCarMobileActivity", "initAlam START");
        if (alarm==null) {
			Log.i("FlashCarMobileActivity", "initAlam Schedule wake up from now more 1 minute");
	    	Calendar cal = Calendar.getInstance();
			alarm = new Alarm();
	    	alarm.setActive(true);
	    	Time t = new Time();
	    	t.year = cal.get(Calendar.YEAR);
	    	t.month = cal.get(Calendar.MONTH);
	    	t.monthDay = cal.get(Calendar.DAY_OF_MONTH);
	    	t.hour = cal.get(Calendar.HOUR_OF_DAY);
	    	t.minute = cal.get(Calendar.MINUTE);
	
	    	t.minute += 1;
	    	alarm.setTime(t);
        }

		planifierAlarm();
        logMe("FlashCarMobileActivity", "initAlam END");
    }
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        logMe("FlashCarMobileActivity", "onDateSet START");
		Time t = alarm.getTime();
		t.year = year;
		t.month = monthOfYear;
		t.monthDay = dayOfMonth;
		alarm.setTime(t);
		planifierAlarm();
        logMe("FlashCarMobileActivity", "onDateSet END");
	}
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        logMe("FlashCarMobileActivity", "onTimeSet START");
		Time t = alarm.getTime();
		t.hour = hourOfDay;
		t.minute = minute;
		alarm.setTime(t);
		planifierAlarm();
        logMe("FlashCarMobileActivity", "onTimeSet END");
	}

	private void planifierAlarm() {
        logMe("FlashCarMobileActivity", "planifierAlarm START");
    	//Récupération de l'instance du service AlarmManager.
    	AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        logMe("FlashCarMobileActivity", "planifierAlarm AlarmManager:"+am);

    	//On instancie l'Intent qui va être appelé au moment du reveil.
    	Intent intent = new Intent(this, AlarmWakeUpKmReceiver.class);
        logMe("FlashCarMobileActivity", "planifierAlarm intent:"+intent);

    	//On créer le pending Intent qui identifie l'Intent de reveil avec un ID et un/des flag(s)
    	PendingIntent pendingintent = PendingIntent.getBroadcast(this, ALARM_ID, intent, 0);
        logMe("FlashCarMobileActivity", "planifierAlarm pendingintent:"+pendingintent);

    	//On annule l'alarm pour replanifier si besoin
    	am.cancel(pendingintent);
        logMe("FlashCarMobileActivity", "planifierAlarm AlarmManager Canceled");

    	//on va déclencher un calcul pour connaitre le temps qui nous sépare du prochain reveil.
    	Calendar reveil  = Calendar.getInstance();
    	Calendar cal = Calendar.getInstance();
    	// MOKE
    	if (alarm==null || alarm.getTimeInMillis()<cal.getTimeInMillis()) {
    		// Schedule wake up from now more 10 seconds
    		logMe("FlashCarMobileActivity", "planifierAlarm Schedule wake up from now more 10 seconds");
        	reveil.add(Calendar.SECOND, 10);

        	Time time = new Time();
        	time.year = reveil.get(Calendar.YEAR);
        	time.month = reveil.get(Calendar.MONTH);
        	time.monthDay = reveil.get(Calendar.DAY_OF_MONTH);
        	time.hour = reveil.get(Calendar.HOUR_OF_DAY);
        	time.minute = reveil.get(Calendar.MINUTE);

        	if (alarm==null)
        		alarm = new Alarm();
	    	alarm.setTime(time);
    	}
    	else {
	    	reveil.set(Calendar.YEAR, alarm.getTime().year);
	    	reveil.set(Calendar.MONTH, alarm.getTime().month);
	    	reveil.set(Calendar.DAY_OF_MONTH, alarm.getTime().monthDay);
	    	reveil.set(Calendar.HOUR_OF_DAY, alarm.getTime().hour);
	    	reveil.set(Calendar.MINUTE, alarm.getTime().minute);
	    	reveil.set(Calendar.SECOND, 0);

	    	cal.set(Calendar.SECOND, 0);
    	}

    	long diff = reveil.getTimeInMillis() - cal.getTimeInMillis();
		logMe("FlashCarMobileActivity", "planifierAlarm diff:"+diff);

		String date = new SimpleDateFormat(DATETIME_FORMAT).format(reveil.getTime());
		logMe("FlashCarMobileActivity", "planifierAlarm reveil:"+date);

        //On ajoute le reveil au service de l'AlarmManager
    	am.set(AlarmManager.RTC_WAKEUP,reveil.getTimeInMillis(), pendingintent);

    	String msg = getString(R.string.msg_alarm_time) + " " + new SimpleDateFormat(ALARM_DATETIME_FORMAT).format(reveil.getTime());
    	Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        logMe("FlashCarMobileActivity", "planifierAlarm msg:"+msg);

        affichageAlarm();
        logMe("FlashCarMobileActivity", "planifierAlarm END");
	}

    private void affichageAlarm() {
    	//Ici on a juste voulu créer un affichage de la date qui soit au format yyyy-mm-dd.
		String dateReveil = alarm.getTime().format(BTN_DATE_FORMAT);
		Button btn_alarm_date = (Button)findViewById(R.id.date);
		btn_alarm_date.setText(dateReveil);

		//Ici on a juste voulu créer un affichage de l'heure qui soit au format hh:mm.
		String heureReveil = alarm.getTime().format(BTN_HEURE_FORMAT);
		Button btn_alarm_heure = (Button)findViewById(R.id.heure);
		btn_alarm_heure.setText(heureReveil);
	}
	
    /*
     * changeHeure se déclenche automatiquement au click sur l'heure ou la CheckBox.
     * Active ou désactive le reveil.
     * Affiche un dialog pour choisir l'heure de reveil
     */
    public void changeDate(View target){
    	DatePickerDialog dialog = new DatePickerDialog(this, this, alarm.getTime().year, alarm.getTime().month, alarm.getTime().monthDay);
    	dialog.show();
    }
	
    /*
     * changeHeure se déclenche automatiquement au click sur l'heure ou la CheckBox.
     * Active ou désactive le reveil.
     * Affiche un dialog pour choisir l'heure de reveil
     */
    public void changeHeure(View target){
    	TimePickerDialog dialog = new TimePickerDialog(this, this, alarm.getTime().hour, alarm.getTime().minute, true);
    	dialog.show();
    }

	/**
	  * Send google document report
	  */
	 private final Button.OnClickListener sendReport = new  Button.OnClickListener() {
		@Override
		  public void onClick(View v) {
		      logMe("FlashCarMobileActivity", "onClickListener 'btn_send_report' - START");
		      // Start thread to create report in Google Server
//		      runOnUiThread(new ThreadGDocumentExport(authBundle, flashCar));

              logMe("FlashCarMobileActivity", "sendReport showProgress");
		      showProgress();

		      // Execute Action to return Km
		      logMe("FlashCarMobileActivity", "sendReport onClick Create Indent");
              Intent indentSendReport = new Intent();
              indentSendReport.setAction(FlashCarMobileActivity.INDENT_ACTION_SEND_REPORT);
              indentSendReport.putExtra(ThreadGDocumentReceiver.KEY_AUTHENTIFICATION_BUNDLE, authBundle);
              indentSendReport.putExtra(ThreadGDocumentReceiver.COL_KEY_IMATRICUTION, flashCar.getValueEnter());
              indentSendReport.putExtra(ThreadGDocumentReceiver.COL_KEY_DATE_HEURE_DEPART, (flashCar.getDateEnter()==null ? 0 : flashCar.getDateEnter().getTime()));
              indentSendReport.putExtra(ThreadGDocumentReceiver.COL_KEY_DATE_HEURE_RETOUR, (flashCar.getDateExit()==null ? 0 : flashCar.getDateExit().getTime()));
              indentSendReport.putExtra(ThreadGDocumentReceiver.COL_KEY_IDENTIFICATION_UTILISATEUR, flashCar.getIdUser());
              indentSendReport.putExtra(ThreadGDocumentReceiver.COL_KEY_KM, flashCar.getKm());
		      logMe("FlashCarMobileActivity", "sendReport onClick sendBroadcast indentSendReport:"+indentSendReport);
              //FlashCarMobileActivity.this.sendBroadcast(indentSendReport);
		      BroadcastReceiver resultReceiver = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
		              Log.i("FlashCarMobileActivity", "sendReportReceiver resultReceiver onReceive START");
		              logMe("FlashCarMobileActivity", "sendReportReceiver resultReceiver onReceive hideProgress");
		              hideProgress();
		              logMe("FlashCarMobileActivity", "sendReportReceiver resultReceiver onReceive END");
					}
					
				};

			  Bundle extra = new Bundle();
              FlashCarMobileActivity.this.sendOrderedBroadcast(indentSendReport, null, resultReceiver, null, 0, "", extra);

              logMe("FlashCarMobileActivity", "sendReport Setting btn_flash_enter_car");
              findViewById(R.id.btn_flash_enter_car).setVisibility(android.view.View.VISIBLE);
              logMe("FlashCarMobileActivity", "sendReport Setting btn_flash_exit_car");
              findViewById(R.id.btn_flash_exit_car).setVisibility(android.view.View.INVISIBLE);
              logMe("FlashCarMobileActivity", "sendReport Setting btn_report");
              findViewById(R.id.btn_report).setVisibility(android.view.View.INVISIBLE);

              logMe("FlashCarMobileActivity", "onClickListener 'btn_send_report' - END");
		}
	 };

	    /**
	     * Shows the progress UI for a lengthy operation.
	     */
	    private void showProgress() {
            logMe("FlashCarMobileActivity", "showProgress - START");
	        showDialog(0);
            logMe("FlashCarMobileActivity", "showProgress - END");
	    }

	    /**
	     * Hides the progress UI for a lengthy operation.
	     */
	    private void hideProgress() {
            logMe("FlashCarMobileActivity", "hideProgress - mProgressDialog:"+mProgressDialog+" START");
	        if (mProgressDialog != null) {
	            mProgressDialog.dismiss();
//	            mProgressDialog = null;
	        }
            logMe("FlashCarMobileActivity", "hideProgress - END");
	    }

	    /*
	     * {@inheritDoc}
	     */
	    @Override
	    protected Dialog onCreateDialog(int id, Bundle args) {
            logMe("FlashCarMobileActivity", "onCreateDialog - START");
	        final ProgressDialog dialog = new ProgressDialog(this);
	        dialog.setMessage(getText(R.string.msg_send_report_in_progresse));
	        dialog.setIndeterminate(true);
	        dialog.setCancelable(true);
	        mProgressDialog = dialog;
            logMe("FlashCarMobileActivity", "onCreateDialog - END");
	        return dialog;
	    }

	/**
	 * Enter car flash
	 */
    private final Button.OnClickListener flashEnterCar = new  Button.OnClickListener() {
		@Override
		  public void onClick(View v) {
		      logMe("FlashCarMobileActivity", "onClickListener 'btn_flash_enter_car' - START");
	    	  // 2D Barcode
	    	  startFlash("QR_CODE_MODE", REQUEST_FLASH_ENTER, FLASH_REQUEST_ENTER);
		      logMe("FlashCarMobileActivity", "onClickListener 'btn_flash_enter_car' - END");
		   }
	};

	/**
	 * Exit car flash
	 */
    private final Button.OnClickListener flashExitCar = new Button.OnClickListener() {
      @Override
      public void onClick(View v) {
          logMe("FlashCarMobileActivity", "onClickListener 'btn_flash_exit_car' - START");
    	  // 2D Barcode
    	  startFlash("QR_CODE_MODE", REQUEST_FLASH_EXIT, FLASH_REQUEST_EXIT);
          logMe("FlashCarMobileActivity", "onClickListener 'btn_flash_exit_car' - END");
       }
    };

    /**
     * For test : take picture
     */
    private final Button.OnClickListener takePicture = new Button.OnClickListener() {
      @Override
      public void onClick(View v) {
          logMe("FlashCarMobileActivity", "onClickListener 'btn_take_picture' - START");
    	  setTitle("btn_take_picture click !!");

    	  logMe("FlashCarMobileActivity", "ACTION_IMAGE_CAPTURE - START");
    	  Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    	  startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    	  logMe("FlashCarMobileActivity", "ACTION_IMAGE_CAPTURE - END");

    	  logMe("FlashCarMobileActivity", "onClickListener 'btn_take_picture' - END");
       }
    };
	private BroadcastReceiver alarmDialogKmReceiver;
	private BroadcastReceiver alarmChangeKmReceiver;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		try {
	        logMe("FlashCarMobileActivity", "onActivityResult requestCode:"+requestCode+" resultCode:"+resultCode+" intent:"+intent+" - START");
	        super.onActivityResult(requestCode, resultCode, intent);
	        switch (requestCode) {
	          case CAMERA_PIC_REQUEST:
	              logMe("FlashCarMobileActivity", "onActivityResult CAMERA_PIC_REQUEST START");
	              doPicture(requestCode, resultCode, intent);
	              logMe("FlashCarMobileActivity", "onActivityResult CAMERA_PIC_REQUEST END");
	          break;
	          case IntentIntegrator.REQUEST_CODE:
	              logMe("FlashCarMobileActivity", "onActivityResult FLASH_REQUEST START");
    	    	  if (resultCode == RESULT_OK) {
    	              logMe("FlashCarMobileActivity", "onActivityResult resultCode == RESULT_OK");
		              String value = doFlash(requestCode, resultCode, intent);
    	              logMe("FlashCarMobileActivity", "onActivityResult value:"+value);
		    	      if (value != null) {
	    	    		  if (REQUEST_FLASH_ENTER.equals(requestFlashType)) {
	    	    		      flashCar.setDateEnter(new Date());
	    	    		      flashCar.setValueEnter(value);
	    	    	          findViewById(R.id.btn_flash_enter_car).setVisibility(android.view.View.INVISIBLE);
	    	    	          findViewById(R.id.btn_flash_exit_car).setVisibility(android.view.View.VISIBLE);
	    	    	          findViewById(R.id.btn_report).setVisibility(android.view.View.INVISIBLE);
		    	    	      String msg = getString(R.string.msg_flash_succeeded_enter) + value;
		    	    	      Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		    	    	      logMe("FlashCarMobileActivity", "onActivityResult REQUEST_FLASH_ENTER msg:"+msg);
	    	    		  }
	    	    		  else if (REQUEST_FLASH_EXIT.equals(requestFlashType)) {
	    	    		      flashCar.setDateExit(new Date());
	    	    		      flashCar.setValueExit(value);
	    		    	      findViewById(R.id.btn_flash_enter_car).setVisibility(android.view.View.INVISIBLE);
	    		    	      findViewById(R.id.btn_flash_exit_car).setVisibility(android.view.View.INVISIBLE);
	    	    	          findViewById(R.id.btn_report).setVisibility(android.view.View.VISIBLE);
		    	    	      String msg = getString(R.string.msg_flash_succeeded_enter) + value;
		    	    	      Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		    	    	      logMe("FlashCarMobileActivity", "onActivityResult REQUEST_FLASH_EXIT msg:"+msg);
	    	    		  }
	    	    	  }
			      }
	              logMe("FlashCarMobileActivity", "onActivityResult FLASH_REQUEST END");
	          break;
	          case AUTH_TOKEN_REQUEST:
	        	  if (intent!=null && intent.hasExtra(AccountManager.KEY_AUTHTOKEN)) {
		        	String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
		        	String idUser = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			        Log.i("FlashCarMobileActivity", "onActivityResult AUTH_TOKEN_REQUEST authToken:"+authToken);

			        if (this.flashCar==null)
			        	this.flashCar = new FlashCar();
			        this.flashCar.setIdUser(idUser);

					this.authBundle = new Bundle();
					if (authToken!=null) {
						String[] l = authToken.split(";");
						for(int i=0 ; i<l.length ; i++) {
							String[] j = l[i].split(":");
							if (j.length==2) {
								Log.i("FlashCarMobileActivity", "onActivityResult AUTH_TOKEN_REQUEST authBundle add key:"+j[0]+" value:"+j[1]);
								authBundle.putString(j[0], j[1]);
							}
						}
					}

			        logMe("FlashCarMobileActivity", "onActivityResult AUTH_TOKEN_REQUEST BEFORE CALL initAlam");
			        initAlam();

			        // Start thread to create report in Google Server
		            //runOnUiThread(new ThreadGDocumentExport(authToken, flashCar));
	        	  }
			      Log.i("FlashCarMobileActivity", "onActivityResult AUTH_TOKEN_REQUEST no authToken found");
		          break;
	        }
	        logMe("FlashCarMobileActivity", "onActivityResult requestCode:"+requestCode+" resultCode:"+resultCode+" intent:"+intent+" - END");
		}
		catch (RuntimeException ex) {
			logMe("FlashCarMobileActivity", ex);
			throw ex;
		}
	}

    private final void startFlash(String scanMode, String requestFlash, int requestCode) {
    	try {
	    	Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	    	intent.putExtra("SCAN_MODE", scanMode);
	    	requestFlashType = requestFlash;
	        intent.putExtra("PROMPT_MESSAGE", getString(R.string.msg_flash));
	        startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);
		}
		catch (RuntimeException ex) {
			logMe("FlashCarMobileActivity", ex);
			throw ex;
		}
    }

	private void doPicture(int requestCode, int resultCode, Intent intent) {
        logMe("FlashCarMobileActivity", "onActivityResult CAMERA_PIC_REQUEST START intent:"+intent);
        if (intent!=null && intent.getExtras()!=null) {
	        Bitmap thumbnail = (Bitmap) intent.getExtras().get("data");
	        logMe("FlashCarMobileActivity", "doPicture get Bitmap thumbnail:"+thumbnail);
	        if (thumbnail!=null) {
	        	ImageView image = (ImageView) findViewById(R.id.photoResultView);
		        logMe("FlashCarMobileActivity", "doPicture find photoResultView image:"+image);
		        image.setImageBitmap(thumbnail);
		        logMe("FlashCarMobileActivity", "doPicture setImageBitmap");
	        }
        }
        logMe("FlashCarMobileActivity", "onActivityResult CAMERA_PIC_REQUEST END");
	}

	private String doFlash(int requestCode, int resultCode, Intent intent) {
        logMe("FlashCarMobileActivity", "doFlash START");
		String ret = null;
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        logMe("FlashCarMobileActivity", "doFlash result:"+result);
	    if (result != null) {
	    	ret = result.getContents();
	    }
	    logMe("FlashCarMobileActivity", "doFlash ret:"+ret);
	    if (ret==null)
	        showDialog(R.string.title_failed, getString(R.string.msg_flash_failed));
        logMe("FlashCarMobileActivity", "doFlash END");
	    return ret;
	}

	private void showDialog(int title, CharSequence message) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(title);
	    builder.setMessage(message);
	    builder.setPositiveButton("OK", null);
	    builder.show();
	  }

	private void initLogSD() {
		Logger.initLogSD();
//        if (LOG_WRITE_SD) {
//	        try {
//				new File(getLogFilename()).createNewFile();
//			} catch (IOException ex) {
//				logMe("FlashCarMobileActivity", ex);
//			}
//        }
	}

	private void logMe(String tag, String msg) {
		Logger.logMe(tag, msg);
//		if(LOG_WRITE_SYSOUT)
//			System.out.println(tag+" "+msg);
//
//		Log.i(tag, msg);
//
//        if (LOG_WRITE_SD)
//        	writeMeSD(tag+" "+msg);
    }

	private void logMe(String tag, Exception ex) {
		Logger.logMe(tag, ex);
//		ex.printStackTrace();
//
//		if (LOG_WRITE_SD)
//			writeMeSD(ex);
    }
//
//	private String getLogFilename() {
//		return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "log_"+getString(R.string.app_name)+".txt";
//	}
//
//	// write on SD card file text
//	private void writeMeSD(String text) {
//		FileOutputStream fOut = null;
//		OutputStreamWriter myOutWriter = null;
//		try {
//			String path = getLogFilename();
//			File myFile = new File(path);
////				if (!myFile.exists())
////					myFile.createNewFile();
//			fOut = new FileOutputStream(myFile, true);
//			myOutWriter = new OutputStreamWriter(fOut);
//			
//			String date = new SimpleDateFormat(DATETIME_FORMAT).format(new Date());
//			myOutWriter.append("\r\n"+date+"-"+text);
//		} catch (Exception e) {
//			e.printStackTrace();
//        }
//		finally {
//			if (myOutWriter!=null) {
//				try {
//					myOutWriter.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			if (fOut!=null) {
//				try {
//					fOut.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//    }
//
//	// write on SD card file text
//	private void writeMeSD(Exception ex) {
//		FileOutputStream fOut = null;
//		OutputStreamWriter myOutWriter = null;
//		PrintStream ps = null;
//		try {
//			writeMeSD(ex.getMessage());
//
//			String path = getLogFilename();
//			File myFile = new File(path);
////				if (!myFile.exists())
////					myFile.createNewFile();
//			fOut = new FileOutputStream(myFile, true);
//			myOutWriter = new OutputStreamWriter(fOut);
//
//			ps = new PrintStream(fOut);
//			ex.printStackTrace(ps);
//		} catch (Exception e) {
//			e.printStackTrace();
//        }
//		finally {
//			if (myOutWriter!=null) {
//				try {
//					myOutWriter.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			if (ps!=null) {
//				try {
//					fOut.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			if (fOut!=null) {
//				ps.close();
//			}
//		}
//    }
}