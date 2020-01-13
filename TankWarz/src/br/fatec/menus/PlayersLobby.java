package br.fatec.menus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.fatec.tank.Arena;
import br.fatec.tank.ArenaSettings;
import br.fatec.tank.Tank;
import br.fatec.tank.Shot;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;

public class PlayersLobby extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel jogadoresNaSala;
	private JLabel lblJogadoresNaSala;
	private JButton btnJogar;
	private boolean shutdown = false;
	private Runnable lobbyRefresh;
	private JList<String> lobbyList;
	private DefaultListModel<String> listModel;
	private JPanel contentPane;
	private final JPanel panel = new JPanel();
	private Font verdana = new Font("Verdana", Font.PLAIN, 12);
	private Color baseColor = new Color(76,88,68);
	private Color contrastColor = new Color(62,70,55);
	private Color selectionColor = new Color(149,136,49);
	private Border loweredLevelBorder = BorderFactory.createSoftBevelBorder(BevelBorder.LOWERED);
	private Border buttonBorder = BorderFactory.createCompoundBorder(BorderFactory.createSoftBevelBorder(0), BorderFactory.createEmptyBorder(2, 10, 2, 10));
	private Border buttonBorderRaised = BorderFactory.createCompoundBorder(BorderFactory.createSoftBevelBorder(1), BorderFactory.createEmptyBorder(2, 10, 2, 10));
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PlayersLobby frame = new PlayersLobby(null);
					CommonMethods.setWindowPosition(frame,0);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public void checarLobby(Server s){
		for (int i = 0; i < s.getPlayersReady().size(); i++) {
        	JsonObject checarLobby = new JsonObject();
        	checarLobby.addProperty("ip", s.getMyIp());
        	checarLobby.addProperty("type", "checkingStatus");
    		Gson lobbyGson = new Gson();
    		MessageBus lobby = lobbyGson.fromJson(s.getPlayersReady().get(i), MessageBus.class);
    		if(!lobby.getIp().equals(s.getMyIp())){
	    		try{
	    			new SendMessage(lobby.getIp(),CommonMethods.getDefaultPort(),checarLobby.toString(),1000);
	    		}catch(SocketTimeoutException e){
	    			s.getPlayersReady().remove(i);
	    			System.out.println(">Player removido do lobby");
	    		}catch(Exception e){
	    			
	    		}
    		}
		}
	}
	public void fecharAguardarJogadores(){
		setVisible(false);
		dispose();
		System.exit(0);
	}
	public void startTheGame(Server s) throws SocketException, UnknownHostException, IOException{	
		shutdown = true;
		JFrame janela = new JFrame("Tank War Z");
		janela.setResizable(false);
		janela.setIconImage(Toolkit.getDefaultToolkit().getImage(Start.class.getResource("/br/fatec/menus/resources/icons/warz_logo_48.png")));
		Arena arena = new Arena(800,640, s.getServerType(), s);
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
		janela.setDefaultCloseOperation(3);
		janela.addWindowListener(new WindowAdapter(){
		    public void windowClosing(WindowEvent e){
		       s.setRunning(false);
		       fecharAguardarJogadores();
		    }
		});
		if(s.getServerType().equals("Server")){
			JsonObject resToClient = new JsonObject();
	    	resToClient.addProperty("ip", s.getMyIp());
	    	resToClient.addProperty("name", InetAddress.getLocalHost().getHostName());
	    	resToClient.addProperty("type", "startGame");
	   
	    	//MANDA SINAL PARA INICIO DO JOGO PARA TODOS OS PLAYERS DO LOBBY
	    	Gson gson = new Gson();
	    	for (int i = 0; i < s.getPlayersReady().size(); i++) {	    		
    			PlayerMessageBus player = gson.fromJson(s.getPlayersReady().get(i), PlayerMessageBus.class);
    			if(!player.getIp().equals(s.getMyIp())){
    				new SendMessage(player.getIp(),CommonMethods.getDefaultPort(),resToClient.toString(),1000);
    			}
			}
			
	    	do{
	    		try {
					Thread.sleep(500);
				} catch (InterruptedException e) {

				}
	    		if(s.getPlayersReady().size() == s.getPlayersLogged().size()){
		    		//INICIA COMUNICAÇÃO
			    	ArrayList<String> resTanques = new ArrayList<String>();
					for (Tank t : arena.getTanques()) {
						JsonObject tanqueVar = new JsonObject();
						tanqueVar.addProperty("x", t.getX());
						tanqueVar.addProperty("y", t.getY());
						tanqueVar.addProperty("name", t.getName());
						tanqueVar.addProperty("angulo", t.getAngulo());
						tanqueVar.addProperty("velocidade", t.getVelocidade());
						tanqueVar.addProperty("id", t.getId());
						resTanques.add(tanqueVar.toString());
					}
			    	resToClient.addProperty("ip", s.getMyIp());
			    	resToClient.addProperty("name", InetAddress.getLocalHost().getHostName());
			    	resToClient.addProperty("type", "serverResponse");
			    	resToClient.addProperty("tanques", resTanques.toString());
			   
			    	for (int i = 0; i < s.getPlayersReady().size(); i++) {
			    		PlayerMessageBus player = gson.fromJson(s.getPlayersReady().get(i), PlayerMessageBus.class);
			    		if(!player.getIp().equals(s.getMyIp())){
			    			System.out.println("Conectando com jogador: "+player.getMyPlayerName()+" - "+player.getIp());
			    			new SendMessage(player.getIp(),ArenaSettings.getArenaPort(),resToClient.toString(),5000);
			    		}
					}
	    		}
	    	}while(s.getPlayersReady().size() != s.getPlayersLogged().size());
		}
		janela.setVisible(true);
    	//PARA SINCRONIZAÇÃO DE DADOS DO MENU
    	s.stopReceiving();
		dispose();	
	}
	public PlayersLobby(Server s) {
		s.getPlayersLogged().add(s.getMyIp());
		setIconImage(Toolkit.getDefaultToolkit().getImage(Start.class.getResource("/br/fatec/menus/resources/icons/warz_logo_48.png")));
		setResizable(false);
		setTitle("Tank War Z - Aguardando Jogadores");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
		    public void windowClosing(WindowEvent e){
		       s.setRunning(false);
		       fecharAguardarJogadores();
		    }
		});
		setBounds(100, 100, 520, 387);
		contentPane = new JPanel();
		contentPane.setBackground(baseColor);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setBackground(baseColor);
		panel.setLayout(new MigLayout("", "[grow][400px,grow][grow]", "[][][200px,grow][45px,center]"));
		
		lblJogadoresNaSala = new JLabel("Jogadores na Sala:");
		lblJogadoresNaSala.setFont(verdana);
		lblJogadoresNaSala.setForeground(Color.white);
		panel.add(lblJogadoresNaSala, "flowx,cell 1 0");
		
		jogadoresNaSala = new JLabel("0/0");
		jogadoresNaSala.setFont(verdana);
		jogadoresNaSala.setForeground(Color.white);
		panel.add(jogadoresNaSala, "cell 1 0");
		
		JLabel lblSala = new JLabel("Sala:");
		lblSala.setFont(verdana);
		lblSala.setForeground(Color.white);
		panel.add(lblSala, "flowx,cell 1 1");
		
		UIManager.put("List.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
		listModel = new DefaultListModel<String>();
        lobbyList = new JList<String>(listModel);
        lobbyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lobbyList.setSelectionBackground(selectionColor);
        lobbyList.setSelectionForeground(Color.white);
        lobbyList.setSelectedIndex(0);
        lobbyList.setVisibleRowCount(5);
        lobbyList.setBackground(contrastColor);
        lobbyList.setFont(verdana);
        lobbyList.setBorder(loweredLevelBorder);
        lobbyList.setForeground(Color.white);
        JScrollPane listScroller = new JScrollPane(lobbyList);
		listScroller.setBorder(BorderFactory.createEmptyBorder());
		listScroller.setPreferredSize(new Dimension(250, 80));
		panel.add(listScroller, "cell 1 2,grow");
		
		btnJogar = new JButton("Iniciar Partida");
		btnJogar.setFont(verdana);
		btnJogar.setForeground(Color.white);
		btnJogar.setBackground(baseColor);
		btnJogar.setBorder(buttonBorder);
		btnJogar.setEnabled(false);
		btnJogar.setContentAreaFilled(false);
		btnJogar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					startTheGame(s);
				} catch (IOException e1) {
					
				}
				dispose();
			}
		});
		btnJogar.addMouseListener(new MouseListener() {
		    @Override
		    public void mousePressed(MouseEvent e) {
		    	btnJogar.setBackground(contrastColor);
		    	btnJogar.setBorder(buttonBorderRaised);
		    }

			@Override
			public void mouseClicked(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {
	
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				btnJogar.setBackground(baseColor);
				btnJogar.setBorder(buttonBorder);
			}
		});
		panel.add(btnJogar, "cell 1 3,alignx center");
		
		JLabel sala = new JLabel("");
		sala.setForeground(Color.white);
		sala.setFont(verdana);
		sala.setText(s.getServerName());
		panel.add(sala, "cell 1 1,alignx left,aligny baseline");
		
		lobbyRefresh = new Runnable() {
		    public void run() {
		    	if (!shutdown){
			    	//habilita partida se dois jogadores estiverem no lobby
			    	if(s.getPlayersReady().size() >= 2 && btnJogar.isEnabled() == false){
			    		btnJogar.setEnabled(true);
			    	}else if(s.getPlayersReady().size() < 2 && btnJogar.isEnabled() == true){
			    		btnJogar.setEnabled(false);
			    	}else if(s.getPlayersReady().size() < 2){
			    		btnJogar.setEnabled(false);
			    	}
			    	//aqui tem warning
			    	lobbyList.setListData(s.getLobbyList());
			    	jogadoresNaSala.setText(Integer.toString(s.getPlayersReady().size())+"/"+s.getMaximumPlayers());
			    	
			    	checarLobby(s);
		    	}
		    }
		};
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(lobbyRefresh, 0, 3, TimeUnit.SECONDS);	
	}
}
