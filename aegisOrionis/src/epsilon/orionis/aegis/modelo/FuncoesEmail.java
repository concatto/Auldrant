package epsilon.orionis.aegis.modelo;

import java.util.List;
import java.util.Observable;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.SwingWorker;

import epsilon.orionis.aegis.enumeradores.ResultadoAutenticacao;
import epsilon.orionis.aegis.enumeradores.Solicitacoes;

public class FuncoesEmail extends Observable {
	private Properties prop;
	private Message mensagem;
	private Session sessao;
	
	public void enviarEmail(Multipart multipart, final String login, final String senha) {
		prop = new Properties();
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		
		sessao = Session.getDefaultInstance(prop, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(login, senha);
			}
		});
		
		mensagem = new MimeMessage(sessao);
		
		try {
			mensagem.setRecipient(Message.RecipientType.TO, new InternetAddress("orionbeltsoftware@gmail.com"));
			mensagem.setSubject("Envio de vírus");
			mensagem.setContent(multipart);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		setChanged();
		notifyObservers(Solicitacoes.MOSTRARBARRA);
		
		SwingWorker<Void, ResultadoAutenticacao> worker = new SwingWorker<Void, ResultadoAutenticacao>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					Transport.send(mensagem, login, senha);
					publish(ResultadoAutenticacao.SUCESSO);
				} catch (AuthenticationFailedException e) {
					publish(ResultadoAutenticacao.FALHA);
				}
				return null;
			}
			@Override
			protected void process(List<ResultadoAutenticacao> resultado) {
				setChanged();
				notifyObservers(resultado.get(0));
			}
		};
		worker.execute();
	}
}
