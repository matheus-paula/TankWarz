package br.fatec.menus;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SendMessage{
	public Socket socket;
    public Thread send;
    public SendMessage(String serverPublicIP, int port, String data, int timeout) throws SocketException, UnknownHostException, IOException{
            socket = new Socket();
            socket.connect(new InetSocketAddress(serverPublicIP,port),timeout);
            send(data);
    }
    public void send(String toSend) throws ConnectException{
        send = new Thread("Send") {
            public void run() {
                PrintWriter outToServer = null;
                try {
                    outToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                    outToServer.print(toSend);
                    outToServer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    outToServer.close();
                }
            }   
        };
        send.start();
    }
}
