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
 * Slu�ba ktor� ma na starsoti komunik�ciu so serverom, predstavuje REST API klienta
 * 
 * @author Vlastimil Bre�ka
 * 
 */
public class RestService extends AbstractRestService {

	/**
	 * URL servera
	 */
	private static final String BASE_URL = "http://brecka.absolution.sk/redirecto";

	/**
	 * URL prihl�senia
	 */
	private static final String LOGIN_URL = BASE_URL + "/login";

	/**
	 * URL odhl�senia
	 */
	private static final String LOGOUT_URL = BASE_URL + "/logout";

	/**
	 * URL lokaliz�cie
	 */
	private static final String LOCALIZE_URL = BASE_URL + "/localize";

	/**
	 * URL ru�nej lokaliz�cie
	 */
	private static final String FORCE_LOCALIZE_URL = BASE_URL + "/force_localize";

	/**
	 * URL z�skania zoznamu v�etk�ch miestnost�
	 */
	private static final String GET_ALL_ROOMS_URL = BASE_URL + "/get_all_rooms";

	/**
	 * URL z�skania pou��vate�ov�ch mienost�
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
	 * URL odoslania nov�ch odtla�kov
	 */
	private static final String NEW_FINGERPRINTS_URL = BASE_URL + "/new_fingerprints";

	/**
	 * URL z�skania mietsnost� a pr�stupov�ch bodov
	 */
	private static final String GET_ROOMS_AND_APS_URL = BASE_URL + "/get_rooms_and_aps";

	/**
	 * URL zmeny koeficientu tolerancie
	 */
	private static final String CHANGE_COEF_SETTING_URL = BASE_URL + "/change_coef_settings";

	/**
	 * K��� pre token
	 */
	public static final String RESULT_TOKEN = "token";

	/**
	 * K��� pre email
	 */
	public static final String RESULT_EMAIL = "email";

	/**
	 * K��� pre �i je admin
	 */
	public static final String RESULT_IS_ADMIN = "is_admin";

	/**
	 * K��� pre telef�nne ��slo pou��vate�a
	 */
	public static final String RESULT_DIRECTORY_NUMBER = "directory_number";

	/**
	 * K��� pre miestnosti
	 */
	public static final String RESULT_ROOMS = "rooms";

	/**
	 * K��� pre vlo�en� ID
	 */
	public static final String RESULT_INSERTED_ID = "inserted_id";

	/**
	 * K��� �iadn�ch miestnost�
	 */
	public static final String RESULT_NO_ROOMS = "no_rooms";

	/**
	 * K��� lokalizovanej miestnosti
	 */
	public static final String RESULT_LOCALIZED_ROOM = "loc_room";

	public RestService() {
		super(RestService.class.toString());
	}

	/**
	 * API volanie z�skania pou��vate�ovych miestnost�
	 * 
	 * @param context
	 *        Kontext
	 * @param token
	 *        Autentifika�n� token
	 * @param callback
	 *        Sp�tn� volanie
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
	 * API volanie z�skania v�etk�ch miestnost�
	 * 
	 * @param context
	 *        Kontext
	 * @param token
	 *        Autentifika�n� token
	 * @param callback
	 *        Sp�tn� volanie
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
	 *        Autentifika�n� token
	 * @param callback
	 *        Sp�tn� volanie
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
	 *        Autentifika�n� token
	 * @param callback
	 *        Sp�tn� volanie
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
	 * API volanie lokaliz�cie pou��vate�a
	 * 
	 * @param context
	 *        Kontext
	 * @param token
	 *        Autentifika�n� token
	 * @param currentRoomId
	 *        ID poslednej aktu�lnej miestnosti
	 * @param fingerprint
	 *        Aktu�lny odtla�ok
	 * @param callback
	 *        Sp�tn� volanie
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
	 * API volanie ru�n�ho nastavenia miestnosti ako aktu�lnej
	 * 
	 * @param context
	 *        Kontext
	 * @param roomId
	 *        ID mienosti
	 * @param token
	 *        Autentifika�n� token
	 * @param callback
	 *        Sp�tn� volanie
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
	 * API volanie prihl�senia sa
	 * 
	 * @param context
	 *        Kontext
	 * @param username
	 *        Pou��vate�sk� meno
	 * @param password
	 *        Pou�ivate�sk� heslo
	 * @param callback
	 *        Sp�tn� volanie
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
	 * API volanie odhl�senia sa
	 * 
	 * @param context
	 *        Kontext
	 * @param token
	 *        Autentifika�n� token
	 * @param callback
	 *        Sp�tn� volanie
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
	 * API volanie z�skania miestnost� a pr�stupov�ch bodov
	 * 
	 * @param context
	 *        Kontext
	 * @param token
	 *        Autentifika�n� token
	 * @param callback
	 *        Sp�tn� volanie
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
	 * API volanie odoslania nameran�ch odtla�kov
	 * 
	 * @param context
	 *        Kontext
	 * @param token
	 *        Autentifika�n� token
	 * @param roomId
	 *        ID meranej miestnosti
	 * @param fingerprints
	 *        Nameran� odtla�ky
	 * @param callback
	 *        Sp�tn� volanie
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
	 *        Autentifika�n� token
	 * @param coefSetting
	 *        Nov� koeficient
	 * @param callback
	 *        Sp�tn� volanie
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