package br.fatec.tank;
public class GameDataBus {
	private String type;
	private String ip;
	private String tanques;
	private String tiro;
	private String chatMsg;
	private String playerName;
	private String playerServerType;
	private int killTank;
	private int tankId;
	private int tiroId;
	
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public String getPlayerServerType() {
		return playerServerType;
	}
	public void setPlayerServerType(String playerServerType) {
		this.playerServerType = playerServerType;
	}
	public String getChatMsg() {
		return chatMsg;
	}
	public void setChatMsg(String chatMsg) {
		this.chatMsg = chatMsg;
	}
	public int getTiroId() {
		return tiroId;
	}
	public void setTiroId(int tiroId) {
		this.tiroId = tiroId;
	}
	public String getTiro() {
		return tiro;
	}
	public void setTiro(String tiro) {
		this.tiro = tiro;
	}
	public int getTankId() {
		return tankId;
	}
	public void setTankId(int tankId) {
		this.tankId = tankId;
	}
	public int getKillTank() {
		return killTank;
	}
	public void setKillTank(int killTank) {
		this.killTank = killTank;
	}
	public String getTanques() {
		return tanques;
	}
	public void setTanques(String tanques) {
		this.tanques = tanques;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
}
