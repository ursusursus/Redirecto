package sk.tuke.ursus.redirecto.model;

public class AccessPoint {
	
	public String ssid;
	public int rssi;

	public AccessPoint(String ssid, int rssi) {
		this.ssid = ssid;
		this.rssi = rssi;
	}

}
