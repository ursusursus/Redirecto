package sk.tuke.ursus.redirecto.net;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sk.tuke.ursus.redirecto.net.RestUtils.AbstractRestService;
import sk.tuke.ursus.redirecto.net.RestUtils.Callback;
import sk.tuke.ursus.redirecto.net.RestUtils.Methods;
import sk.tuke.ursus.redirecto.net.RestUtils.RequestBuilder;
import sk.tuke.ursus.redirecto.net.processor.AddMyRoomProcessor;
import sk.tuke.ursus.redirecto.net.processor.GetRoomsAndAPsProcessor;
import sk.tuke.ursus.redirecto.net.processor.GetAllRoomsProcessor;
import sk.tuke.ursus.redirecto.net.processor.GetMyRoomsProcessor;
import sk.tuke.ursus.redirecto.net.processor.LocalizeProcessor;
import sk.tuke.ursus.redirecto.net.processor.LoginProcessor;
import sk.tuke.ursus.redirecto.net.processor.LogoutProcessor;
import sk.tuke.ursus.redirecto.net.processor.RemoveMyRoomProcessor;
import sk.tuke.ursus.redirecto.net.processor.SimpleProcessor;
import android.content.Context;
import android.net.wifi.ScanResult;

import com.awaboom.ursus.agave.LOG;

/**
 * 
 * @author ursus
 * 
 */
public class RestService extends AbstractRestService {

	private static final String BASE_URL = "http://brecka.absolution.sk/redirecto";

	private static final String LOGIN_URL = BASE_URL + "/login";
	private static final String LOGOUT_URL = BASE_URL + "/logout";
	private static final String LOCALIZE_URL = BASE_URL + "/localize";
	private static final String FORCE_LOCALIZE_URL = BASE_URL + "/force_localize";
	private static final String GET_ALL_ROOMS_URL = BASE_URL + "/get_all_rooms";
	public static final String GET_MY_ROOMS_URL = BASE_URL + "/get_my_rooms";
	private static final String ADD_MY_ROOM_URL = BASE_URL + "/add_my_room";
	private static final String REMOVE_MY_ROOM_URL = BASE_URL + "/remove_my_room";
	private static final String NEW_FINGERPRINTS_URL = BASE_URL + "/new_fingerprints";
	private static final String GET_ROOMS_AND_APS_URL = BASE_URL + "/get_rooms_and_aps";

	public static final String RESULTS_TOKEN_KEY = "token";
	public static final String RESULTS_EMAIL_KEY = "email";
	public static final String RESULTS_ROOMS_KEY = "rooms";
	public static final String RESULTS_INSERTED_ID_KEY = "inserted_id";
	public static final String RESULTS_NO_ROOMS_KEY = "no_rooms";


	public RestService() {
		super(RestService.class.toString());
	}

	public static void getMyRooms(Context context, String token, Callback callback) {
		try {
			String params = new JSONObject()
					.put("token", token)
					.toString();

			new RequestBuilder()
					.setMethod(Methods.POST)
					.setUrl(GET_MY_ROOMS_URL)
					.setParams(params)
					.setCallback(callback)
					.setProcessor(new GetMyRoomsProcessor())
					.execute(context, RestService.class);
		} catch (JSONException e) {
		}
	}

	public static void getAllRooms(Context context, String token, Callback callback) {
		try {
			String params = new JSONObject()
					.put("token", token)
					.toString();

			new RequestBuilder()
					.setMethod(Methods.POST)
					.setUrl(GET_ALL_ROOMS_URL)
					.setParams(params)
					.setCallback(callback)
					.setProcessor(new GetAllRoomsProcessor())
					.execute(context, RestService.class);
		} catch (JSONException e) {
		}
	}

	public static void removeMyRoom(Context context, int id, String token, Callback callback) {
		try {
			String params = new JSONObject()
					.put("token", token)
					.put("room_id", id)
					.toString();

			new RequestBuilder()
					.setMethod(Methods.POST)
					.setUrl(REMOVE_MY_ROOM_URL)
					.setParams(params)
					.setCallback(callback)
					.setProcessor(new RemoveMyRoomProcessor())
					.execute(context, RestService.class);
		} catch (JSONException e) {
		}
	}

	public static void addMyRoom(Context context, int id, String token, Callback callback) {
		try {
			String params = new JSONObject()
					.put("token", token)
					.put("room_id", id)
					.toString();

			new RequestBuilder()
					.setMethod(Methods.POST)
					.setUrl(ADD_MY_ROOM_URL)
					.setParams(params)
					.setCallback(callback)
					.setProcessor(new AddMyRoomProcessor())
					.execute(context, RestService.class);
		} catch (JSONException e) {
		}
	}

	public static void localize(Context context, String token, int currentRoomId, JSONArray fingerprint,
			Callback callback) {
		try {
			String params = new JSONObject()
					.put("token", token)
					.put("last_room_id", currentRoomId)
					.put("fingerprint", fingerprint)
					.toString();

			new RequestBuilder()
					.setMethod(Methods.POST)
					.setUrl(LOCALIZE_URL)
					.setParams(params)
					.setCallback(callback)
					.setProcessor(new LocalizeProcessor())
					.execute(context, RestService.class);
		} catch (JSONException e) {
		}
	}

	public static void forceLocalize(Context context, int roomId, String token, Callback callback) {
		try {
			String params = new JSONObject()
					.put("token", token)
					.put("room_id", roomId)
					.toString();

			new RequestBuilder()
					.setMethod(Methods.POST)
					.setUrl(FORCE_LOCALIZE_URL)
					.setParams(params)
					.setCallback(callback)
					.setProcessor(new LocalizeProcessor())
					.execute(context, RestService.class);
		} catch (JSONException e) {
		}
	}

	public static void login(Context context, String username, String password, Callback callback) {
		try {
			String params = new JSONObject()
					.put("email", username)
					.put("password", password)
					.toString();

			new RequestBuilder()
					.setMethod(Methods.POST)
					.setUrl(LOGIN_URL)
					.setParams(params)
					.setCallback(callback)
					.setProcessor(new LoginProcessor())
					.execute(context, RestService.class);
		} catch (JSONException e) {
		}
	}

	public static void logout(Context context, String token, Callback callback) {
		try {
			String params = new JSONObject()
					.put("token", token)
					.toString();

			new RequestBuilder()
					.setMethod(Methods.POST)
					.setUrl(LOGOUT_URL)
					.setParams(params)
					.setCallback(callback)
					.setProcessor(new LogoutProcessor())
					.execute(context, RestService.class);
		} catch (JSONException e) {
		}
	}


	public static void getRoomsAndAPs(Context context, String token, Callback callback) {
		try {
			String params = new JSONObject()
					.put("token", token)
					.toString();

			new RequestBuilder()
					.setMethod(Methods.POST)
					.setUrl(GET_ROOMS_AND_APS_URL)
					.setParams(params)
					.setCallback(callback)
					.setProcessor(new GetRoomsAndAPsProcessor())
					.execute(context, RestService.class);
		} catch (JSONException e) {
		}
	}

	public static void newFingerprints(Context context, String token, int roomId,
			JSONArray fingerprints, Callback callback) {
		try {
			String params = new JSONObject()
					.put("token", token)
					.put("room_id", roomId)
					.put("fingerprints", fingerprints)
					.toString();

			new RequestBuilder()
					.setMethod(Methods.POST)
					.setUrl(NEW_FINGERPRINTS_URL)
					.setParams(params)
					.setCallback(callback)
					.setProcessor(new SimpleProcessor())
					.execute(context, RestService.class);
		} catch (JSONException e) {
		}
	}

}