package sk.tuke.ursus.redirecto.net.response;

import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse;

import com.google.gson.annotations.SerializedName;

public class RemoveRoomResponse extends JsonRpcResponse {

	@SerializedName("result")
	public String deletedId;

}
