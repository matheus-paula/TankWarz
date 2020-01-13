package br.fatec.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;

public class Start extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton btnEntrarNoJogo;
	private JButton btnCriarSala;
	private Font verdana = new Font("Verdana", Font.PLAIN, 12);
	private Color baseColor = new Color(76,88,68);
	private Color contrastColor = new Color(62,70,55);
	private Border buttonBorder = BorderFactory.createCompoundBorder(BorderFactory.createSoftBevelBorder(0), BorderFactory.createEmptyBorder(2, 10, 2, 10));
	private Border buttonBorderRaised = BorderFactory.createCompoundBorder(BorderFactory.createSoftBevelBorder(1), BorderFactory.createEmptyBorder(2, 10, 2, 10));
	
	public void fecharInicio(){
		setVisible(false);
		dispose();
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Start frame = new Start();
					CommonMethods.setWindowPosition(frame,0);
					frame.addWindowListener(new WindowAdapter(){
					    public void windowClosing(WindowEvent e){
					    	frame.fecharInicio();
						}
					});
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public Start() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Start.class.getResource("/br/fatec/menus/resources/icons/warz_logo_48.png")));
		setTitle("Tank War Z");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 313, 246);
		getContentPane().setBackground(baseColor);
		
		JPanel tankWarPanel = new JPanel();
		getContentPane().add(tankWarPanel, BorderLayout.NORTH);
		tankWarPanel.setLayout(new MigLayout("", "[grow][200px][grow]", "[][50px][][]"));
		tankWarPanel.setBackground(baseColor);
		
		JLabel lblTankWarZ = new JLabel("Tank War Z");
		lblTankWarZ.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblTankWarZ.setHorizontalAlignment(SwingConstants.CENTER);
		lblTankWarZ.setForeground(Color.white);
		tankWarPanel.add(lblTankWarZ, "cell 1 0,alignx center");
		
		btnCriarSala = new JButton("Hospedar Sala");
		btnCriarSala.setFocusable(false);
		btnCriarSala.setBackground(baseColor);
		btnCriarSala.setFont(verdana);
		btnCriarSala.setForeground(Color.white);
		btnCriarSala.setBorder(buttonBorder);
		btnCriarSala.setContentAreaFilled(false);
		tankWarPanel.add(btnCriarSala, "cell 1 2,alignx center");
		btnCriarSala.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//CRIA UM SERVER
				Server s1 = new Server("Server",CommonMethods.getDefaultPort());
	        	CreateLobby cs = new CreateLobby(s1);
	        	CommonMethods.setWindowPosition(cs,0);
	        	cs.setVisible(true);
	        	fecharInicio();
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
		
		btnEntrarNoJogo = new JButton("Entrar no Jogo");
		btnEntrarNoJogo.setFocusable(false);
		btnEntrarNoJogo.setFont(verdana);
		btnEntrarNoJogo.setForeground(Color.white);
		btnEntrarNoJogo.setBackground(baseColor);
		btnEntrarNoJogo.setBorder(buttonBorder);
		btnEntrarNoJogo.setContentAreaFilled(false);
		tankWarPanel.add(btnEntrarNoJogo, "cell 1 3,alignx center");
		btnEntrarNoJogo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//MOSTRA SERVERS PARA SE ENTRAR NO JOGO
				Server s1 = new Server("Client",CommonMethods.getDefaultPort());
	        	CreatePlayer lobbys = new CreatePlayer(s1);
	        	CommonMethods.setWindowPosition(lobbys,0);
	        	lobbys.setVisible(true);
	        	fecharInicio();
			}
		});
		btnEntrarNoJogo.addMouseListener(new MouseListener() {
		    @Override
		    public void mousePressed(MouseEvent e) {
		    	btnEntrarNoJogo.setBackground(contrastColor);
		    	btnEntrarNoJogo.setBorder(buttonBorderRaised);
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
				btnEntrarNoJogo.setBackground(baseColor);
				btnEntrarNoJogo.setBorder(buttonBorder);
			}
		});
	}
}
