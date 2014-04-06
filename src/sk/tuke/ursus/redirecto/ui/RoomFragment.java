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

/**
 * Fragment zobrazuj�ci detailn� inform�cie o miestnosti
 * 
 * @author Vlastimil Bre�ka
 * 
 */
public class RoomFragment extends Fragment {
	/**
	 * Tag
	 */
	public static final String TAG = "room";

	/**
	 * K��� ID
	 */
	public static final String EXTRA_ID = "id";

	/**
	 * K��� mena miestnosti
	 */
	public static final String EXTRA_NAME = "name";

	/**
	 * K��� popisu
	 */
	public static final String EXTRA_DESC = "desc";

	/**
	 * K��� klapky miestnosti
	 */
	public static final String EXTRA_PHONE_NUMBER = "phone_number";

	/**
	 * K��� �i je miestnos� aktu�lna
	 */
	public static final String EXTRA_IS_CURRENT = "is_current";

	/**
	 * Kontext
	 */
	private FragmentActivity mContext;

	/**
	 * Aplika�n� singleton
	 */
	private MyApplication mApp;

	/**
	 * ID zobrazovanej miestnosti
	 */
	private int mRoomId;

	/**
	 * �i je zobrazovan� miestnos� aktu�lna
	 */
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

	/**
	 * Zobraz� stav aktu�lnosti miestnosti v pou��vate�skom rozhran�
	 * 
	 * @param view
	 *        Poh�ad
	 * @param isCurrent
	 *        �i je miestnos� aktu�lna
	 */
	private void displayState(View view, boolean isCurrent) {
		TextView stateTextView = (TextView) view.findViewById(R.id.stateTextView);
		stateTextView.setText(isCurrent ? R.string.room_current : R.string.room_not_current);

	}

	/**
	 * Zobraz� ProgressBar
	 */
	protected void showProgressBar() {
		getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
	}

	/**
	 * Skryje ProgressBar
	 */
	protected void hideProgressBar() {
		getView().findViewById(R.id.progressBar).setVisibility(View.GONE);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(EXTRA_IS_CURRENT, mCurrent);
	};

	/**
	 * Na��va� klinut� tla�idla
	 */
	OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.removeButton:
					// Call RemoveMyRoom API
					RestService.removeMyRoom(
							mContext,
							mRoomId,
							mApp.getToken(),
							mRemoveMyRoomCallback);
					break;

				case R.id.forceButton:
					// Call ForceLocalize API
					RestService.forceLocalize(
							mContext,
							mRoomId,
							mApp.getToken(),
							mForceLocAndForwardCallback);
					break;
			}
		}
	};

	/**
	 * Sp�tn� volanie z API volania ForceLocalize
	 */
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

	/**
	 * Sp�tn� volanie z API volania RemoveMyRoom
	 */
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
