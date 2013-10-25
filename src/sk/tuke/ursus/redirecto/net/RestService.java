package sk.tuke.ursus.redirecto.net;

import sk.tuke.ursus.redirecto.net.RestUtils.AbstractRestService;
import sk.tuke.ursus.redirecto.net.RestUtils.Callback;
import sk.tuke.ursus.redirecto.net.RestUtils.Methods;
import sk.tuke.ursus.redirecto.net.RestUtils.RequestBuilder;
import sk.tuke.ursus.redirecto.net.processor.GetAllRoomsProcessor;
import sk.tuke.ursus.redirecto.net.processor.LocalizeManuallyProcessor;
import sk.tuke.ursus.redirecto.net.processor.LocalizeProcessor;
import sk.tuke.ursus.redirecto.net.processor.LoginProcessor;
import sk.tuke.ursus.redirecto.net.processor.LogoutProcessor;
import sk.tuke.ursus.redirecto.net.processor.GetMyRoomsProcessor;
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
	private static final String LOCALIZE_URL = BASE_URL + "/";
	private static final String LOCALIZE_MANUALLY_URL = BASE_URL + "/";
	private static final String GET_ALL_ROOMS_URL = BASE_URL + "/";
	private static final String GET_MY_ROOMS_URL = BASE_URL + "/";

	public static final String RESULTS_TOKEN_KEY = "token";
	public static final String RESULTS_ROOMS_KEY = "rooms";

	public RestService() {
		super(RestService.class.toString());
	}
	
	public static void getMyRooms(Context context, String token, Callback callback) {
		String params = new RestUtils.ParamBuilder()
				.addParam("token", token)
				.build();

		new RequestBuilder()
				.setMethod(Methods.POST)
				.setUrl(GET_MY_ROOMS_URL)
				.setParams(params)
				.setCallback(callback)
				.setProcessor(new GetMyRoomsProcessor())
				.execute(context, RestService.class);
	}

	public static void getAllRooms(Context context, String token, Callback callback) {
		String params = new RestUtils.ParamBuilder()
				.addParam("token", token)
				.build();

		new RequestBuilder()
				.setMethod(Methods.POST)
				.setUrl(GET_ALL_ROOMS_URL)
				.setParams(params)
				.setCallback(callback)
				.setProcessor(new GetAllRoomsProcessor())
				.execute(context, RestService.class);
	}

	public static void localize(Context context, String token, Callback callback) {
		// Tu budu dBm vsetkych wificiek
		// takze bude parameter json
		// teda asi lepsie ak bude vsade,
		// ako v robote
		String params = new RestUtils.ParamBuilder()
				.addParam("token", token)
				.build();

		new RequestBuilder()
				.setMethod(Methods.POST)
				.setUrl(LOCALIZE_URL)
				.setParams(params)
				.setCallback(callback)
				.setProcessor(new LocalizeProcessor())
				.execute(context, RestService.class);
	}

	public static void localizeManually(Context context, int id, String token, Callback callback) {
		String params = new RestUtils.ParamBuilder()
				.addParam("id", String.valueOf(id))
				.addParam("token", token)
				.build();

		new RequestBuilder()
				.setMethod(Methods.POST)
				.setUrl(LOCALIZE_MANUALLY_URL)
				.setParams(params)
				.setCallback(callback)
				.setProcessor(new LocalizeManuallyProcessor())
				.execute(context, RestService.class);
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