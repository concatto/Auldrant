package orionis.epsilon.vinculus.modelo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class Servidor extends Conexao {
	private ServerSocket server;

	public Servidor(Mensageiro mensageiro) {
		super(mensageiro);
	}
	
	public void criarServidor(int porta) throws IOException {
		server = new ServerSocket(porta);
	}
	
	public String getEnderecoExterno() {
		String endereco;
		try {
			URL checker = new URL("http://checkip.amazonaws.com/");
			BufferedReader in = new BufferedReader(new InputStreamReader(checker.openStream()));
			endereco = in.readLine();
			in.close();
		} catch (IOException e) {
			endereco = null;
		}
		return endereco;
	}
	
	public Socket aguardarConexao() {
		try {
			return server.accept();
		} catch (IOException e) {
			/* O ServerSocket foi fechado */
			return null;
		}
	}
	
	public void aguardarArquivo(Socket cliente) {		
		try {
			in = new DataInputStream(cliente.getInputStream());
			File caminho = mensageiro.obterDestino(in.readUTF(), cliente);
			if (caminho == null) {
				mensageiro.comunicar(cliente, Mensagem.FIM);
				return;
			}
			escrever(cliente, Mensagem.ACEITAR);
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(caminho));
			int theByte;
			int total = 0;
			int progresso = 0;
			int intervalo = in.readInt();
			BufferedInputStream buffer = new BufferedInputStream(in);
			while ((theByte = buffer.read()) != -1) {
				total++;
				if (total % intervalo == 0) {
					progresso++;
					mensageiro.comunicar(cliente, progresso);
				}
				out.write(theByte);
			}
			out.flush();
			out.close();
			mensageiro.comunicar(cliente, Mensagem.FIM);
		} catch (IOException e) {
			System.out.println("Erro IO ao aguardar o arquivo. Conserte-me!");
			e.printStackTrace();
		}
	}
	
	public void fecharServerSocket() {
		try {
			server.close();
		} catch (IOException e) {
			System.out.println("Erro IO ao fechar o servidor. Conserte-me!");
		}
	}
}
