package parksys.dao;
import java.sql.*;

import javax.swing.JOptionPane;

import parksys.modelo.Configuracao;
import parksys.utilidades.Funcoes;

public class ConfiguracaoDAO {
	private ParkSysDataSource dataSource = new ParkSysDataSource();

	public Configuracao getConfiguracaoSistema() throws DataAccessException {
		Configuracao config = null;
		try (Connection conn = dataSource.getConnection()) {
			
			try (Statement stmt = conn.createStatement()) {		

				String select = String.format("SELECT * FROM configuracao;");
				try(ResultSet rs = stmt.executeQuery(select)){
					if (rs.next()) {
						config = new Configuracao(rs.getDouble("duracao_bloco"), rs.getDouble("tarifa"), rs.getDouble("desconto_mensalista"), rs.getInt("qtd_minima_horas"), rs.getInt("vagas_max"));
					}
				}
			}
		} catch(SQLException e) {
			throw new DataAccessException(e);
		}
			
		return config;
	}
	
	public void setConfiguracaoSistema(Configuracao config) throws DataAccessException {
		try (Connection conn = dataSource.getConnection()) {
			conn.setAutoCommit(false);
			try (Statement stmt = conn.createStatement()) {			
				String insert = String.format("INSERT INTO configuracao "
						+ "VALUES (%s, %s, %s, %d, %d);", 
						Funcoes.DoubleToString(config.getDuracao_bloco()),
						Funcoes.DoubleToString(config.getTarifa()),
						Funcoes.DoubleToString(config.getDesconto()),
						config.getHoras_minimas(),
						config.getVagas_max());
								
				stmt.executeUpdate("truncate table configuracao;");
				stmt.executeUpdate(insert);
				conn.commit();
				
			} catch(SQLException e) {
				conn.rollback();
				throw e;
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}
}
