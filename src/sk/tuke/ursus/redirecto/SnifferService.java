package sk.tuke.ursus.redirecto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils.Callback;
import sk.tuke.ursus.redirecto.util.QueryUtils;
import sk.tuke.ursus.redirecto.util.Utils;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
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

public class SnifferService extends Service {

	public static final String ACTION_START_RECORD_FINGERPRINTS = "sk.tuke.ursus.redirecto.ACTION_START_RECORD_FINGERPRINTS";
	public static final String ACTION_STOP_RECORD_FINGERPRINTS = "sk.tuke.ursus.redirecto.ACTION_STOP_RECORD_FINGERPRINTS";
	public static final String ACTION_LOC_AND_FORWARD = "sk.tuke.ursus.redirecto.ACTION_LOC_AND_FORWARD";

	public static final String EXTRA_RECORDED_ROOM = "sk.tuke.ursus.redirecto.EXTRA_RECORDED_ROOM";
	public static final String EXTRA_RECEIVER = "sk.tuke.ursus.redirecto.EXTRA_RECEIVER";
	public static final String EXTRA_VALUES = "sk.tuke.ursus.redirecto.EXTRA_VALUES";

	private ScanReceiver mScanReceiver;
	private WifiManager mWifiManager;
	private ResultReceiver mResultReceiver;
	private String mAction;

	private List<List<ScanResult>> mRecordedResults;
	private int mRecoredRoomId;
	private boolean mLocalizing;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mAction = intent.getAction();

		if (ACTION_LOC_AND_FORWARD.equals(mAction)) {
			LOG.d("ACTION_LOC_AND_FORWARD");
			if(mLocalizing) {
				LOG.i("Localizing already in progress");
				return START_STICKY;
			}
			mResultReceiver = (ResultReceiver) intent.getParcelableExtra(EXTRA_RECEIVER);
			startScanningWifis();

		} else if (ACTION_START_RECORD_FINGERPRINTS.equals(mAction)) {
			if (intent.hasExtra(EXTRA_RECORDED_ROOM)) {
				mResultReceiver = (ResultReceiver) intent.getParcelableExtra(EXTRA_RECEIVER);
				mRecoredRoomId = intent.getIntExtra(EXTRA_RECORDED_ROOM, -1);
				mRecordedResults = new ArrayList<List<ScanResult>>();

				startScanningWifis();
				startForeground(123, Utils.makeNotification(this));
			}

		} else if (ACTION_STOP_RECORD_FINGERPRINTS.equals(mAction)) {
			if (mRecoredRoomId != -1 && mRecordedResults != null) {
				processRecordedResults(mRecoredRoomId, mRecordedResults);
			}
		}

		return START_STICKY;
	}

	private void startScanningWifis() {
		// Hook up receiver
		mScanReceiver = new ScanReceiver();
		IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(mScanReceiver, filter);

		// Start sniffing RSSI values
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		// Pre 4.3+ vie pasivne skenovat wifiny
		// aj ked je wifi vypnute userom
		// mWifiManager.isScanAlwaysAvailable();
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
		mWifiManager.startScan();
		mLocalizing = true;
	}

	protected void processRecordedResults(int roomId, List<List<ScanResult>> resultsList) {
		JSONArray fingerprints = new JSONArray();
		for (List<ScanResult> results : resultsList) {
			JSONArray fingerprint = scanResultsToJsonArray(results);
			fingerprints.put(fingerprint);
		}

		MyApplication myApp = (MyApplication) getApplication();
		RestService.newFingerprints(this, myApp.getToken(), roomId, fingerprints, new Callback() {

			@Override
			public void onSuccess(Bundle data) {
				LOG.d("NewFingerprints # onSuccess");
				showToast("newFingerprints # onSuccess");
				stopSelf();
			}

			@Override
			public void onStarted() {
				LOG.d("NewFingerprints # onStarted");
			}

			@Override
			public void onException() {
				LOG.d("NewFingerprints # onException");
				showToast("newFingerprints # onException");
				stopSelf();
			}

			@Override
			public void onError(int code, String message) {
				LOG.d("NewFingerprints # onError: " + message);
				showToast("newFingerprints # onError=" + message);
				stopSelf();
			}
		});
	}

	protected void showToast(final String string) {
		new Handler().post(new Runnable() {
			public void run() {
				ToastUtils.show(SnifferService.this, string);
			}
		});
	}

	protected void processSniffedResults(List<ScanResult> results) {
		JSONArray fingerprint = scanResultsToJsonArray(results);
		LOG.d(fingerprint.toString());
		
		// Get current room id
		ContentResolver resolver = getContentResolver();
		int roomId = QueryUtils.getCurrentRoomId(resolver);
		LOG.i("Current room=" + roomId);

		// Make REST call
		MyApplication myApp = (MyApplication) getApplication();
		RestService.localize(this, myApp.getToken(), roomId, fingerprint, new Callback() {

			@Override
			public void onSuccess(Bundle data) {
				//
				if (mResultReceiver != null) {
					Bundle bundle = new Bundle();
					bundle.putString("what", data.getString("what"));
					mResultReceiver.send(0, bundle);
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
				showToast("localize # onException");
				stopSelf();
			}

			@Override
			public void onError(int code, String message) {
				LOG.d("Localize # onError: " + message);

				//
				if (mResultReceiver != null) {
					Bundle bundle = new Bundle();
					bundle.putString("what", message);
					mResultReceiver.send(0, bundle);
				}
				//
				stopSelf();
			}
		});
	}

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

	private class ScanReceiver extends BroadcastReceiver {

		private Bundle mBundle;

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (ACTION_LOC_AND_FORWARD.equals(mAction)) {
				// Get RSSI results
				List<ScanResult> results = mWifiManager.getScanResults();
				// JSONArray jsonArray = scanResultsToJsonArray(results);
				// mCollectedJsonArray.put(jsonArray);

				// Do multiple measurements
				/*if (mCounter++ < MAX_COUNTER) {
					mWifiManager.startScan();
					return;
				} */

				// Process results
				// processSniffedResults(mCollectedJsonArray);
				processSniffedResults(results);

			} else if (ACTION_START_RECORD_FINGERPRINTS.equals(mAction)) {
				List<ScanResult> results = mWifiManager.getScanResults();

				// To string
				if (mScanReceiver != null) {
					StringBuilder sb = new StringBuilder();
					for (ScanResult result : results) {
						// sb.append(result.SSID + "=" + result.level + " ");
						sb.append(result.BSSID + "=" + result.level + " ");
					}

					if (mResultReceiver != null) {
						if (mBundle == null) {
							mBundle = new Bundle();
						}
						mBundle.putString(EXTRA_VALUES, sb.toString());
						mResultReceiver.send(0, mBundle);
					}
				}

				// Collect
				mRecordedResults.add(results);

				// Restart
				mWifiManager.startScan();
			}
		}

	}

}
