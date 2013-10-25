package sk.tuke.ursus.redirecto.net.processor;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import sk.tuke.ursus.redirecto.model.Room;
import sk.tuke.ursus.redirecto.net.RestService;
import sk.tuke.ursus.redirecto.net.RestUtils.Processor;
import sk.tuke.ursus.redirecto.net.RestUtils.Status;
import sk.tuke.ursus.redirecto.provider.RedirectoContract.Rooms;

public class GetMyRoomsProcessor extends Processor {

	@Override
	public int onProcessResponse(Context context, String contentType, InputStream stream, Bundle results) throws Exception {
		Thread.sleep(1000);
		
		// DUMMY
		ArrayList<Room> rooms = new ArrayList<Room>();
		rooms.add(new Room(0, "A520", "5. poschodie"));
		rooms.add(new Room(1, "A521", "5. poschodie"));
		rooms.add(new Room(2, "A522", "4. poschodie"));
		rooms.add(new Room(3, "B523", "5. poschodie"));
		rooms.add(new Room(4, "A524", "4. poschodie"));
		rooms.add(new Room(5, "B525", "5. poschodie"));
		rooms.add(new Room(6, "A526", "5. poschodie"));
		rooms.add(new Room(7, "A527", "3. poschodie"));
		rooms.add(new Room(8, "B528", "5. poschodie"));
		rooms.add(new Room(9, "A529", "5. poschodie"));
		
		// Insert to database
		ContentResolver resolver = context.getContentResolver();
		for (Room room : rooms) {
			resolver.insert(Rooms.CONTENT_URI, room.toContentValues());
		}
		// END DUMMY

		//
		// TODO: PERFORM SYNC WITH CURRENT DATABASE
		// DEPENDING ON CHANGED_TIMESTAMP
		//
		
		// Notify change
		resolver.notifyChange(Rooms.CONTENT_URI, null);

		return Status.OK;
	}

}