package sk.tuke.ursus.redirecto.util;

import org.json.JSONException;
import org.json.JSONObject;

import sk.tuke.ursus.redirecto.R;
import sk.tuke.ursus.redirecto.SnifferService;
import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils.AbstractRestService;
import sk.tuke.ursus.redirecto.net.RestUtils.Methods;
import sk.tuke.ursus.redirecto.net.processor.GetMyRoomsProcessor;
import sk.tuke.ursus.redirecto.ui.RecordActivity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.awaboom.ursus.agave.LOG;

public class Utils {

	public static final String PREFS_TOKEN_KEY = "token";
	public static final String PREFS_USERNAME_KEY = "username";
	public static final String PREFS_DIRECTORY_NUMBER_KEY = "directory_number";
	public static final String PREFS_AUTO_LOC_KEY = "auto_loc";
	public static final String PREFS_LOC_FREQUENCY_KEY = "loc_frequency";
	public static final String PREFS_ACC_COEFICIENT_KEY = "acc_coef";
	public static final int MIN_ACC_COEFICIENT = 50;
	public static final int DEFAULT_ACC_COEFICIENT = 70 - MIN_ACC_COEFICIENT;

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static Notification makeNotification(Context context) {
		Intent intent = new Intent(context, RecordActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 1234, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setTicker("")
				.setContentIntent(pendingIntent)
				.setContentTitle("Zaznamenávam odtlaèky")
				.setContentText("REDIRECTO")
				.build();

		return notification;
	}

	public static String dotConcat(String string1, String string2) {
		return string1 + " · " + string2;
	}

}
