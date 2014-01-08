package sk.tuke.ursus.redirecto.net.processor;

import java.io.InputStream;

import sk.tuke.ursus.redirecto.model.Room;
import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse.Error;
import sk.tuke.ursus.redirecto.net.RestUtils.Processor;
import sk.tuke.ursus.redirecto.net.RestUtils.Status;
import sk.tuke.ursus.redirecto.net.response.AddRoomResponse;
import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

public class AddMyRoomProcessor extends Processor {

	@Override
	public int onProcessResponse(Context context, String contentType, InputStream stream, Bundle results) throws Exception {
		AddRoomResponse response = RestUtils.fromJson(stream, AddRoomResponse.class);
		
		if (response.hasError()) {
			// Post error
			Error error = response.error;
			results.putInt(RestUtils.ERROR_CODE, error.code);
			results.putString(RestUtils.ERROR_MESSAGE, error.message);
			return Status.ERROR;
		}

		//
		Room newRoom = response.insertedRoom;
		
		//
		ContentResolver resolver = context.getContentResolver();
		resolver.insert(Rooms.CONTENT_URI, newRoom.toContentValues());
		resolver.notifyChange(Rooms.CONTENT_URI, null);
		
		results.putInt(RestService.RESULTS_INSERTED_ID_KEY, newRoom.id);
		return Status.OK;
	}

}
