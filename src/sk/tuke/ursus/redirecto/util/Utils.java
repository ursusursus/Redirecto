package sk.tuke.ursus.redirecto.util;

import org.json.JSONException;
import org.json.JSONObject;

import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils.AbstractRestService;
import sk.tuke.ursus.redirecto.net.RestUtils.Methods;
import sk.tuke.ursus.redirecto.net.processor.GetMyRoomsProcessor;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class Utils {

	private static final int ALARM_ID = 1234;
	private static final long AUTOSYNC_INTERVAL = 24 * 60 * 60 * 1000; //24 hodin

	public static final String PREFS_TOKEN_KEY = "token";
	public static final String PREFS_USERNAME_KEY = "username";

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

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
					ALARM_ID,
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
