package sk.tuke.ursus.redirecto.net.response;

import java.util.ArrayList;

import sk.tuke.ursus.redirecto.model.Room;
import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse;


public class GetRoomsAndAPsResponse extends JsonRpcResponse {
	public GetRoomsAndAPsResult result;
	
	public class GetRoomsAndAPsResult {
		public ArrayList<Room> rooms;
		public ArrayList<String> aps;
	}
}
