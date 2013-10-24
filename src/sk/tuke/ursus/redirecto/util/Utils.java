package sk.tuke.ursus.redirecto.util;

import android.os.Build;

public class Utils {
	
	public static final String PREFS_TOKEN_KEY = "token";
	
	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

}
