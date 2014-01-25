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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.awaboom.ursus.agave.LOG;
import com.awaboom.ursus.agave.ToastUtils;

public class RecordActivity extends ActionBarActivity implements OnClickListener, OnRoomPickedListener {

	private static final String EXTRA_VALUES_LIST = "values";

	private boolean mRecording;
	private Button mToggleButton;
	private Room mPickedRoom;
	private ArrayList<String> mValuesList;
	private ArrayAdapter<String> mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);

		if (savedInstanceState == null) {
			mValuesList = new ArrayList<String>();
			mRecording = false;

			MyApplication myApp = (MyApplication) getApplication();
			RestService.getRoomsAndAPs(this, myApp.getToken(), mRoomsAndAPsCallback);
		} else {
			mValuesList = savedInstanceState.getStringArrayList(EXTRA_VALUES_LIST);
		}

		mAdapter = new ArrayAdapter<String>(
				this, R.layout.item_values,
				R.id.textView, mValuesList);

		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		// listView.setStackFromBottom(true);
		listView.setAdapter(mAdapter);

		mToggleButton = (Button) findViewById(R.id.toggleButton);
		mToggleButton.setEnabled(false);
		mToggleButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (!mRecording) {

			ToastUtils.show(this, "Sp��tam meranie v [" + mPickedRoom.id + "] " + mPickedRoom.name + "...");

			Intent intent = new Intent(this, SnifferService.class);
			intent.setAction(SnifferService.ACTION_START_RECORD_FINGERPRINTS);
			intent.putExtra(SnifferService.EXTRA_RECORDED_ROOM, mPickedRoom.id);
			intent.putExtra(SnifferService.EXTRA_RECEIVER, mReceiver);
			startService(intent);

			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			mToggleButton.setText("Stop");
			mRecording = true;

		} else {
			ToastUtils.show(this, "Ukon�ujem meranie v [" + mPickedRoom.id + "] " + mPickedRoom.name + "...");

			Intent intent = new Intent(this, SnifferService.class);
			intent.setAction(SnifferService.ACTION_STOP_RECORD_FINGERPRINTS);
			startService(intent);

			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			mToggleButton.setText("�tart");
			mRecording = false;
		}

	}

	@Override
	public void onRoomPicked(Room room) {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setSubtitle("v miestnosti " + room.name);

		mToggleButton.setEnabled(true);
		mPickedRoom = room;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putStringArrayList(EXTRA_VALUES_LIST, mValuesList);
	}

	private ResultReceiver mReceiver = new ResultReceiver(new Handler()) {
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			String values = resultData.getString(SnifferService.EXTRA_VALUES);
			LOG.d(values);

			mValuesList.add(values);
			mAdapter.notifyDataSetChanged();
		};
	};

	private RestUtils.Callback mRoomsAndAPsCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			ArrayList<Room> rooms = data.getParcelableArrayList(GetRoomsAndAPsProcessor.EXTRA_ROOMS);
			ArrayList<String> aps = data.getStringArrayList(GetRoomsAndAPsProcessor.EXTRA_APS);

			RoomPickerDialog d = RoomPickerDialog.newInstance(rooms);
			d.setOnRoomPickedListener(RecordActivity.this);
			d.show(getSupportFragmentManager(), RoomPickerDialog.TAG);
		}

		@Override
		public void onStarted() {
		}

		@Override
		public void onException() {
			LOG.d("onException");
		}

		@Override
		public void onError(int code, String message) {
			LOG.d("onError: " + message);
		}
	};

}
