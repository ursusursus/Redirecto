package sk.tuke.ursus.redirecto;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import com.awaboom.ursus.agave.LOG;

public class SnifferService extends Service {

	public static final String ACTION_SNIFF = "sk.tuke.ursus.redirecto.ACTION_SNIFF";

	private ScanReceiver mReceiver;
	private WifiManager mWifiManager;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();

		if (ACTION_SNIFF.equals(action)) {
			LOG.d("ACTION_SNIFF");
			// Hook up receiver
			mReceiver = new ScanReceiver();
			IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
			registerReceiver(mReceiver, filter);

			// Start sniffing RSSI values
			mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
			mWifiManager.startScan();

		}
		return START_STICKY;
	}

	public void processSniffedResults(JSONArray results) {
		//
		stopSelf();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LOG.d("onDestroy");
		unregisterReceiver(mReceiver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class ScanReceiver extends BroadcastReceiver {

		private static final int MAX_COUNTER = 3;
		private int mCounter = 0;
		private JSONArray mCollectedJsonArray = new JSONArray();

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// Get RSSI results
			List<ScanResult> results = mWifiManager.getScanResults();
			JSONArray jsonArray = scanResultsToJsonArray(results);
			mCollectedJsonArray.put(jsonArray);

			// Do multiple measurements
			if (++mCounter < MAX_COUNTER) {
				mWifiManager.startScan();
				return;
			}
			
			// Process results
			processSniffedResults(mCollectedJsonArray);
		}

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
