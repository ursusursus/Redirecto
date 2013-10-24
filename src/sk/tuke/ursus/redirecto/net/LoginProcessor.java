package sk.tuke.ursus.redirecto.net;

import org.json.JSONObject;

import sk.tuke.ursus.redirecto.net.RestUtils.JsonProcessor;
import sk.tuke.ursus.redirecto.net.RestUtils.Status;
import android.content.Context;
import android.os.Bundle;

public class LoginProcessor extends JsonProcessor {

	@Override
	protected int onProcessResponse(Context context, JSONObject json, Bundle results) throws Exception {
		Thread.sleep(1000);
		//
		results.putString(RestService.RESULTS_TOKEN_KEY, "some_token");
		//
		return Status.OK;
	}

}
