package br.fatec.tank;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import br.fatec.menus.SendMessage;
import br.fatec.menus.PlayerMessageBus;
import br.fatec.menus.Server;

@SuppressWarnings("serial")
public class Arena extends JComponent 
		implements MouseListener, ActionListener, KeyListener{
	private Tank apontado;
	private int largura,altura;
	private HashSet<Tank> tanques;
	private Shot tiro;
	private Timer contador;
	private int girando;
	private long ultimaColisao;
	private long agora;
	private int tanqueAnterior = -1;
	private Server s;
	private Chat chat;
	public boolean running = true;
	public Thread receiveingThread;
	public ServerSocket ss;
	public BufferedReader inFromClient = null;

	public void windowGainedFocus(WindowEvent e) {
        System.out.println("Window Gained Focus Event");
     }
	public void windowLostFocus(WindowEvent e) {
        System.out.println("Window Lost Focus Event");
    }
	
	public Arena(int largura,int altura, String type, Server s) throws IOException{
		ss = new ServerSocket(ArenaSettings.getArenaPort());
		this.largura = largura; 
		this.altura = altura;
		this.s = s;
		ArenaSettings.setAlturaTela(altura);
		ArenaSettings.setLarguraTela(largura);
		tanques = new HashSet<Tank>();
		tiro = new Shot(-20, -20, 0, Color.BLACK,-1);
		addMouseListener(this);
		addKeyListener(this);
		setFocusable(true);
		contador = new Timer(30, this);
		contador.start();
		startReceiving();
		chat = new Chat(this,"Chat");
	}
	
	public int getLargura() {
		return largura;
	}
	public void setLargura(int largura) {
		this.largura = largura;
	}
	public int getAltura() {
		return altura;
	}
	public void setAltura(int altura) {
		this.altura = altura;
	}
	public HashSet<Tank> getTanques() {
		return tanques;
	}
	public void setTanques(HashSet<Tank> tanques) {
		this.tanques = tanques;
	}
	public Shot getTiro() {
		return tiro;
	}
	public void setTiro(Shot tiro) {
		this.tiro = tiro;
	}
	public void adicionaTanque(Tank t){
		tanques.add(t);
	}
	public Dimension getMaximumSize(){
		return getPreferredSize();
	}
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}
	public Dimension getPreferredSize(){
		return new Dimension(largura,altura);
	}
	public int getTanqueAnterior() {
		return tanqueAnterior;
	}
	public void setTanqueAnterior(int tanqueAnterior) {
		this.tanqueAnterior = tanqueAnterior;
	}
	public void showMessage(String title, String message){
		JOptionPane.showMessageDialog(null, message,title, 1);
	}
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(new Color(245,245,255));
		g2d.fillRect(0,0,largura,altura);
		g2d.setColor(new Color(220,220,220));
		for(int _largura=0;_largura<=largura;_largura+=20)
			g2d.drawLine(_largura,0,_largura,altura);
		for(int _altura=0;_altura<=altura;_altura+=20) 
			g2d.drawLine(0,_altura,largura,_altura);
		tiro.draw(g2d);
		// DESENHA-SE TODOS OS TANQUES E SEUS TIROS
		for(Tank t:tanques) {
			t.getTiro().draw(g2d);
			t.draw(g2d);
		}
		
	}
    public void mouseClicked(MouseEvent e){
    	requestFocusInWindow();
    	chat.setTransparency(true);
		for(Tank t:tanques){
			t.setEstaAtivo(false);
			if(tanqueAnterior == t.getId()){
				t.setName("");
				//AVISA SERVIDOR QUE TANQUE ANTIGO NAO ESTA MAIS SENDO USADO
            	if(s.getServerType().equals("Client")){
    				JsonObject sres = new JsonObject();
    				sres.addProperty("ip", s.getMyIp());
    				sres.addProperty("type", "tankChanged");
    				sres.addProperty("tankId",t.getId());
					try {
						new SendMessage(s.getConnectedServer(),ArenaSettings.getArenaPort(),sres.toString(),1000);
					} catch (SocketTimeoutException timeout){
                    	showMessage("Conexão Interrompida","A conexão com o host da sala foi perdida.");
            		} catch (SocketException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (UnknownHostException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (IOException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
            	}
			}
		}
		for(Tank t:tanques){
			boolean clicado = t.getRectEnvolvente().contains(e.getX(),e.getY());
			if(!t.isEstaAtivo() && s.getServerType().equals("Server")){
				t.setName("");
			}
			if (clicado && !t.isEstaAtivo() && t.getName() != null && t.getName().isEmpty()){
				t.setEstaAtivo(true);
				t.setName(s.getMyPlayerName());
				apontado=t;
				tanqueAnterior = t.getId();
			}
		}
		repaint();
	}
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	
	public void actionPerformed(ActionEvent e){
		colisao();
		for(Tank t:tanques){
			//SÓ MOVE AUTOMATICAMENTE TANQUES SE FOR PLAYER OU SERVER
			if(s.getServerType().equals("Server") || t.isEstaAtivo() == true){
				t.mover();
			}
			t.calculaTempo();
		}
		//MOVE TIRO DO PLAYER NA TELA
		tiro.mover();
		
		//MOVE TIRO DOS OUTROS PLAYERS NA TELA
		for (Tank t : tanques) {
			if(t != null){
				t.tiro.mover();
			}
		}
		repaint();
	}
	
	public void sendChatMessage(String msg){
		Gson gson = new Gson();
		JsonObject msgObj = new JsonObject();
		msgObj.addProperty("playerServerType", s.getServerType());
		msgObj.addProperty("playerName", s.getMyPlayerName());
		msgObj.addProperty("chatMsg", msg);
		msgObj.addProperty("ip", s.getMyIp());
		
		if(s.getServerType().equals("Server")){
			msgObj.addProperty("type", "chatMsg");
			for (int i = 0; i < s.getPlayersReady().size(); i++) {	    		
    			PlayerMessageBus player = gson.fromJson(s.getPlayersReady().get(i), PlayerMessageBus.class);
    			if(!player.getIp().equals(s.getMyIp())){
    				try {
						new SendMessage(player.getIp(),ArenaSettings.getArenaPort(),msgObj.toString(),1000);
					} catch (SocketTimeoutException e){
                		chat.streamMsg(">>Não foi possível enviar a mensagem!");
                	} catch (SocketException e) {

					} catch (UnknownHostException e) {

					} catch (IOException e) {

					}
    			}
			}	
		}else{
			if(s.getServerType().equals("Client")){
				msgObj.addProperty("type", "redirectChatMsg");
				try {
					new SendMessage(s.getConnectedServer(),ArenaSettings.getArenaPort(),msgObj.toString(),1000);
				} catch (SocketTimeoutException e){
					chat.streamMsg(">>Não foi possível enviar a mensagem!");
            	} catch (SocketException e) {
				
				} catch (UnknownHostException e) {

				} catch (IOException e) {

				}
			}
		}
		chat.streamMsg( ((s.getServerType().equals("Server")) ? "[HOST] ":"")+s.getMyPlayerName()+" diz:"+msg);
	}
	
	public void colisao(){
		if(tiro.estaAtivo){
			for(Tank t : tanques){
				if(t.getId() != tiro.getId()){ 
					double dist = Math.sqrt(Math.pow(tiro.x - t.x, 2) + Math.pow(tiro.y - t.y, 2));
					if(dist <= 20){
						tiro.x = -30;
						tiro.y = -30;
						//FORA DA ARENA
						tanques.remove(t);
						JsonObject killTank = new JsonObject();
						killTank.addProperty("type", "killTank");
						killTank.addProperty("killTank", t.getId());
						killTank.addProperty("tiroId", tiro.getId());
						
						Gson gson = new Gson();
				    	for (int i = 0; i < s.getPlayersReady().size(); i++) {	    		
			    			PlayerMessageBus player = gson.fromJson(s.getPlayersReady().get(i), PlayerMessageBus.class);
			    			try {
								new SendMessage(player.getIp(),ArenaSettings.getArenaPort(),killTank.toString(),1000);
							} catch (SocketTimeoutException timeout){ 
		                    	showMessage("Conexão Interrompida","A conexão com um dos jogadores foi perdida.");
				    	    } catch (SocketException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						tiro.estaAtivo = false;
						break;
					}
					/*TANQUE TENTA SE ESQUIVAR DE MISSEL*/
					if(dist < 100){
						t.setTempo(System.currentTimeMillis());
						if(agora%2 == 0)
							t.girarAntiHorario(7);
						else
							t.girarHorario(7);
						
						t.velocidade = 2.8;
					}
				}
			}
		}
		for(Tank t : tanques){
			autoColisao(t);
		}
	}

	public void autoColisao(Tank tanque){	
		for(Tank t : tanques){/*verifica a distancia para checar colisão entre os  tanques*/
			if(tanque.getId() != t.getId()){
				double dist = Math.sqrt(Math.pow(tanque.x - t.x, 2) + Math.pow(tanque.y - t.y, 2));
				
				if(!tanque.estaAtivo){
					if(girando < 55){
						if(dist <= 65){		
							tanque.velocidade = 0.4;
							tanque.setTempo(System.currentTimeMillis());
							if(agora%2 == 0){						
								tanque.girarHorario(5);					
							}
							girando++;
							if(girando == 54){
								//PARA DE GIRAR E SEGUE O RUMO
								ultimaColisao = System.currentTimeMillis();
							}				
						}
					}else{
						tanque.velocidade = 1;
						girando = 55;//DESABILITA GIRO TEMPORARIAMENTE
						if(System.currentTimeMillis() > ultimaColisao+800){
							girando = 0;
						}
					}
				}
				if(dist < 80 &&  tanque.estaAtivo){
					//TANQUE INIMIGO TENTA FUGIR
					t.setTempo(System.currentTimeMillis());
					if(agora%2 == 0){
						t.girarAntiHorario(5);						
					}else{
						t.girarHorario(5);
					}
					t.velocidade = 1.8;
				}			
			}	
		}
	}
	public void keyPressed(KeyEvent e) {
		for(Tank t:tanques){
			t.setEstaAtivo(false);
			if(t==apontado){
				t.setEstaAtivo(true);
				switch(e.getKeyCode()){
			    case KeyEvent.VK_LEFT: t.girarAntiHorario(2); break;
		        case KeyEvent.VK_UP: t.aumentarVelocidade(); break;
		        case KeyEvent.VK_DOWN : t.diminuirVelocidade(); break;
				case KeyEvent.VK_RIGHT: t.girarHorario(2); break;
				case KeyEvent.VK_SPACE: 
				{
					atirar(t.getId());
					agora = System.currentTimeMillis();
				}break;
			}
			break;
			}
			repaint();
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	public void atirar(int id){
		for(Tank t:tanques){
			if(t.estaAtivo){
				if(! tiro.estaAtivo){
					tiro.x = t.x + Math.sin(t.angulo);
					tiro.y = t.y + Math.cos(t.angulo);
					tiro.angulo = t.angulo;
					tiro.estaAtivo = true;	
					tiro.setId(t.getId());

					//ENVIA TIRO AO CLIENTE
					JsonObject meuTiro = new JsonObject();
					meuTiro.addProperty("id", t.getId());
					meuTiro.addProperty("x", tiro.x);
					meuTiro.addProperty("y", tiro.y);
					meuTiro.addProperty("angulo", tiro.angulo);
					meuTiro.addProperty("estaAtivo", true);
					
					JsonObject res = new JsonObject();
					res.addProperty("type", "shotFired");
					res.addProperty("tiro", meuTiro.toString());
					
					Gson gson = new Gson();
					if(s.getServerType().equals("Server")){
						for (int i = 0; i < s.getPlayersReady().size(); i++) {
				    		PlayerMessageBus player = gson.fromJson(s.getPlayersReady().get(i), PlayerMessageBus.class);
				    		if(!player.getIp().equals(s.getMyIp())){
				    			try {
									new SendMessage(player.getIp(),ArenaSettings.getArenaPort(),res.toString(),1000);
								} catch (SocketException e) {
									
								} catch (UnknownHostException e) {
		
								} catch (IOException e) {
									
								}
				    		}
						}
					}else{
						if(s.getServerType().equals("Client")){
							try {
								new SendMessage(s.getConnectedServer(),ArenaSettings.getArenaPort(),res.toString(),1000);
							} catch(SocketTimeoutException timeout){
								showMessage("Conexão Interrompida","A conexão com o host da sala foi perdida.");
							} catch (SocketException e) {
								
							} catch (UnknownHostException e) {

							} catch (IOException e) {

							}	
						}
					}
				}
			}
		}
	}
	
	public synchronized void startReceiving() {
		receiveingThread = new Thread("GameDataReceive") {
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
		            }else if(!dataFromClient.equals("")) {
		                Gson gson = new Gson();
		                try {
		                	//RECEBE DADOS DO CLIENTE/SERVIDOR USANDO A CLASSE DE APOIO GAMEDATABUS
		                	GameDataBus gameData = gson.fromJson(dataFromClient, GameDataBus.class);
		                	int curTank = -1;
		                	
		            
		                	//SE O QUE CHEGA É DE UM CLIENTE TRATA DADOS DO CLIENTE (SERVIDOR)
		                	if(gameData.getType() != null && gameData.getType().equals("clientResponse")){
		                		//DOS DADOS ENVIADOS PELO CLIENTE RECUPERA A LISTA DE TANQUES
		                		HashSet<Tank> clientTanques = gson.fromJson(gameData.getTanques().toString(), new TypeToken<HashSet<Tank>>(){}.getType());
		                		
		                		//PARA CADA TANQUE DO CLIENTE
		                		for (Tank tanque : clientTanques) {
		                			//RECEBE DADOS APENAS DO TANQUE QUE O USUARIO ESTA CONTROLANDO
									if(tanque.isEstaAtivo() == true){
										//PERCORRE TANQUES DO SERVIDOR
										for (Tank stanques : tanques) {
											//SE O TANQUE NO SERVIDOR FOR IGUAL AO DO CLIENTE 
											//DURANTE O FOR ATUALIZA DADOS DO CLIENTE NA LISTA DO SERVIDOR
											if(stanques.getId() == tanque.getId()){
												//PASSA DADOS DO TANQUE SELECIONADO DO CLIENTE PARA O SERVIDOR
												stanques.x = tanque.getX();
												stanques.y = tanque.getY();
												stanques.name = tanque.getName();
												stanques.angulo = tanque.getAngulo();
												stanques.setTempo(tanque.getTempo());
												stanques.velocidade = tanque.getVelocidade();
												curTank = tanque.getId();
												//SAI DO LOOP POIS TANQUE FOI ENCONTRADO
												//DPS ADICIONAR OU NAO ESTA ATIVO
											}
										}
									}
		                		}
		                		
								//RESPOSTA AO CLIENTE - SERIALIZA DADOS E OS ENVIA PARA O CLIENTE QUE REQUISITOU
		                		ArrayList<String> resTanques = new ArrayList<String>();
		                		for (Tank t : tanques) {
		                			JsonObject tanqueVar = new JsonObject();
		                			tanqueVar.addProperty("x", t.x);
		                			tanqueVar.addProperty("y", t.y);
		                			tanqueVar.addProperty("angulo", t.angulo);
		                			tanqueVar.addProperty("name", t.getName());
		                			tanqueVar.addProperty("velocidade", t.velocidade);
		                			tanqueVar.addProperty("estaAtivo", t.isEstaAtivo());
		                			tanqueVar.addProperty("id", t.getId());
									resTanques.add(tanqueVar.toString());
								}
		 
								JsonObject resToClient = new JsonObject();
		                    	resToClient.addProperty("ip", s.getMyIp());
		                    	resToClient.addProperty("name", InetAddress.getLocalHost().getHostName());
		                    	resToClient.addProperty("type", "serverResponse");
		                    	resToClient.addProperty("tanques", resTanques.toString());
		                    	
		                    	//MANDA DE VOLTA DADOS ATUALIZADOS PARA CLIENTE
								try{
									new SendMessage(gameData.getIp(),ArenaSettings.getArenaPort(),resToClient.toString(),1000);
								}catch (SocketTimeoutException e){
									//REMOVE PLAYER AUSENTE DE JOGO
		                    		for (Tank t : tanques) {
										if(curTank == t.getId()){
											t.setName("");
											t.setEstaAtivo(false);
										}
									}
		                    	}	

							//SE O QUE CHEGA É DE UM SERVIDOR TRATA DADOS DO SERVIDOR
		                	}else if(gameData.getType() != null && gameData.getType().equals("serverResponse")){
		                		//PARA CADA TANQUE DO CLIENTE
		                		for (Tank tanque : tanques) {
		                			//SE NAO FOR O TANQUE DO CLIENTE
									if(tanque.estaAtivo == false){
										//RECEBE DEMAIS TANQUES DO SERVER
										HashSet<Tank> stanques = gson.fromJson(gameData.getTanques().toString(), new TypeToken<HashSet<Tank>>(){}.getType());
										//PARA CADA TANQUE DO SERVER
										for (Tank serverTanque : stanques) {
											//SE FOR O MESMO TANQUE ATUALIZA SEUS DADOS
											if(serverTanque.getId() == tanque.getId()){
												tanque.x = serverTanque.getX();
												tanque.y = serverTanque.getY();
												tanque.angulo = serverTanque.getAngulo();
												tanque.name = serverTanque.getName();
												tanque.velocidade = serverTanque.getVelocidade();
												tanque.setTempo(serverTanque.getTempo());			
											}
										}
									}
								}
		                		
		                		ArrayList<String> resTanques = new ArrayList<String>();
		                		for (Tank t : tanques) {
		                			JsonObject tanqueVar = new JsonObject();
		                			tanqueVar.addProperty("x", t.x);
		                			tanqueVar.addProperty("y", t.y);
		                			tanqueVar.addProperty("angulo", t.angulo);
		                			tanqueVar.addProperty("velocidade", t.velocidade);
		                			tanqueVar.addProperty("name", t.getName());
		                			tanqueVar.addProperty("estaAtivo", t.isEstaAtivo());
		                			tanqueVar.addProperty("id", t.getId());
									resTanques.add(tanqueVar.toString());
								}
		                		//RESPOSTA AO SERVIDOR - SERIALIZA DADOS DO CLIENTE E ENVIA PARA O SERVIDOR
								JsonObject resToServer = new JsonObject();
		                    	resToServer.addProperty("ip", s.getMyIp());
		                    	resToServer.addProperty("name", InetAddress.getLocalHost().getHostName());
		                    	resToServer.addProperty("type", "clientResponse");
		                    	resToServer.addProperty("tanques", resTanques.toString());
		
		                    	//MANDA DE VOLTA DADOS ATUALIZADOS PARA O SERVIDOR
		                    	try{
		                    		new SendMessage(gameData.getIp(),ArenaSettings.getArenaPort(),resToServer.toString(),1000);
		                    	}catch (SocketTimeoutException e){
		                    		showMessage("Conexão Interrompida","A conexão com o host da sala foi perdida.");
		                    	}
							//ADICIONA A LISTA DE PLAYERS PRONTOS PRA JOGO DO SERVIDOR
		                	}else if(gameData.getType().equals("ready") && s.getServerType().equals("Server")){
		                    	s.getPlayersLogged().add(gameData.getIp());
		                    }
		                	
		                	//MATA TANQUE (CLIENTE)
		                	if(gameData.getType() != null && gameData.getType().equals("killTank")){		
		                		for (Tank t:tanques) {
		                			//REMOVE TIRO QUE ACERTOU TANQUE
		                			if(t.getId() == gameData.getTiroId()){
		                				t.tiro.setX(-30);
		                				t.tiro.setY(-30); 
		                				t.tiro.setEstaAtivo(false);
		                				break;
		                			}
								}		                		
		                		for(Tank t : tanques){
		                			//MATA TANQUE
		                			if(t.getId() == gameData.getKillTank()){
		                				tanques.remove(t);
		                				break;
		                			}
		                		}
		                	}
		                	//CLIENTE AVISA SERVER QUE TROCOU DE TANQUE
		                	if(gameData.getType() != null && gameData.getType().equals("tankChanged")){
		                		for(Tank t : tanques){
		                			if(t.getId() == gameData.getTankId()){
		                				t.setName("");
		                				break;
		                			}
		                		}
		                	}
		                	//SE TIRO FOI DISPARADO ENVIA AO SERVER
		                	if(gameData.getType().equals("shotFired")){
		                		Shot cTiro = gson.fromJson(gameData.getTiro().toString(), Shot.class);
		                		for(Tank t : tanques){
		                			if(t.getId() == cTiro.getId()){
										t.tiro.x = cTiro.x + Math.sin(cTiro.angulo);
										t.tiro.y = cTiro.y + Math.cos(cTiro.angulo);
										t.tiro.angulo = cTiro.angulo;
										t.tiro.setId(cTiro.getId());
										t.tiro.estaAtivo = true;
										break;
		                			}
		                		}
		                	}
		                	//CLIENTE/SERVIDOR ATUALIZA JANELA DE CHAT COM MENSAGENS QUE CHEGAM
		                	if(gameData.getType().equals("chatMsg")){
		                		chat.streamMsg( ((gameData.getPlayerServerType().equals("Server")) ? "[HOST] ":"")+gameData.getPlayerName()+" diz:"+gameData.getChatMsg());
		                		//SERVIDOR REDIRECIONA MENSAGENS VINDAS DE CLIENTES PARA TODOS OS OUTROS CLIENTES
		                	}else if(gameData.getType().equals("redirectChatMsg")){
		                		chat.streamMsg( ((gameData.getPlayerServerType().equals("Server")) ? "[HOST] ":"")+gameData.getPlayerName()+" diz:"+gameData.getChatMsg());
		                		for (int i = 0; i < s.getPlayersReady().size(); i++) {	    		
		                			PlayerMessageBus player = gson.fromJson(s.getPlayersReady().get(i), PlayerMessageBus.class);
		                			//SÓ MANDA PARA OS OUTROS CLIENTES
		                			if(!player.getIp().equals(s.getMyIp()) && !player.getIp().equals(gameData.getIp())){
		                				try {
		            						new SendMessage(player.getIp(),ArenaSettings.getArenaPort(),dataFromClient,1000);
		            					} catch (SocketTimeoutException e){
		                            		
		                            	} catch (SocketException e) {

		            					} catch (UnknownHostException e) {

		            					} catch (IOException e) {

		            					}
		                			}
		            			}	
		                	}
		                }catch (Exception e) {
		                    System.out.println("JSON inválido ou transmissão sem mensagem");  
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
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        System.exit(0);
	}
	public static void main(String args[]) throws IOException{
		
		Server s1 = new Server("Server",2311);
		s1.setMyPlayerName("Matheus");
		Arena arena = new Arena(800,640, "Client", s1);
		arena.adicionaTanque(new Tank(400,50,180,Color.BLUE,1));
		arena.adicionaTanque(new Tank(400,200,0,Color.RED,2));
		arena.adicionaTanque(new Tank(400,300,270,Color.GREEN,3));
		arena.adicionaTanque(new Tank(200,50,90,Color.YELLOW,4));
		arena.adicionaTanque(new Tank(100,120,270,Color.GRAY,5));
		arena.adicionaTanque(new Tank(180,307,180,Color.WHITE,6));
		arena.adicionaTanque(new Tank(520,208,23,Color.CYAN, 7));
		arena.adicionaTanque(new Tank(300,300,47,Color.ORANGE,8));
		for (Tank t: arena.getTanques()) {
			t.setTiro(new Shot(-20,-20,0,Color.BLACK,-1));
		}
		JFrame janela = new JFrame("Tank War Z");
		janela.getContentPane().add(arena);
		janela.setResizable(false);
		janela.pack();
		janela.setVisible(true);
		janela.setDefaultCloseOperation(3);
	
	}
	
}