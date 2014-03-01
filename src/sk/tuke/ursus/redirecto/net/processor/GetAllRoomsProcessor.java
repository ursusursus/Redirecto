package sk.tuke.ursus.redirecto.net.processor;

import java.io.InputStream;

import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse.Error;
import sk.tuke.ursus.redirecto.net.RestUtils.Processor;
import sk.tuke.ursus.redirecto.net.RestUtils.Status;
import sk.tuke.ursus.redirecto.net.response.GetRoomsResponse;
import android.content.Context;
import android.os.Bundle;

public class GetAllRoomsProcessor extends Processor {

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

		// Post success
		results.putParcelableArrayList(RestService.RESULT_ROOMS, response.rooms);
		return Status.OK;
	}

}
