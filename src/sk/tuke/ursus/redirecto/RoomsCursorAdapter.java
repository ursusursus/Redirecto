package sk.tuke.ursus.redirecto;

import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import sk.tuke.ursus.redirecto.util.Utils;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class RoomsCursorAdapter extends CursorAdapter {

	public interface RoomOverflowCallback {
		public void onRoomRemoved(int id);

		public void onLocalizedManually(int id);
	}

	private LayoutInflater mInflater;
	private RoomOverflowCallback mCallback;

	public RoomsCursorAdapter(Context c, RoomOverflowCallback callback) {
		super(c, null, FLAG_REGISTER_CONTENT_OBSERVER);
		mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCallback = callback;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final CursorViewHolder holder = (CursorViewHolder) view.getTag();

		holder.name.setText(cursor.getString(cursor.getColumnIndex(Rooms.COLUMN_NAME)));
		
		String floor = cursor.getString(cursor.getColumnIndex(Rooms.COLUMN_FLOOR));
		String phoneNumber = cursor.getString(cursor.getColumnIndex(Rooms.COLUMN_PHONE_NUMBER));
		String roomId = cursor.getString(cursor.getColumnIndex(Rooms.COLUMN_ID));
		holder.floor.setText(Utils.dotConcat(floor, phoneNumber) + " ["	+ roomId + "]");

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
		view.setTag(new CursorViewHolder(view));
		return view;
	}

	static class CursorViewHolder {

		@InjectView(R.id.nameTextView) TextView name;
		@InjectView(R.id.floorTextView) TextView floor;
		@InjectView(R.id.overflowButton) ImageButton overflow;
		@InjectView(R.id.view) View view;

		public CursorViewHolder(View view) {
			ButterKnife.inject(this, view);
		}

	}

}
