package sk.tuke.ursus.redirecto.util;

import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.awaboom.ursus.agave.LOG;

public class QueryUtils {

	private static final int DEFAULT_NO_ROOM_ID = -1;
	
	private static final int FLAG_NOT_CURRENT = 0;
	private static final int FLAG_CURRENT = 1;

	public static int getCurrentRoomId(ContentResolver resolver) {
		String[] columns = new String[] { Rooms.COLUMN_ID };
		String where = Rooms.COLUMN_CURRENT + "=" + FLAG_CURRENT;

		Cursor cursor = resolver.query(Rooms.CONTENT_URI, columns, where, null, null);

		int currentRoomId = DEFAULT_NO_ROOM_ID;
		if (cursor.moveToFirst()) {
			currentRoomId = cursor.getInt(0);
		}
		cursor.close();
		return currentRoomId;
	}

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
