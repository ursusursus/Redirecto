package sk.tuke.ursus.redirecto.net.processor;

import java.io.InputStream;

import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse.Error;
import sk.tuke.ursus.redirecto.net.RestUtils.Processor;
import sk.tuke.ursus.redirecto.net.RestUtils.Status;
import sk.tuke.ursus.redirecto.net.response.RemoveRoomResponse;
import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import sk.tuke.ursus.redirecto.util.LOG;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

public class RemoveMyRoomProcessor extends Processor {

	@Override
	public int onProcessResponse(Context context, String contentType, InputStream stream, Bundle results) throws Exception {
		LOG.d("RemoveMyRoomProcessor # onProcessResponse");
		RemoveRoomResponse response = RestUtils.fromJson(stream, RemoveRoomResponse.class);
		
		if (response.hasError()) {
			// Post error
			Error error = response.error;
			results.putInt(RestUtils.ERROR_CODE, error.code);
			results.putString(RestUtils.ERROR_MESSAGE, error.message);
			LOG.d("ERROR: " + error.message);
			return Status.ERROR;
		}

		// Parse deleted room id
		int roomId = Integer.valueOf(response.deletedId);
		
		// Delete it locally too
		ContentResolver resolver = context.getContentResolver();
		int count = resolver.delete(Rooms.CONTENT_URI, Rooms.COLUMN_ID + "=" + roomId, null);
		resolver.notifyChange(Rooms.CONTENT_URI, null);
		
		return Status.OK;
	}

}
