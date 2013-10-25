package sk.tuke.ursus.redirecto;

import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RoomsCursorAdapter extends CursorAdapter {

	private LayoutInflater mInflater;

	public RoomsCursorAdapter(Context context) {
		super(context, null, FLAG_REGISTER_CONTENT_OBSERVER);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ViewHolder holder = (ViewHolder) view.getTag();

		holder.name.setText(cursor.getString(cursor.getColumnIndex(Rooms.COLUMN_NAME)));
		holder.floor.setText(cursor.getString(cursor.getColumnIndex(Rooms.COLUMN_FLOOR)));
		boolean isCurrentRoom = (cursor.getInt(cursor.getColumnIndex(Rooms.COLUMN_CURRENT)) == 1);
		if (isCurrentRoom) {
			holder.view.setBackgroundResource(R.color.accent_green);
		} else {
			holder.view.setBackgroundResource(R.color.gray);
		}

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.item_room, parent, false);
		ViewHolder holder = new ViewHolder();

		holder.name = (TextView) view.findViewById(R.id.nameTextView);
		holder.floor = (TextView) view.findViewById(R.id.floorTextView);
		holder.view = view.findViewById(R.id.view);

		view.setTag(holder);
		return view;
	}

	private class ViewHolder {

		public TextView name;
		public TextView floor;
		public View view;

	}

}
