package sk.tuke.ursus.redirecto.ui.dialog;

import java.util.ArrayList;

import sk.tuke.ursus.redirecto.R;
import sk.tuke.ursus.redirecto.model.Room;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Dialógovı fragment, ktorı zobrazí moné miestnosti pri meraní odtlaèkov
 * 
 * @author Vlastimil Breèka
 * 
 */
public class RoomPickerDialog extends DialogFragment implements OnItemClickListener {

	/**
	 * Tag
	 */
	public static final String TAG = "room_picker_dialog";

	/**
	 * Rozhranie pre spätné volanie zvolenia miestnosti v dialógu a upovedomenia
	 * aktivity
	 * 
	 * @author Vlastimil Breèka
	 * 
	 */
	public interface OnRoomPickedListener {
		/**
		 * Miestnos bola zvolená
		 * 
		 * @param room
		 *        Zvolená miestnos
		 */
		void onRoomPicked(Room room);
	}

	/**
	 * K¾úè miestnosti
	 */
	private static final String EXTRA_ROOMS = "rooms";

	/**
	 * Naèúvaè zvolenia miestnosti
	 */
	private OnRoomPickedListener mListener;

	/**
	 * Adaptér
	 */
	private ArrayAdapter<Room> mAdapter;

	/**
	 * Nová inštancia
	 * 
	 * @param rooms
	 *        Zoznam miestností na zobrazenie
	 * @return Fragment
	 */
	public static RoomPickerDialog newInstance(ArrayList<Room> rooms) {
		Bundle args = new Bundle();
		args.putParcelableArrayList(EXTRA_ROOMS, rooms);

		RoomPickerDialog f = new RoomPickerDialog();
		f.setCancelable(false);
		f.setArguments(args);
		return f;
	}

	/**
	 * Povinnı prázdny konštruktor
	 */
	public RoomPickerDialog() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return inflater.inflate(R.layout.fragment_dialog_room_picker, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Bundle args = getArguments();
		ArrayList<Room> rooms = args.getParcelableArrayList(EXTRA_ROOMS);

		mAdapter = new ArrayAdapter<Room>(
				getActivity(),
				android.R.layout.simple_list_item_1,
				android.R.id.text1,
				rooms.toArray(new Room[rooms.size()]));

		ListView listView = (ListView) view.findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		listView.setAdapter(mAdapter);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Room room = mAdapter.getItem(position);
		if (mListener != null) {
			mListener.onRoomPicked(room);
		}
		dismiss();
	}

	/**
	 * Nastaví novı naèúvaè zvolenia miestnosti
	 * 
	 * @param l
	 *        Naèúvaè
	 */
	public void setOnRoomPickedListener(OnRoomPickedListener l) {
		mListener = l;
	}

}
