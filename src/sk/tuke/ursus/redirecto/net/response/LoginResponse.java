package sk.tuke.ursus.redirecto.net.response;

import com.google.gson.annotations.SerializedName;

import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse;

/**
 * Objekt odpovede API volania Login
 * 
 * @author Vlastimil Breèka
 * 
 */
public class LoginResponse extends JsonRpcResponse {

	public LoginResult result;

	public class LoginResult {
		public String token;
		public String email;
		@SerializedName("directory_number") public String directoryNumber;
		@SerializedName("is_admin") public boolean isAdmin;
	}

}
