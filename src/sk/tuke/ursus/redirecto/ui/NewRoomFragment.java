package sk.tuke.ursus.redirecto.ui;

import java.util.ArrayList;

import sk.tuke.ursus.redirecto.MyApplication;
import sk.tuke.ursus.redirecto.R;
import sk.tuke.ursus.redirecto.RoomsArrayAdapter;
import sk.tuke.ursus.redirecto.RoomsArrayAdapter.OnRoomAddedListener;
import sk.tuke.ursus.redirecto.model.Room;
import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.util.LOG;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

public class NewRoomFragment extends Fragment {

	private static final String EXTRA_ROOMS_KEY = "rooms";

	public static NewRoomFragment newInstance() {
		return new NewRoomFragment();
	}

	private FragmentActivity mContext;
	private MyApplication mApp;
	private ArrayList<Room> mRooms;
	private RoomsArrayAdapter mAdapter;
	private ListView mListView;
	private EditText mFilterEditText;
	private View mProgressBar;
	private View mErrorTextView;

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

		mProgressBar = view.findViewById(R.id.progressBar);
		mErrorTextView = view.findViewById(R.id.errorTextView);
		
		mAdapter = new RoomsArrayAdapter(mContext, mRooms, mRoomAddedListener);

		mListView = (ListView) view.findViewById(R.id.listView);
		mListView.setAdapter(mAdapter);

		mFilterEditText = (EditText) view.findViewById(R.id.filterEditText);
		mFilterEditText.addTextChangedListener(mTextChangedListener);
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
			LOG.d("Position: " + position + " - Id: " + id);
			addMyNewRoom(id);
		}
	};

	private RestUtils.Callback mAllRoomsCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			hideProgressBar();
			
			ArrayList<Room> newRooms = data.getParcelableArrayList(RestService.RESULTS_ROOMS_KEY);
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
		}

		@Override
		public void onError(int code, String message) {
			hideProgressBar();
		}
	};

	private RestUtils.Callback mAddMyRoomCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			
		}

		@Override
		public void onStarted() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onException() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(int code, String message) {
			// TODO Auto-generated method stub

		}
	};

}
