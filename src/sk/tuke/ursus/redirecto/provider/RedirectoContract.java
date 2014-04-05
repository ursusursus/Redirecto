package sk.tuke.ursus.redirecto.provider;

import android.net.Uri;

/**
 * Trieda datab�zov�ch kon�t�nt
 * 
 * @author Vlastimil Bre�ka
 * 
 */
public class RedirectoContract {

	/**
	 * Content authority
	 */
	public static final String CONTENT_AUTHORITY = "sk.tuke.ursus.redirecto";

	/**
	 * Z�kladn� content URI
	 */
	private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	/**
	 * Entita miestnosti
	 * 
	 * @author Vlastimil Bre�ka
	 * 
	 */
	public static class Rooms {

		/**
		 * Cesta
		 */
		public static final String PATH = "rooms";

		/**
		 * Tabu�ka
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
		 * St�pec ID
		 */
		public static final String COLUMN_ID = "_id";

		/**
		 * St�pec mena miestnosti
		 */
		public static final String COLUMN_NAME = "name";

		/**
		 * St�pec popisu
		 */
		public static final String COLUMN_DESC = "floor";

		/**
		 * St�pec ��sla klapky v miestnosti
		 */
		public static final String COLUMN_PHONE_NUMBER = "phone_number";

		/**
		 * St�pec timestampu vytvorenia
		 */
		public static final String COLUMN_CREATED_AT = "created_at";

		/**
		 * St�pec poslednej zmeny
		 */
		public static final String COLUMN_CHANGED_AT = "changed_at";

		/**
		 * St�pec �i je miestnosti aktu�lna
		 */
		public static final String COLUMN_CURRENT = "current";
	}

	/**
	 * Entita pr�stupov�ho bodu
	 * 
	 * @author Vlastimil Bre�ka
	 * 
	 */
	public static class AccessPoints {

		/**
		 * Cesta
		 */
		public static final String PATH = "accesspoints";

		/**
		 * Tabu�ka
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
		 * St�pec ID
		 */
		public static final String COLUMN_ID = "_id";

		/**
		 * St�pec SSID
		 */
		public static final String COLUMN_SSID = "ssid";

		/**
		 * St�pec BSSID
		 */
		public static final String COLUMN_BSSID = "bssid";
	}

}
