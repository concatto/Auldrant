package orionis.delta.sandbox.tratamento;

import java.util.Date;

public class Tratamento {
	private Remedio remedio;
	private Paciente paciente;
	private Medico medico;
	private Date dataInicio;
	private int quantidadeDias;
	private int vezesPorDia;
	private float dosagem;
	
	public Tratamento(Remedio remedio, Paciente paciente, Medico medico, Date dataInicio, int quantidadeDias, int vezesPorDia, float dosagem) {
		this.remedio = remedio;
		this.paciente = paciente;
		this.dataInicio = dataInicio;
		this.medico = medico;
		this.quantidadeDias = quantidadeDias;
		this.vezesPorDia = vezesPorDia;
		this.dosagem = dosagem;
	}

	public Remedio getRemedio() {
		return remedio;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public Date getDataInicio() {
		return dataInicio;
	}
	
	public Medico getMedico() {
		return medico;
	}

	public int getQuantidadeDias() {
		return quantidadeDias;
	}

	public int getVezesPorDia() {
		return vezesPorDia;
	}
	
	public float getDosagem() {
		return dosagem;
	}
}
