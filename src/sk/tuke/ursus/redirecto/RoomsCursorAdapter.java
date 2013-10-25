package sk.tuke.ursus.redirecto;

import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class RoomsCursorAdapter extends CursorAdapter {

	public interface RoomOverflowCallback {
		public void onRoomRemoved(int id);

		public void onLocalizedManually(int id);
	}

	private LayoutInflater mInflater;
	private RoomOverflowCallback mCallback;

	public RoomsCursorAdapter(Context context, RoomOverflowCallback callback) {
		super(context, null, FLAG_REGISTER_CONTENT_OBSERVER);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCallback = callback;
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

		final int id = cursor.getInt(cursor.getColumnIndex(Rooms.COLUMN_ID));
		holder.overflow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PopupMenu popup = new PopupMenu(mContext, v);
				popup.inflate(R.menu.item_overflow);
				popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch (item.getItemId()) {
							case R.id.action_delete_room:
								//
								mCallback.onRoomRemoved(id);
								return true;

							case R.id.action_localize_manually:
								//
								mCallback.onLocalizedManually(id);
								return true;

							default:
								return false;
						}
					}
				});
				popup.show();
			}
		});
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.item_room, parent, false);
		ViewHolder holder = new ViewHolder();

		holder.name = (TextView) view.findViewById(R.id.nameTextView);
		holder.floor = (TextView) view.findViewById(R.id.floorTextView);
		holder.view = view.findViewById(R.id.view);
		holder.overflow = (ImageButton) view.findViewById(R.id.overflowButton);

		view.setTag(holder);
		return view;
	}

	private class ViewHolder {

		public TextView name;
		public TextView floor;
		public ImageButton overflow;
		public View view;

	}

}
