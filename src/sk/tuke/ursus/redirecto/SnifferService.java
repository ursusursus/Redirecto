package sk.tuke.ursus.redirecto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils.Callback;
import sk.tuke.ursus.redirecto.net.processor.LocalizeProcessor;
import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;

import com.awaboom.ursus.agave.LOG;

public class SnifferService extends Service {

	public static final String ACTION_START_RECORD_FINGERPRINTS = "sk.tuke.ursus.redirecto.ACTION_START_RECORD_FINGERPRINTS";
	public static final String ACTION_STOP_RECORD_FINGERPRINTS = "sk.tuke.ursus.redirecto.ACTION_STOP_RECORD_FINGERPRINTS";
	public static final String ACTION_SNIFF = "sk.tuke.ursus.redirecto.ACTION_SNIFF";

	public static final String EXTRA_RECORDED_ROOM = "sk.tuke.ursus.redirecto.EXTRA_RECORDED_ROOM";

	private ScanReceiver mReceiver;
	private WifiManager mWifiManager;
	private String mAction;

	private List<List<ScanResult>> mRecordedResults;
	private String mRecoredRoom;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mAction = intent.getAction();

		if (ACTION_SNIFF.equals(mAction)) {
			startScanningWifis();

		} else if (ACTION_START_RECORD_FINGERPRINTS.equals(mAction)) {
			if (intent.hasExtra(EXTRA_RECORDED_ROOM)) {
				mRecoredRoom = intent.getStringExtra(EXTRA_RECORDED_ROOM);
				mRecordedResults = new ArrayList<List<ScanResult>>();
				startScanningWifis();
			}

		} else if (ACTION_STOP_RECORD_FINGERPRINTS.equals(mAction)) {
			if (mRecoredRoom != null && mRecordedResults != null) {
				processRecordedResults();
			}
		}

		return START_STICKY;
	}

	private void startScanningWifis() {
		// Hook up receiver
		mReceiver = new ScanReceiver();
		IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(mReceiver, filter);

		// Start sniffing RSSI values
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		// Pre 4.3+ vie pasivne skenovat wifiny
		// aj ked je wifi vypnute userom
		// mWifiManager.isScanAlwaysAvailable();
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
		mWifiManager.startScan();

	}

	private void processRecordedResults() {
		MyApplication myApp = (MyApplication) getApplication();
		RestService.postRecordedFingerprints(this, myApp.getToken(), mRecoredRoom, mRecordedResults, new Callback() {

			@Override
			public void onSuccess(Bundle data) {
				LOG.d("Localize # onSuccess");
				stopSelf();
			}

			@Override
			public void onStarted() {
				LOG.d("Localize # onStarted");
			}

			@Override
			public void onException() {
				LOG.d("Localize # onException");
				stopSelf();
			}

			@Override
			public void onError(int code, String message) {
				LOG.d("Localize # onError");
				stopSelf();
			}
		});
		stopSelf();
	}

	public void processSniffedResults(JSONArray fingerprint) {
		LOG.d("JSON: " + fingerprint.toString());

		// Select current room id
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(
				Rooms.CONTENT_URI,
				new String[] { Rooms.COLUMN_ID },
				Rooms.COLUMN_CURRENT + "=1", null, null);
		
		int currentRoomId = -1;
		if (cursor.moveToFirst()) {
			currentRoomId = cursor.getInt(cursor.getColumnIndex(Rooms.COLUMN_ID));
		}
		cursor.close();
		LOG.d("CurrentRoomId: " + currentRoomId);

		// Make REST call
		MyApplication myApp = (MyApplication) getApplication();
		RestService.localize(this, myApp.getToken(), currentRoomId, fingerprint, new Callback() {

			@Override
			public void onSuccess(Bundle data) {
				LOG.d("Localize # onSuccess");
				stopSelf();
			}

			@Override
			public void onStarted() {
				LOG.d("Localize # onStarted");
			}

			@Override
			public void onException() {
				LOG.d("Localize # onException");
				stopSelf();
			}

			@Override
			public void onError(int code, String message) {
				LOG.d("Localize # onError: " + message);
				stopSelf();
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class ScanReceiver extends BroadcastReceiver {

		private static final int MAX_COUNTER = 1;
		private int mCounter = 0;
		private JSONArray mCollectedJsonArray = new JSONArray();

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (ACTION_SNIFF.equals(mAction)) {
				// Get RSSI results
				List<ScanResult> results = mWifiManager.getScanResults();
				JSONArray jsonArray = scanResultsToJsonArray(results);
				// mCollectedJsonArray.put(jsonArray);

				// Do multiple measurements
				/*if (mCounter++ < MAX_COUNTER) {
					mWifiManager.startScan();
					return;
				} */

				// Process results
				// processSniffedResults(mCollectedJsonArray);
				processSniffedResults(jsonArray);

			} else if (ACTION_START_RECORD_FINGERPRINTS.equals(mAction)) {
				List<ScanResult> results = mWifiManager.getScanResults();
				// Collect
				mRecordedResults.add(results);
			}
		}

		/**
		 * Toto asi zrusit a premiestnit do RestService
		 * 
		 * @param scanResults
		 * @return
		 */
		private JSONArray scanResultsToJsonArray(List<ScanResult> scanResults) {
			JSONArray jsonArray = new JSONArray();
			for (ScanResult scanResult : scanResults) {
				try {
					JSONObject json = new JSONObject();
					json.put("ssid", scanResult.SSID.toLowerCase());
					json.put("rssi", scanResult.level);

					jsonArray.put(json);
				} catch (JSONException e) {
				}
			}
			return jsonArray;

		}

	}

}
