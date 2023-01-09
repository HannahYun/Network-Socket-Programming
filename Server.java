import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
public class Server {
	ServerSocket serverSocket;
	public static HashMap<Integer, Vector<ClientThread>> roomClients= new HashMap<Integer, Vector<ClientThread>>();
	BufferedReader in;
	PrintWriter out;
	DataInputStream input;
	ClientThread newThread;
	public static void main(String[] args) throws Exception {
		new Server();
	}
	public Server() {
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("10.101.36.18",7790)); //컴퓨터 IP주소 사용함
		  while (true) {
              Socket socket = serverSocket.accept();
              newThread = new ClientThread(socket);   
              new Thread(newThread).start();    
          }
		}
		
		catch(IOException e) {
			e.printStackTrace();
			
		}  finally {
            try {
                serverSocket.close();
               //stopServer();
            } 
            catch (Exception e) {
            }
        }
		
	}
}