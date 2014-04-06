package sk.tuke.ursus.redirecto.ui.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Dialógovı fragment, ktorı zobrazuje pouívate¾ovi neurèitı pokrok, pokia¾
 * daná operácia nezobrazí
 * 
 * @author Vlastimil Breèka
 * 
 */
public class ProgressDialogFragment extends DialogFragment {
	public static final String TAG = "progress_dialog";

	/**
	 * Vyvorí novú inštanciu
	 * 
	 * @param message
	 *        Správa ktorá má by zobrazená
	 * @return Fragment
	 */
	public static ProgressDialogFragment newInstance(String message) {
		Bundle args = new Bundle();
		args.putString("message", message);

		ProgressDialogFragment fragment = new ProgressDialogFragment();
		fragment.setCancelable(false);
		fragment.setArguments(args);

		return fragment;
	}

	/**
	 * Povinnı prázdny konštruktor
	 */
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
		// Workaround for a bug
		if (getDialog() != null && getRetainInstance()) {
			getDialog().setOnDismissListener(null);
		}
		super.onDestroyView();
	}

}
