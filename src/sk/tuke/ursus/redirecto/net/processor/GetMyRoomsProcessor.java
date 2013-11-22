package sk.tuke.ursus.redirecto.net.processor;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.ContentProviderOperation;
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
import sk.tuke.ursus.redirecto.provider.RedirectoContract;
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
			return Status.ERROR;
		}

		ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

		// Delete local
		batch.add(ContentProviderOperation.newDelete(Rooms.CONTENT_URI)
				.withSelection(null, null)
				.build());

		int rooms = response.rooms.size();
		if (rooms <= 0) {
			// Post "empty" flag
			results.putBoolean(RestService.RESULTS_NO_ROOMS_KEY, true);
			return Status.OK;
		}

		// Insert new
		for (Room room : response.rooms) {
			batch.add(ContentProviderOperation.newInsert(Rooms.CONTENT_URI)
					.withValues(room.toContentValues())
					.build());
		}

		ContentResolver resolver = context.getContentResolver();
		resolver.applyBatch(RedirectoContract.CONTENT_AUTHORITY, batch);
		resolver.notifyChange(Rooms.CONTENT_URI, null);

		return Status.OK;
	}
}
