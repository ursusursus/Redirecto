package sk.tuke.ursus.redirecto;

import java.util.ArrayList;

import sk.tuke.ursus.redirecto.model.Room;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class RoomsArrayAdapter extends ArrayAdapter<Room> {
	public interface OnRoomAddedListener {
		public void onRoomAdded(int position, int id);
	}

	private LayoutInflater mInflater;
	private ArrayList<Room> mItems;
	private OnRoomAddedListener mListener;

	public RoomsArrayAdapter(Context context, ArrayList<Room> items, OnRoomAddedListener listener) {
		super(context, -1, items);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems = items;
		mListener = listener;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_room_list, parent, false);

			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.nameTextView);
			holder.floor = (TextView) convertView.findViewById(R.id.floorTextView);
			holder.add = (ImageButton) convertView.findViewById(R.id.addButton);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Room room = mItems.get(position);
		holder.name.setText(room.name);
		holder.floor.setText(room.floor);
		holder.add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mListener.onRoomAdded(position, room.id);
			}
		});

		return convertView;
	}

	static class ViewHolder {
		public TextView name;
		public TextView floor;
		public ImageButton add;
	}

}
