package parksys.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import parksys.modelo.EntradaSaida;
import parksys.modelo.Mensalista;
import parksys.modelo.Veiculo;

public class VeiculoDAO {
	
	private ParkSysDataSource dataSource = new ParkSysDataSource();
	
	public void cadastrar (Veiculo veiculo) throws DataAccessException {
		try (Connection conn = dataSource.getConnection()) {
			conn.setAutoCommit(false);
			
			try (Statement stmt = conn.createStatement()) {			
				String insert = String.format("INSERT INTO veiculos (mensalistaID, placa, descricao) "
						+ "VALUES ('%d', '%s', '%s');", 
						veiculo.getMensalistaID(), 
						veiculo.getPlaca(), 
						veiculo.getDescricao());
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
	
	public Veiculo getVeiculo(String placa) throws DataAccessException {
		Veiculo veiculo = null;
		try (Connection conn = dataSource.getConnection()) {
			
			try (Statement stmt = conn.createStatement()) {			
				String select = String.format("SELECT * FROM veiculos WHERE "
						+ "placa like '%s';", 
						placa);
				try(ResultSet rs = stmt.executeQuery(select)){
					if (rs.next()) {
						veiculo = new Veiculo(
								rs.getString("placa"),
								rs.getString("descricao"),
								rs.getInt("mensalistaID"));
					}
				}
				
			} catch(SQLException e) {
				throw e;
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
		return veiculo;
	}
	
}
