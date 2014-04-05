package sk.tuke.ursus.redirecto;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

import sk.tuke.ursus.redirecto.model.Room;
import sk.tuke.ursus.redirecto.util.Utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * ArrayAdapter pre ListView v NewRoomFragment
 * 
 * @author Vlastimil Breèka
 * 
 */
public class RoomsArrayAdapter extends ArrayAdapter<Room> {

	/**
	 * Rozhranie pre spätné volanie pridanej miesnosti
	 * 
	 * @author Vlastimil Breèka
	 * 
	 */
	public interface OnRoomAddedListener {
		public void onRoomAdded(int position, int id);
	}

	/**
	 * Inflater
	 */
	private LayoutInflater mInflater;

	/**
	 * Zoznam miestností
	 */
	private ArrayList<Room> mItems;

	/**
	 * Kópia zoznamu miestností
	 */
	private ArrayList<Room> mItemsCopy;

	/**
	 * Spätné volanie
	 */
	private OnRoomAddedListener mListener;

	/**
	 * Konštruktor
	 * 
	 * @param context
	 *        Kontext
	 * @param items
	 *        Zoznam miestností
	 * @param listener
	 *        Spätné volanie
	 */
	public RoomsArrayAdapter(Context context, ArrayList<Room> items, OnRoomAddedListener listener) {
		super(context, -1, items);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems = items;
		mItemsCopy = items;
		mListener = listener;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		ArrayViewHolder holder;
		if (view == null) {
			view = mInflater.inflate(R.layout.item_room_list, parent, false);
			holder = new ArrayViewHolder(view);
			view.setTag(holder);

		} else {
			holder = (ArrayViewHolder) view.getTag();
		}

		final Room room = mItems.get(position);
		holder.name.setText(room.name);
		holder.floor.setText(Utils.dotConcat(room.description, room.phoneNumber));

		if (room.isAdded()) {
			holder.add.setEnabled(false);
			holder.add.setImageResource(R.drawable.ic_action_success);
			holder.add.setOnClickListener(null);

		} else {
			holder.add.setEnabled(true);
			holder.add.setImageResource(R.drawable.ic_action_new);
			holder.add.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mListener.onRoomAdded(position, room.id);
				}
			});
		}

		return view;
	}

	/**
	 * Vráti objekt miestnosti pod¾a ID
	 * 
	 * @param id
	 *        miestnosti
	 * @return objekt miestnosti
	 */
	public Room roomById(int id) {
		for (Room room : mItems) {
			if (room.id == id) {
				return room;
			}
		}
		return null;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	/**
	 * Vyfiltruje obsah adaptéra pod¾a k¾úèového slova
	 * 
	 * @param keyword
	 *        k¾úèové slovo
	 */
	public void filter(String keyword) {
		ArrayList<Room> filteredItems = new ArrayList<Room>();
		for (Room room : mItemsCopy) {
			if (room.name.toLowerCase().startsWith(keyword.toLowerCase())) {
				filteredItems.add(room);
			}
		}

		mItems = filteredItems;
		notifyDataSetChanged();

	}

	static class ArrayViewHolder {

		@InjectView(R.id.nameTextView) TextView name;
		@InjectView(R.id.floorTextView) TextView floor;
		@InjectView(R.id.addButton) ImageButton add;

		public ArrayViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}

}
