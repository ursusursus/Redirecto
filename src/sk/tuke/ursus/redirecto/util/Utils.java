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

/**
 * Pomocn� trieda
 * 
 * @author Vlastimil Bre�ka
 * 
 */
public class Utils {

	//
	/**
	 * K��� autentifika�n�ho tokenu
	 */
	public static final String PREFS_TOKEN_KEY = "token";

	/**
	 * K��� pou��vate�sk�ho mena
	 */
	public static final String PREFS_USERNAME_KEY = "username";

	/**
	 * K��� �i je pou��vate� administr�tor
	 */
	public static final String PREFS_IS_ADMIN_KEY = "is_admin";

	/**
	 * K��� telef�nneho ��sla pou��vate�a
	 */
	public static final String PREFS_DIRECTORY_NUMBER_KEY = "directory_number";

	/**
	 * K��� �i je auto-lokaliz�cia zapnut�
	 */
	public static final String PREFS_AUTO_LOC_KEY = "auto_loc";

	/**
	 * K��� frekvencie lokaliz�cie
	 */
	public static final String PREFS_LOC_FREQUENCY_KEY = "loc_frequency";

	/**
	 * K��� maxim�lneho koeficientu tolerancie
	 */
	public static final String PREFS_MAX_ACC_COEFICIENT_KEY = "max_acc_coef";

	/**
	 * V�chodia hodnota maxim�lneho koeficientu tolerancie
	 */
	public static final int DEFAULT_MAX_ACC_COEFICIENT = 50;

	/**
	 * Vytvor� notifik�ciu zobrazovan� pri meran� odtla�kov
	 * 
	 * @param context
	 *        Kontext
	 * @return Notifik�ciu
	 */
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

	/**
	 * Vytvor� notifik�ciu zobrazovan� pri zbere pr�stupov�ch bodov
	 * 
	 * @param context
	 *        Kontext
	 * @return Notifik�ciu
	 */
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

	/**
	 * Skonkatenuje dva stringy cez bodku
	 * 
	 * @param string1
	 *        Prv� re�azec
	 * @param string2
	 *        Druh� re�azec
	 * @return Skonkatenovan� re�azec
	 */
	public static String dotConcat(String string1, String string2) {
		return string1 + " � " + string2;
	}

}
