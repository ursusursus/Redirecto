package sk.tuke.ursus.redirecto.net.processor;

import java.io.InputStream;

import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse.Error;
import sk.tuke.ursus.redirecto.net.RestUtils.Processor;
import sk.tuke.ursus.redirecto.net.RestUtils.Status;
import sk.tuke.ursus.redirecto.net.response.LocalizeResponse;
import sk.tuke.ursus.redirecto.util.QueryUtils;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.awaboom.ursus.agave.LOG;

/**
 * Procesor odpovede API volania Localize
 * 
 * @author Vlastimil BreËka
 * 
 */
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
		int newCurrentRoomId = response.result.localizedRoomId;
		String newCurrentRoomName = response.result.localizedRoomName;
		String newCurrentRoomDesc = response.result.localizedRoomDesc;
		LOG.i("Localized in room [name=" + newCurrentRoomName
				+ "], [desc=" + newCurrentRoomDesc
				+ "], [id=" + newCurrentRoomId + "]");

		ContentResolver resolver = context.getContentResolver();
		boolean success = QueryUtils.setNewCurrentRoomId(resolver, newCurrentRoomId);

		if (!success) {
			results.putString(
					RestUtils.ERROR_MESSAGE,
					"Lokalizovan· miestnosù nie je v zozname vaöich miestnostÌ");
			return Status.ERROR;

		} else {
			results.putString(
					RestService.RESULT_LOCALIZED_ROOM,
					newCurrentRoomName + " [" + newCurrentRoomDesc + "]");
			return Status.OK;
		}

	}

}
