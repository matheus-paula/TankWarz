package br.fatec.menus;

public class PlayerMessageBus {
	private String ip;//ip de uma maquina
	private String name;//nome de uma maquina
	private String myPlayerName;
	private String myServerName;
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMyPlayerName() {
		return myPlayerName;
	}
	public void setMyPlayerName(String myPlayerName) {
		this.myPlayerName = myPlayerName;
	}
	public String getMyServerName() {
		return myServerName;
	}
	public void setMyServerName(String myServerName) {
		this.myServerName = myServerName;
	}
	
	
}
