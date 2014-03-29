package sk.tuke.ursus.redirecto.ui;

import java.util.ArrayList;

import sk.tuke.ursus.redirecto.MyApplication;
import sk.tuke.ursus.redirecto.R;
import sk.tuke.ursus.redirecto.SnifferService;
import sk.tuke.ursus.redirecto.model.Room;
import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.net.processor.GetRoomsAndAPsProcessor;
import sk.tuke.ursus.redirecto.ui.dialog.RoomPickerDialog;
import sk.tuke.ursus.redirecto.ui.dialog.RoomPickerDialog.OnRoomPickedListener;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.awaboom.ursus.agave.LOG;
import com.awaboom.ursus.agave.ToastUtils;

public class RecordActivity extends FragmentActivity implements OnClickListener, OnRoomPickedListener {

	private static final String EXTRA_VALUES_LIST = "values";
	private static final String EXTRA_IS_RECORDING = "is_recording";
	private static final String EXTRA_PICKED_ROOM = "picked_room";

	private boolean mRecording;
	private Button mToggleButton;
	private Room mPickedRoom;
	private ArrayList<String> mValuesList;
	private ArrayAdapter<String> mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			mValuesList = new ArrayList<String>();
			mRecording = false;

			MyApplication myApp = (MyApplication) getApplication();
			RestService.getRoomsAndAPs(this, myApp.getToken(), mRoomsAndAPsCallback);
		} else {
			mValuesList = savedInstanceState.getStringArrayList(EXTRA_VALUES_LIST);
			mRecording = savedInstanceState.getBoolean(EXTRA_IS_RECORDING);
			mPickedRoom = savedInstanceState.getParcelable(EXTRA_PICKED_ROOM);
		}

		mAdapter = new ArrayAdapter<String>(
				this, R.layout.item_values,
				R.id.textView, mValuesList);

		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		// listView.setStackFromBottom(true);
		listView.setAdapter(mAdapter);

		mToggleButton = (Button) findViewById(R.id.toggleButton);
		mToggleButton.setOnClickListener(this);
		mToggleButton.setText(mRecording ? getString(R.string.stop) + " [" + mValuesList.size() + "]"
				: getString(R.string.start));

		if (mPickedRoom != null) {
			ActionBar actionBar = getActionBar();
			actionBar.setSubtitle(getString(R.string.in_room) + mPickedRoom.name + "[" + mPickedRoom.id + "]");
			mToggleButton.setEnabled(true);
		} else {
			mToggleButton.setEnabled(false);
		}

	}

	@Override
	public void onClick(View v) {
		if (!mRecording) {

			ToastUtils.show(this, getString(R.string.starting_recording) + mPickedRoom.name + " [id=" + mPickedRoom.id + "]...");

			Intent intent = new Intent(this, SnifferService.class);
			intent.setAction(SnifferService.ACTION_START_RECORD_FINGERPRINTS);
			intent.putExtra(SnifferService.EXTRA_RECORDED_ROOM, mPickedRoom.id);
			intent.putExtra(SnifferService.EXTRA_RECEIVER, mReceiver);
			startService(intent);

			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			mToggleButton.setText(R.string.stop);
			mValuesList.clear();
			mRecording = true;

		} else {
			ToastUtils.show(this, getString(R.string.stopping_recording) + mPickedRoom.name + " [id=" + mPickedRoom.id + "]...");

			Intent intent = new Intent(this, SnifferService.class);
			intent.setAction(SnifferService.ACTION_STOP_RECORD_FINGERPRINTS);
			startService(intent);

			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			mToggleButton.setText(R.string.start);
			mRecording = false;
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRoomPicked(Room room) {
		ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(getString(R.string.in_room) + room.name + " [id=" + room.id + "]");

		mToggleButton.setEnabled(true);
		mPickedRoom = room;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putStringArrayList(EXTRA_VALUES_LIST, mValuesList);
		outState.putBoolean(EXTRA_IS_RECORDING, mRecording);
		outState.putParcelable(EXTRA_PICKED_ROOM, mPickedRoom);
	}

	private ResultReceiver mReceiver = new ResultReceiver(new Handler()) {

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			switch (resultCode) {
				case SnifferService.CODE_ACTION_START:
					break;

				case SnifferService.CODE_ACTION_PROGRESS:
					String values = resultData.getString(SnifferService.EXTRA_VALUES);

					mValuesList.add(values);
					mAdapter.notifyDataSetChanged();

					mToggleButton.setText(mRecording ? getString(R.string.stop) + " [" + mValuesList.size() + "]"
							: getString(R.string.start));
					break;

				case SnifferService.CODE_ACTION_SUCCESS:
					ToastUtils.showInfo(RecordActivity.this, R.string.fingerprints_sent_successfully);
					break;

				case SnifferService.CODE_ACTION_ERROR:
					String msg = resultData.getString(SnifferService.EXTRA_ERROR);
					if (msg != null) {
						ToastUtils.showError(
								RecordActivity.this,
								R.string.unable_to_send_fingerprints,
								msg);

					} else {
						ToastUtils.showError(
								RecordActivity.this,
								R.string.unable_to_send_fingerprints,
								R.string.check_internet_connection);
					}
					break;
			}
		};
	};

	private RestUtils.Callback mRoomsAndAPsCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			LOG.d("GetRoomsAndAPs # onSuccess");
			ArrayList<Room> rooms = data.getParcelableArrayList(GetRoomsAndAPsProcessor.EXTRA_ROOMS);

			RoomPickerDialog d = RoomPickerDialog.newInstance(rooms);
			d.setOnRoomPickedListener(RecordActivity.this);
			d.show(getSupportFragmentManager(), RoomPickerDialog.TAG);
		}

		@Override
		public void onStarted() {
		}

		@Override
		public void onException() {
			LOG.d("GetRoomsAndAPs # onException");
			ToastUtils.showError(
					RecordActivity.this,
					R.string.unable_to_get_rooms,
					R.string.check_internet_connection);
		}

		@Override
		public void onError(int code, String message) {
			LOG.d("GetRoomsAndAPs # onError: " + message);
			ToastUtils.showError(
					RecordActivity.this,
					R.string.unable_to_get_rooms,
					message);
		}
	};

}
