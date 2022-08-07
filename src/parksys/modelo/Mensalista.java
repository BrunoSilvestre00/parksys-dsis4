package parksys.modelo;

public class Mensalista {
	private String CPF;
	private String nome;
	private String telefone;
	private String observacoes;
	private double saldo;
	
	public Mensalista (String CPF, String nome, String telefone) {
		this(CPF, nome, telefone, 0);
	}
	
	public Mensalista (String CPF, String nome, String telefone, double saldo) {
		this.CPF = CPF;
		this.nome = nome;
		this.telefone = telefone;
		this.observacoes = "";
		this.saldo = saldo;
	}
	
	public String getCPF() {
		return CPF;
	}
	
	public String getNome() {
		return nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public String getObservacoes() {
		return observacoes;
	}
	
	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}
	
	public double getSaldo() {
		return saldo;
	}
	
	public void setSaldo(int saldo) {
		this.saldo = saldo;
	}
}
