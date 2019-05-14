package application.unifei.eco009.stubs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
public class XOREncryption extends ServerInfo implements Serializable {
	private String plaintext;
	private String encryptionKey;
	private String returnValue;
	public String getPlaintext() {
		return plaintext;
	}
	public void setPlaintext(String plaintext) {
		this.plaintext = plaintext;
	}
	public String getEncryptionKey() {
		return encryptionKey;
	}
	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
	public String getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}
	public String XOREncryption(String plaintext, String encryptionKey) {
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
		XOREncryption copy = new XOREncryption();
		copy.setPlaintext(plaintext);
		copy.setEncryptionKey(encryptionKey);
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
		XOREncryption response = null;
		try {
			response = (XOREncryption) inputStream.readObject();
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
