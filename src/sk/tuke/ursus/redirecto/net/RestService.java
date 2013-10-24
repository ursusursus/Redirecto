package sk.tuke.ursus.redirecto.net;

import sk.tuke.ursus.redirecto.net.RestUtils.AbstractRestService;
import sk.tuke.ursus.redirecto.net.RestUtils.Callback;
import sk.tuke.ursus.redirecto.net.RestUtils.Methods;
import sk.tuke.ursus.redirecto.net.RestUtils.RequestBuilder;
import sk.tuke.ursus.redirecto.net.processor.LoginProcessor;
import sk.tuke.ursus.redirecto.net.processor.LogoutProcessor;
import android.content.Context;

/**
 * 
 * @author ursus
 * 
 */
public class RestService extends AbstractRestService {

	private static final String BASE_URL = "http://jsonrpc.app.nfd.absolution.sk";
	private static final String LOGIN_URL = BASE_URL + "/";
	private static final String LOGOUT_URL = BASE_URL + "/";

	public static final String RESULTS_TOKEN_KEY = "token";

	public RestService() {
		super(RestService.class.toString());
	}

	public static void login(Context context, String username, String password, Callback callback) {
		String params = new RestUtils.ParamBuilder()
				.addParam("username", username)
				.addParam("password", password)
				.build();

		new RequestBuilder()
				.setMethod(Methods.POST)
				.setUrl(LOGIN_URL)
				.setParams(params)
				.setCallback(callback)
				.setProcessor(new LoginProcessor())
				.execute(context, RestService.class);
	}

	public static void logout(Context context, String token, Callback callback) {
		String params = new RestUtils.ParamBuilder()
				.addParam("token", token)
				.build();

		new RequestBuilder()
				.setMethod(Methods.POST)
				.setUrl(LOGOUT_URL)
				.setParams(params)
				.setCallback(callback)
				.setProcessor(new LogoutProcessor())
				.execute(context, RestService.class);

	}

}