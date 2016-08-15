package orionis.delta.sandbox.tratamento;

import java.util.Date;

public class Principal {
	private Tratamento[] tratamento;
	private int quantidade;
	
	public Principal() {
		System.out.println("Quantos remédios deseja registrar?");
		quantidade = Teclado.lerInteiro();
		tratamento = new Tratamento[quantidade];
		
		for (int i = 0; i < tratamento.length; i++) {
			cadastrarRemedio(i);
		}
	}
	
	private void cadastrarRemedio(int indice) {
		System.out.println("Digite o nome do remédio");
		Remedio remedio = new Remedio(Teclado.lerTexto());
		
		System.out.println("Digite o nome do paciente");
		Paciente paciente = new Paciente(Teclado.lerTexto());
		
		System.out.println("Digite a data de início");
		Date dataInicio = Teclado.lerData();
		
		System.out.println("Digite o nome do médico");
		Medico medico = new Medico(Teclado.lerTexto());
		
		System.out.println("Digite a quantidade de dias em que o remédio será tomado");
		int quantidadeDias = Teclado.lerInteiro();
		
		System.out.println("Digite quantas vezes por dia o remédio será tomado");
		int vezesPorDia = Teclado.lerInteiro();
		
		System.out.println("Digite a dosagem do remédio");
		float dosagem = Teclado.lerNumero();
		
		tratamento[indice] = new Tratamento(remedio, paciente, medico, dataInicio, quantidadeDias, vezesPorDia, dosagem);
	}
}
