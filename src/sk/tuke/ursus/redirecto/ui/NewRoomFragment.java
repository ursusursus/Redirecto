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

import com.awaboom.ursus.agave.LOG;
import com.awaboom.ursus.agave.ToastUtils;

public class NewRoomFragment extends Fragment {

	private static final String EXTRA_ROOMS_KEY = "rooms";

	public static NewRoomFragment newInstance() {
		return new NewRoomFragment();
	}

	private FragmentActivity mContext;
	private MyApplication mApp;
	private ArrayList<Room> mRooms;
	private RoomsArrayAdapter mAdapter;
	
	@InjectView(R.id.listView) ListView mListView;
	@InjectView(R.id.filterEditText) EditText mFilterEditText;
	@InjectView(R.id.progressBar) ProgressBar mProgressBar;
	@InjectView(R.id.errorTextView) TextView mErrorTextView;

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

	private void fetchAllRooms() {
		RestService.getAllRooms(mContext, mApp.getToken(), mAllRoomsCallback);
	}

	protected void addMyNewRoom(int id) {
		RestService.addMyRoom(mApp, id, mApp.getToken(), mAddMyRoomCallback);
	}

	protected void hideProgressBar() {
		mProgressBar.setVisibility(View.GONE);
	}

	protected void showProgressBar() {
		mProgressBar.setVisibility(View.VISIBLE);
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
		if(item.getItemId() == R.id.action_refresh) {
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

	private OnRoomAddedListener mRoomAddedListener = new OnRoomAddedListener() {

		@Override
		public void onRoomAdded(int position, int id) {
			addMyNewRoom(id);
		}
	};

	private RestUtils.Callback mAllRoomsCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			hideProgressBar();

			ArrayList<Room> newRooms = data.getParcelableArrayList(RestService.RESULT_ROOMS);
			if (newRooms != null) {

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
			ToastUtils.showError(mContext, "Nepodarilo sa naËÌtaù miestnosti");
		}

		@Override
		public void onError(int code, String message) {
			hideProgressBar();
			ToastUtils.showError(mContext, "Nepodarilo sa naËÌtaù miestnosti");
		}
	};

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
			LOG.d("AddMyRoomCallback # onException");
			ToastUtils.showError(mContext, "Nepodarilo sa pridaù miestnosù");
		}

		@Override
		public void onError(int code, String message) {
			LOG.d("AddMyRoomCallback # onError: " + message);
			ToastUtils.showError(mContext, "Nepodarilo sa pridaù miestnosù - " + message);
		}
	};

}
