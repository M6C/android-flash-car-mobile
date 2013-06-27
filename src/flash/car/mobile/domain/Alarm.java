package flash.car.mobile.domain;

import java.io.Serializable;
import java.util.Calendar;

import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

public class Alarm implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Time time;
	private boolean active;

	public Alarm() {
		
	}
	
	public Alarm(Bundle savedInstanceState) {
		Log.i("Alarm", "constructor from savedInstanceState START");
		if (savedInstanceState!=null) {
			long lHeure = savedInstanceState.getLong("heureTime", -1);
			Log.i("FlashCar", "constructor from savedInstanceState getLong heureTime:"+lHeure);
			if (lHeure>=0) {
				// Check if date is gone
				Calendar cal = Calendar.getInstance();
				if (lHeure<cal.getTimeInMillis()) {
					Log.i("FlashCar", "constructor from savedInstanceState getLong heureTime is gone!");
					Log.i("FlashCar", "constructor from savedInstanceState Schedule wake up from now more 10 seconds");
					// Schedule wake up from now more 10 seconds
					cal.add(Calendar.SECOND, 10);
					lHeure = cal.getTimeInMillis();
				}

				time = new Time();
				time.set(lHeure);
			}

			active = savedInstanceState.getBoolean("active");
			Log.i("FlashCar", "constructor from savedInstanceState getBoolean active:"+active);
		}
		else {
			Log.i("Alarm", "constructor from savedInstanceState is null");
		}
		Log.i("Alarm", "constructor from savedInstanceState END");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Alarm [heure=" + time + ", active=" + active + "]";
	}

	public void savedInstanceState(Bundle savedInstanceState) {
		Log.i("Alarm", "savedInstanceState Bundle:"+savedInstanceState+" START");
		if (savedInstanceState!=null) {
			if (time!=null) {
				long l = getTimeInMillis();
				Log.i("FlashCar", "savedInstanceState putLong heure:"+l);
				savedInstanceState.putLong("heureTime", l);
			}
			Log.i("FlashCar", "savedInstanceState putBoolean active:"+active);
			savedInstanceState.putBoolean("active", active);
		}
		Log.i("Alarm", "savedInstanceState END");
	}

	public long getTimeInMillis() {
		return (time==null) ? 0 : time.toMillis(true);
	}
	public void setTimeInMillis(long millis) {
		if (time!=null)
			time.set(millis);
	}
	public Time getTime() {
		return time;
	}
	public void setTime(Time time) {
		this.time = time;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
}
