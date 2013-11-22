package sk.tuke.ursus.redirecto.net.processor;

import java.io.InputStream;

import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse.Error;
import sk.tuke.ursus.redirecto.net.RestUtils.Processor;
import sk.tuke.ursus.redirecto.net.RestUtils.Status;
import sk.tuke.ursus.redirecto.net.response.LoginResponse;
import sk.tuke.ursus.redirecto.net.response.LoginResponse.LoginResult;
import sk.tuke.ursus.redirecto.util.LOG;
import android.content.Context;
import android.os.Bundle;

public class LoginProcessor extends Processor {

	@Override
	public int onProcessResponse(Context context, String contentType, InputStream stream, Bundle results) throws Exception {
		LoginResponse loginResponse = RestUtils.fromJson(stream, LoginResponse.class);
		if (loginResponse.hasError()) {
			// Post error
			Error error = loginResponse.error;
			results.putInt(RestUtils.ERROR_CODE, error.code);
			results.putString(RestUtils.ERROR_MESSAGE, error.message);
			LOG.d("ERROR: " + error.message);
			return Status.ERROR;
		}
		
		// Post success
		LoginResult result = loginResponse.result;
		results.putString(RestService.RESULTS_TOKEN_KEY, result.token);
		results.putString(RestService.RESULTS_EMAIL_KEY, result.email);
		return Status.OK;
	}
	

}
