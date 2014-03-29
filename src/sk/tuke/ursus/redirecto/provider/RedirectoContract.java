package sk.tuke.ursus.redirecto.provider;

import android.net.Uri;

public class RedirectoContract {

	public static final String CONTENT_AUTHORITY = "sk.tuke.ursus.redirecto";
	private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	public static class Rooms {
		public static final String PATH = "rooms";
		public static final String TABLE = "rooms";

		public static final Uri CONTENT_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(PATH)
				.build();

		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_DESC = "floor";
		public static final String COLUMN_PHONE_NUMBER = "phone_number";
		public static final String COLUMN_CREATED_AT = "created_at";
		public static final String COLUMN_CHANGED_AT = "changed_at";
		public static final String COLUMN_CURRENT = "current";
	}
	
	public static class AccessPoints {
		public static final String PATH = "accesspoints";
		public static final String TABLE = "accesspoints";

		public static final Uri CONTENT_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(PATH)
				.build();

		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_SSID = "ssid";
		public static final String COLUMN_BSSID = "bssid";
	}

}
