package sk.tuke.ursus.redirecto;

import sk.tuke.ursus.redirecto.util.LOG;
import sk.tuke.ursus.redirecto.util.Utils;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyApplication extends Application {

	private String mToken;

	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mToken = prefs.getString(Utils.PREFS_TOKEN_KEY, null);
		// LOG.i("Token: " + mToken);
	}

	public String getToken() {
		return mToken;
	}

	public void setToken(String token) {
		// Write to preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.edit()
				.putString(Utils.PREFS_TOKEN_KEY, token)
				.commit();

		mToken = token;
	}

	public void removeToken() {
		// Remove token from preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.edit()
				.remove(Utils.PREFS_TOKEN_KEY)
				.commit();

		// Remove from memory
		mToken = null;
	}

}
