package sk.tuke.ursus.redirecto;

import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RoomsAdapter extends CursorAdapter {

	private LayoutInflater mInflater;

	public RoomsAdapter(Context context) {
		super(context, null, FLAG_REGISTER_CONTENT_OBSERVER);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ViewHolder holder = (ViewHolder) view.getTag();

		holder.name.setText(cursor.getString(cursor.getColumnIndex(Rooms.COLUMN_NAME)));
		
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.item_room, parent, false); 
		ViewHolder holder = new ViewHolder();

		holder.name = (TextView) view.findViewById(R.id.nameTextView);
		
		view.setTag(holder);
		return view;
	}

	private class ViewHolder {

		public TextView name;

	}

}
