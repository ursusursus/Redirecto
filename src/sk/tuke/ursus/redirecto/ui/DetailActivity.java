package sk.tuke.ursus.redirecto.ui;

import sk.tuke.ursus.redirecto.MyApplication;
import sk.tuke.ursus.redirecto.R;
import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.awaboom.ursus.agave.LOG;
import com.awaboom.ursus.agave.ToastUtils;

public class DetailActivity extends FragmentActivity {

	public static final String EXTRA_ID = "id";
	public static final String EXTRA_NAME = "name";
	public static final String EXTRA_DESC = "desc";
	public static final String EXTRA_PHONE_NUMBER = "phone_number";
	public static final String EXTRA_IS_CURRENT = "is_current";
	private int mRoomId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		final Intent intent = getIntent();
		mRoomId = intent.getIntExtra(EXTRA_ID, -1);

		TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
		nameTextView.setText(intent.getStringExtra(EXTRA_NAME));

		TextView descTextView = (TextView) findViewById(R.id.descTextView);
		descTextView.setText(intent.getStringExtra(EXTRA_DESC));

		Button removeButton = (Button) findViewById(R.id.removeButton);
		removeButton.setOnClickListener(mClickListener);

		Button forceButton = (Button) findViewById(R.id.forceButton);
		forceButton.setOnClickListener(mClickListener);

	}

	OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			MyApplication myApp = (MyApplication) getApplication();
			switch (v.getId()) {
				case R.id.removeButton:
					RestService.removeMyRoom(
							DetailActivity.this,
							mRoomId,
							myApp.getToken(),
							mRemoveMyRoomCallback);
					break;

				case R.id.forceButton:
					RestService.forceLocalize(
							DetailActivity.this,
							mRoomId,
							myApp.getToken(),
							mForceLocAndForwardCallback);
					break;
			}
		}
	};

	private RestUtils.Callback mForceLocAndForwardCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {

		}

		@Override
		public void onStarted() {

		}

		@Override
		public void onException() {
			ToastUtils.showError(
					DetailActivity.this,
					"Nepodarilo sa nastaviù ako aktu·lnu",
					"Skontrolujte pripojenie na internet");
		}

		@Override
		public void onError(int code, String message) {
			ToastUtils.showError(
					DetailActivity.this,
					"Nepodarilo sa nastaviù ako aktu·lnu",
					message);
		}
	};
	
	private RestUtils.Callback mRemoveMyRoomCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			
		}

		@Override
		public void onStarted() {

		}

		@Override
		public void onException() {

			ToastUtils.showError(
					DetailActivity.this, 
					"Nepodarilo sa odstr·niù miestnosù", 
					"Skontrolujte pripojenie na internet");
		}

		@Override
		public void onError(int code, String message) {
			
			ToastUtils.showError(
					DetailActivity.this, 
					"Nepodarilo sa odstr·niù miestnosù", 
					message);
		}
	};

}
