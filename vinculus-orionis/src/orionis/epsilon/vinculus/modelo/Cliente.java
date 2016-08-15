package orionis.epsilon.vinculus.modelo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

public class Cliente extends Conexao {
	private Socket client;
	private CountDownLatch latch;
	
	public Cliente(Mensageiro mensageiro) {
		super(mensageiro);
	}
	
	public Socket criarSocket(InetAddress endereco, int porta) throws IOException {
		client = new Socket(endereco, porta);
		
		return client;
	}
	
	public void transferirArquivo(final Socket origem, final File arquivo) {
		Thread worker = new Thread(new Runnable() {
			@Override
			public void run() {
				latch = new CountDownLatch(1);
				
				try {
					out = new DataOutputStream(origem.getOutputStream());
					BufferedInputStream in = new BufferedInputStream(new FileInputStream(arquivo));
					
					out.writeUTF(Mensagem.ARQUIVO.toString());
					out.writeUTF(arquivo.getName());
				
					try {
						latch.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					int theByte;
					int total = 0;
					int progresso = 0;
					int intervalo = (int) (arquivo.length() / 100);
					out.writeInt(intervalo);
					BufferedOutputStream buffer = new BufferedOutputStream(out);
					while ((theByte = in.read()) != -1) {
						total++;
						if (total % intervalo == 0) {
							progresso++;
							mensageiro.comunicar(origem, progresso);
						}
						buffer.write(theByte);
					}
					buffer.flush();
					out.flush();
					in.close();
				} catch (SocketException e) {
					mensageiro.comunicar(origem, Mensagem.TRANSFERENCIA_INTERROMPIDA);
				} catch (IOException e) {
					mensageiro.comunicar(origem, Mensagem.ERRO_IO);
				} finally {
					try {
						out.close();
					} catch (IOException e) {
						/* Já está fechado */
					}
				}
			}
		});
		worker.start();
	}
	
	public void prosseguir() {
		latch.countDown();
	}
}
