package orionis.epsilon.vinculus.modelo;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketHandler {
	private List<Socket> clientes = new ArrayList<>();
	
	public SocketHandler() {
		
	}
	
	public void inserirSocket(Socket socket) {
		clientes.add(socket);
	}
	
	public boolean removerSocket(SocketInformation info) {
		return removerSocket(getSocket(info));
	}
	
	public boolean removerSocket(Socket socket) {
		return clientes.remove(socket);
	}
	
	public Socket getSocket(SocketInformation info) {
		for (Socket socket : clientes) {
			if (socket.getInetAddress().equals(info.getLiteralAddress()) && socket.getPort() == info.getLiteralPort()) {
				return socket;
			}
		}
		return null;
	}

	public void limpar() {
		clientes.clear();
	}
}
