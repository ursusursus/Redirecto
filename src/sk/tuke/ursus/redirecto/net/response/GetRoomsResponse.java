package sk.tuke.ursus.redirecto.net.response;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

import sk.tuke.ursus.redirecto.model.Room;
import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse;

/**
 * Objekt odpovede API volania GetMyRooms a GetAllRooms
 * 
 * @author Vlastimil Breèka
 * 
 */
public class GetRoomsResponse extends JsonRpcResponse {

	@SerializedName("result") public ArrayList<Room> rooms;
}
