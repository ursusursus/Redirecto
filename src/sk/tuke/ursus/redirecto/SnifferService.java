package sk.tuke.ursus.redirecto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils.Callback;
import sk.tuke.ursus.redirecto.provider.RedirectoContract.AccessPoints;
import sk.tuke.ursus.redirecto.util.QueryUtils;
import sk.tuke.ursus.redirecto.util.Utils;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;

import com.awaboom.ursus.agave.LOG;
import com.awaboom.ursus.agave.ToastUtils;

/**
 * Sluûba, ktor· ma nastarosti komunik·ciu s Wi-Fi hardvÈrom a vykon·va
 * naËÌtanie aktu·lnych hodnÙt prÌstupov˝ch bodov, z·znam odtlaËkov a zber
 * prÌstupov˝ch bodov
 * 
 * @author Vlastimil BreËka
 * 
 */
public class SnifferService extends Service {

	/**
	 * Akcia zaËatia z·znamu odtlaËkov
	 */
	public static final String ACTION_START_RECORD_FINGERPRINTS = "sk.tuke.ursus.redirecto.ACTION_START_RECORD_FINGERPRINTS";

	/**
	 * Akcia ukonËenia z·znamu odtlaËkov
	 */
	public static final String ACTION_STOP_RECORD_FINGERPRINTS = "sk.tuke.ursus.redirecto.ACTION_STOP_RECORD_FINGERPRINTS";

	/**
	 * Akcia lokaliz·cie a presmerovania
	 */
	public static final String ACTION_LOC_AND_FORWARD = "sk.tuke.ursus.redirecto.ACTION_LOC_AND_FORWARD";

	/**
	 * Akcia zaËatia zberu prÌstupov˝ bodov
	 */
	public static final String ACTION_START_GATHER_APS = "sk.tuke.ursus.redirecto.ACTION_START_GATHER_APS";

	/**
	 * Akcia ukonËenia prÌstupov˝ch bodov
	 */
	public static final String ACTION_STOP_GATHER_APS = "sk.tuke.ursus.redirecto.ACTION_STOP_GATHER_APS";

	/**
	 * KÛd zaËatia akcie
	 */
	public static final int CODE_ACTION_START = 0;

	/**
	 * KÛd ˙speönÈho ukonËenia akcie
	 */
	public static final int CODE_ACTION_SUCCESS = 1;

	/**
	 * KÛd chybovÈho ukonËenia akcie
	 */
	public static final int CODE_ACTION_ERROR = 2;

	/**
	 * KÛd priebeûn˝ch v˝sledkov akcie
	 */
	public static final int CODE_ACTION_PROGRESS = 3;

	/**
	 * Extra nahr·van· miestnosù
	 */
	public static final String EXTRA_RECORDED_ROOM = "sk.tuke.ursus.redirecto.EXTRA_RECORDED_ROOM";

	/**
	 * Extra receiver
	 */
	public static final String EXTRA_RECEIVER = "sk.tuke.ursus.redirecto.EXTRA_RECEIVER";

	/**
	 * Extra hodnoty
	 */
	public static final String EXTRA_VALUES = "sk.tuke.ursus.redirecto.EXTRA_VALUES";

	/**
	 * Extra chybov· spr·va
	 */
	public static final String EXTRA_ERROR = "sk.tuke.ursus.redirecto.EXTRA_ERROR_MESSAGE";

	/**
	 * PrÌjmaË Wi-Fi meranÌ
	 */
	private ScanReceiver mScanReceiver;

	/**
	 * WiFiManager
	 */
	private WifiManager mWifiManager;

	/**
	 * ResultReceiver
	 */
	private ResultReceiver mResultReceiver;

	/**
	 * Akcia
	 */
	private String mAction;

	/**
	 * Zoznam zozbieran˝ch nameran˝ch v˝sledkov skenov
	 */
	private List<List<ScanResult>> mCollectedScanResults;

	/**
	 * ID nahr·vanej miestnosti
	 */
	private int mRecoredRoomId;

	/**
	 * »i lokaliz·cia prebieha
	 */
	private boolean mLocalizing;

	/**
	 * »i prebieha zber
	 */
	private boolean mGathering;

	/**
	 * ContentResolver
	 */
	private ContentResolver mResolver;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mAction = intent.getAction();

		if (ACTION_LOC_AND_FORWARD.equals(mAction)) {
			if (mLocalizing) {
				LOG.i("Localizing already in progress");
				return START_STICKY;
			}

			if (intent.hasExtra(EXTRA_RECEIVER)) {
				mResultReceiver = (ResultReceiver) intent.getParcelableExtra(EXTRA_RECEIVER);
				mResultReceiver.send(CODE_ACTION_START, Bundle.EMPTY);
			}
			startScanningWifis();

		} else if (ACTION_START_RECORD_FINGERPRINTS.equals(mAction)) {
			if (intent.hasExtra(EXTRA_RECORDED_ROOM)) {
				mResultReceiver = (ResultReceiver) intent.getParcelableExtra(EXTRA_RECEIVER);
				mRecoredRoomId = intent.getIntExtra(EXTRA_RECORDED_ROOM, -1);
				mCollectedScanResults = new ArrayList<List<ScanResult>>();

				startScanningWifis();
				startForeground(123, Utils.makeRecordingNotif(this));
			}

		} else if (ACTION_STOP_RECORD_FINGERPRINTS.equals(mAction)) {
			if (mRecoredRoomId != -1 && mCollectedScanResults != null) {
				processRecordedResults(mRecoredRoomId, mCollectedScanResults);
			}

		} else if (ACTION_START_GATHER_APS.equals(mAction)) {
			if (mGathering) {
				return START_STICKY;
			}

			mResolver = getContentResolver();
			startScanningWifis();
			startForeground(124, Utils.makeGatheringNotif(this));

		} else if (ACTION_STOP_GATHER_APS.equals(mAction)) {
			stopSelf();
		}

		return START_STICKY;
	}

	/**
	 * ZaËne skenova Wi-Fi v okolÌ
	 */
	private void startScanningWifis() {
		// Hook up receiver
		mScanReceiver = new ScanReceiver();
		IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(mScanReceiver, filter);

		// Start sniffing RSSI values
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
		mWifiManager.startScan();
		mLocalizing = true;
	}

	/**
	 * Spracuje a odoöle nameranÈ v˝sledky odtlaËkovania
	 * 
	 * @param roomId
	 *        ID meranej miestnosti
	 * @param resultsList
	 *        Zoznam skenov
	 */
	protected void processRecordedResults(int roomId, List<List<ScanResult>> resultsList) {
		// ScanResults to JSON
		JSONArray fingerprints = new JSONArray();
		for (List<ScanResult> results : resultsList) {
			JSONArray fingerprint = scanResultsToJsonArray(results);
			fingerprints.put(fingerprint);
		}

		// Make REST call
		MyApplication myApp = (MyApplication) getApplication();
		RestService.newFingerprints(this, myApp.getToken(), roomId, fingerprints, mNewFingerprintsCallback);
	}

	/**
	 * Spracuje a odoöle nameranÈ v˝sledky lokaliz·cie
	 * 
	 * @param results
	 */
	protected void processSniffedResults(List<ScanResult> results) {
		// ScanResults to JSON
		JSONArray fingerprint = scanResultsToJsonArray(results);

		// Get current room id
		ContentResolver resolver = getContentResolver();
		int roomId = QueryUtils.getCurrentRoomId(resolver);
		LOG.i("Current room [id=" + roomId + "]");

		// Make REST call
		MyApplication myApp = (MyApplication) getApplication();
		RestService.localize(this, myApp.getToken(), roomId, fingerprint, mLocalizeCallback);
	}

	/**
	 * PremenÌ v˝sledky skenov do JSON form·tu
	 * 
	 * @param scanResults
	 * @return
	 */
	protected JSONArray scanResultsToJsonArray(List<ScanResult> scanResults) {
		JSONArray jsonArray = new JSONArray();
		for (ScanResult scanResult : scanResults) {
			try {
				JSONObject json = new JSONObject();
				json.put("bssid", scanResult.BSSID);
				json.put("rssi", scanResult.level);

				jsonArray.put(json);
			} catch (JSONException e) {
			}
		}
		return jsonArray;

	}

	/**
	 * PremenÌ v˝sledky skenov na reùazec
	 * 
	 * @param results
	 * @return
	 */
	protected String scanResultsToString(List<ScanResult> results) {
		StringBuilder sb = new StringBuilder();
		for (ScanResult result : results) {
			sb.append(result.BSSID + "=" + result.level + " ");
		}
		return sb.toString();

	}

	/* protected void showToast(final String string) {
		new Handler().post(new Runnable() {
			public void run() {
				ToastUtils.show(SnifferService.this, string);
			}
		});
	} */

	@Override
	public void onDestroy() {
		super.onDestroy();

		try {
			unregisterReceiver(mScanReceiver);
		} catch (IllegalArgumentException e) {
		}

		stopForeground(true);
		mLocalizing = false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * PrÌjmaË vysielanÌ meranÌ Wi-Fi v okolÌ
	 * 
	 * @author Vlastimil BreËka
	 * 
	 */
	private class ScanReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (ACTION_LOC_AND_FORWARD.equals(mAction)) {
				// Get RSSI results
				List<ScanResult> results = mWifiManager.getScanResults();
				processSniffedResults(results);

			} else if (ACTION_START_RECORD_FINGERPRINTS.equals(mAction)) {
				List<ScanResult> results = mWifiManager.getScanResults();

				// ScanResults to string
				// and post them back to activity
				// to display in UI
				String string = scanResultsToString(results);
				Bundle b = new Bundle();
				b.putString(EXTRA_VALUES, string);
				mResultReceiver.send(CODE_ACTION_PROGRESS, b);

				// Collect
				mCollectedScanResults.add(results);
				// Restart
				mWifiManager.startScan();

			} else if (ACTION_START_GATHER_APS.equals(mAction)) {
				List<ScanResult> results = mWifiManager.getScanResults();

				for (ScanResult r : results) {
					ContentValues values = new ContentValues();
					values.put(AccessPoints.COLUMN_SSID, r.SSID);
					values.put(AccessPoints.COLUMN_BSSID, r.BSSID);
					mResolver.insert(AccessPoints.CONTENT_URI, values);
				}

				mResolver.notifyChange(AccessPoints.CONTENT_URI, null);
				mWifiManager.startScan();
			}
		}
	}

	/**
	 * Sp‰tnÈ volanie REST API lokaliz·cie
	 */
	private Callback mLocalizeCallback = new Callback() {

		@Override
		public void onSuccess(Bundle data) {
			//
			if (mResultReceiver != null) {
				String roomName = data.getString(RestService.RESULT_LOCALIZED_ROOM);

				Bundle b = new Bundle();
				b.putString(EXTRA_VALUES, roomName);
				mResultReceiver.send(CODE_ACTION_SUCCESS, b);
			}
			//
			stopSelf();
		}

		@Override
		public void onStarted() {
		}

		@Override
		public void onException() {
			LOG.d("Localize # onException");
			//
			if (mResultReceiver != null) {
				mResultReceiver.send(CODE_ACTION_ERROR, Bundle.EMPTY);
			}
			//
			stopSelf();
		}

		@Override
		public void onError(int code, String message) {
			LOG.d("Localize # onError: " + message);

			//
			if (mResultReceiver != null) {
				Bundle b = new Bundle();
				b.putString(EXTRA_ERROR, message);
				mResultReceiver.send(CODE_ACTION_ERROR, b);
			}
			//
			stopSelf();
		}
	};

	/**
	 * Sp‰tnÈ volanie REST API nov˝ch odtlaËkov
	 */
	private Callback mNewFingerprintsCallback = new Callback() {

		@Override
		public void onSuccess(Bundle data) {
			LOG.d("NewFingerprints # onSuccess");
			//
			if (mResultReceiver != null) {
				mResultReceiver.send(CODE_ACTION_SUCCESS, Bundle.EMPTY);
			}
			//
			stopSelf();
		}

		@Override
		public void onStarted() {
			LOG.d("NewFingerprints # onStarted");
		}

		@Override
		public void onException() {
			LOG.d("NewFingerprints # onException");
			//
			if (mResultReceiver != null) {
				mResultReceiver.send(CODE_ACTION_ERROR, Bundle.EMPTY);
			}
			//
			stopSelf();
		}

		@Override
		public void onError(int code, String message) {
			LOG.d("NewFingerprints # onError: " + message);
			//
			if (mResultReceiver != null) {
				Bundle b = new Bundle();
				b.putString(EXTRA_ERROR, message);
				mResultReceiver.send(CODE_ACTION_ERROR, b);
			}
			//
			stopSelf();
		}
	};

}
