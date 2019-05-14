package application.unifei.eco009.server;

import application.unifei.eco009.stubs.CesarCipher;
import application.unifei.eco009.stubs.CesarCipherImplementation;
import application.unifei.eco009.stubs.XOREncryption;
import application.unifei.eco009.stubs.XOREncryptionImplementation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
public class ClientHandler extends Thread {
	private Socket clientSocket;
	public ClientHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	@Override
	public void run() {
		ObjectInputStream inputStream = null;
		try {
			inputStream = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			System.out.println("SERVER: Client handler Input Stream.");
		}
		Object function = null;
		try {
			function = inputStream.readObject();
		} catch (IOException e) {
			System.out.println("SERVER: Client handler IOException.");
		} catch (ClassNotFoundException e) {
			System.out.println("SERVER: Client handler ClassNotFoundException.");
		}
		if (function != null && function instanceof CesarCipher) {
			CesarCipher cesarCipher = (CesarCipher) function;
			CesarCipherImplementation cesarCipherImplementation = new CesarCipherImplementation(cesarCipher);
			cesarCipher.setReturnValue(cesarCipherImplementation.cesarCipher());
			ObjectOutputStream outputStream = null;
			try {
				outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			} catch (IOException e) {
				System.out.println("SERVER: Could not create Object Output Stream.");
			}
			try {
				outputStream.writeObject(cesarCipher);
			} catch (IOException e) {
				System.out.println("SERVER: Could not write object.");
			}
		} else if (function != null && function instanceof XOREncryption) {
			XOREncryption XOREncryption = (XOREncryption) function;
			XOREncryptionImplementation XOREncryptionImplementation = new XOREncryptionImplementation(XOREncryption);
			XOREncryption.setReturnValue(XOREncryptionImplementation.XOREncryption());
			ObjectOutputStream outputStream = null;
			try {
				outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			} catch (IOException e) {
				System.out.println("SERVER: Could not create Object Output Stream.");
			}
			try {
				outputStream.writeObject(XOREncryption);
			} catch (IOException e) {
				System.out.println("SERVER: Could not write object.");
			}
		} else {
			try {
				throw new ClassNotFoundException("SERVER: Could not find procedure.");
			} catch (ClassNotFoundException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
