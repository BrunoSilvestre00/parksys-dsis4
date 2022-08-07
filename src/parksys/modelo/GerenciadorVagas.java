package parksys.modelo;

import parksys.dao.ConfiguracaoDAO;
import parksys.dao.DataAccessException;
import parksys.gui.JanelaPrincipal;

public class GerenciadorVagas {
	
	ConfiguracaoDAO dao;
	Configuracao config;
	
	private int vagasOcupadas;
	private int vagasTotais;
	
	JanelaPrincipal janela;
	
	public GerenciadorVagas(JanelaPrincipal janela) {
		vagasOcupadas = 0;
		this.janela = janela;
	}
	
	public void updateDados() throws DataAccessException {
		dao = new ConfiguracaoDAO();
		config = dao.getConfiguracaoSistema();
		vagasTotais = config.getVagas_max();
	}
	
	public void update() {
		janela.updateLabelCount();
	}
	
	public boolean cheio() {
		return vagasOcupadas == vagasTotais;
	}
	
	public void addOcupadas() {
		vagasOcupadas++;
		update();
	}
	
	public void subOcupadas() {
		vagasOcupadas--;
		update();
	}

	public int getVagasOcupadas() {
		return vagasOcupadas;
	}

	public int getVagasTotais() {
		return vagasTotais;
	}
}
