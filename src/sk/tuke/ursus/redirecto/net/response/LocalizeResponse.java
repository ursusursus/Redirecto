package sk.tuke.ursus.redirecto.net.response;

import java.util.ArrayList;

import sk.tuke.ursus.redirecto.model.Room;
import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse;

import com.google.gson.annotations.SerializedName;

public class LocalizeResponse extends JsonRpcResponse {
	
	public LocalizeResult result;
	
	public class LocalizeResult {
		@SerializedName("calculated_room_id")
		public int localizedRoomId;
	}

}
