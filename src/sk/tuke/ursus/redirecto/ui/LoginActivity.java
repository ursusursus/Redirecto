package sk.tuke.ursus.redirecto.ui;

import sk.tuke.ursus.redirecto.MyApplication;
import sk.tuke.ursus.redirecto.R;
import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.ui.dialog.ProgressDialogFragment;
import sk.tuke.ursus.redirecto.util.LOG;
import sk.tuke.ursus.redirecto.util.ToastUtils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

	public static final String ACTION_RELOGIN = "sk.tuke.ursus.redirecto.ACTION_RELOGIN";

	private EditText mUsernameEditText;
	private EditText mPasswordEditText;
	private Button mLoginButton;

	private ProgressDialogFragment mProgressDialog;
	private MyApplication mApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.hide();

		mApp = (MyApplication) getApplication();
		if (RestUtils.isTokenValid(mApp.getToken())) {
			goToMainActivity();
		}

		mUsernameEditText = (EditText) findViewById(R.id.usernameEditText);
		mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);

		mLoginButton = (Button) findViewById(R.id.loginButton);
		mLoginButton.setOnClickListener(mClickListener);

		boolean relogin = getIntent().getBooleanExtra(ACTION_RELOGIN, false);
		if (relogin) {
			Toast.makeText(this, "Relácia vypršala, prihláste sa znova", Toast.LENGTH_SHORT).show();
		}
	}

	protected void login() {
		String username = mUsernameEditText.getText().toString();
		String password = mPasswordEditText.getText().toString();

		if ((username == null || username.length() <= 0 || password == null || password.length() <= 0)) {
			ToastUtils.showInfo(this, "Zadajte meno alebo heslo")	;

		} else {
			RestService.login(this, username, password, mLoginCallback);
			hideKeyboard();
		}
	}

	private void goToMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);

		finish();
	}

	protected void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = ProgressDialogFragment.newInstance("Prihlasujem sa...");
		}
		mProgressDialog.show(getSupportFragmentManager(), ProgressDialogFragment.TAG);
	}

	protected void dismissProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromInputMethod(mUsernameEditText.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			login();
		}
	};

	private RestUtils.Callback mLoginCallback = new RestUtils.Callback() {

		@Override
		public void onSuccess(Bundle data) {
			LOG.d("Login # Status.OK");
			dismissProgressDialog();

			String token = data.getString(RestService.RESULTS_TOKEN_KEY);
			String email = data.getString(RestService.RESULTS_EMAIL_KEY);

			if (token != null) {
				mApp.setTokenAndUsername(token, email);

				boolean relogin = getIntent().getBooleanExtra(ACTION_RELOGIN, false);
				if (relogin) {
					setResult(RESULT_OK);
					finish();
				} else {
					goToMainActivity();
				}
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
			ToastUtils.showError(LoginActivity.this, "Nepodarilo sa prihlási: " + message);
		}

		@Override
		public void onException() {
			LOG.d("Login # Status.EXCEPTION");
			dismissProgressDialog();
			ToastUtils.showError(LoginActivity.this, "Nepodarilo sa prihlási");
		}

	};

}
