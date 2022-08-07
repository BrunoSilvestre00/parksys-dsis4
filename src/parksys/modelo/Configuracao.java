package parksys.modelo;

public class Configuracao {
	private double duracao_bloco;
	private double tarifa;
	private double desconto;
	private int horas_minimas;
	private int vagas_max;
	
	public Configuracao(double duracao_bloco, double tarifa, double desconto, int horas_minimas, int vagas_max) {
		this.duracao_bloco = duracao_bloco;
		this.tarifa = tarifa;
		this.desconto = desconto;
		this.horas_minimas = horas_minimas;
		this.vagas_max = vagas_max;
	}
	
	public double getDuracao_bloco() {
		return duracao_bloco;
	}
	public void setDuracao_bloco(double duracao_bloco) {
		this.duracao_bloco = duracao_bloco;
	}
	public double getTarifa() {
		return tarifa;
	}
	public void setTarifa(double tarifa) {
		this.tarifa = tarifa;
	}
	public double getDesconto() {
		return desconto;
	}
	public void setDesconto(double desconto) {
		this.desconto = desconto;
	}
	public int getHoras_minimas() {
		return horas_minimas;
	}
	public void setHoras_minimas(int horas_minimas) {
		this.horas_minimas = horas_minimas;
	}

	public int getVagas_max() {
		return vagas_max;
	}

	public void setVagas_max(int vagas_max) {
		this.vagas_max = vagas_max;
	}
	
}
