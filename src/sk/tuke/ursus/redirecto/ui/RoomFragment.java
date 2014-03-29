package sk.tuke.ursus.redirecto.ui;

import sk.tuke.ursus.redirecto.MyApplication;
import sk.tuke.ursus.redirecto.R;
import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.awaboom.ursus.agave.ToastUtils;

public class RoomFragment extends Fragment {

	public static final String TAG = "room";

	public static final String EXTRA_ID = "id";
	public static final String EXTRA_NAME = "name";
	public static final String EXTRA_DESC = "desc";
	public static final String EXTRA_PHONE_NUMBER = "phone_number";
	public static final String EXTRA_IS_CURRENT = "is_current";

	private FragmentActivity mContext;
	private MyApplication mApp;

	private int mRoomId;

	private boolean mCurrent;

	public static Fragment newInstance(Bundle args) {
		RoomFragment f = new RoomFragment();
		if (args != null) {
			f.setArguments(args);
		}
		return f;
	}

	public RoomFragment() {
		//
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = getActivity();
		mApp = (MyApplication) getActivity().getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_room, container, false);

		Bundle args = getArguments();
		mRoomId = args.getInt(EXTRA_ID);
		
		if (savedInstanceState != null) {
			mCurrent = savedInstanceState.getBoolean(EXTRA_IS_CURRENT);
		} else {
			mCurrent = args.getBoolean(EXTRA_IS_CURRENT, false);
		}

		TextView nameTextView = (TextView) view.findViewById(R.id.nameTextView);
		nameTextView.setText(args.getString(EXTRA_NAME));

		TextView descTextView = (TextView) view.findViewById(R.id.descTextView);
		descTextView.setText(args.getString(EXTRA_DESC));

		TextView numberTextView = (TextView) view.findViewById(R.id.numberTextView);
		numberTextView.setText(args.getString(EXTRA_PHONE_NUMBER));

		Button removeButton = (Button) view.findViewById(R.id.removeButton);
		removeButton.setOnClickListener(mClickListener);

		Button forceButton = (Button) view.findViewById(R.id.forceButton);
		forceButton.setOnClickListener(mClickListener);

		displayState(view, mCurrent);
		return view;
	}

	private void displayState(View view, boolean isCurrent) {
		TextView stateTextView = (TextView) view.findViewById(R.id.stateTextView);
		stateTextView.setText(isCurrent ? R.string.room_current : R.string.room_not_current);

	}

	protected void hideProgressBar() {
		getView().findViewById(R.id.progressBar).setVisibility(View.GONE);
	}

	protected void showProgressBar() {
		getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(EXTRA_IS_CURRENT, mCurrent);
	};

	OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.removeButton:
					RestService.removeMyRoom(
							mContext,
							mRoomId,
							mApp.getToken(),
							mRemoveMyRoomCallback);
					break;

				case R.id.forceButton:
					RestService.forceLocalize(
							mContext,
							mRoomId,
							mApp.getToken(),
							mForceLocAndForwardCallback);
					break;
			}
		}
	};

	private RestUtils.Callback mForceLocAndForwardCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			mCurrent = true;
			hideProgressBar();
			displayState(getView(), mCurrent);
		}

		@Override
		public void onStarted() {
			showProgressBar();
		}

		@Override
		public void onException() {
			hideProgressBar();
			ToastUtils.showError(
					mContext,
					R.string.unable_to_set_as_current,
					R.string.check_internet_connection);
		}

		@Override
		public void onError(int code, String message) {
			hideProgressBar();
			ToastUtils.showError(
					mContext,
					R.string.unable_to_set_as_current,
					message);
		}
	};

	private RestUtils.Callback mRemoveMyRoomCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			hideProgressBar();
			getActivity().finish();
		}

		@Override
		public void onStarted() {
			showProgressBar();
		}

		@Override
		public void onException() {
			hideProgressBar();
			ToastUtils.showError(
					mContext,
					R.string.unable_to_remove,
					R.string.check_internet_connection);
		}

		@Override
		public void onError(int code, String message) {
			hideProgressBar();
			ToastUtils.showError(
					mContext,
					R.string.unable_to_remove,
					message);
		}
	};

}
