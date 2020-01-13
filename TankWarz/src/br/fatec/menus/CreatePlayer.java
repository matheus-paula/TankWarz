package br.fatec.menus;

import java.awt.BorderLayout;
import java.awt.Color;
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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;


public class CreatePlayer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -467360583611334991L;
	private JPanel contentPane;
	private Color baseColor = new Color(76,88,68);
	private Color contrastColor = new Color(62,70,55);
	private Font verdana = new Font("Verdana", Font.PLAIN, 12);
	private Border loweredLevelInput = BorderFactory.createLoweredSoftBevelBorder();
	private JTextField nomeJogador;
	private Border buttonBorder = BorderFactory.createCompoundBorder(BorderFactory.createSoftBevelBorder(0), BorderFactory.createEmptyBorder(2, 10, 2, 10));
	private Border buttonBorderRaised = BorderFactory.createCompoundBorder(BorderFactory.createSoftBevelBorder(1), BorderFactory.createEmptyBorder(2, 10, 2, 10));
	
	public void entrarNoLobby(Server s) throws UnknownHostException{
		s.setMyPlayerName(nomeJogador.getText());
		s.setServerName(InetAddress.getLocalHost().getHostName());
		ServersLobby salas = new ServersLobby(s);
		CommonMethods.setWindowPosition(salas,0);
		salas.setVisible(true);
	}
	public void fecharCriarJogador(){
		setVisible(false);
		dispose();
		System.exit(0);
	}
	public CreatePlayer(Server s) {
		setTitle("Tank War Z - Criar Jogador");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Start.class.getResource("/br/fatec/menus/resources/icons/warz_logo_48.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
		    public void windowClosing(WindowEvent e){
		    	s.setRunning(false);
		    	fecharCriarJogador();
			}
		});
		setBounds(100, 100, 403, 207);
		contentPane = new JPanel();
		contentPane.setBackground(baseColor);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setBackground(baseColor);
		panel.setLayout(new MigLayout("", "[grow][200,grow]", "[][30px][50px][30px][grow]"));
		
		JLabel lblNomedoJogador = new JLabel("Nome de Jogador");
		lblNomedoJogador.setForeground(Color.white);
		lblNomedoJogador.setFont(verdana);
		panel.add(lblNomedoJogador, "cell 0 1");
		
		nomeJogador = new JTextField();
		panel.add(nomeJogador, "cell 1 1,grow");
		nomeJogador.setBackground(contrastColor);
		nomeJogador.setFont(verdana);
		nomeJogador.setCaretColor(Color.white);
		nomeJogador.setForeground(Color.white);
		nomeJogador.setBorder(loweredLevelInput);
		nomeJogador.setHorizontalAlignment(SwingConstants.LEFT);
		nomeJogador.setColumns(10);
		nomeJogador.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
            	finalizarCriacao(s);
            }
		});

		JButton btnCriarJogador = new JButton("Criar Jogador");
		btnCriarJogador.setBackground(baseColor);
		btnCriarJogador.setForeground(Color.white);
		btnCriarJogador.setFont(verdana);
		btnCriarJogador.setBorder(buttonBorder);
		btnCriarJogador.setFocusable(false);
		btnCriarJogador.setContentAreaFilled(false);
		panel.add(btnCriarJogador, "cell 1 3,alignx right");
		btnCriarJogador.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				finalizarCriacao(s);
			}
		});
		btnCriarJogador.addMouseListener(new MouseListener() {
		    @Override
		    public void mousePressed(MouseEvent e) {
		    	btnCriarJogador.setBackground(contrastColor);
		    	btnCriarJogador.setBorder(buttonBorderRaised);
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
				btnCriarJogador.setBackground(baseColor);
				btnCriarJogador.setBorder(buttonBorder);
			}
		});
	}
	public void finalizarCriacao(Server s){
		if(CommonMethods.validarNome(nomeJogador.getText()) == false){
			JOptionPane.showMessageDialog(null, "Nome de jogador não informado ou inválido!\nTente alguma destas soluções:\n- Não utilizar caracteres especiais como: \\ / : * ? \" < > e |\n- Digitar nome com mais de três letras e sem espaços","Nome de jogador inválido",2);
		}else{
			try {
				entrarNoLobby(s);
			} catch (UnknownHostException e) {

			}
			dispose();
		}
	}
}
