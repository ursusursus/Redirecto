package sk.tuke.ursus.redirecto.net.processor;

import java.io.InputStream;

import android.content.Context;
import android.os.Bundle;
import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils;
import sk.tuke.ursus.redirecto.net.RestUtils.Processor;
import sk.tuke.ursus.redirecto.net.RestUtils.Status;

public class LogoutProcessor extends Processor {

	@Override
	public int onProcessResponse(Context context, String contentType, InputStream stream, Bundle results) throws Exception {
		//
		Thread.sleep(1000);
		//
		return Status.OK;
	}

}
