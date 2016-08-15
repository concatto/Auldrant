package orionis.delta.sandbox.copa;

import java.util.Date;

public class Pessoa {
	private String nome;
	private Date nascimento;
	private String escolaridade;
	private float altura;
	private float peso;
	
	public Pessoa(String nome, Date nascimento, String escolaridade, float altura, float peso) {
		this.nome = nome;
		this.nascimento = nascimento;
		this.escolaridade = escolaridade;
		this.altura = altura;
		this.peso = peso;
	}
	
	public String getNome() {
		return nome;
	}
	
	public Date getNascimento() {
		return nascimento;
	}
	
	public String getEscolaridade() {
		return escolaridade;
	}
	
	public float getAltura() {
		return altura;
	}
	
	public float getPeso() {
		return peso;
	}
}
