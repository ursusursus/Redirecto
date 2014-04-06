package sk.tuke.ursus.redirecto;

import sk.tuke.ursus.redirecto.util.Utils;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.awaboom.ursus.agave.LOG;

/**
 * Glob�lny aplika�n� singleton
 * 
 * @author Vlastimil Bre�ka
 * 
 */
public class MyApplication extends Application {

	/**
	 * Autentifika�n� token
	 */
	private String mToken;

	/**
	 * Metad�ta pou��vate�a
	 */
	private String mMetadata;

	/**
	 * �i je pou��vate� administr�tor
	 */
	private boolean mAdmin;

	/**
	 * Met�da onCreate
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mToken = prefs.getString(Utils.PREFS_TOKEN_KEY, null);
		mAdmin = prefs.getBoolean(Utils.PREFS_IS_ADMIN_KEY, false);

		String username = prefs.getString(Utils.PREFS_USERNAME_KEY, null);
		String directoryNumber = prefs.getString(Utils.PREFS_DIRECTORY_NUMBER_KEY, null);
		mMetadata = Utils.dotConcat(username, directoryNumber);

		LOG.i("Token=" + mToken);
		LOG.i("Metadata=" + mMetadata);
		LOG.i("Is admin=" + mAdmin);
	}

	/**
	 * Vr�ti, �i je pou��vate� administr�tor
	 * 
	 * @return
	 */
	public boolean isAdmin() {
		return mAdmin;
	}

	/**
	 * Vr�ti autentifika�n� token
	 * 
	 * @return
	 */
	public String getToken() {
		return mToken;
	}

	/**
	 * Vr�ti pou��vate�sk� metad�ta
	 * 
	 * @return
	 */
	public String getMetadata() {
		return mMetadata;
	}

	/**
	 * Nastav� token, metad�ta a �i je pou��vate� administr�tor do pam�te aj do
	 * Preferences
	 * 
	 * @param token
	 *        Autentifika�n� token
	 * @param username
	 *        Pou��vate�sk� meno
	 * @param isAdmin
	 *        �i je pou��vate� administr�tor
	 * @param directoryNumber
	 *        Telef�nne ��slo pou��vate�a
	 */
	public void setTokenAndMetadata(String token, String username, boolean isAdmin, String directoryNumber) {
		// Write to preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.edit()
				.putString(Utils.PREFS_TOKEN_KEY, token)
				.putBoolean(Utils.PREFS_IS_ADMIN_KEY, isAdmin)
				.putString(Utils.PREFS_USERNAME_KEY, username)
				.putString(Utils.PREFS_DIRECTORY_NUMBER_KEY, directoryNumber)
				.commit();

		mToken = token;
		mAdmin = isAdmin;
		mMetadata = Utils.dotConcat(username, directoryNumber);
	}

	/**
	 * Odstr�ni token, metad�ta a �i je pou��vate� administr�tor z pam�te a z
	 * Preferences
	 */
	public void removeTokenAndMetadata() {
		// Remove token from preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.edit()
				.remove(Utils.PREFS_TOKEN_KEY)
				.remove(Utils.PREFS_USERNAME_KEY)
				.remove(Utils.PREFS_IS_ADMIN_KEY)
				.remove(Utils.PREFS_DIRECTORY_NUMBER_KEY)
				.commit();

		// Remove from memory
		mToken = null;
		mMetadata = null;
	}

}
