package br.fatec.menus;

import java.util.ArrayList;

public class MessageBus {
	private String type;//tipo de requisicao
	private String ip;//ip de uma maquina
	private String name;//nome de uma maquina
	private String myPlayerName;
	private String myServerName;
	private String status;
	private boolean waitingStatus;
	private int playersOnline;
	private int maximumPlayers;
	private ArrayList<String> playersConectados = new ArrayList<String>(); 
	private String playersReady;
	
	
	
	public String getPlayersReady() {
		return playersReady;
	}
	public void setPlayersReady(String playersReady) {
		this.playersReady = playersReady;
	}
	public String getMyServerName() {
		return myServerName;
	}
	public int getPlayersOnline() {
		return playersOnline;
	}
	public void setPlayersOnline(int playersOnline) {
		this.playersOnline = playersOnline;
	}
	public String getMyPlayerName() {
		return myPlayerName;
	}
	public ArrayList<String> getConectados() {
		return playersConectados;
	}
	public void addConectados(String cName) {
		this.playersConectados.add(cName);
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMaximumPlayers() {
		return maximumPlayers;
	}
	public void setMaximumPlayers(int maximumPlayers) {
		this.maximumPlayers = maximumPlayers;
	}
	public boolean isWaiting() {
		return waitingStatus;
	}
	public void setWaitingStatus(boolean waitingStatus) {
		this.waitingStatus = waitingStatus;
	}

}
