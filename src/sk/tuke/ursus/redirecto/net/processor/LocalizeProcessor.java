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
		LOG.i("New current room=" + newCurrentRoom);
		ContentResolver resolver = context.getContentResolver();

		boolean success = QueryUtils.setNewCurrentRoomId(resolver, newCurrentRoom);
		if (!success) {
			results.putString(RestUtils.ERROR_MESSAGE, "Lokalizovan· miestnosù nie je v zozname vaöich miestnostÌ");
			return Status.ERROR;
		} else {
			results.putString("what", "Lokalizovan˝ v [" + newCurrentRoom + "]");
			return Status.OK;
		}

	}

}
