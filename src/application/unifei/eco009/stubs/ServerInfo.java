package application.unifei.eco009.stubs;

import java.io.Serializable;
import java.net.Socket;
public abstract class ServerInfo implements Serializable {
	protected Socket socket;
	protected String serverAddress;
	protected int serverPort;
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public String getServerAddress() {
		return serverAddress;
	}
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	public int getServerPort() {
		return serverPort;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
}
