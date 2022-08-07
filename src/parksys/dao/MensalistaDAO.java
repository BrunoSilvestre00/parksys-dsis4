package parksys.dao;

import java.sql.*;

import parksys.modelo.Mensalista;
import parksys.utilidades.Funcoes;

public class MensalistaDAO {
	private ParkSysDataSource dataSource = new ParkSysDataSource();
	
	public boolean existeMensalista(String cpf) throws DataAccessException {
		return (localizarMensalista(cpf) != null);
	}
	
	public int getID(String cpf) throws DataAccessException {
		int id = 0;
		try (Connection conn = dataSource.getConnection()){
			try (Statement stmt = conn.createStatement()){
				String query = String.format("select m.id from mensalistas m where m.cpf like '%s';", cpf);		
				try(ResultSet rs = stmt.executeQuery(query)){
					if (rs.next())
						id = rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
		
		return id;
	}
	
	public Mensalista localizarMensalista(String dado) throws DataAccessException {
		try {
			Mensalista mensalista = localizarCPF(dado);
			if (mensalista != null) {
				return mensalista;
			}
			return localizarTelefone(dado);
		} catch (DataAccessException e) {
			throw new DataAccessException(e);
		}
	}
	
	public Mensalista localizarCPF(String cpf) throws DataAccessException {
		Mensalista mensalista = null;
		
		try (Connection conn = dataSource.getConnection()){
			try (Statement stmt = conn.createStatement()){
				String query = String.format("select m.cpf, m.nome, m.telefone, m.saldo from mensalistas m where m.cpf like '%s';", cpf);		
				try(ResultSet rs = stmt.executeQuery(query)){
					if (rs.next())
						mensalista = new Mensalista(rs.getString(1), rs.getString(2), rs.getString(3), rs.getDouble(4));
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
		
		return mensalista;
	}
	
	public Mensalista localizarTelefone(String telefone) throws DataAccessException {
		Mensalista mensalista = null;
		try (Connection conn = dataSource.getConnection()){
			try (Statement stmt = conn.createStatement()){
				String query = String.format("select m.cpf, m.nome, m.telefone, m.saldo from mensalistas m where m.telefone like '%s';", telefone);		
				try(ResultSet rs = stmt.executeQuery(query)){
					if (rs.next())
						mensalista = new Mensalista(rs.getString(1), rs.getString(2), rs.getString(3), rs.getDouble(4));
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
		
		return mensalista;
	}
	
	public Mensalista localizarID(int id) throws DataAccessException {
		Mensalista mensalista = null;
		try (Connection conn = dataSource.getConnection()){
			try (Statement stmt = conn.createStatement()){
				String query = String.format("select m.cpf, m.nome, m.telefone, m.saldo from mensalistas m where m.ID = %d;", id);		
				try(ResultSet rs = stmt.executeQuery(query)){
					if (rs.next())
						mensalista = new Mensalista(rs.getString(1), rs.getString(2), rs.getString(3), rs.getDouble(4));
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
		
		return mensalista;
	}
	
	public void cadastrar (Mensalista mensalista) throws DataAccessException {
		try (Connection conn = dataSource.getConnection()) {
			conn.setAutoCommit(false);
			
			try (Statement stmt = conn.createStatement()) {			
				String insert = String.format("INSERT INTO mensalistas (CPF, nome, telefone, observacao, saldo) "
						+ "VALUES ('%s', '%s', '%s', '%s', %s);", 
						mensalista.getCPF(), 
						mensalista.getNome(), 
						mensalista.getTelefone(), 
						mensalista.getObservacoes(),
						Funcoes.DoubleToString(mensalista.getSaldo()));
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
	
	public void adicionarSaldo(Mensalista mensalista, double saldo) throws DataAccessException {
		try (Connection conn = dataSource.getConnection()) {
			conn.setAutoCommit(false);
			try (Statement stmt = conn.createStatement()) {
				String update = String.format("update mensalistas set saldo = saldo + %s where cpf like '%s'", Funcoes.DoubleToString(saldo), mensalista.getCPF());				
				stmt.executeUpdate(update);
				conn.commit();
			} catch(SQLException e) {
				conn.rollback();
				throw e;
			}	
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}
	
	public void subtrairSaldo(Mensalista mensalista, double saldo) throws DataAccessException {
		try (Connection conn = dataSource.getConnection()) {
			conn.setAutoCommit(false);
			try (Statement stmt = conn.createStatement()) {
				String update = String.format("update mensalistas set saldo = saldo - %s where cpf like '%s'", Funcoes.DoubleToString(saldo), mensalista.getCPF());
				stmt.executeUpdate(update);
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
