package sk.tuke.ursus.redirecto.ui;


import sk.tuke.ursus.redirecto.R;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;

public class NewRoomActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ActionBar actionBar = getActionBar();

		// Inflate a "Done" custom action bar view to serve as the "Up" affordance.
		final LayoutInflater inflater = (LayoutInflater) actionBar.getThemedContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		final View customView = inflater.inflate(R.layout.actionbar_custom_view_done, null);
		customView.findViewById(R.id.actionbar_done).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// "Done"
				finish();
			}
		});

		// Show the custom action bar view and hide the normal Home icon and title.
		actionBar.setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setCustomView(customView);
		// END_INCLUDE (inflate_set_custom_view)

		setContentView(R.layout.activity_new_room);

		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.container, NewRoomFragment.newInstance())
					.commit();
		}

	}

}
