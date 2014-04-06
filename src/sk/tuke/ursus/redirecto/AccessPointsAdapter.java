package sk.tuke.ursus.redirecto;

import sk.tuke.ursus.redirecto.provider.RedirectoContract.AccessPoints;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Cursor adaptér pre ListView v GatherActivity
 * 
 * @author Vlastimil Breèka
 * 
 */
public class AccessPointsAdapter extends CursorAdapter {

	private LayoutInflater mInflater;

	public AccessPointsAdapter(Context c) {
		super(c, null, FLAG_REGISTER_CONTENT_OBSERVER);
		mInflater = LayoutInflater.from(c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ViewHolder holder = (ViewHolder) view.getTag();

		String ssid = cursor.getString(cursor.getColumnIndex(AccessPoints.COLUMN_SSID));
		String bssid = cursor.getString(cursor.getColumnIndex(AccessPoints.COLUMN_BSSID));

		holder.textView.setText(ssid + " [" + bssid + "]");
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.item_values, parent, false);
		ViewHolder holder = new ViewHolder();

		holder.textView = (TextView) view.findViewById(R.id.textView);

		view.setTag(holder);
		return view;
	}

	static class ViewHolder {
		public TextView textView;
	}

}
