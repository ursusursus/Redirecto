package sk.tuke.ursus.redirecto;

import sk.tuke.ursus.redirecto.util.Utils;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.awaboom.ursus.agave.LOG;

public class MyApplication extends Application {

	private String mToken;
	private String mMetadata;

	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mToken = prefs.getString(Utils.PREFS_TOKEN_KEY, null);

		String username = prefs.getString(Utils.PREFS_USERNAME_KEY, null);
		String directoryNumber = prefs.getString(Utils.PREFS_DIRECTORY_NUMBER_KEY, null);
		mMetadata = Utils.dotConcat(username, directoryNumber);

		LOG.i("Token=" + mToken);
		LOG.i("Metadata=" + mMetadata);
	}

	public String getToken() {
		return mToken;
	}

	public String getMetadata() {
		return mMetadata;
	}

	public void setTokenAndMetadata(String token, String username, String directoryNumber) {
		// Write to preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.edit()
				.putString(Utils.PREFS_TOKEN_KEY, token)
				.putString(Utils.PREFS_USERNAME_KEY, username)
				.putString(Utils.PREFS_DIRECTORY_NUMBER_KEY, directoryNumber)
				.commit();

		mToken = token;
		mMetadata = Utils.dotConcat(username, directoryNumber);
	}

	public void removeTokenAndMetadata() {
		// Remove token from preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.edit()
				.remove(Utils.PREFS_TOKEN_KEY)
				.remove(Utils.PREFS_USERNAME_KEY)
				.remove(Utils.PREFS_DIRECTORY_NUMBER_KEY)
				.commit();

		// Remove from memory
		mToken = null;
		mMetadata = null;
	}

}
