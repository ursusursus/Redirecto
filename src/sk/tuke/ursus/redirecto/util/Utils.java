package sk.tuke.ursus.redirecto.util;

import sk.tuke.ursus.redirecto.R;
import sk.tuke.ursus.redirecto.ui.GatherActivity;
import sk.tuke.ursus.redirecto.ui.RecordActivity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class Utils {

	public static final String PREFS_TOKEN_KEY = "token";
	public static final String PREFS_USERNAME_KEY = "username";
	public static final String PREFS_IS_ADMIN_KEY = "is_admin";
	public static final String PREFS_DIRECTORY_NUMBER_KEY = "directory_number";
	public static final String PREFS_AUTO_LOC_KEY = "auto_loc";
	public static final String PREFS_LOC_FREQUENCY_KEY = "loc_frequency";
	public static final String PREFS_MAX_ACC_COEFICIENT_KEY = "max_acc_coef";
	public static final int DEFAULT_MAX_ACC_COEFICIENT = 50;

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static Notification makeRecordingNotif(Context context) {
		Intent intent = new Intent(context, RecordActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 1234, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setTicker("")
				.setContentIntent(pendingIntent)
				.setContentTitle(context.getString(R.string.recording_fingerprints))
				.setContentText(context.getString(R.string.app_name))
				.build();

		return notification;
	}

	public static Notification makeGatheringNotif(Context context) {
		Intent intent = new Intent(context, GatherActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 1234, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setTicker("")
				.setContentIntent(pendingIntent)
				.setContentTitle(context.getString(R.string.gathering_aps))
				.setContentText(context.getString(R.string.app_name))
				.build();

		return notification;
	}

	public static String dotConcat(String string1, String string2) {
		return string1 + " · " + string2;
	}

}
