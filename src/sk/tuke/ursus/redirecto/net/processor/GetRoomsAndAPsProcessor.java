package sk.tuke.ursus.redirecto.net.processor;

import java.io.InputStream;

import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse.Error;
import sk.tuke.ursus.redirecto.net.RestUtils.Processor;
import sk.tuke.ursus.redirecto.net.RestUtils.Status;
import sk.tuke.ursus.redirecto.net.response.GetRoomsAndAPsResponse;
import sk.tuke.ursus.redirecto.net.response.GetRoomsAndAPsResponse.GetRoomsAndAPsResult;
import android.content.Context;
import android.os.Bundle;

public class GetRoomsAndAPsProcessor extends Processor {

	public static final String EXTRA_ROOMS = "rooms";
	public static final String EXTRA_APS = "aps";

	@Override
	public int onProcessResponse(Context context, String contentType, InputStream stream, Bundle results)
			throws Exception {
		GetRoomsAndAPsResponse response = RestUtils.fromJson(stream, GetRoomsAndAPsResponse.class);
		if (response.hasError()) {
			Error error = response.error;
			results.putInt(RestUtils.ERROR_CODE, error.code);
			results.putString(RestUtils.ERROR_MESSAGE, error.message);
			return Status.ERROR;
		}

		GetRoomsAndAPsResult result = response.result;
		results.putParcelableArrayList(EXTRA_ROOMS, result.rooms);
		results.putStringArrayList(EXTRA_APS, result.aps);
		return Status.OK;
	}
}
