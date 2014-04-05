package sk.tuke.ursus.redirecto.net.processor;

import java.io.InputStream;

import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse.Error;
import sk.tuke.ursus.redirecto.net.RestUtils.Processor;
import sk.tuke.ursus.redirecto.net.RestUtils.Status;
import sk.tuke.ursus.redirecto.net.response.LoginResponse;
import sk.tuke.ursus.redirecto.net.response.LoginResponse.LoginResult;
import android.content.Context;
import android.os.Bundle;

/**
 * Procesor odpovede API volania Login
 * 
 * @author Vlastimil Breèka
 * 
 */
public class LoginProcessor extends Processor {

	@Override
	public int onProcessResponse(Context context, String contentType, InputStream stream, Bundle results) throws Exception {
		LoginResponse loginResponse = RestUtils.fromJson(stream, LoginResponse.class);
		if (loginResponse.hasError()) {
			// Post error
			Error error = loginResponse.error;
			results.putInt(RestUtils.ERROR_CODE, error.code);
			results.putString(RestUtils.ERROR_MESSAGE, error.message);
			return Status.ERROR;
		}
		
		// Post success
		LoginResult result = loginResponse.result;
		results.putString(RestService.RESULT_TOKEN, result.token);
		results.putString(RestService.RESULT_EMAIL, result.email);
		results.putBoolean(RestService.RESULT_IS_ADMIN, result.isAdmin);
		results.putString(RestService.RESULT_DIRECTORY_NUMBER, result.directoryNumber);
		return Status.OK;
	}
	

}
