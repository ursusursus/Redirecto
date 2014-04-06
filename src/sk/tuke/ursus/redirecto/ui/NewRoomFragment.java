package sk.tuke.ursus.redirecto.ui;

import java.util.ArrayList;

import sk.tuke.ursus.redirecto.MyApplication;
import sk.tuke.ursus.redirecto.R;
import sk.tuke.ursus.redirecto.RoomsArrayAdapter;
import sk.tuke.ursus.redirecto.RoomsArrayAdapter.OnRoomAddedListener;
import sk.tuke.ursus.redirecto.model.Room;
import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.awaboom.ursus.agave.ToastUtils;

/**
 * Fragment pridania novej miestnosti, pouûÌvateæ si vyber· zo zoznamu
 * dostupn˝ch a prid·va ich tak do svojho zoznamu
 * 
 * @author Vlastimil BreËka
 * 
 */
public class NewRoomFragment extends Fragment {

	/**
	 * Kæ˙Ë miestnosti
	 */
	private static final String EXTRA_ROOMS_KEY = "rooms";

	/**
	 * Kontext
	 */
	private FragmentActivity mContext;

	/**
	 * AplikaËn˝ singleton
	 */
	private MyApplication mApp;

	/**
	 * Zoznam miestnostÌ
	 */
	private ArrayList<Room> mRooms;

	/**
	 * AdaptÈr
	 */
	private RoomsArrayAdapter mAdapter;

	/**
	 * ListView
	 */
	@InjectView(R.id.listView) ListView mListView;

	/**
	 * EditText filtr·cie obsahu
	 */
	@InjectView(R.id.filterEditText) EditText mFilterEditText;

	/**
	 * ProgressBar
	 */
	@InjectView(R.id.progressBar) ProgressBar mProgressBar;

	/**
	 * TextView chyby
	 */
	@InjectView(R.id.errorTextView) TextView mErrorTextView;

	/**
	 * VytvorÌ nov˙ inötanciu
	 * 
	 * @return nov˝ fragment
	 */
	public static NewRoomFragment newInstance() {
		return new NewRoomFragment();
	}

	/**
	 * Pr·zdny konötruktor
	 */
	public NewRoomFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		mContext = getActivity();
		mApp = (MyApplication) getActivity().getApplication();

		if (savedInstanceState != null) {
			mRooms = savedInstanceState.getParcelableArrayList(EXTRA_ROOMS_KEY);

		} else {
			mRooms = new ArrayList<Room>();
			fetchAllRooms();
		}
	}

	/**
	 * API volanie pre vr·tenie vöetk˝ch miestnostÌ
	 */
	private void fetchAllRooms() {
		RestService.getAllRooms(mContext, mApp.getToken(), mAllRoomsCallback);
	}

	/**
	 * API volanie pre pridanie miestnosti medzi svojes
	 * 
	 * @param id
	 *        ID miestnosti ktor˙ chceme pridaù
	 */
	protected void addMyNewRoom(int id) {
		RestService.addMyRoom(mApp, id, mApp.getToken(), mAddMyRoomCallback);
	}

	/**
	 * Skryje ProgressBar
	 */
	protected void hideProgressBar() {
		mProgressBar.setVisibility(View.GONE);
	}

	/**
	 * ZobrazÌ ProgressBar
	 */
	protected void showProgressBar() {
		mProgressBar.setVisibility(View.VISIBLE);
	}

	/**
	 * ZobrazÌ chybu. Ak je uû zoznam zobrazen˝, zobrazÌ toast, inak zobrazÌ
	 * TextView
	 * 
	 * @param message
	 *        Hlavn· chybov· spr·va
	 * @param submessage
	 *        Vedæajöia chybov· spr·va
	 */
	protected void showError(String message, String submessage) {
		if (mRooms.size() == 0) {
			mErrorTextView.setVisibility(View.VISIBLE);
			if (submessage != null) {
				mErrorTextView.setText(message + "\n" + submessage);
			} else {
				mErrorTextView.setText(message);
			}
		} else {
			ToastUtils.showError(mContext, message, submessage);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_new_room, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.inject(this, view);

		mAdapter = new RoomsArrayAdapter(mContext, mRooms, mRoomAddedListener);
		mListView.setAdapter(mAdapter);
		mFilterEditText.addTextChangedListener(mTextChangedListener);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_refresh) {
			fetchAllRooms();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_new_room, menu);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(EXTRA_ROOMS_KEY, mRooms);
	}

	/**
	 * EditText naË˙vaË, naË˙va pre zmeny v EditText a tak dynamicky filtruje
	 * zoznam po kaûdom znaku
	 */
	private TextWatcher mTextChangedListener = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence cs, int start, int before, int count) {
			mAdapter.filter(cs.toString());
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	/**
	 * Sp‰tnÈ volanie pridania miestnosti z ListView
	 */
	private OnRoomAddedListener mRoomAddedListener = new OnRoomAddedListener() {

		@Override
		public void onRoomAdded(int position, int id) {
			addMyNewRoom(id);
		}
	};

	/**
	 * Sp‰tnÈ volanie API volania zobrazenia vöetk˝ch miestnostÌ
	 */
	private RestUtils.Callback mAllRoomsCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			hideProgressBar();

			ArrayList<Room> newRooms = data.getParcelableArrayList(RestService.RESULT_ROOMS);
			if (newRooms != null) {

				if (newRooms.size() == 0) {
					mErrorTextView.setText(R.string.no_rooms_to_add);
					mErrorTextView.setVisibility(View.VISIBLE);
				} else {
					mErrorTextView.setVisibility(View.GONE);
				}

				mAdapter.clear();
				for (Room room : newRooms) {
					mAdapter.add(room);
				}

				mRooms = newRooms;
			}
		}

		@Override
		public void onStarted() {
			showProgressBar();
		}

		@Override
		public void onException() {
			hideProgressBar();
			showError(getString(R.string.unable_to_load_rooms), null);
		}

		@Override
		public void onError(int code, String message) {
			hideProgressBar();
			showError(getString(R.string.unable_to_load_rooms), message);
		}
	};

	/**
	 * Sp‰tnÈ volanie API volania pridania miestnosti
	 */
	private RestUtils.Callback mAddMyRoomCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			int newId = data.getInt(RestService.RESULT_INSERTED_ID);
			Room newRoom = mAdapter.roomById(newId);
			if (newRoom != null) {
				newRoom.setAdded(true);
				mAdapter.notifyDataSetChanged();
			}
		}

		@Override
		public void onStarted() {
		}

		@Override
		public void onException() {
			showError(getString(R.string.unable_to_add_room), null);
		}

		@Override
		public void onError(int code, String message) {
			showError(getString(R.string.unable_to_add_room), message);
		}
	};
}
