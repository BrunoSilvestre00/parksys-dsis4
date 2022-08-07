package parksys.modelo;

public class Pagamento {
	
	int mensalistaID, blocos;
	double valor;

	public Pagamento(int mensalistaID, double valor, int blocos) {
		this.mensalistaID = mensalistaID;
		this.valor = valor;
		this.blocos = blocos;
	}

	public int getMensalistaID() {
		return mensalistaID;
	}

	public int getBlocos() {
		return blocos;
	}

	public double getValor() {
		return valor;
	}
	
	
}
