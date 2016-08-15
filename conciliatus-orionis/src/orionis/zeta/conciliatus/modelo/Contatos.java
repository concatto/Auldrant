package orionis.zeta.conciliatus.modelo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import orionis.zeta.conciliatus.visao.AbstractStage;

import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.FullName;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

public class Contatos {
	private URL DEFAULT;
	private String REL = "http://schemas.google.com/g/2005#other";
	
	private String user;
	private ContactsService servico = new ContactsService(AbstractStage.NAME);
	private List<ContactEntry> contatos;
	
	public Contatos() {
		try {
			DEFAULT = new URL("https://www.google.com/m8/feeds/contacts/default/full");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public void conectar(String login, String senha) throws AuthenticationException {
		servico.setUserCredentials(login, senha);
		int index = login.indexOf("@");
		if (index < 0) index = login.length();
		user = login.substring(0, index);
		
		Query query = new Query(DEFAULT);
		query.setMaxResults(99999);
		
		try {
			contatos = servico.query(query, ContactFeed.class).getEntries();
		} catch (IOException | ServiceException e) {
			e.printStackTrace();
		}
	}
	
	public void exibirContatos() throws IOException, ServiceException {
		contatos.stream()
			.filter(ContactEntry::hasName)
			.map(t -> t.getName().getFullName().getValue())
			.forEach(System.out::println);
	}
	
	public boolean adicionarContato(Inscrito inscrito) {
		return adicionarContato(inscrito.getNome(), inscrito.getEmail());
	}
	
	public boolean adicionarContato(String nome, String email) {
		boolean existe = contatos.stream()
			.map(ContactEntry::getEmailAddresses)
			.filter(t -> !t.isEmpty())
			.map(t -> t.get(0).getAddress())
			.anyMatch(t -> t.equals(email));
		
		if (existe) return false;
		
		ContactEntry contato = new ContactEntry();
		
		Name dadoNome = new Name();
		dadoNome.setFullName(new FullName(nome, null));
		contato.setName(dadoNome);
		
		Email dadoEmail = new Email();
		dadoEmail.setAddress(email);
		dadoEmail.setRel(REL);

		contato.addEmailAddress(dadoEmail);
		
		try {
			servico.insert(DEFAULT, contato);
			return true;
		} catch (IOException | ServiceException e) {
			return false;
		}
	}

	public String getUser() {
		return user;
	}
}
