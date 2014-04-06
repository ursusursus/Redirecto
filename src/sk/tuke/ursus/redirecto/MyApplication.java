package sk.tuke.ursus.redirecto;

import sk.tuke.ursus.redirecto.util.Utils;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.awaboom.ursus.agave.LOG;

/**
 * Globálny aplikaèný singleton
 * 
 * @author Vlastimil Breèka
 * 
 */
public class MyApplication extends Application {

	/**
	 * Autentifikaèný token
	 */
	private String mToken;

	/**
	 * Metadáta používate¾a
	 */
	private String mMetadata;

	/**
	 * Èi je používate¾ administrátor
	 */
	private boolean mAdmin;

	/**
	 * Metóda onCreate
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
	 * Vráti, èi je používate¾ administrátor
	 * 
	 * @return
	 */
	public boolean isAdmin() {
		return mAdmin;
	}

	/**
	 * Vráti autentifikaèný token
	 * 
	 * @return
	 */
	public String getToken() {
		return mToken;
	}

	/**
	 * Vráti používate¾ské metadáta
	 * 
	 * @return
	 */
	public String getMetadata() {
		return mMetadata;
	}

	/**
	 * Nastaví token, metadáta a èi je používate¾ administrátor do pamäte aj do
	 * Preferences
	 * 
	 * @param token
	 *        Autentifikaèný token
	 * @param username
	 *        Používate¾ské meno
	 * @param isAdmin
	 *        Èi je používate¾ administrátor
	 * @param directoryNumber
	 *        Telefónne èíslo používate¾a
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
	 * Odstráni token, metadáta a èi je používate¾ administrátor z pamäte a z
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
