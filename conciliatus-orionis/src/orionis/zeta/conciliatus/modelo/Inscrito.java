package orionis.zeta.conciliatus.modelo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Inscrito {
	private StringProperty nome = new SimpleStringProperty();
	private StringProperty email = new SimpleStringProperty();
	
	public Inscrito(String nome, String email) {
		this.nome.set(nome);
		this.email.set(email);
	}
	
	public String getNome() {
		return nome.get();
	}
	
	public StringProperty getPropriedadeNome() {
		return nome;
	}
	
	public String getEmail() {
		return email.get();
	}
	
	public StringProperty getPropriedadeEmail() {
		return email;
	}
}
