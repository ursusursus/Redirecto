package sk.tuke.ursus.redirecto.net.processor;

import java.io.InputStream;

import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse.Error;
import sk.tuke.ursus.redirecto.net.RestUtils.Processor;
import sk.tuke.ursus.redirecto.net.RestUtils.Status;
import sk.tuke.ursus.redirecto.net.response.LocalizeResponse;
import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import sk.tuke.ursus.redirecto.util.QueryUtils;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.os.Bundle;

import com.awaboom.ursus.agave.LOG;

public class LocalizeProcessor extends Processor {

	public static final String RESULT_LOCALIZED_ROOM_ID = "localized_room_id";

	@Override
	public int onProcessResponse(Context context, String contentType, InputStream stream, Bundle results)
			throws Exception {
		LocalizeResponse response = RestUtils.fromJson(stream, LocalizeResponse.class);

		if (response.hasError()) {
			// Post error
			Error error = response.error;
			results.putInt(RestUtils.ERROR_CODE, error.code);
			results.putString(RestUtils.ERROR_MESSAGE, error.message);
			return Status.ERROR;
		}

		// Update new localized room in database
		int newCurrentRoom = response.result.localizedRoomId;
		ContentResolver resolver = context.getContentResolver();
		
		boolean success = QueryUtils.setNewCurrentRoomId(resolver, newCurrentRoom);
		if (!success) {
			results.putString(RestUtils.ERROR_MESSAGE, "Lokalizovaná miestnosť nie je v zozname vašich miestností");
			return Status.ERROR;
		} else {
			return Status.OK;
		}

		/* ContentValues values = new ContentValues();
		values.put(Rooms.COLUMN_CURRENT, 1);
		int count = resolver.update(Rooms.CONTENT_URI,
				values,
				Rooms.COLUMN_ID + "=" + response.result.localizedRoomId,
				null);

		if (count > 0) {
			// Znulovat ostatne riadky
			values.put(Rooms.COLUMN_CURRENT, 0);
			resolver.update(Rooms.CONTENT_URI,
					values,
					Rooms.COLUMN_ID + "!=" + response.result.localizedRoomId,
					null);

			// Oznamit UI o zmene
			resolver.notifyChange(Rooms.CONTENT_URI, null);
			return Status.OK;

		} else {
			results.putString(RestUtils.ERROR_MESSAGE, "Lokalizovaná miestnosť nie je v zozname vašich miestností");
			return Status.ERROR;
		} */

	}

}
