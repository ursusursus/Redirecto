package sk.tuke.ursus.redirecto.ui;

import sk.tuke.ursus.redirecto.MyApplication;
import sk.tuke.ursus.redirecto.R;
import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.ui.dialog.ProgressDialogFragment;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.awaboom.ursus.agave.LOG;
import com.awaboom.ursus.agave.ToastUtils;

/**
 * Aktivita prihl�senia pou��vate�a
 * 
 * @author Vlastimil Bre�ka
 * 
 */
public class LoginActivity extends FragmentActivity {

	// public static final String ACTION_RELOGIN =
	// "sk.tuke.ursus.redirecto.ACTION_RELOGIN";

	/**
	 * EditText pou��vate�sk�ho mena
	 */
	private EditText mUsernameEditText;

	/**
	 * EditText hesla
	 */
	private EditText mPasswordEditText;

	/**
	 * Tla�idlo prihl�senia
	 */
	private Button mLoginButton;

	/**
	 * Dial�g pokroku
	 */
	private ProgressDialogFragment mProgressDialog;

	/**
	 * MyApplication
	 */
	private MyApplication mApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		final ActionBar actionBar = getActionBar();
		actionBar.hide();

		mApp = (MyApplication) getApplication();
		if (RestUtils.isTokenValid(mApp.getToken())) {
			// Token is valid, processed to app
			// right away
			goToMainActivity();
		}

		mUsernameEditText = (EditText) findViewById(R.id.usernameEditText);
		mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);

		mLoginButton = (Button) findViewById(R.id.loginButton);
		mLoginButton.setOnClickListener(mClickListener);

		/* boolean relogin = getIntent().getBooleanExtra(ACTION_RELOGIN, false);
		if (relogin) {
			Toast.makeText(this, R.string.session_expired, Toast.LENGTH_SHORT).show();
		} */
	}

	/**
	 * Prihl�si pou��vate�a Skontroluje textov� polia a vykon� REST volanie
	 */
	protected void login() {
		String username = mUsernameEditText.getText().toString();
		String password = mPasswordEditText.getText().toString();

		if ((username == null || username.length() <= 0 || password == null || password.length() <= 0)) {
			ToastUtils.showInfo(this, R.string.enter_name_and_password);

		} else {
			RestService.login(this, username, password, mLoginCallback);
			hideKeyboard();
		}
	}

	/**
	 * Odnaviguje do hlavnej aktivity a ukon�� aktu�lnu
	 */
	private void goToMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);

		finish();
	}

	/**
	 * Zobraz� dial�g pokroku
	 */
	protected void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = ProgressDialogFragment.newInstance(getString(R.string.logging_in));
		}
		mProgressDialog.show(getSupportFragmentManager(), ProgressDialogFragment.TAG);
	}

	/**
	 * Zru�� dialog pokroku
	 */
	protected void dismissProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	/**
	 * Skryje softv�rov� kl�vesnicu
	 */
	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromInputMethod(mUsernameEditText.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
	}

	/**
	 * Na��va� stla�en� tla�idla
	 */
	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			login();
		}
	};

	/**
	 * Sp�tn� volanie API volania prihl�senia
	 */
	private RestUtils.Callback mLoginCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			LOG.d("Login # Status.OK");
			dismissProgressDialog();

			String token = data.getString(RestService.RESULT_TOKEN);
			String email = data.getString(RestService.RESULT_EMAIL);
			boolean isAdmin = data.getBoolean(RestService.RESULT_IS_ADMIN);
			String directoryNumber = data.getString(RestService.RESULT_DIRECTORY_NUMBER);

			if (token != null) {
				// Set values to app singleton
				mApp.setTokenAndMetadata(token, email, isAdmin, directoryNumber);

				/* boolean relogin = getIntent().getBooleanExtra(ACTION_RELOGIN, false);
				if (relogin) {
					setResult(RESULT_OK);
					finish();
				} else {
					goToMainActivity();
				} */

				// and navigate forward
				goToMainActivity();
			}
		}

		@Override
		public void onStarted() {
			showProgressDialog();
		}

		@Override
		public void onError(int code, String message) {
			LOG.d("Login # Status.ERROR");
			dismissProgressDialog();
			ToastUtils.showError(LoginActivity.this, R.string.unable_to_login, message);
		}

		@Override
		public void onException() {
			LOG.d("Login # Status.EXCEPTION");
			dismissProgressDialog();
			ToastUtils.showError(LoginActivity.this, R.string.unable_to_login, R.string.check_internet_connection);
		}

	};

}
