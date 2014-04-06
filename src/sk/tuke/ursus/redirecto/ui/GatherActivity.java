package sk.tuke.ursus.redirecto.ui;

import sk.tuke.ursus.redirecto.AccessPointsAdapter;
import sk.tuke.ursus.redirecto.R;
import sk.tuke.ursus.redirecto.SnifferService;
import sk.tuke.ursus.redirecto.provider.RedirectoContract.AccessPoints;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
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

import com.awaboom.ursus.agave.ToastUtils;

/**
 * Aktivita zberu prÌstupov˝ch bodov
 * 
 * @author Vlastimil BreËka
 * 
 */
public class GatherActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {

	/**
	 * SSID siete ktorÈ chceme vyfiltrovaù zo zoznamu vöetk˝ch zozberan˝ch
	 */
	private static final String FILTERED_SSID = "TUNET-guest";

	/**
	 * AdaptÈr
	 */
	private AccessPointsAdapter mAdapter;

	/**
	 * TlaËidlo
	 */
	private Button mToggleButton;

	/**
	 * »i prebieha zber
	 */
	protected boolean mGathering = false;

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
				if (mGathering) {
					stop();
				} else {
					start();
				}
			}
		});

		getSupportLoaderManager().initLoader(0, null, this);
	}

	/**
	 * SpustÌ zber prÌstupov˝ch bodov
	 */
	private void start() {
		Intent intent = new Intent(this, SnifferService.class);
		intent.setAction(SnifferService.ACTION_START_GATHER_APS);
		startService(intent);

		mToggleButton.setText(R.string.stop);
		mGathering = true;
	}

	/**
	 * ZastavÌ zber prÌstupov˝ch bodov
	 */
	private void stop() {
		Intent intent = new Intent(this, SnifferService.class);
		intent.setAction(SnifferService.ACTION_STOP_GATHER_APS);
		startService(intent);

		mToggleButton.setText(R.string.start);
		mGathering = false;
	}

	/**
	 * Vyexportuje z datab·zy prÌstupovÈ body podæa filtrovanÈho SSID
	 * 
	 * Vygeneruje SQL skript pre tabuæku odtlaËkov a pole akceptovan˝ch BSSID
	 */
	private void export() {
		ContentResolver r = getContentResolver();
		Cursor c = r.query(
				AccessPoints.CONTENT_URI,
				null,
				AccessPoints.COLUMN_SSID + "='" + FILTERED_SSID + "'",
				null,
				AccessPoints.COLUMN_SSID + "," + AccessPoints.COLUMN_BSSID + " ASC");

		if (!c.moveToFirst()) {
			Toast.makeText(this, R.string.unable_to_export, Toast.LENGTH_SHORT).show();
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

		Intent chooser = Intent.createChooser(intent, getString(R.string.export_via));
		startActivity(chooser);
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

	/**
	 * Vypr·zdni datab·zu
	 */
	private void clear() {
		ContentResolver r = getContentResolver();
		r.delete(AccessPoints.CONTENT_URI, null, null);
		r.notifyChange(AccessPoints.CONTENT_URI, null);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(this,
				AccessPoints.CONTENT_URI,
				null, null, null,
				AccessPoints.COLUMN_SSID + "," + AccessPoints.COLUMN_BSSID + " ASC");

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
		ToastUtils.show(this, getString(R.string.gathered_aps_count) + cursor.getCount());
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

}
