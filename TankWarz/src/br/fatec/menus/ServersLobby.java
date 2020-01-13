package br.fatec.menus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JTextField;

public class ServersLobby extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	Gson lobbyGson = new Gson();
	private JButton btnEntrarNaSala;
	private MessageBus lobbyJson;
	private MessageBus lobbyPlayersJson;
	private JList<String> listaPlayers;
	private JList<String> listaSalas;
	private Font verdana = new Font("Verdana", Font.PLAIN, 12);
	private Color baseColor = new Color(76,88,68);
	private Color contrastColor = new Color(62,70,55);
	private Color selectionColor = new Color(149,136,49);
	private Border raisedLevelBorder = BorderFactory.createSoftBevelBorder(BevelBorder.RAISED);
	private Border loweredLevelBorder = BorderFactory.createSoftBevelBorder(BevelBorder.LOWERED);
	private Border buttonBorder = BorderFactory.createCompoundBorder(BorderFactory.createSoftBevelBorder(0), BorderFactory.createEmptyBorder(2, 10, 2, 10));
	private Border buttonBorderRaised = BorderFactory.createCompoundBorder(BorderFactory.createSoftBevelBorder(1), BorderFactory.createEmptyBorder(2, 10, 2, 10));
	private DefaultListModel<String> listaModelo;
	private JPanel contentPane;
	private int animCount = 1;
	private boolean shutdown = false;
	private JTextField lobbyName;
	private JTextField playersNum;
	private JTextField selectedLobbyIp;
	private JTextField lobbyStatus;

	public void fecharSalasDisponiveis(){
		setVisible(false);
		dispose();
		System.exit(0);
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServersLobby frame = new ServersLobby(null);
					CommonMethods.setWindowPosition(frame,0);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public void showMessage(String title, String message){
		JOptionPane.showMessageDialog(null, message,title, 1);
	}
	public void limparTela(){
		listaSalas.setListData(new String[] {"Não há salas disponíveis"});
		listaPlayers.setListData(new String[] {"Não há players para exibir"});
		btnEntrarNaSala.setEnabled(true);
		playersNum.setText("");
		lobbyName.setText("");
		selectedLobbyIp.setText("");
	}
	public void mostrarPlayers(Server s){
		if(s.getPlayersReady().size() > 0){
			String[] playersAtivos = new String[s.getPlayersReady().size()];
			for (int i = 0; i < s.getPlayersReady().size(); i++) {
				lobbyPlayersJson = lobbyGson.fromJson(s.getPlayersReady().get(i), MessageBus.class);
				playersAtivos[i] = lobbyPlayersJson.getMyPlayerName()+" - "+lobbyPlayersJson.getName()+" - "+lobbyPlayersJson.getIp(); 
			}
			listaPlayers.setListData(playersAtivos);
			playersNum.setText(s.getPlayersReady().size()+"/"+s.getMaximumPlayers());
		}else{
			listaPlayers.setListData(new String[] {"Não há players aguardando nesta sala!"});
		}
	}
	public void requisitarAcessoaoLobby(String ip, String nome, String playername) throws SocketException, UnknownHostException, IOException{
		JsonObject innerObject = new JsonObject();
    	innerObject.addProperty("ip", NetworkScan.getCurrentIp());
    	innerObject.addProperty("name", nome);
    	innerObject.addProperty("myPlayerName", playername);
    	innerObject.addProperty("type", "joinRequest");
    	innerObject.addProperty("status", "free");
        new SendMessage(ip, CommonMethods.getDefaultPort(),innerObject.toString(),1000);
	}
	public ServersLobby(Server s) {
		setTitle("Tank War Z - Salas dispon\u00EDveis");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Start.class.getResource("/br/fatec/menus/resources/icons/warz_logo_48.png")));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
		    public void windowClosing(WindowEvent e){
		    	s.setRunning(false);
		    	fecharSalasDisponiveis();
			}
		});
		setBounds(100, 100, 826, 480);
		contentPane = new JPanel();
		contentPane.setBackground(baseColor);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel conteudoJanela = new JPanel();
		contentPane.add(conteudoJanela, BorderLayout.CENTER);
		conteudoJanela.setBackground(baseColor);
		conteudoJanela.setLayout(new MigLayout("", "[grow][400px,grow][grow][400px,grow][grow]", "[100px][grow]"));
		
		JPanel detalhesSala = new JPanel();
		detalhesSala.setBackground(baseColor);
		detalhesSala.setBorder(new TitledBorder(raisedLevelBorder, "Sala Selecionada", TitledBorder.LEFT, TitledBorder.TOP, verdana, Color.white));
		
		conteudoJanela.add(detalhesSala, "cell 1 0 3 1,grow");
		detalhesSala.setLayout(new MigLayout("", "[][300px][][140px][]", "[][][]"));
		
		//JOIN LOBBY BUTTON
		btnEntrarNaSala = new JButton("Entrar na Sala");
		btnEntrarNaSala.setBackground(baseColor);
		btnEntrarNaSala.setFont(verdana);
		btnEntrarNaSala.setForeground(Color.white);
		btnEntrarNaSala.setBorder(buttonBorder);
		btnEntrarNaSala.setFocusPainted(false);
		btnEntrarNaSala.setFocusable(false);
		btnEntrarNaSala.setContentAreaFilled(false);
		detalhesSala.add(btnEntrarNaSala, "cell 4 0");
		btnEntrarNaSala.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					requisitarAcessoaoLobby(selectedLobbyIp.getText(),s.getServerName(),s.getMyPlayerName());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnEntrarNaSala.addMouseListener(new MouseListener() {
		    @Override
		    public void mousePressed(MouseEvent e) {
		    	btnEntrarNaSala.setBackground(contrastColor);
		    	btnEntrarNaSala.setBorder(buttonBorderRaised);
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
				btnEntrarNaSala.setBackground(baseColor);
				btnEntrarNaSala.setBorder(buttonBorder);
			}
		});
		//MOSTRA NUM DE JOGADORES NA SALA SEL.
		JLabel lblNumJogadores = new JLabel("Jogadores");
		lblNumJogadores.setForeground(Color.white);
		lblNumJogadores.setFont(verdana);
		detalhesSala.add(lblNumJogadores, "cell 0 1,alignx right");
		playersNum = new JTextField();
		playersNum.setEditable(false);
		playersNum.setEnabled(false);
		playersNum.setFont(verdana);
		playersNum.setForeground(Color.white);
		playersNum.setBackground(baseColor);
		playersNum.setBorder(loweredLevelBorder);
		detalhesSala.add(playersNum, "cell 1 1");
		playersNum.setColumns(10);
			
		//MOSTRA NUM DE JOGADORES NA SALA SEL.
		JLabel lblIp = new JLabel("IP");
		lblIp.setForeground(Color.white);
		lblIp.setFont(verdana);
		detalhesSala.add(lblIp, "cell 0 2,alignx right");
		selectedLobbyIp = new JTextField();
		selectedLobbyIp.setEditable(false);
		selectedLobbyIp.setEnabled(false);
		selectedLobbyIp.setFont(verdana);
		selectedLobbyIp.setForeground(Color.white);
		selectedLobbyIp.setBackground(baseColor);
		selectedLobbyIp.setBorder(loweredLevelBorder);
		detalhesSala.add(selectedLobbyIp, "cell 1 2");
		selectedLobbyIp.setColumns(10);
		
		//MOSTRA NOME DA SALA
		JLabel lblNomeDaSala = new JLabel("Nome da Sala");
		lblNomeDaSala.setForeground(Color.white);
		lblNomeDaSala.setFont(verdana);
		detalhesSala.add(lblNomeDaSala, "cell 0 0,alignx right");
		lobbyName = new JTextField();
		lobbyName.setEditable(false);
		lobbyName.setEnabled(false);
		lobbyName.setFont(verdana);
		lobbyName.setForeground(Color.white);
		lobbyName.setBackground(baseColor);
		lobbyName.setBorder(loweredLevelBorder);
		detalhesSala.add(lobbyName, "cell 1 0");
		lobbyName.setColumns(10);
			
		JLabel lblStatus = new JLabel("Status");
		lblStatus.setForeground(Color.white);
		lblStatus.setFont(verdana);
		detalhesSala.add(lblStatus, "cell 2 0");
		lobbyStatus = new JTextField();
		lobbyStatus.setEditable(false);
		lobbyStatus.setEnabled(false);
		lobbyStatus.setForeground(Color.white);
		lobbyStatus.setBackground(baseColor);
		lobbyStatus.setBorder(loweredLevelBorder);
		lobbyStatus.setFont(verdana);
		detalhesSala.add(lobbyStatus, "cell 3 0");
		lobbyStatus.setColumns(10);
		
		JPanel salasDisponiveis = new JPanel();
		salasDisponiveis.setBorder(new TitledBorder(raisedLevelBorder, "Salas Disponíveis", TitledBorder.LEFT, TitledBorder.TOP, verdana, Color.white));
		salasDisponiveis.setBackground(baseColor);
		conteudoJanela.add(salasDisponiveis, "cell 1 1,grow");
		salasDisponiveis.setLayout(new MigLayout("", "[grow,fill]", "[grow,fill]"));
		
		//LISTA SALAS
		UIManager.put("List.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
		listaModelo = new DefaultListModel<String>();
		listaModelo.addElement("Carregando...");
		listaSalas = new JList<String>(listaModelo);
		listaSalas.setBackground(contrastColor);
		listaSalas.setForeground(Color.white);
		listaSalas.setFont(verdana);
		listaSalas.setSelectionBackground(selectionColor);
		listaSalas.setBorder(raisedLevelBorder);
		listaSalas.setSelectionForeground(Color.white);
		listaSalas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaSalas.setSelectedIndex(0);
        listaSalas.setVisibleRowCount(5);
        listaSalas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                	String server = (String)listaSalas.getModel().getElementAt(listaSalas.locationToIndex(e.getPoint()));
                	if(server.indexOf(" - ") != -1){
                		if(server.split(" - ")[4].trim() != "" && server.split(" - ")[4].trim().equals("Livre")){
                			selectedLobbyIp.setText(server.split(" - ")[2].trim());
                			lobbyName.setText(server.split(" - ")[0].trim());
                			lobbyStatus.setText(server.split(" - ")[4].trim());
                		}else{
                			showMessage("Sala Ocupada","A sala não pode ser selecionada por alguma das seguintes razões:\n- A sala está cheia\n- A sala já iniciou a partida");
                		}
                	}
                }
            }
        });
        JScrollPane listaSalasScroller = new JScrollPane(listaSalas);
        listaSalasScroller.setPreferredSize(new Dimension(250, 80));
        listaSalasScroller.setBorder(BorderFactory.createEmptyBorder());
		salasDisponiveis.add(listaSalasScroller, "cell 0 0,alignx left,aligny top");
		
		JPanel jogadoresSala = new JPanel();
		jogadoresSala.setBackground(baseColor);
		jogadoresSala.setBorder(new TitledBorder(raisedLevelBorder, "Jogadores na Sala", TitledBorder.LEFT, TitledBorder.TOP, verdana, Color.white));
		conteudoJanela.add(jogadoresSala, "cell 3 1,grow");
		jogadoresSala.setLayout(new MigLayout("", "[grow,fill]", "[grow,fill]"));
		
		//LISTA PLAYERS
        listaPlayers = new JList<String>(listaModelo);
        listaPlayers.setBackground(contrastColor);
        listaPlayers.setFont(verdana);
        listaPlayers.setSelectionBackground(selectionColor);
        listaPlayers.setSelectionForeground(Color.white);
        listaPlayers.setForeground(Color.white);
        listaPlayers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaPlayers.setSelectedIndex(0);
        listaPlayers.setVisibleRowCount(5);
        listaPlayers.setBorder(raisedLevelBorder);
        JScrollPane listaPlayersScroller = new JScrollPane(listaPlayers);
        listaPlayersScroller.setPreferredSize(new Dimension(250, 80));
        listaPlayersScroller.setBorder(BorderFactory.createEmptyBorder());
        jogadoresSala.add(listaPlayersScroller, "cell 0 0,alignx left,aligny top");
		
		Runnable lobbyRefresh = new Runnable() {
		    public void run() {
		    	if (!shutdown){
		    		if(s.isRunning() == false){shutdown = true;dispose();}
		    		
			    	//ANIMAÇÃO TRES PONTOS
		    		String dots = "";
		    		if(animCount == 1){
		    			dots = ".";
		    		}else if(animCount == 2){
		    			dots = "..";		    			
		    		}else if(animCount == 3){
		    			dots = "...";
		    		}
			    	//ATUALIZA A LISTA DE SERVIDORES ONLINE
			    	if(s.ips.isScanRunning() == false){
				    	if(s.getServersReady().size() > 0){
				    		// ESSE FOR TAVA TRAVANDO A BAGAÇA TODA
			    			for (int i = 0; i < s.getServersReady().size(); i++) {
			    				JsonObject checarLobby = new JsonObject();
			    	        	checarLobby.addProperty("ip", s.getMyIp());
			    	        	checarLobby.addProperty("type", "isServer");
			    	        	checarLobby.addProperty("myPlayerName", s.getMyPlayerName());
			    	    		lobbyJson = lobbyGson.fromJson(s.getServersReady().get(i), MessageBus.class);
			    	    		try{
			    	    			new SendMessage(lobbyJson.getIp(),CommonMethods.getDefaultPort(),checarLobby.toString(),1000);
			    	    		}catch(SocketTimeoutException e){
			    	    			s.getServersReady().remove(i);
			    	    			System.out.println(">[SERVER OFFLINE] - Server removido da lista");
			    	    		}catch(Exception e){
			    	    			e.printStackTrace();
			    	    		}
							}
			    			listaSalas.setListData(s.getServerList());		    	
				    	}else{
				    		if(s.getServersReady().size() == 0){
				    			limparTela();
				    			s.setWaitingStatus(false);
				    			s.getPlayersReady().clear();
				    			
				    		}
				    	}
		    			s.ips.serverListUpdate();
	
			    	}else{
			    		listaSalas.setListData(new String[] {"Escaneando Rede"+dots});
			    	}
			    	System.out.println(">[atualizando lobby...]");
			    	animCount++;
			    	if(animCount > 3){
			    		animCount = 1;
			    	}
			    	if(s.getServersReady().size() > 0 && !lobbyName.getText().equals("")){
			    		mostrarPlayers(s);
			    		System.out.println(">Atualizando players");
			    	}
			    	if(s.isWaiting() == true){
			    		btnEntrarNaSala.setEnabled(false);
			    	}else{
			    		btnEntrarNaSala.setEnabled(true);
			    	}
			    }
		    }
		};

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(lobbyRefresh, 0, 1, TimeUnit.SECONDS);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("fiz alguma coisa");
	}

}
