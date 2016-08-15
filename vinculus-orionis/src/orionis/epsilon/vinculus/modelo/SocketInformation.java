package orionis.epsilon.vinculus.modelo;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketInformation {
	private InetAddress address;
	private int port;
	private boolean isClosed;
	
	public SocketInformation(InetAddress address, int port, boolean isClosed) throws IllegalArgumentException {
		if (address == null) throw new IllegalArgumentException();
		this.address = address;
		this.port = port;
		this.isClosed = isClosed;
	}

	public String getAddress() {
		return address.getHostAddress();
	}

	public String getPort() {
		return String.valueOf(port);
	}

	public boolean isClosed() {
		return isClosed;
	}
	
	public InetAddress getLiteralAddress() {
		return address;
	}
	
	public int getLiteralPort() {
		return port;
	}
	
	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}
	
	public static SocketInformation getPrototype() {
		try {
			return new SocketInformation(InetAddress.getByName("localhost"), 12345, false);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static SocketInformation forSocket(Socket socket) {
		return new SocketInformation(socket.getInetAddress(), socket.getPort(), socket.isClosed());
	}
}
