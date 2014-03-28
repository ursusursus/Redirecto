package sk.tuke.ursus.redirecto.ui;

import sk.tuke.ursus.redirecto.MyApplication;
import sk.tuke.ursus.redirecto.R;
import sk.tuke.ursus.redirecto.RoomsCursorAdapter;
import sk.tuke.ursus.redirecto.RoomsCursorAdapter.RoomOverflowCallback;
import sk.tuke.ursus.redirecto.SnifferService;
import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import sk.tuke.ursus.redirecto.ui.dialog.ProgressDialogFragment;
import sk.tuke.ursus.redirecto.util.AlarmUtils;
import sk.tuke.ursus.redirecto.util.Utils;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.awaboom.ursus.agave.LOG;
import com.awaboom.ursus.agave.ToastUtils;

public class RoomsListFragment extends Fragment implements LoaderCallbacks<Cursor> {

	private static final int LOADER_ID = 1234;

	private Context mContext;
	private MyApplication mApp;
	private RoomsCursorAdapter mAdapter;
	private ProgressDialogFragment mProgressDialog;

	@InjectView(R.id.gridView) GridView mGridView;
	@InjectView(R.id.progressBar) ProgressBar mProgressBar;
	@InjectView(R.id.errorTextView) TextView mErrorTextView;
	@InjectView(R.id.boardingButton) Button mBoardingButton;

	private SharedPreferences mPrefs;

	public static RoomsListFragment newInstance() {
		return new RoomsListFragment();
	}

	public RoomsListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		mContext = getActivity();
		mApp = (MyApplication) getActivity().getApplication();
		mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_rooms_list, container, false);
		ButterKnife.inject(this, v);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mAdapter = new RoomsCursorAdapter(mContext, mRoomOverflowCallback);
		mGridView.setOnItemClickListener(mItemClickListener);
		// mGridView.setOnItemLongClickListener(mItemLongClickListener);
		mGridView.setAdapter(mAdapter);

		String metadata = mApp.getMetadata();
		if (metadata != null) {
			getActivity().getActionBar().setSubtitle(metadata);
		}

		getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mPrefs.registerOnSharedPreferenceChangeListener(mPrefsListener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mPrefs.unregisterOnSharedPreferenceChangeListener(mPrefsListener);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_rooms_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_localize: {
				localizeNow();
				return true;
			}
			case R.id.action_add_room: {
				goToNewRoomActivity();
				return true;
			}

			case R.id.action_sync_rooms: {
				syncMyRooms();
				return true;
			}

			case R.id.action_record: {
				Intent intent = new Intent(mContext, RecordActivity.class);
				startActivity(intent);
				return true;
			}

			case R.id.action_settings: {
				Intent intent = new Intent(mContext, MyPreferencesActivity.class);
				startActivity(intent);
				return true;
			}

			case R.id.action_about: {
				Intent intent = new Intent(mContext, AboutActivity.class);
				startActivity(intent);
				return true;
			}

			case R.id.action_logout: {
				logout();
				return true;
			}

			default:
				return false;
		}
	}

	protected void localizeNow() {
		Intent intent = new Intent(mContext, SnifferService.class);
		intent.setAction(SnifferService.ACTION_LOC_AND_FORWARD);
		intent.putExtra(SnifferService.EXTRA_RECEIVER, mLocalizeReceiver);

		mContext.startService(intent);
	}

	protected void syncMyRooms() {
		RestService.getMyRooms(mContext, mApp.getToken(), mGetMyRoomsCallback);
	}

	protected void forceLocalize(int id) {
		RestService.forceLocalize(mContext, id, mApp.getToken(), mForceLocAndForwardCallback);
	}

	protected void removeMyRoom(int id) {
		RestService.removeMyRoom(mContext, id, mApp.getToken(), mRemoveMyRoomCallback);
	}

	private void logout() {
		// When token is invalid, I cant go
		// logout out as it requires a token
		// so for now, skip it, just clear
		// local tokens, metadata, data
		// and go log in again
		//
		// RestService.logout(mContext, mApp.getToken(), mLogoutCallback);

		// Clean up database
		ContentResolver resolver = mContext.getContentResolver();
		resolver.delete(Rooms.CONTENT_URI, null, null);

		// Remove data
		mApp.removeTokenAndMetadata();

		// Finish and start login activity
		Intent intent = new Intent(mContext, LoginActivity.class);
		startActivity(intent);
		getActivity().finish();
	}

	protected void changeCoeficientSettings(int coef) {
		RestService.changeCoefSetting(mContext, mApp.getToken(), coef, mChangeCoefCallback);
	}

	@OnClick(R.id.boardingButton)
	protected void goToNewRoomActivity() {
		Intent intent = new Intent(mContext, NewRoomActivity.class);
		startActivity(intent);
	}

	protected void hideProgressBar() {
		/* ActionBarActivity activity = (ActionBarActivity) getActivity();
		if (activity != null) {
			activity.setSupportProgressBarIndeterminateVisibility(false);
		} */
		FragmentActivity activity = getActivity();
		if (activity != null) {
			activity.setProgressBarIndeterminateVisibility(false);
		}
	}

	protected void showProgressBar() {
		/* ActionBarActivity activity = (ActionBarActivity) getActivity();
		if (activity != null) {
			activity.setSupportProgressBarIndeterminateVisibility(true);
		} */
		FragmentActivity activity = getActivity();
		if (activity != null) {
			activity.setProgressBarIndeterminateVisibility(true);
		}
	}

	protected void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = ProgressDialogFragment.newInstance("Odhlasujem sa...");
		}
		mProgressDialog.show(getFragmentManager(), ProgressDialogFragment.TAG);
	}

	protected void dismissProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		return new CursorLoader(mContext, Rooms.CONTENT_URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loaderId, Cursor cursor) {
		mAdapter.swapCursor(cursor);

		if (cursor.getCount() == 0) {
			// Utils.setupAutoSync(mContext, mApp.getToken());
			syncMyRooms();
		} else {
			mBoardingButton.setVisibility(View.GONE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loaderId) {
		mAdapter.swapCursor(null);
	}

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			Cursor cursor = (Cursor) mAdapter.getItem(position);
			int id = cursor.getInt(cursor.getColumnIndex(Rooms.COLUMN_ID));

			Bundle bundle = ActivityOptionsCompat.makeScaleUpAnimation(
					view, 0, 0,
					view.getWidth(), view.getHeight()
					).toBundle();

			Intent intent = new Intent(mContext, DetailActivity.class);
			ActivityCompat.startActivity(getActivity(), intent, bundle);
		}
	};

	/* private OnItemLongClickListener mItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			Cursor cursor = (Cursor) mAdapter.getItem(position);
			int id = cursor.getInt(cursor.getColumnIndex(Rooms.COLUMN_ID));

			//
			forceLocalize(id);
			//
			return true;
		}

	}; */

	private RoomOverflowCallback mRoomOverflowCallback = new RoomOverflowCallback() {

		@Override
		public void onRoomRemoved(int id) {
			removeMyRoom(id);
		}

		@Override
		public void onLocalizedManually(int id) {
			forceLocalize(id);
		}
	};

	ResultReceiver mLocalizeReceiver = new ResultReceiver(new Handler()) {
		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);

			switch (resultCode) {
				case SnifferService.CODE_ACTION_START:
					showProgressBar();
					break;

				case SnifferService.CODE_ACTION_SUCCESS:
					hideProgressBar();
					if (resultData.containsKey(SnifferService.EXTRA_VALUES)) {
						String localizedRoom = resultData.getString(SnifferService.EXTRA_VALUES);
						ToastUtils.showInfo(mContext, "Lokalizovan˝ v " + localizedRoom
								+ "\nHovory ˙speöne presmerovanÈ");
					}
					break;

				case SnifferService.CODE_ACTION_ERROR:
					hideProgressBar();
					String errorMessage = resultData.getString(SnifferService.EXTRA_ERROR);
					if (errorMessage != null) {
						ToastUtils.showError(mContext, "Nepodarilo sa lokalizovaù a presmerovaù", errorMessage);
					} else {
						ToastUtils.showError(mContext, "Nepodarilo sa lokalizovaù a presmerovaù",
								"Skontrolujte pripojenie na internet");
					}
					break;
			}
		}
	};

	private RestUtils.Callback mLogoutCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			LOG.d("Logout # onSuccess");
			dismissProgressDialog();

			// Remove data
			mApp.removeTokenAndMetadata();

			// Finish and start login activity
			Intent intent = new Intent(mContext, LoginActivity.class);
			startActivity(intent);
			getActivity().finish();
		}

		@Override
		public void onStarted() {
			showProgressDialog();
		}

		@Override
		public void onError(int code, String message) {
			dismissProgressDialog();
			ToastUtils.showError(mContext, "Nepodarilo sa odhl·siù", message);
		}

		@Override
		public void onException() {
			dismissProgressDialog();
			ToastUtils.showError(mContext, "Nepodarilo sa odhl·siù", "Skontrolujte pripojenie na internet");
		}

	};

	private RestUtils.Callback mForceLocAndForwardCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			hideProgressBar();
		}

		@Override
		public void onStarted() {
			showProgressBar();
		}

		@Override
		public void onException() {
			hideProgressBar();
			ToastUtils
					.showError(mContext, "Nepodarilo sa nastaviù ako aktu·lnu", "Skontrolujte pripojenie na internet");
		}

		@Override
		public void onError(int code, String message) {
			hideProgressBar();
			ToastUtils.showError(mContext, "Nepodarilo sa nastaviù ako aktu·lnu", message);
		}
	};

	private RestUtils.Callback mGetMyRoomsCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			LOG.d("MyRoomsCallback # onSuccess");
			hideProgressBar();

			//
			boolean emptyFlag = data.getBoolean(RestService.RESULT_NO_ROOMS, false);
			if (emptyFlag) {
				mBoardingButton.setVisibility(View.VISIBLE);
				/* if (Utils.hasHoneycomb()) {
					final DecelerateInterpolator interpolator = new DecelerateInterpolator();
					mBoardingButton.animate()
							.scaleX(0.9F)
							.scaleY(0.9F)
							.setDuration(200)
							.setInterpolator(interpolator)
							.setListener(new AnimatorListenerAdapter() {

								public void onAnimationEnd(android.animation.Animator animation) {
									mBoardingButton.animate()
											.scaleX(1F)
											.scaleY(1F)
											.setInterpolator(interpolator)
											.setDuration(100)
											.setListener(null);
								};
							});
				} */
			} else {
				mBoardingButton.setVisibility(View.GONE);
			}
		}

		@Override
		public void onStarted() {
			showProgressBar();
		}

		@Override
		public void onException() {
			hideProgressBar();
			ToastUtils.showError(mContext, "Nepodarilo sa synchronizovaù miestnosti",
					"Skontrolujte pripojenie na internet");
		}

		@Override
		public void onError(int code, String message) {
			hideProgressBar();
			ToastUtils.showError(mContext, "Nepodarilo sa synchronizovaù miestnosti", message);
		}
	};

	private RestUtils.Callback mRemoveMyRoomCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			LOG.d("RemoveMyRoom # onSuccess");
			hideProgressBar();
		}

		@Override
		public void onStarted() {
			showProgressBar();
		}

		@Override
		public void onException() {
			hideProgressBar();
			ToastUtils.showError(mContext, "Nepodarilo sa odstr·niù miestnosù", "Skontrolujte pripojenie na internet");
		}

		@Override
		public void onError(int code, String message) {
			hideProgressBar();
			ToastUtils.showError(mContext, "Nepodarilo sa odstr·niù miestnosù", message);
		}
	};

	private RestUtils.Callback mChangeCoefCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			LOG.d("ChangeCoef # onSuccess");
			hideProgressBar();
		}

		@Override
		public void onStarted() {
			showProgressBar();
		}

		@Override
		public void onException() {
			hideProgressBar();
			ToastUtils.showError(mContext, "Nepodarilo sa zmeniù koeficient", "Skontrolujte pripojenie na internet");
		}

		@Override
		public void onError(int code, String message) {
			hideProgressBar();
			ToastUtils.showError(mContext, "Nepodarilo sa zmeniù koeficient", message);
		}
	};

	private OnSharedPreferenceChangeListener mPrefsListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			if (Utils.PREFS_AUTO_LOC_KEY.equals(key)) {
				boolean autoLocalize = prefs.getBoolean(
						Utils.PREFS_AUTO_LOC_KEY,
						AlarmUtils.DEFAULT_AUTO_LOC);

				if (autoLocalize) {
					AlarmUtils.startAutoLocalization(mContext, mPrefs);
				} else {
					AlarmUtils.stopAutoLocalization(mContext);
				}

			} else if (Utils.PREFS_LOC_FREQUENCY_KEY.equals(key)) {
				boolean autoLocalize = prefs.getBoolean(
						Utils.PREFS_AUTO_LOC_KEY,
						AlarmUtils.DEFAULT_AUTO_LOC);

				if (autoLocalize) {
					AlarmUtils.startAutoLocalization(mContext, mPrefs);
				}

			} else if (Utils.PREFS_MAX_ACC_COEFICIENT_KEY.equals(key)) {
				int coef = prefs.getInt(
						Utils.PREFS_MAX_ACC_COEFICIENT_KEY,
						Utils.DEFAULT_MAX_ACC_COEFICIENT);

				changeCoeficientSettings(coef);
			}
		}
	};

}
