package sk.tuke.ursus.redirecto.net.response;

import sk.tuke.ursus.redirecto.model.Room;
import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse;

import com.google.gson.annotations.SerializedName;

public class AddMyRoomResponse extends JsonRpcResponse {
	
	@SerializedName("result")
	public Room insertedRoom;

}
