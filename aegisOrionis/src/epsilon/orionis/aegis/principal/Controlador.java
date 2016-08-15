package epsilon.orionis.aegis.principal;

import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import epsilon.orionis.aegis.modelo.FuncoesEmail;
import epsilon.orionis.aegis.modelo.FuncoesPrincipais;
import epsilon.orionis.aegis.modelo.Utilidades;
import epsilon.orionis.aegis.visao.CompositorEmail;
import epsilon.orionis.aegis.visao.JanelaPrincipal;
import epsilon.orionis.aegis.visao.MostradorEmail;

public class Controlador {
	private JanelaPrincipal janelaPrincipal;
	private CompositorEmail compositorEmail;
	private FuncoesPrincipais funcoesPrincipais;
	private MostradorEmail mostradorEmail;
	private Utilidades utilidades;
	private FuncoesEmail email;
	private Multipart multipart;
	private MimeBodyPart parte;
	
	private String[] dados;
	
	public Controlador() {
		janelaPrincipal = new JanelaPrincipal(this);
		compositorEmail = new CompositorEmail(this);
		mostradorEmail = new MostradorEmail(this);
		funcoesPrincipais = new FuncoesPrincipais();
		utilidades = new Utilidades();
		email = new FuncoesEmail();
		multipart = new MimeMultipart();
		
		funcoesPrincipais.addObserver(janelaPrincipal);
		utilidades.addObserver(compositorEmail);
		email.addObserver(mostradorEmail);
	}
	
	public void realizarVacina(File caminho) {
		funcoesPrincipais.vacinar(caminho);
	}
	
	public void realizarRemocao(File caminho) {
		funcoesPrincipais.prepararRemocao(caminho);
	}
	
	public void forcarRemocao(File caminho) {
		funcoesPrincipais.remover(caminho);
	}
	
	public void realizarEmail() {
		dados = compositorEmail.coletarDados();
		if (dados == null) {
			return;
		}
		String destinatario = filtrarLogin(dados[0]);
		compositorEmail.comporMensagem(destinatario);
	}
	
	public void fornecerDados(String texto) throws MessagingException {
		if (texto.isEmpty()) {
			texto = "Mensagem enviada em branco. (mensagem programada)";
		}
		parte = new MimeBodyPart();
		parte.setText(texto);
		multipart.addBodyPart(parte);
	}
	
	public void fornecerDados(File anexo) throws IOException, MessagingException {
		if (anexo == null) {
			return;
		}
		parte = new MimeBodyPart();
		parte.attachFile(anexo);
		multipart.addBodyPart(parte);
	}
	
	public void solicitarEnvio() {
		email.enviarEmail(multipart, dados[0], dados[1]);
	}
	
	public void concluirEmail() {
		dados = null;
		
		try {
			for (int i = 0; i < multipart.getCount(); i++) {
				multipart.removeBodyPart(i);
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		compositorEmail.destruirComponentes();
	}
	
	public void realizarFiltroBytes(long bytes) {
		utilidades.filtrarBytes(bytes);
	}
	
	private String filtrarLogin(String login) {
		StringBuilder builder = new StringBuilder(login);
		int indexArroba = login.indexOf("@");
		int indexFinal = login.length();
		if (indexArroba != -1) {
			if (login.equals(compositorEmail.getLoginBase())) {
				builder.replace(0, indexFinal, "Anônimo");
			} else {
				builder.delete(indexArroba, indexFinal);
			}
		}
		builder.insert(0, "<html><b>De: </b>");
		builder.append("</html>");
		
		return builder.toString();
	}
}
