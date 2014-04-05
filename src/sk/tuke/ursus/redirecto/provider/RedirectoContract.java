package sk.tuke.ursus.redirecto.provider;

import android.net.Uri;

/**
 * Trieda databázových konštánt
 * 
 * @author Vlastimil Breèka
 * 
 */
public class RedirectoContract {

	/**
	 * Content authority
	 */
	public static final String CONTENT_AUTHORITY = "sk.tuke.ursus.redirecto";

	/**
	 * Základná content URI
	 */
	private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	/**
	 * Entita miestnosti
	 * 
	 * @author Vlastimil Breèka
	 * 
	 */
	public static class Rooms {

		/**
		 * Cesta
		 */
		public static final String PATH = "rooms";

		/**
		 * Tabu¾ka
		 */
		public static final String TABLE = "rooms";

		/**
		 * Content URI
		 */
		public static final Uri CONTENT_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(PATH)
				.build();

		/**
		 * Ståpec ID
		 */
		public static final String COLUMN_ID = "_id";

		/**
		 * Ståpec mena miestnosti
		 */
		public static final String COLUMN_NAME = "name";

		/**
		 * Ståpec popisu
		 */
		public static final String COLUMN_DESC = "floor";

		/**
		 * Ståpec èísla klapky v miestnosti
		 */
		public static final String COLUMN_PHONE_NUMBER = "phone_number";

		/**
		 * Ståpec timestampu vytvorenia
		 */
		public static final String COLUMN_CREATED_AT = "created_at";

		/**
		 * Ståpec poslednej zmeny
		 */
		public static final String COLUMN_CHANGED_AT = "changed_at";

		/**
		 * Ståpec èi je miestnosti aktuálna
		 */
		public static final String COLUMN_CURRENT = "current";
	}

	/**
	 * Entita prístupového bodu
	 * 
	 * @author Vlastimil Breèka
	 * 
	 */
	public static class AccessPoints {

		/**
		 * Cesta
		 */
		public static final String PATH = "accesspoints";

		/**
		 * Tabu¾ka
		 */
		public static final String TABLE = "accesspoints";

		/**
		 * Content URI
		 */
		public static final Uri CONTENT_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(PATH)
				.build();

		/**
		 * Ståpec ID
		 */
		public static final String COLUMN_ID = "_id";

		/**
		 * Ståpec SSID
		 */
		public static final String COLUMN_SSID = "ssid";

		/**
		 * Ståpec BSSID
		 */
		public static final String COLUMN_BSSID = "bssid";
	}

}
