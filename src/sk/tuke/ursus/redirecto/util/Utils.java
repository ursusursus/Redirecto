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
 * Pomocn· trieda
 * 
 * @author Vlastimil BreËka
 * 
 */
public class Utils {

	//
	/**
	 * Kæ˙Ë autentifikaËnÈho tokenu
	 */
	public static final String PREFS_TOKEN_KEY = "token";

	/**
	 * Kæ˙Ë pouûÌvateæskÈho mena
	 */
	public static final String PREFS_USERNAME_KEY = "username";

	/**
	 * Kæ˙Ë Ëi je pouûÌvateæ administr·tor
	 */
	public static final String PREFS_IS_ADMIN_KEY = "is_admin";

	/**
	 * Kæ˙Ë telefÛnneho ËÌsla pouûÌvateæa
	 */
	public static final String PREFS_DIRECTORY_NUMBER_KEY = "directory_number";

	/**
	 * Kæ˙Ë Ëi je auto-lokaliz·cia zapnut·
	 */
	public static final String PREFS_AUTO_LOC_KEY = "auto_loc";

	/**
	 * Kæ˙Ë frekvencie lokaliz·cie
	 */
	public static final String PREFS_LOC_FREQUENCY_KEY = "loc_frequency";

	/**
	 * Kæ˙Ë maxim·lneho koeficientu tolerancie
	 */
	public static final String PREFS_MAX_ACC_COEFICIENT_KEY = "max_acc_coef";

	/**
	 * V˝chodia hodnota maxim·lneho koeficientu tolerancie
	 */
	public static final int DEFAULT_MAX_ACC_COEFICIENT = 50;

	/**
	 * VytvorÌ notifik·ciu zobrazovan˙ pri meranÌ odtlaËkov
	 * 
	 * @param context
	 *        Kontext
	 * @return Notifik·ciu
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
	 * VytvorÌ notifik·ciu zobrazovan˙ pri zbere prÌstupov˝ch bodov
	 * 
	 * @param context
	 *        Kontext
	 * @return Notifik·ciu
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
	 *        Prv˝ reùazec
	 * @param string2
	 *        Druh˝ reùazec
	 * @return Skonkatenovan˝ reùazec
	 */
	public static String dotConcat(String string1, String string2) {
		return string1 + " ∑ " + string2;
	}

}
