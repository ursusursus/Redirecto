package sk.tuke.ursus.redirecto.provider;

import android.net.Uri;

public class RedirectoContract {

	protected static final String CONTENT_AUTHORITY = "sk.tuke.ursus.redirecto";
	protected static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	public static class Rooms {
		public static final String PATH = "rooms";
		public static final String TABLE = "rooms";

		public static final Uri CONTENT_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(PATH)
				.build();

		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_CURRENT = "current";
	}

}
