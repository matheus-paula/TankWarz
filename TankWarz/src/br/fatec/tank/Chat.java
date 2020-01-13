package br.fatec.tank;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;
import net.miginfocom.swing.MigLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Chat {
	private JInternalFrame chat;
	private JButton minimizeBtn;
	private JScrollPane chatStreamScroll;
	private JTextField messageField;
	private JTextArea chatStream;
	private JButton btnEnviar;
	private JPanel chatPanel;
	private Font verdana = new Font("Verdana", Font.PLAIN, 12);
	private Font verdana10 = new Font("Verdana", Font.PLAIN, 10);
	private Color baseColor = new Color(76,88,68);
	private Color scrollBgColor = new Color(90, 105, 82);
	private Color contrastColor = new Color(62,70,55);
	private int posX=0,posY=0;
	private Border buttonBorderLowered = BorderFactory.createCompoundBorder(BorderFactory.createSoftBevelBorder(0), BorderFactory.createEmptyBorder(2, 10, 2, 10));
	private Border buttonBorderRaised = BorderFactory.createCompoundBorder(BorderFactory.createSoftBevelBorder(1), BorderFactory.createEmptyBorder(2, 10, 2, 10));
	private Border closeBtnBorderLowered = BorderFactory.createCompoundBorder(BorderFactory.createSoftBevelBorder(0), BorderFactory.createEmptyBorder(0, 0, 0, 0));
	private Border closeBtnBorderRaised = BorderFactory.createCompoundBorder(BorderFactory.createSoftBevelBorder(1), BorderFactory.createEmptyBorder(0, 0, 0, 0));
	private Border loweredLevelBorder = BorderFactory.createSoftBevelBorder(BevelBorder.LOWERED);
	private JLabel titleLabel;
	
	public void setTransparency(boolean s){
		if(s == true){
			((MyInternalFrame) chat).setComp(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
		}else{
			((MyInternalFrame) chat).setComp(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
		}
	}
	public void openCloseChat(JInternalFrame chat, Arena a){
		if(chat.getSize().getHeight() > 35){
			minimizeBtn.setIcon(new ImageIcon(Chat.class.getResource("/br/fatec/menus/resources/icons/maximize_ico_16.png")));
			chat.setBounds(a.getLargura()-110, a.getAltura()-45, 100, 35);
		}else{
			minimizeBtn.setIcon(new ImageIcon(Chat.class.getResource("/br/fatec/menus/resources/icons/minimize_ico_16.png")));
			chat.setBounds(a.getLargura()-310, a.getAltura()-310, 300, 300);
		}
	}
	public String getChatMsg(){
		return messageField.getText();
	}
	public void streamMsg(String msg){
		chatStream.append(msg + "\n");
		chatStream.setCaretPosition(chatStream.getText().length());
	}
	public Chat(Arena a, String title){
		chat = new MyInternalFrame("Chat", (a.getLargura()-310),(a.getAltura()-310), 300, 300, 0.5f);
		chat.setBorder(null);
		chat.setBackground(baseColor);
		chat.setDefaultCloseOperation(3);
		chat.getRootPane().setWindowDecorationStyle(0);
		
		chatPanel = new JPanel();
		chatPanel.setBackground(baseColor);
		chatPanel.setBorder(null);
		chat.setContentPane(chatPanel);
		chatPanel.setLayout(new MigLayout("", "[grow]", "[24px:24px][grow][]"));
		
		/* TITULO */
		titleLabel = new JLabel(title);
		titleLabel.setFont(verdana);
		titleLabel.setForeground(Color.WHITE);
		chatPanel.add(titleLabel, "flowx,cell 0 0,growx,aligny center");
		
		/* MINIMIZAR */
		minimizeBtn = new JButton("");
		minimizeBtn.setFocusable(false);
		minimizeBtn.setIcon(new ImageIcon(Chat.class.getResource("/br/fatec/menus/resources/icons/minimize_ico_16.png")));
		minimizeBtn.setForeground(Color.WHITE);
		minimizeBtn.setBackground(baseColor);
		minimizeBtn.setHorizontalAlignment(SwingConstants.RIGHT);
		minimizeBtn.setBorder(closeBtnBorderLowered);
		minimizeBtn.setFont(verdana);
		minimizeBtn.setContentAreaFilled(false);
		minimizeBtn.setRolloverEnabled(false);
		minimizeBtn.setFocusPainted(false);
		minimizeBtn.setFocusable(false);
		minimizeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openCloseChat(chat, a);
			}
		});
		minimizeBtn.addMouseListener(new MouseListener() {
		    @Override
		    public void mousePressed(MouseEvent e) {
		    	minimizeBtn.setBackground(contrastColor);
		    	minimizeBtn.setBorder(closeBtnBorderRaised);
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
				minimizeBtn.setBackground(baseColor);
				minimizeBtn.setBorder(closeBtnBorderLowered);
			}
		});
		chatPanel.add(minimizeBtn, "cell 0 0,alignx trailing,aligny center");
		
		/* STREAM DE MENSAGENS */
		chatStream = new JTextArea();
		chatStream.setFont(verdana10);
		chatStream.setEditable(false);
		chatStream.setEnabled(false);
		chatStream.setLineWrap(true);
		chatStream.setCaretColor(Color.white);
		chatStream.setForeground(Color.white);
		chatStream.setBackground(contrastColor);
		chatStream.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		chatStreamScroll = new JScrollPane(chatStream);
		chatStreamScroll.getVerticalScrollBar().setBackground(scrollBgColor);
		chatStreamScroll.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
		chatStreamScroll.setBorder(loweredLevelBorder);
	    
		UIManager.put("ScrollBar.thumbDarkShadow", new Color(39,48,31));
	    UIManager.put("ScrollBar.thumbShadow", baseColor);
	    UIManager.put("ScrollBar.thumb", baseColor);
	    UIManager.put("ScrollBar.thumbHighlight",  new Color(132,146,126));
		chatStreamScroll.getVerticalScrollBar().setUI(new BasicScrollBarUI(){
			@Override 
	        protected void configureScrollBarColors(){
	            this.trackColor = scrollBgColor;
	            this.thumbColor = baseColor;
	            this.thumbDarkShadowColor = new Color(39,48,31);
	            this.thumbLightShadowColor = baseColor;
	            this.thumbHighlightColor = new Color(132,146,126);
	        }
		});

		chatStreamScroll.setPreferredSize(new Dimension(250, 80));
	    chatStreamScroll.setBackground(contrastColor);
		chatPanel.add(chatStreamScroll, "cell 0 1,grow");
		
		/* CAMPO DE MENSAGEM */
		messageField = new JTextField();
		messageField.setBackground(contrastColor);
		messageField.setFont(verdana);
		messageField.setColumns(10);
		messageField.setForeground(Color.white);
		messageField.setCaretColor(Color.white);
		messageField.setBorder(loweredLevelBorder);
		messageField.addFocusListener(new FocusListener() {
	        @Override
	        public void focusGained(FocusEvent e) {
	            setTransparency(false);
	        }
	        @Override
	        public void focusLost(FocusEvent e) {
	            setTransparency(true);
	        }
	    });
		messageField.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
            	a.sendChatMessage(messageField.getText());
            	messageField.setText("");
            }
        });
		chatPanel.add(messageField, "flowx,cell 0 2,growx");
		
		/* BOTAO ENVIAR */
		btnEnviar = new JButton("Enviar");
		btnEnviar.setForeground(Color.white);
		btnEnviar.setBackground(baseColor);
		btnEnviar.setBorder(buttonBorderLowered);
		btnEnviar.setFont(verdana);
		btnEnviar.setContentAreaFilled(false);
		btnEnviar.setFocusable(false);
		btnEnviar.setRolloverEnabled(false);
		btnEnviar.setFocusPainted(false);
		btnEnviar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				a.sendChatMessage(messageField.getText());
				messageField.setText("");
			}
		});
		btnEnviar.addMouseListener(new MouseListener() {
		    @Override
		    public void mousePressed(MouseEvent e) {
		        btnEnviar.setBackground(contrastColor);
		        btnEnviar.setBorder(buttonBorderRaised);
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
				btnEnviar.setBackground(baseColor);
				btnEnviar.setBorder(buttonBorderLowered);
			}
		});
		chatPanel.add(btnEnviar, "cell 0 2");

		/* PERMITE ARRASTAR JANELA */		
		chat.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				setTransparency(false);
				posX=e.getX()+70;
				posY=e.getY()+25;
			}
		});
		chat.addMouseMotionListener(new MouseAdapter(){
			public void mouseDragged(MouseEvent evt){
				chat.setLocation(evt.getXOnScreen()-posX,evt.getYOnScreen()-posY);			
			}
		});
		((javax.swing.plaf.basic.BasicInternalFrameUI) chat.getUI()).setNorthPane(null);		
		a.add(chat);
	}
}
