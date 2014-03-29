package sk.tuke.ursus.redirecto.ui;

import java.util.List;

import sk.tuke.ursus.redirecto.AccessPointsAdapter;
import sk.tuke.ursus.redirecto.R;
import sk.tuke.ursus.redirecto.provider.RedirectoContract.AccessPoints;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class GatherActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {

	private static final String FILTERED_ROOM = "TUNET-guest";
	private WifiManager mWifiManager;
	private ScanReceiver mReceiver;
	private AccessPointsAdapter mAdapter;
	private ContentResolver mResolver;

	protected boolean mRunning = false;
	private Button mToggleButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gather);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		mAdapter = new AccessPointsAdapter(this);

		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(mAdapter);

		mToggleButton = (Button) findViewById(R.id.button);
		mToggleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mRunning) {
					stop();
				} else {
					start();
				}
			}
		});

		// Hook up receiver
		mReceiver = new ScanReceiver();
		IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(mReceiver, filter);

		mResolver = getContentResolver();

		// Start sniffing RSSI values
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}

		getSupportLoaderManager().initLoader(0, null, this);
	}

	private void start() {
		mWifiManager.startScan();
		mToggleButton.setText("STOP");
		mRunning = true;
	}

	private void stop() {
		mToggleButton.setText("äTART");
		mRunning = false;
	}

	private void export() {
		Cursor c = mResolver.query(AccessPoints.CONTENT_URI, null, AccessPoints.COLUMN_SSID + "=\"" + FILTERED_ROOM
				+ "\"", null,
				AccessPoints.COLUMN_SSID + ","
						+ AccessPoints.COLUMN_BSSID + " ASC");

		if (!c.moveToFirst()) {
			Toast.makeText(this, "Unable to share", Toast.LENGTH_SHORT).show();
			return;
		}

		StringBuilder sbArray = new StringBuilder();
		sbArray.append("$ACCEPTED_BSSIDs = array(");

		StringBuilder sbCreate = new StringBuilder();
		sbCreate.append("Printing ");
		sbCreate.append(c.getCount());
		sbCreate.append(" APs\n\n-----------\n\n");

		sbCreate.append("CREATE TABLE IF NOT EXISTS `redirecto_fingerprint` (");
		sbCreate.append("\n`id` int(11) NOT NULL AUTO_INCREMENT,");

		while (!c.isAfterLast()) {
			String bssid = c.getString(c.getColumnIndex(AccessPoints.COLUMN_BSSID));

			sbArray.append("\n\"");
			sbArray.append(bssid);
			sbArray.append("\"");
			if (!c.isLast()) {
				sbArray.append(",");
			}

			sbCreate.append("\n`ap_");
			sbCreate.append(bssid);
			sbCreate.append("` int(11) DEFAULT '-110',");

			c.moveToNext();
		}
		sbArray.append(");");

		sbCreate.append("\n`room_id` int(11) NOT NULL,");
		sbCreate.append("\nPRIMARY KEY (`id`),");
		sbCreate.append("\nKEY `room_id` (`room_id`))");

		c.close();

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, sbCreate.toString() + "\n\n--------------\n\n" + sbArray.toString());
		intent.setType("text/plain");

		Intent chooser = Intent.createChooser(intent, "Exportovaù cez...");
		startActivity(chooser);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_gather, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;

			case R.id.action_export:
				export();
				return true;
				
			case R.id.action_clear:
				clear();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}

	}

	private void clear() {
		mResolver.delete(AccessPoints.CONTENT_URI, null, null);
		mResolver.notifyChange(AccessPoints.CONTENT_URI, null);
	}

	private class ScanReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!mRunning) {
				return;
			}

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

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(this, AccessPoints.CONTENT_URI, null, null, null, AccessPoints.COLUMN_SSID + ","
				+ AccessPoints.COLUMN_BSSID + " ASC");

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
		Toast.makeText(this, "Zozbieran˝ch prÌstupov˝ch bodov : " + cursor.getCount(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

}
