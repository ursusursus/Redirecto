package sk.tuke.ursus.redirecto;

import sk.tuke.ursus.redirecto.util.Utils;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.awaboom.ursus.agave.LOG;

public class MyApplication extends Application {

	private String mToken;
	private String mUsername;

	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mToken = prefs.getString(Utils.PREFS_TOKEN_KEY, null);
		mUsername = prefs.getString(Utils.PREFS_USERNAME_KEY, null);

		LOG.i("Token: " + mToken);
	}

	public String getToken() {
		return mToken;
	}

	public String getUsername() {
		return mUsername;
	}

	public void setTokenAndUsername(String token, String username) {
		// Write to preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.edit()
				.putString(Utils.PREFS_TOKEN_KEY, token)
				.putString(Utils.PREFS_USERNAME_KEY, username)
				.commit();

		mToken = token;
		mUsername = username;
	}

	public void removeTokenAndUsername() {
		// Remove token from preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.edit()
				.remove(Utils.PREFS_TOKEN_KEY)
				.remove(Utils.PREFS_USERNAME_KEY)
				.commit();

		// Remove from memory
		mToken = null;
		mUsername = null;
	}

}
