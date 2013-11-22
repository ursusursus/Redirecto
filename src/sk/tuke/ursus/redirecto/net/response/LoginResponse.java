package sk.tuke.ursus.redirecto.net.response;

import sk.tuke.ursus.redirecto.net.RestUtils.JsonRpcResponse;


public class LoginResponse extends JsonRpcResponse {

	public LoginResult result;

	public class LoginResult {
		public String token;
		public String email;
	}

}
