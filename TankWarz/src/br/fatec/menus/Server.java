package br.fatec.menus;


import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import br.fatec.tank.Arena;
import br.fatec.tank.ArenaSettings;
import br.fatec.tank.Tank;
import br.fatec.tank.Shot;


public class Server {
	private int maximumPlayers;
	private String connectedServer;
	private String serverType;//TIPO DE SERVER (SÓ CLIENTE OU CLIENTE/SERVER)
	private String serverName;//NOME DO SERVER/CLIENTE
	private String serverStatus;//DEFINE STATUS (FREE/BUSY) DO SERVER
	private boolean serverReady;//DIZ SE O SERVER ESTA PRONTO PARA RECEBER CONEXOES
	private String myPlayerName;//NOME DO JOGADOR (SERVER/CLIENTE)
	private boolean waitingStatus = false;//INDICA QUE O JOGADOR FOI ADICIONADO AO LOBBY (CLIENTE)
	public NetworkScan ips;//CLASSE RESPONSAVEL PELO ESCANEAMENTO DA REDE
    public ServerSocket ss;//SOCKET DE CONEXAO
    public Socket clientSocket;//SOCKET CLIENTE (DESATIVADO)
    public Thread sendThread;//THREAD CLIENTE (DESATIVADO)
    public Thread receiveingThread;//THREAD DE RECEBIMENTO DE DADOS
    public BufferedReader inFromClient = null;//READER
    public boolean running = true;//DETERMINA SE THEAD DE ESCUTA ESTA ATIVA OU NAO
    private String myIp;//IP DA MAQUINA ATUAL
    private ArrayList<String> playersReady = new ArrayList<String>();//LOBBY DE JOGADORES
    private ArrayList<String> playersLogged = new ArrayList<String>();//JOGADORES LOGADOS AGUARDANDO INICIO DO JOGO
    private ArrayList<String> serversReady = new ArrayList<String>();//SERVERS
    
    
	public Server(String type, int port) {
    	this.serverType = type;
        try {
            this.serverType = type;
        	ips = new NetworkScan();
        	myIp = NetworkScan.getCurrentIp();
            ss = new ServerSocket(port);
           // clientSocket = new Socket(serverPublicIP, port);
            startReceiving();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
	public void showMessage(String title, String message){
		JOptionPane.showMessageDialog(null, message,title, 1);
	}
    public String[] getLobbyList(){
    	String[] hostNames = new String[playersReady.size()];
    	for (int i = 0; i < playersReady.size(); i++) {
    		Gson gson = new Gson();
    		PlayerMessageBus lobby = gson.fromJson(playersReady.get(i), PlayerMessageBus.class);
			hostNames[i] = lobby.getMyPlayerName()+" - "+lobby.getName()+" - "+lobby.getIp();
		}
    	return hostNames;
    }
    public String[] getServerList(){
    	String[] serverNames = new String[serversReady.size()];
    	String sStatus = "";
    	for (int i = 0; i < serversReady.size(); i++) {
    		Gson gson = new Gson();
    		MessageBus lobby = gson.fromJson(serversReady.get(i), MessageBus.class);
    		if(serverStatus.equals("free")){
    			sStatus = "Livre";
    		}else{
    			if(serverStatus.equals("busy")){
    				sStatus = "Ocupado";
    			}
    		}
    		serverNames[i] = lobby.getMyServerName()+" - "+lobby.getName()+" - "+lobby.getIp()+" - "+playersReady.size()+"/"+lobby.getMaximumPlayers()+" - "+sStatus;
		}
    	return serverNames;
    }
    
    public String getConnectedServer() {
		return connectedServer;
	}
	public void setConnectedServer(String connectedServer) {
		this.connectedServer = connectedServer;
	}
	public ArrayList<String> getPlayersLogged() {
		return playersLogged;
	}
	public void setPlayersLogged(ArrayList<String> playersLogged) {
		this.playersLogged = playersLogged;
	}
	public String getServerStatus() {
		return serverStatus;
	}
	public void setServerStatus(String serverStatus) {
		this.serverStatus = serverStatus;
	}
	public boolean isWaiting() {
		return waitingStatus;
	}
	public void setWaitingStatus(boolean waitingStatus) {
		this.waitingStatus = waitingStatus;
	}
	public int getMaximumPlayers() {
		return maximumPlayers;
	}
	public void setMaximumPlayers(int maximumPlayers) {
		this.maximumPlayers = maximumPlayers;
	}
	public ArrayList<String> getServersReady() {
		return serversReady;
	}
	public void addServersReady(String serversReady) {
		this.serversReady.add(serversReady);
	}
	public String getServerType() {
		return serverType;
	}
	public String getMyPlayerName() {
		return myPlayerName;
	}
	public String getMyIp() {
		return myIp;
	}
	public boolean isServerReady() {
		return serverReady;
	}
	public void setServerReady(boolean serverReady) {
		this.serverReady = serverReady;
	}
	public void setMyPlayerName(String myPlayerName) {
		this.myPlayerName = myPlayerName;
	}
	public ArrayList<String> getPlayersReady() {
		return playersReady;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
    public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public static boolean isJSONValid(String jsonInString) {
    	Gson gson = new Gson();
        try {
            gson.fromJson(jsonInString, Object.class);
            return true;
        } catch(JsonSyntaxException ex) { 
            return false;
        }
    }
    
    public void startGame(String ip) throws IOException{
    	//INICIA A ARENA NO CLIENTE
		JFrame janela = new JFrame("Tank War Z");
		janela.setResizable(false);
		janela.setDefaultCloseOperation(3);
		janela.setIconImage(Toolkit.getDefaultToolkit().getImage(Start.class.getResource("/br/fatec/menus/resources/icons/warz_logo_48.png")));
    	Arena arena = new Arena(800,640, serverType, this);
		arena.adicionaTanque(new Tank(400,50,180,Color.BLUE,1));
		arena.adicionaTanque(new Tank(400,200,0,Color.RED,2));
		arena.adicionaTanque(new Tank(400,300,270,Color.GREEN,3));
		arena.adicionaTanque(new Tank(200,50,90,Color.YELLOW,4));
		arena.adicionaTanque(new Tank(100,120,270,Color.GRAY,5));
		arena.adicionaTanque(new Tank(180,307,180,Color.MAGENTA,6));
		arena.adicionaTanque(new Tank(520,208,23,Color.CYAN, 7));
		arena.adicionaTanque(new Tank(300,300,47,Color.ORANGE,8));
		for (Tank t: arena.getTanques()) {
			t.setTiro(new Shot(-20,-20,0,Color.BLACK,-1));
		}
		janela.getContentPane().add(arena);
		janela.pack();
		janela.addWindowListener(new WindowAdapter(){
		    public void windowClosing(WindowEvent e){
		       setRunning(false);
		    }
		});
		//CLIENTE DIZ AO SERVIDOR QUE ESTA PRONTO PARA COMEÇAR O JOGO
    	JsonObject imReady = new JsonObject();
    	imReady.addProperty("type", "ready");
    	imReady.addProperty("ip", getMyIp());
    	try {
			new SendMessage(ip,ArenaSettings.getArenaPort(),imReady.toString(),1000);
		} catch (SocketTimeoutException timeout){
        	showMessage("Conexão Interrompida","A conexão com o host da sala foi perdida.");
		}catch (SocketException e) {
			e.printStackTrace();
		}
		janela.setVisible(true);
		//PARA SINCRONIZAÇÃO DE DADOS DO MENU
    	stopReceiving();
    }
    
    public synchronized void startReceiving() {
        receiveingThread = new Thread("Recieve") {
            public void run() {
                String dataFromClient = new String("");
                while (running) {
                	Socket escuta;
                    try {
                    	escuta = ss.accept();
                        inFromClient = new BufferedReader(new InputStreamReader(escuta.getInputStream()));
                        dataFromClient = inFromClient.readLine();
                    }catch (Exception e) {
                        //e.printStackTrace();
                    }
                    if (dataFromClient.equals("TERMINATOR_KEY")){
                        stopReceiving();
                    }
                    else if(!dataFromClient.equals("")){
                        Gson gson = new Gson();
                        try {
                        	//DESSERIALIZA DADOS RECEBIDOS EM JSON
                        	MessageBus join = gson.fromJson(dataFromClient, MessageBus.class);
                        	      	
                        	//ATUALIZA LISTA DE PLAYERS DA SALA
                        	if(join.getPlayersReady() != null && serverType.equals("Client")){
                        		ArrayList<PlayerMessageBus> playersReadyFromServer = gson.fromJson(join.getPlayersReady(), new TypeToken<ArrayList<PlayerMessageBus>>(){}.getType());
                        		playersReady.clear();
                        		for (int i = 0; i < playersReadyFromServer.size(); i++) {
                        			JsonObject player = new JsonObject();
                        			player.addProperty("ip", playersReadyFromServer.get(i).getIp());
                        			player.addProperty("name", playersReadyFromServer.get(i).getName());
                        			player.addProperty("myPlayerName", playersReadyFromServer.get(i).getMyPlayerName());
                        			player.addProperty("myServerName", playersReadyFromServer.get(i).getMyServerName());
                                	playersReady.add(player.toString());
								}	
                        	}
                  
                        	//CLIENTE REQUERE CONEXÃO AO SERVER (SALA)
                            if(join.getType().equals("joinRequest")){
                            	//SE O MAXIMO DE PLAYERS NA SALA NAO FOI ATINGIDO E O SERVIDOR ESTA LIVRE
                            	//ADICIONA PLAYER AO LOBBY
	                            if(playersReady.size() < maximumPlayers && !playersReady.contains(dataFromClient)){
	                            	playersReady.add(dataFromClient);//IP
	                            	JsonObject resToClient = new JsonObject();
	                            	resToClient.addProperty("ip", myIp);
	                            	resToClient.addProperty("name", InetAddress.getLocalHost().getHostName());
	                            	resToClient.addProperty("type", "addedToLobby");
	                            	resToClient.addProperty("waitingStatus", true);
	                            	new SendMessage(join.getIp(),CommonMethods.getDefaultPort(),resToClient.toString(),1000);
	                            	System.out.println(">"+join.getMyPlayerName()+" foi adicionado ao lobby!");
	                            	//adiciona requisição de jogatina para a sala
	                            }
	                        //CLIENTE VERIFICA SE ESTE PC É UM SERVER 
	                        //E	O SERVER RESPONDE COM SEUS DADOS DE SERVER    
                            }else if(join.getType().equals("serverCheck") && serverType.equals("Server")){
                            	JsonObject meuServer = new JsonObject();
                            	meuServer.addProperty("ip", myIp);
                            	meuServer.addProperty("name", InetAddress.getLocalHost().getHostName());
                            	meuServer.addProperty("type", "serverResponse");
                            	meuServer.addProperty("playersOnline", playersReady.size());
                            	meuServer.addProperty("playersReady", playersReady.toString());
                            	meuServer.addProperty("maximumPlayers", maximumPlayers);
                            	meuServer.addProperty("myPlayerName",myPlayerName);
                            	//DIZ AO CLIENTE SE O SERVER ESTA CHEIO/OCUPADO OU NÃO
                            	if(playersReady.size() >= maximumPlayers || serverReady == false){
                            		meuServer.addProperty("status", "busy");//SERVER OCUPADO/PARTIDA INICIADA
                            	}else{
                            		meuServer.addProperty("status", "free");//SERVER LIVRE NO LOBBY
                            	}
                            	new SendMessage(join.getIp(),CommonMethods.getDefaultPort(),meuServer.toString(),1000);
                            
                            //CLIENTE PROCESSA RESPOSTA DO SERVER E SOLICITA CONEXÃO AO LOBBY
                            }else if(join.getType().equals("serverResponse") && join.getStatus().equals("free")){
                            	String hostname = InetAddress.getLocalHost().getHostName();
                            	JsonObject cliente = new JsonObject();
                            	cliente.addProperty("ip", myIp);
                            	cliente.addProperty("name", hostname);
                            	cliente.addProperty("type", "joinRequest");
                            	if(playersReady.size() > 0 && playersReady.size() < maximumPlayers && serverReady == true){
                            		cliente.addProperty("status", "free");
                            	}else{
                            		cliente.addProperty("status", "busy");
                            		System.out.println(">Server ocupado...");
                            	}
                            	new SendMessage(join.getIp(),CommonMethods.getDefaultPort(),cliente.toString(),1000);
                            //APENAS RESPONDE AO CLIENTE QUE SE TRATA DE UM SERVER 
                            }else if(join.getType().equals("isServer") && serverType.equals("Server")){
                            	JsonObject isServer = new JsonObject();
                            	isServer.addProperty("ip", myIp);
                            	isServer.addProperty("name", InetAddress.getLocalHost().getHostName());
                            	isServer.addProperty("type", "serverType");
                            	isServer.addProperty("playersOnline", playersReady.size());
                            	isServer.addProperty("maximumPlayers", maximumPlayers);
                            	isServer.addProperty("myServerName",serverName);
                            	isServer.addProperty("myPlayerName",myPlayerName);
                            	isServer.addProperty("playersReady",playersReady.toString());
                            	//DIZ AO CLIENTE SE O SERVER ESTA CHEIO/OCUPADO OU NÃO
                            	if(playersReady.size() >= maximumPlayers || serverReady == false){
                            		isServer.addProperty("status", "busy");//SERVER OCUPADO/PARTIDA INICIADA
                            	}else{
                            		isServer.addProperty("status", "free");//SERVER LIVRE NO LOBBY
                            	}
                            	new SendMessage(join.getIp(),CommonMethods.getDefaultPort(),isServer.toString(),1000);
                            	
                            //CLIENTE CONFIRMA SERVIDOR ENCONTRADO E ADICIONA NA LISTA DE SERVERS
                            }else if(join.getType().equals("serverType")){
                            	//ADICIONA O SERVIDOR ENCONTRADO NA LISTA DE SERVIDORES
                            	JsonObject server = new JsonObject();
                            	server.addProperty("name", join.getName());
                            	server.addProperty("type", "serverType");
                            	server.addProperty("ip", join.getIp());
                            	server.addProperty("myServerName", join.getMyServerName());
                            	server.addProperty("myPlayerName",join.getMyPlayerName());
                            	server.addProperty("playersOnline",join.getPlayersOnline());
                            	server.addProperty("maximumPlayers",join.getMaximumPlayers());
                            	server.addProperty("status", join.getStatus());
                            	
                            	if(join.getStatus().equals("free")){
	                            	if(!serversReady.contains(server.toString())){
	                            		serversReady.add(server.toString());
	                            	}else{
	                            		serversReady.set(serversReady.indexOf(server.toString()), server.toString());
	                            	}
                            	}
                            	maximumPlayers = join.getMaximumPlayers();
                            	serverStatus = join.getStatus();
                            //RECEBE MENSAGEM DE CONFIRMAÇÃO DO SERVIDOR DE QUE FOI ADICIONADO AO LOBBY 
                            }else if(join.getType().equals("addedToLobby") && serverType.equals("Client")){
                            	waitingStatus = join.isWaiting();
                            	connectedServer = join.getIp();
                            //RECEBE DIZ AO SERVIDOR QUE ESTA PRONTO PARA COMEÇAR PARTIDA	
                            }else if(join.getType().equals("startGame") && serverType.equals("Client")){
                            	startGame(join.getIp());
                            }
                           
                        }catch (Exception e) {
                        	//e.printStackTrace();
                            System.out.println(">JSON error");  
                        }           
                    }    
                }
            }
        };
        receiveingThread.start();
    }

    public synchronized void stopReceiving() {
        try {
        	running = false;
            receiveingThread.join();
            ss.close();
            //clientSocket.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        //System.exit(0);
    }
}
