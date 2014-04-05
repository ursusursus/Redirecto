package sk.tuke.ursus.redirecto.util;

import org.json.JSONException;
import org.json.JSONObject;

import sk.tuke.ursus.redirecto.SnifferService;
import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils.AbstractRestService;
import sk.tuke.ursus.redirecto.net.RestUtils.Methods;
import sk.tuke.ursus.redirecto.net.processor.GetMyRoomsProcessor;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.awaboom.ursus.agave.LOG;

/**
 * Pomocná trieda pre automatizácie
 * 
 * @author Vlastimil Breèka
 * 
 */
public class AlarmUtils {

	/**
	 * Východzia frekvencia lokalizácie
	 */
	public static final String DEFAULT_LOC_FREQUENCY = "15000";

	/**
	 * Východzia hodnota zapnutej auto-lokalizácie
	 */
	public static final boolean DEFAULT_AUTO_LOC = false;

	/**
	 * Interval auto-synchronizácie
	 */
	private static final long AUTOSYNC_INTERVAL = 24 * 60 * 60 * 1000; // 24 hodin

	/**
	 * ID synchronizaèného alarmu
	 */
	private static final int SYNC_ALARM_ID = 1234;

	/**
	 * ID lokalizaèného alarmu
	 */
	private static final int LOC_ALARM_ID = 1235;

	/**
	 * Zapne automatizovanú lokalizáciu
	 * 
	 * @param context
	 *        Kontext
	 * @param prefs
	 *        Preferences
	 */
	public static void startAutoLocalization(Context context, SharedPreferences prefs) {
		Intent intent = new Intent(context, SnifferService.class)
				.setAction(SnifferService.ACTION_LOC_AND_FORWARD);

		PendingIntent pendingIntent = PendingIntent.getService(
				context,
				LOC_ALARM_ID,
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		long frequency = Long.parseLong(prefs.getString(
				Utils.PREFS_LOC_FREQUENCY_KEY,
				DEFAULT_LOC_FREQUENCY));

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setInexactRepeating(
				AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(),
				frequency,
				pendingIntent);

		LOG.i("AutoLocalization started with frequency " + frequency);
	}

	/**
	 * Vypne automatizovanú lokalizáciu
	 * 
	 * @param context
	 */
	public static void stopAutoLocalization(Context context) {
		Intent intent = new Intent(context, SnifferService.class)
				.setAction(SnifferService.ACTION_LOC_AND_FORWARD);

		PendingIntent pendingIntent = PendingIntent.getService(
				context,
				LOC_ALARM_ID,
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		LOG.d("AutoLocalization stopped");
	}

	/**
	 * Zapne automatizovanú synchronizáciu dát
	 * 
	 * @param context
	 *        Kontext
	 * @param token
	 *        Autentifikaèný token
	 */
	public static void setupAutoSync(Context context, String token) {
		try {
			String params = new JSONObject()
					.put("token", token)
					.toString();

			// Intent
			Intent intent = new Intent(context, RestService.class)
					.setAction(AbstractRestService.ACTION)
					.putExtra(AbstractRestService.EXTRA_METHOD, Methods.POST)
					.putExtra(AbstractRestService.EXTRA_URL, RestService.GET_MY_ROOMS_URL)
					.putExtra(AbstractRestService.EXTRA_PARAMS, params)
					.putExtra(AbstractRestService.EXTRA_PROCESSOR, new GetMyRoomsProcessor());

			// Pending intent
			PendingIntent pendingIntent = PendingIntent.getService(
					context,
					SYNC_ALARM_ID,
					intent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			// Setup alarm
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.setInexactRepeating(
					AlarmManager.RTC,
					System.currentTimeMillis(),
					AUTOSYNC_INTERVAL,
					pendingIntent);

			LOG.i("AutosyncAlarm set");
		} catch (JSONException e) {
		}

	}
}
