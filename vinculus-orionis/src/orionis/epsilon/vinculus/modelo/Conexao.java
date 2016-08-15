package orionis.epsilon.vinculus.modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public abstract class Conexao {
	protected Mensageiro mensageiro;
	protected DataInputStream in;
	protected DataOutputStream out;
	
	public Conexao(Mensageiro mensageiro) {
		this.mensageiro = mensageiro;
	}
	
	public void ouvir(Socket socket) {		
		Thread thread = new Thread(() -> {
			try {
				in = new DataInputStream(socket.getInputStream());
				String mensagem = in.readUTF();
				mensageiro.comunicar(socket, mensagem);
				/* Se a solicitação não indicar o Fim, ouvir o mesmo Socket novamente */
				if (!mensagem.equals(Mensagem.FIM)) ouvir(socket);
			} catch (SocketException | EOFException e) {
				mensageiro.comunicar(socket, Mensagem.FIM);
			} catch (IOException e) {
				mensageiro.comunicar(socket, Mensagem.ERRO_IO);
			}
		});
		
		thread.start();
	}
	
	public void escrever(Socket destino, Mensagem mensagem) throws IOException, SocketException {
		out = new DataOutputStream(destino.getOutputStream());
		out.writeUTF(mensagem.toString());
	}
}
