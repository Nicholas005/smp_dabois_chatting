package chat_v4;

import java.net.*;
import java.util.*;
import java.io.*;

public class Client {
	public static void main(String[ ] args) throws IOException {
		try (Socket s = new Socket("127.0.0.1", 65432); Scanner usrIn = new Scanner(System.in);) {
			System.out.println("Connected to server.");
			ClientThread t = new ClientThread(s);
			t.start();
			
			PrintWriter output = new PrintWriter(s.getOutputStream(), true);
			
			while (true) {
				String sending = usrIn.nextLine();
				if (sending.equals("q") || sending.equals("exit")) {
					output.println(sending);
					break;
				}
				output.println(sending);
			}
			s.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class ClientThread extends Thread {
	Socket s;
	
	public ClientThread(Socket s) {
		this.s = s;
	}

	public void run() {
		try (BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));) {
			String message;
			
			while (true) {
				message = input.readLine();
				if (message != null) {
					System.out.println(message);
				}
			}
		}
		catch (Exception e) {
			System.out.println("Failed to start message thread. Closing connection...");
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