package application.unifei.eco009.stubs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
public class CesarCipher extends ServerInfo implements Serializable {
	private String plaintext;
	private int key;
	private String returnValue;
	public String getPlaintext() {
		return plaintext;
	}
	public void setPlaintext(String plaintext) {
		this.plaintext = plaintext;
	}
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}
	public String cesarCipher(String plaintext, int key) {
		try {
			socket = new Socket(serverAddress, serverPort);
		} catch (IOException e) {
			System.out.println("CLIENT: Could not reach server.");
		}
		ObjectOutputStream outputStream = null;
		try {
			outputStream = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("CLIENT: Could not create Object Output Stream.");
		}
		CesarCipher copy = new CesarCipher();
		copy.setPlaintext(plaintext);
		copy.setKey(key);
		try {
			outputStream.writeObject(copy);
		} catch (IOException e) {
			System.out.println("CLIENT: Could not write object.");
		}
		ObjectInputStream inputStream = null;
		try {
			inputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("CLIENT: Could not create Object Input Stream");
		}
		CesarCipher response = null;
		try {
			response = (CesarCipher) inputStream.readObject();
		} catch (IOException e) {
			System.out.println("CLIENT: Could not read object.");
		} catch (ClassNotFoundException e) {
			System.out.println("CLIENT: Could not cast to ");
		}
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("CLIENT: Could not close socket.");
		}
		return response.getReturnValue();
	}
}
