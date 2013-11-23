package sk.tuke.ursus.redirecto.ui;

import sk.tuke.ursus.redirecto.MyApplication;
import sk.tuke.ursus.redirecto.R;
import sk.tuke.ursus.redirecto.RoomsCursorAdapter;
import sk.tuke.ursus.redirecto.RoomsCursorAdapter.RoomOverflowCallback;
import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import sk.tuke.ursus.redirecto.ui.dialog.ProgressDialogFragment;
import sk.tuke.ursus.redirecto.util.LOG;
import sk.tuke.ursus.redirecto.util.ToastUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;

public class RoomsListFragment extends Fragment implements LoaderCallbacks<Cursor> {

	private static final int LOADER_ID = 1234;

	private Context mContext;
	private MyApplication mApp;

	private GridView mGridView;
	private RoomsCursorAdapter mAdapter;

	private ProgressDialogFragment mProgressDialog;
	private View mProgressBar;
	private View mErrorTextView;

	private Button mBoardingButton;

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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_rooms_list, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mProgressBar = view.findViewById(R.id.progressBar);
		mErrorTextView = view.findViewById(R.id.errorTextView);
		mBoardingButton = (Button) getView().findViewById(R.id.boardingButton);

		mAdapter = new RoomsCursorAdapter(mContext, mRoomOverflowCallback);

		mGridView = (GridView) view.findViewById(R.id.gridView);
		mGridView.setOnItemClickListener(mItemClickListener);
		mGridView.setOnItemLongClickListener(mItemLongClickListener);
		mGridView.setAdapter(mAdapter);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		String username = mApp.getUsername();
		if (username != null) {
			ActionBarActivity activity = ((ActionBarActivity) getActivity());
			ActionBar actionBar = activity.getSupportActionBar();
			actionBar.setSubtitle(username);
		}

		getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	protected void syncMyRooms() {
		RestService.getMyRooms(mContext, mApp.getToken(), mMyRoomsCallback);
	}

	protected void localizeMe() {
		RestService.localizeMe(mContext, mApp.getToken(), mLocalizeMeCallback);
	}

	protected void localizeMeManually(int id) {
		RestService.localizeMeManually(mContext, id, mApp.getToken(), mLocalizeMeManuallyCallback);
	}

	protected void removeMyRoom(int id) {
		RestService.removeMyRoom(mContext, id, mApp.getToken(), mRemoveMyRoomCallback);
	}

	private void logout() {
		RestService.logout(mContext, mApp.getToken(), mLogoutCallback);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_rooms_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_localize: {
				localizeMe();
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

	private void goToNewRoomActivity() {
		Intent intent = new Intent(mContext, NewRoomActivity.class);
		startActivity(intent);
	}

	protected void hideProgressBar() {
		mProgressBar.setVisibility(View.GONE);
	}

	protected void showProgressBar() {
		mProgressBar.setVisibility(View.VISIBLE);
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
		LOG.d("CursorCount: " + cursor.getCount());
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

	private RoomOverflowCallback mRoomOverflowCallback = new RoomOverflowCallback() {

		@Override
		public void onRoomRemoved(int id) {
			LOG.d("onRoomRemoved: " + id);
			removeMyRoom(id);
		}

		@Override
		public void onLocalizedManually(int id) {
			LOG.d("onLocalizedManually: " + id);
			localizeMeManually(id);
		}
	};

	private OnItemLongClickListener mItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			Cursor cursor = (Cursor) mAdapter.getItem(position);
			int id = cursor.getInt(cursor.getColumnIndex(Rooms.COLUMN_ID));

			//
			localizeMeManually(id);
			//
			return true;
		}

	};

	private RestUtils.Callback mLogoutCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			LOG.d("Logout # onSuccess");
			dismissProgressDialog();

			// Remove data
			mApp.removeTokenAndUsername();

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

	private RestUtils.Callback mLocalizeMeManuallyCallback = new RestUtils.Callback() {

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
		}

		@Override
		public void onError(int code, String message) {
			hideProgressBar();
		}
	};

	private RestUtils.Callback mLocalizeMeCallback = new RestUtils.Callback() {

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
		}

		@Override
		public void onError(int code, String message) {
			hideProgressBar();
		}
	};

	private RestUtils.Callback mMyRoomsCallback = new RestUtils.Callback() {

		@SuppressLint("NewApi")
		@Override
		public void onSuccess(Bundle data) {
			LOG.d("MyRoomsCallback # onSuccess");
			hideProgressBar();

			//
			boolean emptyFlag = data.getBoolean(RestService.RESULTS_NO_ROOMS_KEY, false);
			if (emptyFlag) {
				mBoardingButton.setVisibility(View.VISIBLE);
				mBoardingButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						goToNewRoomActivity();
					}
				});
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
			ToastUtils.showError(mContext, "Nepodarilo sa synchronizovaù so serverom", "Skontrolujte pripojenie na internet");
		}

		@Override
		public void onError(int code, String message) {
			hideProgressBar();
			ToastUtils.showError(mContext, "Nepodarilo sa synchronizovaù so serverom", message);
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
}
