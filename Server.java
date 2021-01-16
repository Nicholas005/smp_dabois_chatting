package chat_v4;

import java.util.*;
import java.net.*;
import java.io.*;

public class Server {
	public static List<Object> threads = new ArrayList<>();
	
	public static void main(String[ ] args) throws IOException {
		
		try (ServerSocket ss = new ServerSocket(65432);) {
			System.out.println("Listening for connections on port 65432...");
			while (true) {
				try {
					Socket s = ss.accept();
					String addr = "<" + s.getInetAddress() + ">";
					System.out.println("Client connected: " + addr);
					ClientHandler t = new ClientHandler(s, addr);
					threads.add(t);
					t.start();
				}
				catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("Program finished");
		}
	}
	
	public static void broadcast(String message, Socket socket) {
		for (Object client : threads) {
			if (client != socket) {
				try (PrintWriter output = new PrintWriter(socket.getOutputStream(), true);) {
					output.println(message);
				}
				catch (Exception e) {
					System.out.println("Failed to send message to client " + "<" + client + ">");
					e.printStackTrace();
				}
			}
		}
	}
}

class ClientHandler extends Thread {
	final Socket s;
	final String addr;
	
	public ClientHandler(Socket s, String addr) {
		this.s = s;
		this.addr = addr;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Client thread started");
			String received;
			
			BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			while (true) {
				received = input.readLine();
				
				if (received == "q" || received == "exit") {
					System.out.println("Client " + addr + " closed connection with server.");
					s.close();
					break;
				}
				
				System.out.println(addr + " " + received);
				Server.broadcast(received, s);
			}
		} 
		catch (Exception e) {
			System.out.println("Failed to run client thread. Closing connection...");
			e.printStackTrace();
			try {
				s.close();
			}
			catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
