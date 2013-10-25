package sk.tuke.ursus.redirecto.ui;

import sk.tuke.ursus.redirecto.MyApplication;
import sk.tuke.ursus.redirecto.R;
import sk.tuke.ursus.redirecto.RoomsCursorAdapter;
import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import sk.tuke.ursus.redirecto.ui.dialog.ProgressDialogFragment;
import sk.tuke.ursus.redirecto.util.LOG;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

public class RoomsListFragment extends Fragment implements LoaderCallbacks<Cursor> {

	private static final int LOADER_ID = 1234;

	private Context mContext;
	private MyApplication mApp;

	private GridView mGridView;
	private RoomsCursorAdapter mAdapter;

	private ProgressDialogFragment mProgressDialog;

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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_rooms_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_add_room: {
				Intent intent = new Intent(mContext, NewRoomActivity.class);
				startActivity(intent);
				return true;
			}
			case R.id.action_localize: {
				localize();
				return true;
			}

			case R.id.action_logout: {
				logout();
				return true;
			}

			case R.id.action_about: {
				Intent intent = new Intent(mContext, AboutActivity.class);
				startActivity(intent);
				return true;
			}

			case R.id.action_settings: {
				Intent intent = new Intent(mContext, MyPreferencesActivity.class);
				startActivity(intent);
				return true;
			}

			default:
				return false;
		}
	}

	private void syncMyRooms() {
		RestService.getMyRooms(mContext, mApp.getToken(), mMyRoomsCallback);
	}
	
	private void localize() {
		RestService.localize(mContext, mApp.getToken(), mLocalizeCallback);
	}

	private void logout() {
		MyApplication app = (MyApplication) getActivity().getApplication();
		RestService.logout(mContext, app.getToken(), mLogoutCallback);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mAdapter = new RoomsCursorAdapter(mContext);

		mGridView = (GridView) view.findViewById(R.id.gridView);
		mGridView.setOnItemClickListener(mItemClickListener);
		mGridView.setOnItemLongClickListener(mItemLongClickListener);
		mGridView.setAdapter(mAdapter);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(LOADER_ID, null, this);
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
		
		if(cursor.getCount() == 0) {
			syncMyRooms();
		}
	}


	@Override
	public void onLoaderReset(Loader<Cursor> loaderId) {
		mAdapter.swapCursor(null);
	}

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			Cursor cursor = (Cursor) mAdapter.getItem(position);
			int id = cursor.getInt(cursor.getColumnIndex(Rooms.COLUMN_ID));

			LOG.d("Position: " + position + " - Id: " + id);
		}
	};

	private OnItemLongClickListener mItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			Cursor cursor = (Cursor) mAdapter.getItem(position);
			int id = cursor.getInt(cursor.getColumnIndex(Rooms.COLUMN_ID));

			//
			RestService.localizeManually(mContext, id, mApp.getToken(), mSetLocationCallback);
			//
			return true;
		}

	};

	private RestUtils.Callback mLogoutCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			LOG.d("Logout # onSuccess");
			dismissProgressDialog();

			// Remove bundle
			mApp.removeToken();

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

		}

		@Override
		public void onException() {

		}

	};

	private RestUtils.Callback mSetLocationCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {

		}

		@Override
		public void onStarted() {

		}

		@Override
		public void onException() {

		}

		@Override
		public void onError(int code, String message) {

		}
	};

	private RestUtils.Callback mLocalizeCallback = new RestUtils.Callback() {

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
	
	private RestUtils.Callback mMyRoomsCallback = new RestUtils.Callback() {

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
