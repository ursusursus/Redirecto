package sk.tuke.ursus.redirecto.util;

import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

/**
 * Pomocn� trieda pre pr�cu s datab�zou
 * 
 * @author Vlastimil Bre�ka
 * 
 */
public class QueryUtils {

	/**
	 * Kon�tanta �iadnej miestnosti
	 */
	public static final int NO_ROOM_ID = -1;

	/**
	 * Pr�znak ne-aktu�lnej miestnosti
	 */
	private static final int FLAG_NOT_CURRENT = 0;

	/**
	 * Pr�znak aktu�lnej miestnosti
	 */
	private static final int FLAG_CURRENT = 1;

	/**
	 * Vr�ti ID aktu�lnej miestnosti
	 * 
	 * @param resolver
	 *        ContentResolver
	 * @return ID aktu�lnej miestnosti
	 */
	public static int getCurrentRoomId(ContentResolver resolver) {
		String[] columns = new String[] { Rooms.COLUMN_ID };
		String where = Rooms.COLUMN_CURRENT + "=" + FLAG_CURRENT;

		Cursor cursor = resolver.query(Rooms.CONTENT_URI, columns, where, null, null);

		int currentRoomId = NO_ROOM_ID;
		if (cursor.moveToFirst()) {
			currentRoomId = cursor.getInt(0);
		}
		cursor.close();
		return currentRoomId;
	}

	/**
	 * Nastav� ID novej lokalizovanej miestnosti
	 * 
	 * @param resolver
	 *        ContentResolver
	 * @param newCurrentRoom
	 *        ID novej miestnosti
	 * @return �spechu oper�cie
	 */
	public static boolean setNewCurrentRoomId(ContentResolver resolver, int newCurrentRoom) {
		// Set flag for new current room
		ContentValues values = new ContentValues();
		values.put(Rooms.COLUMN_CURRENT, FLAG_CURRENT);
		int count = resolver.update(Rooms.CONTENT_URI,
				values,
				Rooms.COLUMN_ID + "=" + newCurrentRoom,
				null);

		if (count <= 0) {
			return false;

		} else {
			// Reset all other rows
			values.put(Rooms.COLUMN_CURRENT, FLAG_NOT_CURRENT);
			resolver.update(Rooms.CONTENT_URI,
					values,
					Rooms.COLUMN_ID + "!=" + newCurrentRoom,
					null);

			// Notify UI
			resolver.notifyChange(Rooms.CONTENT_URI, null);
			return true;
		}

	}

}
