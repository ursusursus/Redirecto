package sk.tuke.ursus.redirecto.ui.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {
	public static final String TAG = "progress_dialog";

	public static ProgressDialogFragment newInstance(String message) {
		Bundle args = new Bundle();
		args.putString("message", message);

		ProgressDialogFragment fragment = new ProgressDialogFragment();
		fragment.setCancelable(false);
		fragment.setArguments(args);

		return fragment;
	}

	public ProgressDialogFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage(getArguments().getString("message"));
		dialog.setIndeterminate(true);
		return dialog;
	}

	@Override
	public void onDestroyView() {
		// Workaround
		if (getDialog() != null && getRetainInstance()) {
			getDialog().setOnDismissListener(null);
		}
		super.onDestroyView();
	}

}
