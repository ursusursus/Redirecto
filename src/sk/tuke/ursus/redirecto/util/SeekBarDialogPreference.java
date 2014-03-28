package sk.tuke.ursus.redirecto.util;

import sk.tuke.ursus.redirecto.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

public class SeekBarDialogPreference extends DialogPreference {

	private static final int DEFAULT_PROGRESS = 70;
	private SeekBar mSeekBar;
	private int mProgress;

	public SeekBarDialogPreference(Context ctxt, AttributeSet attrs) {
		super(ctxt, attrs);
	}

	@Override
	protected View onCreateDialogView() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.preference_seekbar_dialog, null);
		mSeekBar = (SeekBar) view.findViewById(R.id.seekBar);
		return view;
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		mSeekBar.setProgress(mProgress);
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			mProgress = mSeekBar.getProgress();
			if (callChangeListener(mProgress)) {
				persistInt(mProgress);
			}
		}
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		if (restoreValue) {
			if (defaultValue != null) {
				mProgress = getPersistedInt(Integer.parseInt(defaultValue.toString()));
			} else {
				mProgress = getPersistedInt(DEFAULT_PROGRESS);
			}
		} else {
			mProgress = Integer.parseInt(defaultValue.toString());
		}
	}
}
