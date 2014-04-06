package sk.tuke.ursus.redirecto.ui;

import sk.tuke.ursus.redirecto.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

/**
 * Hlavná aktivita, prakticky len kontainer pre RoomsListFragment
 * 
 * @author Vlastimil Breèka
 * 
 */
public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Allows displaying progress bar in action bar
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.container, RoomsListFragment.newInstance())
					.commit();
		}

	}

}
