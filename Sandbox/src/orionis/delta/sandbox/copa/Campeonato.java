package orionis.delta.sandbox.copa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

public class Campeonato {
	private Registro registro = new Registro();
	
	public Campeonato() {
		String inicio = "Digite 1 para registrar um time";
		String inicioAlternativo = "Digite 1 para prosseguir para o campeonato";
		String termino = ", 2 para registrar um estádio ou 3 para sair";
		boolean completo = false;
		int opcao;
		
		while(true) {
			if (registro.getIndiceTime() < registro.getTimes().length) {
				completo = false;
				opcao = lerInt(inicio + termino);
			} else {
				completo = true;
				opcao = lerInt(inicioAlternativo + termino);
			}
			
			if (opcao == 1) {
				if (!completo) {
					registro.registrarTime();
				} else {
					iniciarCampeonato();
				}
			} else if (opcao == 2) {
				registro.registrarEstadio();
			} else if (opcao == 3) {
				System.exit(0);
			} else {
				System.out.println("Opção inválida");
			}
		}
	}
	
	private void iniciarCampeonato() {
		for (int i = 0; i < (registro.getTimes().length / 4); i++) {				//32 / 4 = 8
			for (int j = 0; j < (registro.getTimes().length / 8); j++) {			//32 / 8 = 4
				lerTexto("Digite o nome do " + (j+1) + "º time do ");
			}
		}
		
		for (int i = 0; i < (registro.getTimes().length / 4); i++) {				//32 / 4 = 8
			lerTexto("Digite o nome do melhor time do " + (i+1) + "º grupo");
			lerTexto("Digite o nome do segundo melhor time do " + (i+1) + "º grupo");
		}
	}

	public static String lerTexto(String mensagem) {
		return JOptionPane.showInputDialog(mensagem);
	}
	
	public static int lerInt(String mensagem) {
		return Integer.parseInt(lerTexto(mensagem));
	}
	
	public static float lerFloat(String mensagem) {
		return Float.parseFloat(lerTexto(mensagem));
	}
	
	public static Date lerData(String mensagem) {
		try {
			return new SimpleDateFormat("dd/MM/yyyy").parse(lerTexto(mensagem));
		} catch (ParseException e) {
			System.out.println("Formato errado. O formato correto é dd/mm/aaaa");
			return null;
		}
	}
	
	public static void main(String[] args) {
		new Campeonato();
	}
}
