package parksys.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import parksys.modelo.Pagamento;
import parksys.utilidades.Funcoes;

public class PagamentoDAO {
	private ParkSysDataSource dataSource = new ParkSysDataSource();
	
	public void registrar(Pagamento pagamento) throws DataAccessException {
		try (Connection conn = dataSource.getConnection()) {
			conn.setAutoCommit(false);
			
			try (Statement stmt = conn.createStatement()) {			
				String insert = String.format("INSERT INTO pagamentos (mensalistaID, dataPagamento, valor, blocos) "
						+ "VALUES (%d, now(), %s, %d);", 
						pagamento.getMensalistaID(),
						Funcoes.DoubleToString(pagamento.getValor()),
						pagamento.getBlocos());
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
