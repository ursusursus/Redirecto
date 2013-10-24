package sk.tuke.ursus.redirecto.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONObject;

import sk.tuke.ursus.redirecto.util.LOG;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;

/**
 * 
 * @author ursus
 * 
 */
public class RestUtils {

	private static final int TIMEOUT_MS = 10000;
	public static final String ERROR_CODE = "error_code";
	public static final String ERROR_MESSAGE = "error_message";

	public static abstract class AbstractRestService extends IntentService {
		private static final String ACTION = "com.whatever.AbstractRestService";

		private static final String EXTRA_PARAMS = "params";
		private static final String EXTRA_CALLBACK = "receiver";
		private static final String EXTRA_METHOD = "method";
		private static final String EXTRA_URL = "url";
		private static final String EXTRA_PROCESSOR = "processor";

		public AbstractRestService(String name) {
			super(name);
		}

		/**
		 * Background
		 */
		@Override
		protected void onHandleIntent(Intent intent) {
			String action = intent.getAction();
			if (!action.equals(ACTION)) {
				LOG.e("Wrong ACTION");
				return;
			}

			ResultReceiver callback = (ResultReceiver) intent.getParcelableExtra(EXTRA_CALLBACK);
			Processor processor = (Processor) intent.getSerializableExtra(EXTRA_PROCESSOR);

			int method = intent.getIntExtra(EXTRA_METHOD, Methods.GET);
			String url = intent.getStringExtra(EXTRA_URL);
			String params = intent.getStringExtra(EXTRA_PARAMS);

			if (callback != null) {
				callback.send(Status.RUNNING, Bundle.EMPTY);
			}

			try {
				Bundle result = new Bundle();
				//
				int resultCode = RestUtils.execute(this, method, url, params, processor, result);
				//
				if (callback != null) {
					callback.send(resultCode, result);
				}

			} catch (Exception e) {
				LOG.e(e);

				if (callback != null) {
					callback.send(Status.EXCEPTION, Bundle.EMPTY);
				}
			}
		}

	}

	public static int post(Context context, String endpoint, String params, Processor processor, Bundle results) throws Exception {
		URL url;
		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid url: " + endpoint);
		}

		int resultCode = Status.OK;
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

			if (params != null) {
				conn.setDoOutput(true);
				conn.setConnectTimeout(TIMEOUT_MS);
				conn.setRequestProperty("Content-Length", Integer.toString(params.length()));

				// Post the request
				OutputStream out = conn.getOutputStream();
				out.write(params.getBytes());
				out.close();
			}

			// Handle the response code
			/* int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			} */

			// Handle response type
			/* String received = conn.getContentType();
			String expected = processor.getExpectedContentType();
			if (received != null && !received.equals(expected)) {
				throw new IllegalArgumentException("Received wrong content type: " + received + " - Expected: " + expected);
			} */

			// Handle response
			if (processor != null) {
				String contentType = conn.getContentType();
				InputStream inputStream = conn.getInputStream();
				resultCode = processor.onProcessResponse(context, contentType, inputStream, results);
			}

		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return resultCode;

	}

	public static int get(Context context, String endpoint, String params, Processor processor, Bundle results) throws Exception {
		URL url;
		try {
			url = params != null ? new URL(endpoint + "?" + params) : new URL(endpoint);

		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}

		int resultCode = Status.OK;
		HttpURLConnection conn = null;
		try {
			// Make connection
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(TIMEOUT_MS);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

			// Handle response
			if (processor != null) {
				String contentType = conn.getContentType();
				InputStream inputStream = conn.getInputStream();
				resultCode = processor.onProcessResponse(context, contentType, inputStream, results);
			}

		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return resultCode;
	}

	public static int execute(Context context, int method, String url, String params, Processor processor, Bundle results)
			throws Exception {
		if (method == Methods.GET) {
			return get(context, url, params, processor, results);
		} else if (method == Methods.POST) {
			return post(context, url, params, processor, results);
		}
		return -1;
	}

	public static String inputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String responseLine;
		StringBuilder responseBuilder = new StringBuilder();

		while ((responseLine = bufferedReader.readLine()) != null) {
			responseBuilder.append(responseLine);
		}

		return responseBuilder.toString();
	}

	/* public static <T> T fromJson(InputStream inputStream, Class<T> clazz) {
		InputStreamReader reader = new InputStreamReader(inputStream);
		T parsedObject = new Gson().fromJson(reader, clazz);
		try {
			reader.close();
		} catch (IOException e) {
		}

		return parsedObject;
	} */

	public static boolean isTokenValid(String token) {
		if (token != null) {
			boolean isValid = true;
			if (isValid) {
				// Tu potom dorobit aj tie timestamp pre valid?
			}
			return true;
		} else {
			return false;
		}
	}

	/* public static class JsonRpcResponse {

		@SerializedName("jsonrpc")
		public String jsonRpc;
		public Error error;
		public long id;

		public boolean hasError() {
			return error != null;
		}

		public class Error {
			public int code;
			public String message;
		}

	} */

	public static class RequestBuilder {
		private int mMethod;
		private String mUrl;
		private String mParams;
		private Processor mProcessor;
		private Callback mCallback;

		public RequestBuilder setMethod(int method) {
			mMethod = method;
			return this;
		}

		public RequestBuilder setUrl(String url) {
			mUrl = url;
			return this;
		}

		public RequestBuilder setParams(String params) {
			mParams = params;
			return this;
		}

		public RequestBuilder setProcessor(Processor processor) {
			mProcessor = processor;
			return this;
		}

		public RequestBuilder setCallback(Callback callback) {
			mCallback = callback;
			return this;
		}

		public void execute(Context context, Class<? extends AbstractRestService> serviceClass) {
			if (mUrl == null) {
				LOG.e("Url is missing!");
				return;
			}

			Intent intent = new Intent(context, serviceClass)
					.setAction(AbstractRestService.ACTION)
					.putExtra(AbstractRestService.EXTRA_METHOD, mMethod)
					.putExtra(AbstractRestService.EXTRA_URL, mUrl)
					.putExtra(AbstractRestService.EXTRA_PARAMS, mParams)
					.putExtra(AbstractRestService.EXTRA_PROCESSOR, mProcessor)
					.putExtra(AbstractRestService.EXTRA_CALLBACK, mCallback);

			context.startService(intent);
		}
	}

	public static class ParamBuilder {

		private HashMap<String, String> mParams;

		public ParamBuilder() {
			mParams = new HashMap<String, String>();
		}

		public ParamBuilder addParam(String param, String value) {
			mParams.put(param, value);
			return this;
		}

		public String build() {
			StringBuilder sb = new StringBuilder();
			Iterator<Entry<String, String>> iterator = mParams.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<String, String> param = iterator.next();

				sb.append(param.getKey())
						.append('=')
						.append(param.getValue());

				if (iterator.hasNext()) {
					sb.append('&');
				}
			}

			return sb.toString();
		}
	}

	/**
	 * Response handler
	 * 
	 * @author ursus
	 * 
	 */
	public static abstract class Processor implements Serializable {
		private static final long serialVersionUID = 1L;

		/* public Processor() {
		}

		public Processor(Parcel in) {
		} */

		/**
		 * Processes inputStream from URLConnection. Happens on the background thread, so do your database operations
		 * here.
		 * 
		 * Post results to main thread by putting stuff in getResults() bundle.
		 * 
		 * @param inputStream
		 * @throws Exception
		 */
		public abstract int onProcessResponse(Context context, String contentType, InputStream stream, Bundle results) throws Exception;

		/* @Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {

		}

		public static final Parcelable.Creator<Processor> CREATOR = new Parcelable.Creator<Processor>() {

			@Override
			public Processor createFromParcel(Parcel in) {
				return new Processor(in);
			}

			@Override
			public Processor[] newArray(int size) {
				return new Processor[size];
			}
		}; */
	}

	public static abstract class JsonProcessor extends Processor {
		private static final long serialVersionUID = 1L;

		@Override
		public int onProcessResponse(Context context, String contentType, InputStream stream, Bundle results) throws Exception {
			JSONObject json = new JSONObject(RestUtils.inputStreamToString(stream));
			return onProcessResponse(context, json, results);
		}

		protected abstract int onProcessResponse(Context context, JSONObject json, Bundle results) throws Exception;
	}

	/**
	 * Callback that receives results bundle from worker IntentService
	 * 
	 * @author ursus
	 * 
	 */
	public static abstract class Callback extends ResultReceiver {

		public Callback() {
			super(putHandlerIfNeccessary());
		}

		private static Handler putHandlerIfNeccessary() {
			if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
				return new Handler();
			} else {
				return null;
			}
		}

		public int getId() {
			return 0;
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			switch (resultCode) {
				case Status.RUNNING: {
					onStarted();
					break;
				}
				case Status.OK: {
					onSuccess(resultData);
					break;
				}
				case Status.ERROR: {
					int errorCode = resultData.getInt(ERROR_CODE);
					String errorMessage = resultData.getString(ERROR_MESSAGE);
					onError(errorCode, errorMessage);
					break;
				}
				case Status.EXCEPTION: {
					onException();
					break;
				}
			}
		}

		public abstract void onSuccess(Bundle data);

		public abstract void onStarted();

		public abstract void onError(int code, String message);

		public abstract void onException();

	}

	/**
	 * Our server error exception, i.e. {"error":{"text":"Already registered!"}} Handle it by Status.INVALID response
	 * code in Callback.onResult()
	 * 
	 * @author ursus
	 * 
	 */
	public static class ServerUtilsException extends Exception {
		private static final long serialVersionUID = 1L;

		public ServerUtilsException(String message) {
			super(message);
		}
	}

	public static class ResponseType {
		public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
		public static final String APPLICATION_JSON = "application/json";
		public static final String TEXT_HTML = "text/html";
	}

	public static class Status {

		/**
		 * Everything OK
		 */
		public static final int OK = 0;

		/**
		 * Networking or some other exception
		 */
		public static final int EXCEPTION = 1;

		/**
		 * Our server response error, like {"error":{"text":"Already registered!"}}
		 */
		public static final int ERROR = 2;

		/**
		 * When request begins processing, useful for showing progress dialog
		 */
		public static final int RUNNING = 3;
		public static final int NONE = 4;
	}

	public static class Methods {
		public static final int GET = 0;
		public static final int POST = 1;
	}

	public static class ErrorCodes {
		public static final int NOT_LOGGED_IN = -32001;
	}

}
