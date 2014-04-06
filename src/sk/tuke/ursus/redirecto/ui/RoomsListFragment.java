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

/**
 * Hlavn˝ fragment, zobrazuj˙ci pouûÌvateæove miestnosti. Miestnosti s˙
 * zobrazovanÈ z datab·zy pomocou mrieûky GridView
 * 
 * @author Vlastimil BreËka
 * 
 */
public class RoomsListFragment extends Fragment implements LoaderCallbacks<Cursor> {

	/**
	 * ID datab·zovÈho loadera
	 */
	private static final int LOADER_ID = 1234;

	/**
	 * Kontext
	 */
	private Context mContext;

	/**
	 * AplikaËn˝ singleton
	 */
	private MyApplication mApp;

	/**
	 * AdaptÈr
	 */
	private RoomsCursorAdapter mAdapter;

	/**
	 * DialÛg pokroku
	 */
	private ProgressDialogFragment mProgressDialog;

	/**
	 * GridView
	 */
	@InjectView(R.id.gridView) GridView mGridView;

	/**
	 * ProgressBar
	 */
	@InjectView(R.id.progressBar) ProgressBar mProgressBar;

	/**
	 * TextView chyby
	 */
	@InjectView(R.id.errorTextView) TextView mErrorTextView;

	/**
	 * TlaËidlo prvÈho spustenia
	 */
	@InjectView(R.id.boardingButton) Button mBoardingButton;

	/**
	 * Preferences
	 */
	private SharedPreferences mPrefs;

	/**
	 * VytvorÌ nov˙ inötanciu
	 * 
	 * @return Fragment
	 */
	public static RoomsListFragment newInstance() {
		return new RoomsListFragment();
	}

	/**
	 * Povinn˝ pr·zdny konötruktor
	 */
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
	public void onPrepareOptionsMenu(Menu menu) {
		if (!mApp.isAdmin()) {
			// If user is not a admin, disable some
			// buttons to make sure he doesnt even
			// get to restricted areas
			menu.findItem(R.id.action_record).setVisible(false);
			menu.findItem(R.id.action_gather).setVisible(false);
		}
		super.onPrepareOptionsMenu(menu);
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

			case R.id.action_gather: {
				Intent intent = new Intent(mContext, GatherActivity.class);
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

	/**
	 * SpustÌ manu·lne lokaliz·ciu. SpustÌ meraciu sluûbu za ˙Ëelom lokaliz·cie
	 */
	protected void localizeNow() {
		Intent intent = new Intent(mContext, SnifferService.class);
		intent.setAction(SnifferService.ACTION_LOC_AND_FORWARD);
		intent.putExtra(SnifferService.EXTRA_RECEIVER, mLocalizeReceiver);

		mContext.startService(intent);
	}

	/**
	 * Synchronizuje miestnosti lok·lne v datab·ze aplik·cie so vzdialenou
	 * datab·zou na serveri
	 */
	protected void syncMyRooms() {
		RestService.getMyRooms(mContext, mApp.getToken(), mGetMyRoomsCallback);
	}

	/**
	 * API volanie, ruËne nastavi miestnosù ako aktu·lnu, teda do nej presmeruje
	 * VoIP hovory
	 * 
	 * @param id
	 *        ID miestnosti do ktorej chceme presmerovaù
	 */
	protected void forceLocalize(int id) {
		RestService.forceLocalize(mContext, id, mApp.getToken(), mForceLocAndForwardCallback);
	}

	/**
	 * API volanie, odstr·ni miestnosù zo zoznamu svojich
	 * 
	 * @param id
	 *        ID miestnosti na zmazanie
	 */
	protected void removeMyRoom(int id) {
		RestService.removeMyRoom(mContext, id, mApp.getToken(), mRemoveMyRoomCallback);
	}

	/**
	 * Odhl·si pouûÌvateæa
	 */
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

		// Stop auto-loc
		AlarmUtils.stopAutoLocalization(mContext);

		// Finish and start login activity
		Intent intent = new Intent(mContext, LoginActivity.class);
		startActivity(intent);
		getActivity().finish();
	}

	/**
	 * API volanie, zmenÌ maxim·lny koeficient tolerancie presnosti lokaliz·cie
	 * 
	 * @param coef
	 *        Nov˝ koeficient
	 */
	protected void changeCoeficientSettings(int coef) {
		RestService.changeCoefSetting(mContext, mApp.getToken(), coef, mChangeCoefCallback);
	}

	/**
	 * Prejde do aktivity NewRoomActivity pre pridanie ÔalöÌch miestnostÌ do
	 * zoznamu svojich
	 */
	@OnClick(R.id.boardingButton)
	protected void goToNewRoomActivity() {
		Intent intent = new Intent(mContext, NewRoomActivity.class);
		startActivity(intent);
	}

	/**
	 * ZobrazÌ ProgressBar v ActionBare
	 */
	protected void showProgressBar() {
		FragmentActivity activity = getActivity();
		if (activity != null) {
			activity.setProgressBarIndeterminateVisibility(true);
		}
	}

	/**
	 * Skryje ProgressBar v ActionBare
	 */
	protected void hideProgressBar() {
		FragmentActivity activity = getActivity();
		if (activity != null) {
			activity.setProgressBarIndeterminateVisibility(false);
		}
	}

	/**
	 * ZobrazÌ dialÛg pokroku, ktor˝ oznamuje pouûÌvateæovi, ûe aplik·cia na
	 * nieËom pracuje
	 */
	protected void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = ProgressDialogFragment.newInstance(getString(R.string.logging_out));
		}
		mProgressDialog.show(getFragmentManager(), ProgressDialogFragment.TAG);
	}

	/**
	 * Skryje dialÛg pokroku
	 */
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

	/**
	 * NaË˙vaË kliknutÌ na poloûky v ListView. ZobrazÌ nov˙ aktivitu s detailom
	 * danej miestnosti
	 */
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			// Get cursor row data
			Cursor cursor = (Cursor) mAdapter.getItem(position);
			int id = cursor.getInt(cursor.getColumnIndex(Rooms.COLUMN_ID));
			String name = cursor.getString(cursor.getColumnIndex(Rooms.COLUMN_NAME));
			String desc = cursor.getString(cursor.getColumnIndex(Rooms.COLUMN_DESC));
			String phoneNumber = cursor.getString(cursor.getColumnIndex(Rooms.COLUMN_PHONE_NUMBER));
			boolean isCurrent = (cursor.getInt(cursor.getColumnIndex(Rooms.COLUMN_CURRENT)) == 1);

			// Bundle it
			Intent intent = new Intent(mContext, RoomActivity.class);
			intent.putExtra(RoomFragment.EXTRA_ID, id);
			intent.putExtra(RoomFragment.EXTRA_NAME, name);
			intent.putExtra(RoomFragment.EXTRA_DESC, desc);
			intent.putExtra(RoomFragment.EXTRA_PHONE_NUMBER, phoneNumber);
			intent.putExtra(RoomFragment.EXTRA_IS_CURRENT, isCurrent);

			// Start new activity
			Bundle bundle = ActivityOptionsCompat.makeScaleUpAnimation(
					view, 0, 0,
					view.getWidth(), view.getHeight()
					).toBundle();
			ActivityCompat.startActivity(getActivity(), intent, bundle);
		}
	};

	/**
	 * Sp‰tnÈ volanie pre kliknutie na overflow poloûky v GridView
	 */
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

	/**
	 * ResultReceiver, prostriedok komunik·cie medzi Aktivitou a Sluûbou
	 */
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
						ToastUtils.showInfo(mContext, getString(R.string.localized_in) + localizedRoom
								+ getString(R.string.calls_redirected));
					}
					break;

				case SnifferService.CODE_ACTION_ERROR:
					hideProgressBar();
					String errorMessage = resultData.getString(SnifferService.EXTRA_ERROR);
					if (errorMessage != null) {
						ToastUtils.showError(mContext, R.string.unable_to_log_and_fwd, errorMessage);
					} else {
						ToastUtils.showError(mContext, R.string.unable_to_log_and_fwd,
								R.string.check_internet_connection);
					}
					break;
			}
		}
	};

	/**
	 * Sp‰tnÈ volanie API volania odhl·senia sa
	 */
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
			ToastUtils.showError(mContext, R.string.unable_to_logout, message);
		}

		@Override
		public void onException() {
			dismissProgressDialog();
			ToastUtils.showError(mContext, R.string.unable_to_logout, R.string.check_internet_connection);
		}

	};

	/**
	 * SpatnÈ volanie API volania ForceLocalize
	 */
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
					.showError(mContext, R.string.unable_to_set_as_current, R.string.check_internet_connection);
		}

		@Override
		public void onError(int code, String message) {
			hideProgressBar();
			ToastUtils.showError(mContext, R.string.unable_to_set_as_current, message);
		}
	};

	/**
	 * Sp‰tnÈ volanie API volania GetMyRooms
	 */
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
			ToastUtils.showError(
					mContext,
					R.string.unable_to_sync_rooms,
					R.string.check_internet_connection);
		}

		@Override
		public void onError(int code, String message) {
			hideProgressBar();
			ToastUtils.showError(
					mContext,
					R.string.unable_to_sync_rooms, message);
		}
	};

	/**
	 * Sp‰tnÈ volanie API volania RemoveMyRoom
	 */
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
			ToastUtils.showError(
					mContext,
					R.string.unable_to_remove,
					R.string.check_internet_connection);
		}

		@Override
		public void onError(int code, String message) {
			hideProgressBar();
			ToastUtils.showError(mContext, R.string.unable_to_remove, message);
		}
	};

	/**
	 * Sp‰tnÈ volanie API volania ChangeCoefSetting
	 */
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
			ToastUtils.showError(
					mContext,
					R.string.unable_to_change_coef,
					R.string.check_internet_connection);
		}

		@Override
		public void onError(int code, String message) {
			hideProgressBar();
			ToastUtils.showError(
					mContext,
					R.string.unable_to_change_coef,
					message);
		}
	};

	/**
	 * NaË˙vaË zmien v Preferences nastaveniach
	 */
	private OnSharedPreferenceChangeListener mPrefsListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			if (Utils.PREFS_AUTO_LOC_KEY.equals(key)) {
				// Auto-localization settings changed
				boolean autoLocalize = prefs.getBoolean(
						Utils.PREFS_AUTO_LOC_KEY,
						AlarmUtils.DEFAULT_AUTO_LOC);

				if (autoLocalize) {
					AlarmUtils.startAutoLocalization(mContext, mPrefs);
				} else {
					AlarmUtils.stopAutoLocalization(mContext);
				}

			} else if (Utils.PREFS_LOC_FREQUENCY_KEY.equals(key)) {
				// Auto-localization frequency changed
				boolean autoLocalize = prefs.getBoolean(
						Utils.PREFS_AUTO_LOC_KEY,
						AlarmUtils.DEFAULT_AUTO_LOC);

				if (autoLocalize) {
					AlarmUtils.startAutoLocalization(mContext, mPrefs);
				}

			} else if (Utils.PREFS_MAX_ACC_COEFICIENT_KEY.equals(key)) {
				// Maximum accuracy coeficient changed
				int coef = prefs.getInt(
						Utils.PREFS_MAX_ACC_COEFICIENT_KEY,
						Utils.DEFAULT_MAX_ACC_COEFICIENT);

				changeCoeficientSettings(coef);
			}
		}
	};

}
