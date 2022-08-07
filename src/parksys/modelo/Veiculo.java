package parksys.modelo;

public class Veiculo {
	String placa;
	String descricao;
	int mensalistaID;
	
	public Veiculo (String placa) {
		this(placa, "");
	}
	
	public Veiculo (String placa, String descricao) {
		this(placa, descricao, 0);
	}
	
	public Veiculo (String placa, String descricao, int mensalistaID) {
		this.placa = placa;
		this.descricao = descricao;
		this.mensalistaID = mensalistaID;
	}
	
	public String getPlaca() {
		return placa;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public int getMensalistaID() {
		return mensalistaID;
	}

	public void setMensalistaID(int mensalistaID) {
		this.mensalistaID = mensalistaID;
	}
	
}
