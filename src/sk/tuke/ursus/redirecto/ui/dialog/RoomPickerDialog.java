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
 * Dial�gov� fragment, ktor� zobraz� mo�n� miestnosti pri meran� odtla�kov
 * 
 * @author Vlastimil Bre�ka
 * 
 */
public class RoomPickerDialog extends DialogFragment implements OnItemClickListener {

	/**
	 * Tag
	 */
	public static final String TAG = "room_picker_dialog";

	/**
	 * Rozhranie pre sp�tn� volanie zvolenia miestnosti v dial�gu a upovedomenia
	 * aktivity
	 * 
	 * @author Vlastimil Bre�ka
	 * 
	 */
	public interface OnRoomPickedListener {
		/**
		 * Miestnos� bola zvolen�
		 * 
		 * @param room
		 *        Zvolen� miestnos�
		 */
		void onRoomPicked(Room room);
	}

	/**
	 * K��� miestnosti
	 */
	private static final String EXTRA_ROOMS = "rooms";

	/**
	 * Na��va� zvolenia miestnosti
	 */
	private OnRoomPickedListener mListener;

	/**
	 * Adapt�r
	 */
	private ArrayAdapter<Room> mAdapter;

	/**
	 * Nov� in�tancia
	 * 
	 * @param rooms
	 *        Zoznam miestnost� na zobrazenie
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
	 * Povinn� pr�zdny kon�truktor
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
	 * Nastav� nov� na��va� zvolenia miestnosti
	 * 
	 * @param l
	 *        Na��va�
	 */
	public void setOnRoomPickedListener(OnRoomPickedListener l) {
		mListener = l;
	}

}
