package sk.tuke.ursus.redirecto.net.response;

import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse;

import com.google.gson.annotations.SerializedName;

/**
 * Objekt odpovede API volania Localize
 * 
 * @author Vlastimil Breèka
 * 
 */
public class LocalizeResponse extends JsonRpcResponse {

	public LocalizeResult result;

	public class LocalizeResult {
		@SerializedName("calculated_room_id") public int localizedRoomId;
		@SerializedName("calculated_room_name") public String localizedRoomName;
		@SerializedName("calculated_room_desc") public String localizedRoomDesc;
	}

}
