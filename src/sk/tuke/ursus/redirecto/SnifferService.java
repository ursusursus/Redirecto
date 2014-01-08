package sk.tuke.ursus.redirecto;

import android.app.IntentService;
import android.content.Intent;

import com.awaboom.ursus.agave.LOG;

public class SnifferService extends IntentService {

	public SnifferService() {
		super(SnifferService.class.toString());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		LOG.d("SnifferService # onHandleIntent");
		
	}

}
