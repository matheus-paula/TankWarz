package br.fatec.menus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.JTextComponent;
import com.google.gson.JsonObject;
import net.miginfocom.swing.MigLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JComboBox;

public class CreateLobby extends JFrame {

	private static final long serialVersionUID = -467360583611334991L;
	private JPanel contentPane;
	private JComboBox<String> maxPlayers;
	private JTextField serverName;
	private JTextField nomeJogador;
	private Font verdana = new Font("Verdana", Font.PLAIN, 12);
	private Color baseColor = new Color(76,88,68);
	private Color contrastColor = new Color(62,70,55);
	private Color selectionColor = new Color(149,136,49);
	private Border loweredLevelBorder = BorderFactory.createSoftBevelBorder(BevelBorder.LOWERED);
	private Border buttonBorder = BorderFactory.createCompoundBorder(BorderFactory.createSoftBevelBorder(0), BorderFactory.createEmptyBorder(2, 10, 2, 10));
	private Border buttonBorderRaised = BorderFactory.createCompoundBorder(BorderFactory.createSoftBevelBorder(1), BorderFactory.createEmptyBorder(2, 10, 2, 10));
	

	public void fecharCriarSala(){
		setVisible(false);
		dispose();
		System.exit(0);
	}
	public void fieldAlerts(String title, String msg){
		JOptionPane.showMessageDialog(null, msg,title,2);
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CreateLobby frame = new CreateLobby(null);
					CommonMethods.setWindowPosition(frame,0);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void criarLobby(Server s) throws UnknownHostException{
		//GameCore.setMaximumPlayers((Integer)maximodeJogadores.getValue());
		s.setMaximumPlayers(Integer.valueOf((String)maxPlayers.getSelectedItem()));
		s.setServerName(serverName.getText());
		s.setMyPlayerName(nomeJogador.getText());
		s.setServerReady(true);
		//adiciona host como o primeiro jogador da sala
		JsonObject lobbyHost = new JsonObject();
        lobbyHost.addProperty("ip", s.getMyIp());
        lobbyHost.addProperty("name", InetAddress.getLocalHost().getHostName());
        lobbyHost.addProperty("type", "serverHost");
        lobbyHost.addProperty("myPlayerName", "[HOST] "+nomeJogador.getText());
        lobbyHost.addProperty("myServerName", serverName.getText());
        lobbyHost.addProperty("status", "free");
        s.getPlayersReady().add(lobbyHost.toString());
		
		PlayersLobby aguardarJogadores = new PlayersLobby(s);
		CommonMethods.setWindowPosition(aguardarJogadores,0);
		aguardarJogadores.setVisible(true);
	}
	public CreateLobby(Server s) {
		setTitle("Tank War Z - Hospedar Sala");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Start.class.getResource("/br/fatec/menus/resources/icons/warz_logo_48.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
		    public void windowClosing(WindowEvent e){
		    	s.setRunning(false);
		    	fecharCriarSala();
			}
		});
		setBounds(100, 100, 360, 271);
		contentPane = new JPanel();
		contentPane.setBackground(baseColor);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		JPanel mainContainer = new JPanel();
		mainContainer.setBackground(baseColor);
		contentPane.add(mainContainer, BorderLayout.NORTH);
		mainContainer.setLayout(new MigLayout("", "[150px,grow][200,grow]", "[30px][5px][30px][5px][30px][50px][30px][grow]"));
		
		//PLAYER NAME LABEL
		JLabel lblNomedoJogador = new JLabel("Nome de Jogador");
		lblNomedoJogador.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNomedoJogador.setFont(verdana);
		lblNomedoJogador.setForeground(Color.white);
		mainContainer.add(lblNomedoJogador, "cell 0 0,alignx right");
		
		//PLAYER NAME
		nomeJogador = new JTextField();
		mainContainer.add(nomeJogador, "cell 1 0,grow");
		nomeJogador.setFont(verdana);
		nomeJogador.setForeground(Color.white);
		nomeJogador.setBackground(contrastColor);
		nomeJogador.setSelectionColor(selectionColor);
		nomeJogador.setCaretColor(Color.white);
		nomeJogador.setBorder(loweredLevelBorder);
		nomeJogador.setHorizontalAlignment(SwingConstants.LEFT);
		nomeJogador.setColumns(10);
		nomeJogador.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
            	finalizarCriacao(s);
            }
		});
		//SERVER NAME LABEL
		JLabel lblNomeDaSala = new JLabel("Nome da Sala");
		lblNomeDaSala.setFont(verdana);
		lblNomeDaSala.setForeground(Color.white);
		mainContainer.add(lblNomeDaSala, "cell 0 2,alignx right");
		
		//SERVER NAME
		serverName = new JTextField();
		serverName.setHorizontalAlignment(SwingConstants.LEFT);
		serverName.setFont(verdana);
		serverName.setBackground(contrastColor);
		serverName.setCaretColor(Color.white);
		serverName.setForeground(Color.white);
		serverName.setBorder(loweredLevelBorder);
		serverName.setColumns(10);
		serverName.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
            	finalizarCriacao(s);
            }
		});
		mainContainer.add(serverName, "cell 1 2,grow");
		
		JLabel lblMaximodeJogadores = new JLabel("M\u00E1x. de jogadores");
		lblMaximodeJogadores.setFont(verdana);
		lblMaximodeJogadores.setForeground(Color.white);
		mainContainer.add(lblMaximodeJogadores, "cell 0 4,alignx right");
		
		//COMBO-BOX
		UIManager.put("ComboBox.foreground", new ColorUIResource(Color.white));
		UIManager.put("ComboBox.selectionBackground", new ColorUIResource(selectionColor));
	    UIManager.put("ComboBox.selectionForeground", new ColorUIResource(Color.white));
		maxPlayers = new JComboBox<String>();
		for (int i = 2; i < 9; i++) {
			maxPlayers.addItem(String.valueOf(i));	
		}
		maxPlayers.setSelectedIndex(0);
		maxPlayers.setBackground(contrastColor);
		maxPlayers.setUI(new BasicComboBoxUI());
		maxPlayers.setForeground(Color.white);
		maxPlayers.setBorder(loweredLevelBorder);
		maxPlayers.getEditor().getEditorComponent().setBackground(contrastColor);
		maxPlayers.getEditor().getEditorComponent().setForeground(Color.white);
		maxPlayers.setFocusable(false);
		((JTextComponent) maxPlayers.getEditor().getEditorComponent()).setBorder(BorderFactory.createEmptyBorder());
		((JTextComponent) maxPlayers.getEditor().getEditorComponent()).setForeground(Color.white);;
		for (int i=0; i<maxPlayers.getComponentCount(); i++){
			if (maxPlayers.getComponent(i) instanceof AbstractButton){
				((JButton)maxPlayers.getComponent(i)).setBackground(contrastColor);
				((JButton)maxPlayers.getComponent(i)).setForeground(Color.white);
				((JButton)maxPlayers.getComponent(i)).setBorder(loweredLevelBorder);
			}
		}
		mainContainer.add(maxPlayers, "cell 1 4,grow");
		
		//LOBBY CREATION BUTTON
		JButton btnCriarSala = new JButton("Criar Sala");
		btnCriarSala.setForeground(Color.white);
		btnCriarSala.setFont(verdana);
		btnCriarSala.setBackground(baseColor);
		btnCriarSala.setBorder(buttonBorder);
		btnCriarSala.setFocusable(false);
		btnCriarSala.setContentAreaFilled(false);
		mainContainer.add(btnCriarSala, "cell 1 6,alignx right");
		btnCriarSala.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				finalizarCriacao(s);
			}
		});
		btnCriarSala.addMouseListener(new MouseListener() {
		    @Override
		    public void mousePressed(MouseEvent e) {
		    	btnCriarSala.setBackground(contrastColor);
		    	btnCriarSala.setBorder(buttonBorderRaised);
		    }

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				btnCriarSala.setBackground(baseColor);
				btnCriarSala.setBorder(buttonBorder);
			}
		});
	}
	public void finalizarCriacao(Server s){
		if(CommonMethods.validarNome(nomeJogador.getText()) == false){
			fieldAlerts("Nome de jogador inválido","Nome de jogador não informado ou inválido!\nTente alguma destas soluções:\n- Não utilizar caracteres especiais como: \\ / : * ? \" < > e |\n- Digitar nome com mais de três letras e sem espaços");
		}else{
			if(CommonMethods.validarNome(serverName.getText()) == false){
				fieldAlerts("Nome do servidor inválido","Nome do servidor não informado ou inválido!\nTente alguma destas soluções:\n- Não utilizar caracteres especiais como: \\ / : * ? \" < > e |\n- Digitar nome com mais de três letras e sem espaços");
			}else{
				try {
					criarLobby(s);
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				dispose();
			}
		}
	}
}
