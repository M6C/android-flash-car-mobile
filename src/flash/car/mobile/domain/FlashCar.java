package flash.car.mobile.domain;

import java.util.Date;

import android.os.Bundle;
import android.util.Log;

public class FlashCar {
	private String idUser = null;
	private String valueEnter = null;
	private Date dateEnter = null;
	private String valueExit = null;
	private Date dateExit = null;
	private String km = null;

	public FlashCar() {
	}

	public FlashCar(Bundle savedInstanceState) {
		Log.i("FlashCar", "constructor from savedInstanceState START");
		if (savedInstanceState!=null) {
			idUser = savedInstanceState.getString("idUser");
			Log.i("FlashCar", "constructor from savedInstanceState getString idUser:"+idUser);

			valueEnter = savedInstanceState.getString("valueEnter");
			Log.i("FlashCar", "constructor from savedInstanceState getString valueEnter:"+valueEnter);

			Long lDateEnter = savedInstanceState.getLong("dateEnterTime");
			Log.i("FlashCar", "constructor from savedInstanceState getLong dateEnterTime:"+lDateEnter);
			if (lDateEnter!=null)
				dateEnter = new Date(lDateEnter);

			valueExit = savedInstanceState.getString("valueExit");
			Log.i("FlashCar", "constructor from savedInstanceState getString valueExit:"+valueExit);

			Long lDateExit = savedInstanceState.getLong("dateExitTime");
			Log.i("FlashCar", "constructor from savedInstanceState getLong dateExitTime:"+lDateExit);
			if (lDateExit!=null)
				dateExit = new Date(lDateExit);

			km = savedInstanceState.getString("km");
			Log.i("FlashCar", "constructor from savedInstanceState getString km:"+km);
		}
		else {
			Log.i("FlashCar", "constructor from savedInstanceState is null");
		}
		Log.i("FlashCar", "constructor from savedInstanceState END");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FlashCar [idUser=" + idUser + ", valueEnter=" + valueEnter
				+ ", dateEnter=" + dateEnter + ", valueExit=" + valueExit
				+ ", dateExit=" + dateExit + ", km=" + km + "]";
	}

	public void savedInstanceState(Bundle savedInstanceState) {
		Log.i("FlashCar", "savedInstanceState Bundle:"+savedInstanceState+" START");
		if( savedInstanceState!=null) {
			Log.i("FlashCar", "savedInstanceState putString idUser:"+idUser);
			savedInstanceState.putString("idUser", idUser);
			Log.i("FlashCar", "savedInstanceState putString valueEnter:"+valueEnter);
			savedInstanceState.putString("valueEnter", valueEnter);
			if (dateEnter!=null) {
				Log.i("FlashCar", "savedInstanceState putLong dateEnter:"+dateEnter.getTime());
				savedInstanceState.putLong("dateEnterTime", dateEnter.getTime());
			}
			savedInstanceState.putString("valueExit", valueExit);
			if (dateExit!=null) {
				Log.i("FlashCar", "savedInstanceState putLong dateExit:"+dateExit.getTime());
				savedInstanceState.putLong("dateExitTime", dateExit.getTime());
			}
			savedInstanceState.putString("km", km);
		}
		Log.i("FlashCar", "savedInstanceState START");
		
	}

	/**
	 * @return the idUser
	 */
	public String getIdUser() {
		return idUser;
	}
	/**
	 * @param idUser the idUser to set
	 */
	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}
	/**
	 * @return the valueEnter
	 */
	public String getValueEnter() {
		return valueEnter;
	}
	/**
	 * @param valueEnter the valueEnter to set
	 */
	public void setValueEnter(String valueEnter) {
		this.valueEnter = valueEnter;
	}
	/**
	 * @return the dateEnter
	 */
	public Date getDateEnter() {
		return dateEnter;
	}
	/**
	 * @param dateEnter the dateEnter to set
	 */
	public void setDateEnter(Date dateEnter) {
		this.dateEnter = dateEnter;
	}
	/**
	 * @return the valueExit
	 */
	public String getValueExit() {
		return valueExit;
	}
	/**
	 * @param valueExit the valueExit to set
	 */
	public void setValueExit(String valueExit) {
		this.valueExit = valueExit;
	}
	/**
	 * @return the dateExit
	 */
	public Date getDateExit() {
		return dateExit;
	}
	/**
	 * @param dateExit the dateExit to set
	 */
	public void setDateExit(Date dateExit) {
		this.dateExit = dateExit;
	}
	/**
	 * @return the km
	 */
	public String getKm() {
		return km;
	}
	/**
	 * @param km the km to set
	 */
	public void setKm(String km) {
		this.km = km;
	}
}
