package br.fatec.menus;


import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.google.gson.JsonObject;


public class NetworkScan extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static ImageIcon Adaptericon = new ImageIcon(NetworkScan.class.getResource("/br/fatec/menus/resources/icons/adapter_64.png"));
	static JFrame frame;
	private static ArrayList<String> adaptadores = new ArrayList<String>();
	private static ArrayList<String> ipsAtivos = new ArrayList<String>();
	private boolean scanRunning = true;
	private static String currentIp;
	private static String currentHostName;
	private Thread scan, ips1, ips2, ips3, ips4, ips5, ips6, ips7, ips8, ips9, ips10, ips11, ips12;
	
	
	public static ArrayList<String> getIpsAtivos() {
		return ipsAtivos;
	}
	public static String getCurrentHostName() {
		return currentHostName;
	}
	public static String getCurrentIp() {
		return currentIp;
	}
	public boolean isScanRunning() {
		return scanRunning;
	}
	public NetworkScan() throws IOException{
		checarAdaptadores();//verifica adaptadores de rede
		getAllIps();//recupera todos os ips ativos da rede
	}
	public static String getIpSubnet(String ip){
		String[] ipParts = ip.split("\\.");
		String subnet = ipParts[0]+"."+ipParts[1]+"."+ipParts[2];
		return subnet;
	}
	
	public static ArrayList<String> checarAdaptadores() throws IOException{
		if(adaptadores.size() <= 0){
			//LOCALIZA TODOS OS ADAPTADORES DE REDE E SUAS RESPECTIVAS REDES
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
	        for (NetworkInterface netint : Collections.list(nets)) {
	        	if (netint.isUp() && !netint.isLoopback() && !netint.isVirtual()) {
	        		if(netint.getInetAddresses().nextElement() instanceof Inet4Address){
	        			adaptadores.add(netint.getDisplayName().replace(" - ", " -- ") + " - "+ netint.getInetAddresses().nextElement().getHostAddress());
	        		}
	        	}
	        }
			
			//AVISA USUARIO QUE FORAM ENCONTRADOS MAIS DE UM ADAPTADOR DE REDE
		    if(adaptadores.size() > 1){
				String[] opc = adaptadores.toArray(new String[0]);
				JFrame frame = new JFrame("Múltiplos Adaptadores");
			    String adaptadoresRede = (String) JOptionPane.showInputDialog(frame, 
			        "Foi detectado mais de um adptador\nde rede ativo, qual deseja utilizar?",
			        "Múltiplos adaptadores de rede",
			        JOptionPane.QUESTION_MESSAGE, 
			        Adaptericon, 
			        opc, 
			        opc[0]);
			    if(adaptadoresRede == null){
			    	currentIp = adaptadores.get(0).split(" - ")[1];
			    	//USA PRIMEIRO ADAPTADOR COMO PADRAO CASO FECHE O DIALOGO
			    }else{
			    	currentIp = adaptadoresRede.split(" - ")[1];
		    	}
				
	        }else{
	        	currentIp = adaptadores.get(0).split(" - ")[1];
	        	//USA UNICO ADAPTADOR ENCONTRADO
	        }
		}
		return adaptadores;
	}
	
	public void checkIps(int start, int end, Thread t) throws UnknownHostException, IOException, InterruptedException{
		int timeout = 1000;	
		String subnet = getIpSubnet(adaptadores.get(0).split(" - ")[1]);
		for (int i = start; i < end; i++){
		   	String host = subnet + "." + i;
		   	//System.out.println(">tentando: "+host);
            if (InetAddress.getByName(host).isReachable(timeout) && !ipsAtivos.contains(host)){
                ipsAtivos.add(host);
            }
    	}
	}
    public void checkServers(String ip) throws SocketException, IOException{
		JsonObject checarServers = new JsonObject();
		checarServers.addProperty("name", InetAddress.getLocalHost().getHostName());
		checarServers.addProperty("type", "isServer");
		checarServers.addProperty("ip",currentIp);
    	new SendMessage(ip,CommonMethods.getDefaultPort(),checarServers.toString(),1000);
	}
    public void serverListUpdate(){
    	//atualiza lista de servers
    	if(ipsAtivos.size() > 0){
			for (int i = 0; i < ipsAtivos.size(); i++) {
				try {
					//System.out.println(">checando: "+ipsAtivos.get(i));
					checkServers(ipsAtivos.get(i));
				} catch (IOException e) {
					//System.out.println(">"+ipsAtivos.get(i)+" nao é um server");
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}	
			}
    	}
    }
	public synchronized void getAllIps(){
		scan = new Thread("FullScanWatchDog") {
            public void run(){
    
            	while(scanRunning){
            		if(ips1.isAlive() == false && 
    						ips2.isAlive() == false && 
    						ips3.isAlive() == false && 
    						ips4.isAlive() == false && 
    						ips5.isAlive() == false && 
    						ips6.isAlive() == false && 
    						ips7.isAlive() == false && 
    						ips8.isAlive() == false && 
    						ips9.isAlive() == false && 
    						ips10.isAlive() == false && 
    						ips11.isAlive() == false && 
    						ips12.isAlive() == false){
						serverListUpdate();
						scanRunning = false;
						System.out.println(">Escaneamento da rede finalizado");
						//System.out.println(ipsAtivos);
						//Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
						//System.out.println(threadSet);
						
					}
				}
		    }
		};
		ips1 = new Thread("GetIpsThread1") {
            public void run(){
            	try {
					checkIps(1,21,ips1);
				} catch (IOException | InterruptedException e) {
					//e.printStackTrace();
				}
		    }
		};
		ips2 = new Thread("GetIpsThread2") {
            public void run(){
            	try {
					checkIps(22,43,ips2);
				} catch (IOException | InterruptedException e) {
					//e.printStackTrace();
				}
		    }
		};
		ips3 = new Thread("GetIpsThread3") {
            public void run(){
            	try {
					checkIps(44,66,ips3);
				} catch (IOException | InterruptedException e) {
					//e.printStackTrace();
				}
		    }
		};
		ips4 = new Thread("GetIpsThread4") {
            public void run(){
            	try {
					checkIps(67,88,ips4);
				} catch (IOException | InterruptedException e) {
					//e.printStackTrace();
				}
		    }
		};
		ips5 = new Thread("GetIpsThread5") {
            public void run(){
            	try {
					checkIps(89,110,ips5);
				} catch (IOException | InterruptedException e) {
					//e.printStackTrace();
				}
		    }
		};
		ips6 = new Thread("GetIpsThread6") {
            public void run(){
            	try {
					checkIps(111,132,ips6);
				} catch (IOException | InterruptedException e) {
					//e.printStackTrace();
				}
		    }
		};
		ips7 = new Thread("GetIpsThread7") {
            public void run(){
            	try {
					checkIps(133,154,ips7);
				} catch (IOException | InterruptedException e) {
					//e.printStackTrace();
				}
		    }
		};
		ips8 = new Thread("GetIpsThread8") {
            public void run(){
            	try {
					checkIps(155,176,ips8);
				} catch (IOException | InterruptedException e) {
					//e.printStackTrace();
				}
		    }
		};
		ips9 = new Thread("GetIpsThread9") {
            public void run(){
            	try {
					checkIps(177,198,ips9);
				} catch (IOException | InterruptedException e) {
					//e.printStackTrace();
				}
		    }
		};
		ips10 = new Thread("GetIpsThread10") {
            public void run(){
            	try {
					checkIps(199,220,ips10);
				} catch (IOException | InterruptedException e) {
					//e.printStackTrace();
				}
		    }
		};
		ips11 = new Thread("GetIpsThread11") {
            public void run(){
            	try {
					checkIps(221,242,ips11);
				} catch (IOException | InterruptedException e) {
					//e.printStackTrace();
				}
		    }
		};
		ips12 = new Thread("GetIpsThread12") {
            public void run(){
            	try {
					checkIps(243,256,ips12);
				} catch (IOException | InterruptedException e) {
					//e.printStackTrace();
				}
		    }
		};
		ips1.start();
		ips2.start();
		ips3.start();
		ips4.start();
		ips5.start();
		ips6.start();
		ips7.start();
		ips8.start();
		ips9.start();
		ips10.start();
		ips11.start();
		ips12.start();
		scan.start();
	}
	public static void main(String[] args) throws IOException {
		 //checkHosts();
		 //System.out.println(ipsAtivos);
		 //System.out.println(checkAdaptersSubnet());
		//checkHosts();
		//new IPScan();
		
		//System.out.println(InetAddress.getLocalHost().getHostAddress());
	}
}
