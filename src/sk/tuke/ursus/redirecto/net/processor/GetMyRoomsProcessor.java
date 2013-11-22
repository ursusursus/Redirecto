package sk.tuke.ursus.redirecto.net.processor;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import sk.tuke.ursus.redirecto.model.Room;
import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.net.RestUtils.Processor;
import sk.tuke.ursus.redirecto.net.RestUtils.Status;
import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse.Error;
import sk.tuke.ursus.redirecto.net.response.GetRoomsResponse;
import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import sk.tuke.ursus.redirecto.util.LOG;

public class GetMyRoomsProcessor extends Processor {

	@Override
	public int onProcessResponse(Context context, String contentType, InputStream stream, Bundle results) throws Exception {
		GetRoomsResponse response = RestUtils.fromJson(stream, GetRoomsResponse.class);
		if (response.hasError()) {
			// Post error
			Error error = response.error;
			results.putInt(RestUtils.ERROR_CODE, error.code);
			results.putString(RestUtils.ERROR_MESSAGE, error.message);
			LOG.d("ERROR: " + error.message);
			return Status.ERROR;
		}

		
		// Insert to database
		ArrayList<Room> rooms = response.rooms;
		ContentResolver resolver = context.getContentResolver();
		for (Room room : rooms) {
			resolver.insert(Rooms.CONTENT_URI, room.toContentValues());
		}
		
		// Notify change
		resolver.notifyChange(Rooms.CONTENT_URI, null);

		return Status.OK;
	}

}
