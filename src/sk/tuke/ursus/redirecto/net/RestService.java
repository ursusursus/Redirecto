package sk.tuke.ursus.redirecto.net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sk.tuke.ursus.redirecto.net.RestUtils.AbstractRestService;
import sk.tuke.ursus.redirecto.net.RestUtils.Callback;
import sk.tuke.ursus.redirecto.net.RestUtils.Methods;
import sk.tuke.ursus.redirecto.net.RestUtils.RequestBuilder;
import sk.tuke.ursus.redirecto.net.processor.AddMyRoomProcessor;
import sk.tuke.ursus.redirecto.net.processor.GetAllRoomsProcessor;
import sk.tuke.ursus.redirecto.net.processor.GetMyRoomsProcessor;
import sk.tuke.ursus.redirecto.net.processor.GetRoomsAndAPsProcessor;
import sk.tuke.ursus.redirecto.net.processor.LocalizeProcessor;
import sk.tuke.ursus.redirecto.net.processor.LoginProcessor;
import sk.tuke.ursus.redirecto.net.processor.LogoutProcessor;
import sk.tuke.ursus.redirecto.net.processor.RemoveMyRoomProcessor;
import sk.tuke.ursus.redirecto.net.processor.SimpleProcessor;
import android.content.Context;

/**
 * Služba ktorá ma na starsoti komunikáciu so serverom, predstavuje REST API klienta
 * 
 * @author Vlastimil Breèka
 * 
 */
public class RestService extends AbstractRestService {

	/**
	 * URL servera
	 */
	private static final String BASE_URL = "http://brecka.absolution.sk/redirecto";

	/**
	 * URL prihlásenia
	 */
	private static final String LOGIN_URL = BASE_URL + "/login";

	/**
	 * URL odhlásenia
	 */
	private static final String LOGOUT_URL = BASE_URL + "/logout";

	/**
	 * URL lokalizácie
	 */
	private static final String LOCALIZE_URL = BASE_URL + "/localize";

	/**
	 * URL ruènej lokalizácie
	 */
	private static final String FORCE_LOCALIZE_URL = BASE_URL + "/force_localize";

	/**
	 * URL získania zoznamu všetkých miestností
	 */
	private static final String GET_ALL_ROOMS_URL = BASE_URL + "/get_all_rooms";

	/**
	 * URL získania používate¾ových mieností
	 */
	public static final String GET_MY_ROOMS_URL = BASE_URL + "/get_my_rooms";

	/**
	 * URL pridania miesnosti ku svojim
	 */
	private static final String ADD_MY_ROOM_URL = BASE_URL + "/add_my_room";

	/**
	 * URL odobratia miestnosti zo svojich
	 */
	private static final String REMOVE_MY_ROOM_URL = BASE_URL + "/remove_my_room";

	/**
	 * URL odoslania nových odtlaèkov
	 */
	private static final String NEW_FINGERPRINTS_URL = BASE_URL + "/new_fingerprints";

	/**
	 * URL získania mietsností a prístupových bodov
	 */
	private static final String GET_ROOMS_AND_APS_URL = BASE_URL + "/get_rooms_and_aps";

	/**
	 * URL zmeny koeficientu tolerancie
	 */
	private static final String CHANGE_COEF_SETTING_URL = BASE_URL + "/change_coef_settings";

	/**
	 * K¾úè pre token
	 */
	public static final String RESULT_TOKEN = "token";

	/**
	 * K¾úè pre email
	 */
	public static final String RESULT_EMAIL = "email";

	/**
	 * K¾úè pre èi je admin
	 */
	public static final String RESULT_IS_ADMIN = "is_admin";

	/**
	 * K¾úè pre telefónne èíslo používate¾a
	 */
	public static final String RESULT_DIRECTORY_NUMBER = "directory_number";

	/**
	 * K¾úè pre miestnosti
	 */
	public static final String RESULT_ROOMS = "rooms";

	/**
	 * K¾úè pre vložené ID
	 */
	public static final String RESULT_INSERTED_ID = "inserted_id";

	/**
	 * K¾úè žiadných miestností
	 */
	public static final String RESULT_NO_ROOMS = "no_rooms";

	/**
	 * K¾úè lokalizovanej miestnosti
	 */
	public static final String RESULT_LOCALIZED_ROOM = "loc_room";

	public RestService() {
		super(RestService.class.toString());
	}

	/**
	 * API volanie získania používate¾ovych miestností
	 * 
	 * @param context
	 *        Kontext
	 * @param token
	 *        Autentifikaèný token
	 * @param callback
	 *        Spätné volanie
	 */
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

	/**
	 * API volanie získania všetkých miestností
	 * 
	 * @param context
	 *        Kontext
	 * @param token
	 *        Autentifikaèný token
	 * @param callback
	 *        Spätné volanie
	 */
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

	/**
	 * API volanie odobratia miestnosti
	 * 
	 * @param context
	 *        Kontext
	 * @param id
	 *        ID miestnosti
	 * @param token
	 *        Autentifikaèný token
	 * @param callback
	 *        Spätné volanie
	 */
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

	/**
	 * API volanie pridania miestnosti medzi svoje
	 * 
	 * @param context
	 *        Kontext
	 * @param id
	 *        ID miestnosti
	 * @param token
	 *        Autentifikaèný token
	 * @param callback
	 *        Spätné volanie
	 */
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

	/**
	 * API volanie lokalizácie používate¾a
	 * 
	 * @param context
	 *        Kontext
	 * @param token
	 *        Autentifikaèný token
	 * @param currentRoomId
	 *        ID poslednej aktuálnej miestnosti
	 * @param fingerprint
	 *        Aktuálny odtlaèok
	 * @param callback
	 *        Spätné volanie
	 */
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

	/**
	 * API volanie ruèného nastavenia miestnosti ako aktuálnej
	 * 
	 * @param context
	 *        Kontext
	 * @param roomId
	 *        ID mienosti
	 * @param token
	 *        Autentifikaèný token
	 * @param callback
	 *        Spätné volanie
	 */
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

	/**
	 * API volanie prihlásenia sa
	 * 
	 * @param context
	 *        Kontext
	 * @param username
	 *        Používate¾ské meno
	 * @param password
	 *        Použivate¾ské heslo
	 * @param callback
	 *        Spätné volanie
	 */
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

	/**
	 * API volanie odhlásenia sa
	 * 
	 * @param context
	 *        Kontext
	 * @param token
	 *        Autentifikaèný token
	 * @param callback
	 *        Spätné volanie
	 */
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

	/**
	 * API volanie získania miestností a prístupových bodov
	 * 
	 * @param context
	 *        Kontext
	 * @param token
	 *        Autentifikaèný token
	 * @param callback
	 *        Spätné volanie
	 */
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

	/**
	 * API volanie odoslania nameraných odtlaèkov
	 * 
	 * @param context
	 *        Kontext
	 * @param token
	 *        Autentifikaèný token
	 * @param roomId
	 *        ID meranej miestnosti
	 * @param fingerprints
	 *        Namerané odtlaèky
	 * @param callback
	 *        Spätné volanie
	 */
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

	/**
	 * API volania zmeni koeficientu tolerancie
	 * 
	 * @param context
	 *        Kontext
	 * @param token
	 *        Autentifikaèný token
	 * @param coefSetting
	 *        Nový koeficient
	 * @param callback
	 *        Spätné volanie
	 */
	public static void changeCoefSetting(Context context, String token, int coefSetting, Callback callback) {
		try {
			String params = new JSONObject()
					.put("token", token)
					.put("coef_setting", coefSetting)
					.toString();

			new RequestBuilder()
					.setMethod(Methods.POST)
					.setUrl(CHANGE_COEF_SETTING_URL)
					.setParams(params)
					.setCallback(callback)
					.setProcessor(new SimpleProcessor())
					.execute(context, RestService.class);
		} catch (JSONException e) {
		}
	}

}