package sk.tuke.ursus.redirecto;

import sk.tuke.ursus.redirecto.util.LOG;
import android.app.IntentService;
import android.content.Intent;

public class SnifferService extends IntentService {

	public SnifferService() {
		super(SnifferService.class.toString());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		LOG.d("SnifferService # onHandleIntent");
		
	}

}
