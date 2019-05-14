package application.unifei.eco009.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CryptoService {
	public static void main(String[] args) {
		int portNumber = 0;
		try {
			portNumber = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			System.out.println("CryptoService usage:\njava CryptoService [portNumber]");
			System.exit(0);
		}
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println("[ERROR] Could not create server.");
		}
		for (;;) {
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
				ClientHandler clientHandler = new ClientHandler(clientSocket);
				clientHandler.start();
			} catch (IOException e) {
				System.out.println("[ERROR] Could not accept resquest from client.");
			}
		}
		/* * * * * * * * *
		*   NOT REACHED  *
		* * * * * * * * */
	}
}
