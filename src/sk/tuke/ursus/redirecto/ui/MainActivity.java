package sk.tuke.ursus.redirecto.ui;

import android.os.Bundle;
import sk.tuke.ursus.redirecto.R;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.container, RoomsListFragment.newInstance())
					.commit();
		}

	}

}
