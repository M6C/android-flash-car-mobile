package flash.car.mobile.gdocument.thread;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.util.Log;

import com.pras.SpreadSheet;
import com.pras.SpreadSheetFactory;
import com.pras.WorkSheet;
import com.pras.auth.Authenticator;

import flash.car.mobile.domain.FlashCar;

public class ThreadGDocumentExport implements Runnable {

    private static final String FORMAT_DATE_EXPORT = "yyyy/MM/dd HH:mm";
	private static final String TITLE_SPREADSHEET = "FlashCarMobileReport";
    private static final String TITLE_WORKSHEET = "report";

    private static final String COL_KEY_IMATRICUTION = "imatriculation";
    private static final String COL_KEY_DATE_HEURE_DEPART = "depart";
    private static final String COL_KEY_DATE_HEURE_RETOUR = "retour";
    private static final String COL_KEY_IDENTIFICATION_UTILISATEUR = "utilisateur";
    private static final String COL_KEY_KM = "km";

    private static final String[] COLUMNS = {COL_KEY_IMATRICUTION, COL_KEY_DATE_HEURE_DEPART, COL_KEY_DATE_HEURE_RETOUR, COL_KEY_IDENTIFICATION_UTILISATEUR, COL_KEY_KM};

	private Bundle authBundle;
	private FlashCar flashCar;

//	public ThreadGDocumentExport(String authToken, FlashCar flashCar) {
//		this.authBundle = new Bundle();
//		this.flashCar = flashCar;
//		if (authToken!=null) {
//			String[] l = authToken.split(";");
//			for(int i=0 ; i<l.length ; i++) {
//				String[] j = l[i].split(":");
//				if (j.length==2) {
//					Log.i("ThreadGDocumentExport", "ThreadGDocumentExport constructor authBundle add key:"+j[0]+" value:"+j[1]);
//					authBundle.putString(j[0], j[1]);
//				}
//			}
//		}
//		Log.i("ThreadGDocumentExport", "ThreadGDocumentExport constructor authBundle.size:"+authBundle.size());
//	}

	public ThreadGDocumentExport(Bundle authBundle, FlashCar flashCar) {
		this.authBundle = authBundle;
		this.flashCar = flashCar;
		Log.i("ThreadGDocumentExport", "ThreadGDocumentExport constructor authBundle.size:"+authBundle.size());
	}

	public void run(){
		Log.i("ThreadGDocumentExport", "ThreadGDocumentExport run START");
		Authenticator authenticator = new Authenticator() {
			public String getAuthToken(String service) {
				String token = authBundle.getString(service);
				Log.i("ThreadGDocumentExport", "ThreadGDocumentExport getAuthToken service:["+service+"] token:"+token);
				return token;
			}	        				
		};
		SpreadSheetFactory factory = SpreadSheetFactory.getInstance(authenticator);

		// Get selected SpreadSheet 	
		ArrayList<SpreadSheet> spreadSheets = factory.getSpreadSheet(TITLE_SPREADSHEET, false);
		
		if(spreadSheets == null || spreadSheets.size() == 0){
			Log.i("ThreadGDocumentExport", "No SpreadSheet Exists!");
	        factory.createSpreadSheet(TITLE_SPREADSHEET);
	        Log.i("ThreadGDocumentExport", "SpreadSheet '"+TITLE_SPREADSHEET+"' created");
	        spreadSheets = factory.getSpreadSheet(TITLE_SPREADSHEET, false);
		}
		
		Log.i("ThreadGDocumentExport", "Number of SpreadSheets: "+ spreadSheets.size());
		
		SpreadSheet sp = spreadSheets.get(0);
		WorkSheet workSheet = null;
		List<WorkSheet> workSheets = sp.getWorkSheet(TITLE_WORKSHEET, false);
		if(workSheets == null || workSheets.size() == 0){
			Log.i("ThreadGDocumentExport", "### Creating WorkSheet for ListFeed ###");
			workSheet = sp.addListWorkSheet(TITLE_WORKSHEET, 1, COLUMNS);
			Log.i("ThreadGDocumentExport", "WorkSheet '"+workSheet+"' created");
		}
		else {
			workSheet = workSheets.get(0);
		}

		HashMap<String, String> row_data = new HashMap<String, String>();
		row_data.put(COL_KEY_IMATRICUTION, (flashCar.getDateEnter()==null ? "" : flashCar.getValueEnter()));
		row_data.put(COL_KEY_DATE_HEURE_DEPART, (flashCar.getDateEnter()==null ? "" : new SimpleDateFormat(FORMAT_DATE_EXPORT).format(flashCar.getDateEnter())));
		row_data.put(COL_KEY_DATE_HEURE_RETOUR, (flashCar.getDateExit()==null ? "" : new SimpleDateFormat(FORMAT_DATE_EXPORT).format(flashCar.getDateExit())));
		row_data.put(COL_KEY_IDENTIFICATION_UTILISATEUR, (flashCar.getIdUser()==null ? "" : flashCar.getIdUser()));
		row_data.put(COL_KEY_KM, (flashCar.getKm()==null ? "" : flashCar.getKm()));
		
		// Add entries
		workSheet.addListRow(row_data);
		Log.i("ThreadGDocumentExport", "Data '"+row_data+"' added");

        Log.i("ThreadGDocumentExport", "ThreadGDocumentExport 14 flushMe DEFORE");
		factory.flushMe();
		Log.i("ThreadGDocumentExport", "ThreadGDocumentExport 15 flushMe AFTER");

        Log.i("ThreadGDocumentExport", "ThreadGDocumentExport 16 run END");
	}
}