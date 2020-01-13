package br.fatec.menus;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

public class CommonMethods {

	
	/**/
	final static int defaultPort = 6698;
	public static int getDefaultPort() {
		return defaultPort;
	}
	public static boolean validarNome(String usuario){
        Pattern p = Pattern.compile("^[a-zA-Z0-9._-]{3,}$");
        Matcher u = p.matcher(usuario);
        if (u.find()){
            return true;
        }else{
           return false;
        }
    }
	static void setWindowPosition(JFrame window, int screen){        
	    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] allDevices = env.getScreenDevices();
	    int topLeftX, topLeftY, screenX, screenY, windowPosX, windowPosY;
	    if (screen < allDevices.length && screen > -1){
	        topLeftX = allDevices[screen].getDefaultConfiguration().getBounds().x;
	        topLeftY = allDevices[screen].getDefaultConfiguration().getBounds().y;
	        screenX  = allDevices[screen].getDefaultConfiguration().getBounds().width;
	        screenY  = allDevices[screen].getDefaultConfiguration().getBounds().height;
	    }else{
	        topLeftX = allDevices[0].getDefaultConfiguration().getBounds().x;
	        topLeftY = allDevices[0].getDefaultConfiguration().getBounds().y;
	        screenX  = allDevices[0].getDefaultConfiguration().getBounds().width;
	        screenY  = allDevices[0].getDefaultConfiguration().getBounds().height;
	    }
	    windowPosX = ((screenX - window.getWidth())  / 2) + topLeftX;
	    windowPosY = ((screenY - window.getHeight()) / 2) + topLeftY;
	    window.setLocation(windowPosX, windowPosY);
	}

	public static void main(String[] args) throws IOException {
		Start inicio = new Start();
		setWindowPosition(inicio, 0);
		inicio.setVisible(true);
	}
}
