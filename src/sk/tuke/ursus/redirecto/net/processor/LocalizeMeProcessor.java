package sk.tuke.ursus.redirecto.net.processor;

import java.io.InputStream;
import java.util.Random;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import sk.tuke.ursus.redirecto.net.RestUtils.Processor;
import sk.tuke.ursus.redirecto.net.RestUtils.Status;
import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;
import sk.tuke.ursus.redirecto.util.LOG;

public class LocalizeMeProcessor extends Processor {

	@Override
	public int onProcessResponse(Context context, String contentType, InputStream stream, Bundle results) throws Exception {
		// Faking it
		ContentResolver resolver = context.getContentResolver();
		
		int randomId = new Random().nextInt(10);
		
		ContentValues values = new ContentValues();
		values.put(Rooms.COLUMN_CURRENT, 1);
		
		int count = resolver.update(Rooms.CONTENT_URI, values, Rooms.COLUMN_ID + "=" + randomId, null);
		LOG.d("RandomId: " + randomId + " - Count: " + count);
		
		resolver.notifyChange(Rooms.CONTENT_URI, null);
		return Status.OK;
	}

}
