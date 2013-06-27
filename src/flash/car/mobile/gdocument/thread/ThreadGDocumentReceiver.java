package flash.car.mobile.gdocument.thread;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.pras.SpreadSheet;
import com.pras.SpreadSheetFactory;
import com.pras.WorkSheet;
import com.pras.auth.Authenticator;

import flash.car.mobile.FlashCarMobileActivity;
import flash.car.mobile.R;
import flash.car.mobile.domain.FlashCar;
import flash.car.mobile.log.Logger;

public class ThreadGDocumentReceiver extends BroadcastReceiver {

    private static final String FORMAT_DATE_EXPORT = "yyyy/MM/dd HH:mm";
	private static final String TITLE_SPREADSHEET = "FlashCarMobileReport";
    private static final String TITLE_WORKSHEET = "report";

    public static final String KEY_AUTHENTIFICATION_BUNDLE = "authBundle";
    public static final String COL_KEY_IMATRICUTION = "imatriculation";
    public static final String COL_KEY_DATE_HEURE_DEPART = "depart";
    public static final String COL_KEY_DATE_HEURE_RETOUR = "retour";
    public static final String COL_KEY_IDENTIFICATION_UTILISATEUR = "utilisateur";
    public static final String COL_KEY_KM = "km";

    private static final String[] COLUMNS = {COL_KEY_IMATRICUTION, COL_KEY_DATE_HEURE_DEPART, COL_KEY_DATE_HEURE_RETOUR, COL_KEY_IDENTIFICATION_UTILISATEUR, COL_KEY_KM};

	@Override
    public void onReceive(final Context context, Intent intent) {
		logMe("ThreadGDocumentExport", "onReceive START");
		if (intent.getAction().equals(FlashCarMobileActivity.INDENT_ACTION_SEND_REPORT)) {
			try {
				final Bundle authBundle = intent.getBundleExtra(KEY_AUTHENTIFICATION_BUNDLE);
				Authenticator authenticator = new Authenticator() {
					public String getAuthToken(String service) {
						String token = authBundle.getString(service);
						logMe("ThreadGDocumentExport", "onReceive getAuthToken service:["+service+"] token:"+token);
						return token;
					}	        				
				};
				SpreadSheetFactory factory = SpreadSheetFactory.getInstance(authenticator);
		
				// Get selected SpreadSheet 	
				ArrayList<SpreadSheet> spreadSheets = factory.getSpreadSheet(TITLE_SPREADSHEET, false);
				
				if(spreadSheets == null || spreadSheets.size() == 0){
					logMe("ThreadGDocumentExport", "onReceive No SpreadSheet Exists!");
			        factory.createSpreadSheet(TITLE_SPREADSHEET);
			        logMe("ThreadGDocumentExport", "onReceive SpreadSheet '"+TITLE_SPREADSHEET+"' created");
			        spreadSheets = factory.getSpreadSheet(TITLE_SPREADSHEET, false);
//					SpreadSheet sp = spreadSheets.get(0);
//					// Supprime les WorkSheet par défaut à la création
//					ArrayList<WorkSheet> list = sp.getAllWorkSheets();
//					for(WorkSheet w : list) {
//						sp.deleteWorkSheet(w);
//					}
				}
				
				logMe("ThreadGDocumentExport", "onReceive Number of SpreadSheets: "+ spreadSheets.size());
				
				SpreadSheet sp = spreadSheets.get(0);
				WorkSheet workSheet = null;
				List<WorkSheet> workSheets = sp.getWorkSheet(TITLE_WORKSHEET, false);
				if(workSheets == null || workSheets.size() == 0){
					logMe("ThreadGDocumentExport", "onReceive ### Creating WorkSheet for ListFeed ###");
					workSheet = sp.addListWorkSheet(TITLE_WORKSHEET, 1, COLUMNS);
					logMe("ThreadGDocumentExport", "onReceive WorkSheet '"+workSheet+"' created");
				}
				else {
					workSheet = workSheets.get(0);
				}
		
				String valueEnter = intent.getStringExtra(COL_KEY_IMATRICUTION);
				long dateEnter = intent.getLongExtra(COL_KEY_DATE_HEURE_DEPART, 0);
				long dateExit = intent.getLongExtra(COL_KEY_DATE_HEURE_RETOUR, 0);
				String idUser = intent.getStringExtra(COL_KEY_IDENTIFICATION_UTILISATEUR);
				String km = intent.getStringExtra(COL_KEY_KM);
		
				HashMap<String, String> row_data = new HashMap<String, String>();
				row_data.put(COL_KEY_IMATRICUTION, (valueEnter==null ? "" : valueEnter));
				row_data.put(COL_KEY_DATE_HEURE_DEPART, (dateEnter<=0 ? "" : new SimpleDateFormat(FORMAT_DATE_EXPORT).format(new Date(dateEnter))));
				row_data.put(COL_KEY_DATE_HEURE_RETOUR, (dateExit<=0 ? "" : new SimpleDateFormat(FORMAT_DATE_EXPORT).format(new Date(dateExit))));
				row_data.put(COL_KEY_IDENTIFICATION_UTILISATEUR, (idUser==null ? "" : idUser));
				row_data.put(COL_KEY_KM, (km==null ? "" : km));
				
				// Add entries
				workSheet.addListRow(row_data);
				logMe("ThreadGDocumentExport", "onReceive Data '"+row_data+"' added");
		
		        logMe("ThreadGDocumentExport", "onReceive 14 flushMe DEFORE");
				factory.flushMe();
				logMe("ThreadGDocumentExport", "onReceive 15 flushMe AFTER");
		
				// Message
		        Toast.makeText(context, context.getString(R.string.msg_send_report_complete), Toast.LENGTH_SHORT).show();
			}
	        finally {	
	            logMe("ThreadGDocumentExport", "onReceive 16 run END");
	        }
		}
	}

	private static void logMe(String tag, String msg) {
		Logger.logMe(tag, msg);
    }
}