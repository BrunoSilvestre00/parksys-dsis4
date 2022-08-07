package parksys.modelo;

import java.util.Date;

public class EntradaSaida {
	private String placa;
	private String descricao;
	private Date hora_entrada;
	private Date hora_saida;
	private Double duracao_bloco;
	
	public EntradaSaida(String placa, String descricao, Date hora_entrada) {
		this(placa, descricao, hora_entrada, new Date(), 0d);
	}
	
	public EntradaSaida(String placa, String descricao, Date hora_entrada, Date hora_saida, Double duracao_bloco) {
		this.placa = placa;
		this.descricao = descricao;
		this.hora_entrada = hora_entrada;
		this.hora_saida = hora_saida;
		this.duracao_bloco = duracao_bloco;
	}

	public String getPlaca() {
		return placa;
	}
	
	public void setPlaca(String placa) {
		this.placa = placa;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Double getDuracao_bloco() {
		return duracao_bloco;
	}
	
	public void setDuracao_bloco(Double duracao_bloco) {
		this.duracao_bloco = duracao_bloco;
	}

	public Date getHora_entrada() {
		return hora_entrada;
	}

	public void setHora_entrada(Date hora_entrada) {
		this.hora_entrada = hora_entrada;
	}

	public Date getHora_saida() {
		return hora_saida;
	}

	public void setHora_saida(Date hora_saida) {
		this.hora_saida = hora_saida;
	}
	
	
	
	
}
