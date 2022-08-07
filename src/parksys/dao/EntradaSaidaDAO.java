package parksys.dao;
import java.sql.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import parksys.modelo.Configuracao;
import parksys.modelo.EntradaSaida;

public class EntradaSaidaDAO {
	private ParkSysDataSource dataSource = new ParkSysDataSource();
	
	public void registrarEntrada(EntradaSaida entradaSaida) throws DataAccessException {
		try (Connection conn = dataSource.getConnection()) {
			conn.setAutoCommit(false);
			String insert = "INSERT INTO entrada_saida (placa, descricao, horario_entrada, duracao_bloco) "
					+ "VALUES (?, ?, ?, ?);";
			try (PreparedStatement pstmt = conn.prepareStatement(insert)) {		
				Configuracao config = new ConfiguracaoDAO().getConfiguracaoSistema();
						
				pstmt.setString(1, entradaSaida.getPlaca()); 
				pstmt.setString(2,entradaSaida.getDescricao());
				
				long entrada_num = entradaSaida.getHora_entrada().getTime();
				Timestamp entrada = new Timestamp(entrada_num);
				pstmt.setTimestamp(3, entrada);
				
				pstmt.setDouble(4, config.getDuracao_bloco());
				
				pstmt.execute();
				
				conn.commit();
				
			} catch(SQLException e) {
				conn.rollback();
				throw e;
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}
	
	public void registrarSaida(EntradaSaida entradaSaida) throws DataAccessException {
		try (Connection conn = dataSource.getConnection()) {
			conn.setAutoCommit(false);
			String update = "UPDATE entrada_saida "
						  + "SET horario_saida = ?"
						  + "WHERE placa = ?"
						  + "AND horario_saida is null;";
			
			try (PreparedStatement pstmt = conn.prepareStatement(update)) {		
				Configuracao config = new ConfiguracaoDAO().getConfiguracaoSistema();
						
				pstmt.setTimestamp(1, new Timestamp(entradaSaida.getHora_saida().getTime())); 
				pstmt.setString(2,entradaSaida.getPlaca());
				
				pstmt.execute();
				
				conn.commit();
				
			} catch(SQLException e) {
				conn.rollback();
				throw e;
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
		
		
	}
	
	public EntradaSaida getEntradaSaida(String placa) throws DataAccessException {
		EntradaSaida es = null;
		
		try (Connection conn = dataSource.getConnection()) {
			try (Statement stmt = conn.createStatement()) {		

				String select = String.format("select * from entrada_saida where placa like '%s' and horario_saida is null;", placa);
				try(ResultSet rs = stmt.executeQuery(select)){
					if (rs.next()) {
						es = new EntradaSaida(
								rs.getString("placa"),
								rs.getString("descricao"),
								rs.getTimestamp("horario_entrada"),
								rs.getTimestamp("horario_saida"),
								rs.getDouble("duracao_bloco"));
					}
				}
			}
		} catch(SQLException e) {
			throw new DataAccessException(e);
		}
		
		return es;
	}
	
	public boolean procurarEntrada(String placa) throws DataAccessException{
		EntradaSaida es = getEntradaSaida(placa);
		if( es != null)
			return es.getPlaca() != null;
		else
			return false;
	}
	
	public List<Double> getFaturamento(List<java.util.Date> datas) throws DataAccessException, ParseException {
		List<Double> faturamento = new ArrayList<>();
		Configuracao config = new ConfiguracaoDAO().getConfiguracaoSistema();
		try (Connection conn = dataSource.getConnection()) {
			try (Statement stmt = conn.createStatement()) {		
				datas.forEach((data) -> {
					String select = "select SUM(duracao_bloco * ?) as faturamento from entrada_saida WHERE DATE_FORMAT(horario_entrada, '%Y-%m-%d') = ? group by DATE_FORMAT(horario_entrada, '%Y-%m-%d');";
					try(PreparedStatement pstmt = conn.prepareStatement(select)){
						pstmt.setString(2, new SimpleDateFormat("yyyy-MM-dd").format(data));
						pstmt.setString(1, String.valueOf(config.getTarifa()).replace(',', '.')); 
						ResultSet rs = pstmt.executeQuery();
						if (rs.next()) {
							faturamento.add(rs.getDouble("faturamento"));
						} else {
							faturamento.add(0.0);
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			}
			
			return faturamento;
		} catch(SQLException e) {
			throw new DataAccessException(e);
		}

	}
	
	public List<Double> getMediaPermanencia(List<java.util.Date> datas) throws DataAccessException {
		List<Double> permanencia = new ArrayList<>();
		Configuracao config = new ConfiguracaoDAO().getConfiguracaoSistema();
		try (Connection conn = dataSource.getConnection()) {
			try (Statement stmt = conn.createStatement()) {	
				datas.forEach((data) -> {
					Timestamp horario_entrada;
					Timestamp horario_saida;
					long diff;
					double horas;
					int contador_while;
					String select = "select horario_entrada, horario_saida from entrada_saida WHERE DATE_FORMAT(horario_entrada, '%Y-%m-%d') = ?";
					try(PreparedStatement pstmt = conn.prepareStatement(select)){
						pstmt.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(data));
						ResultSet rs = pstmt.executeQuery();
						horas = 0;
						contador_while = 0;
				        while(rs.next()) {
							horario_entrada = rs.getTimestamp("horario_entrada");
							horario_saida = rs.getTimestamp("horario_saida");
							if(horario_saida != null) {
								if(horario_entrada.before(horario_saida)) {
									diff = horario_entrada.getTime() - horario_saida.getTime();
							        TimeUnit time = TimeUnit.MINUTES; 
							        long difference = time.convert(diff, TimeUnit.MILLISECONDS);
							        horas =  horas + Math.abs(difference);
								}
							}  else{
								String data_saida = new SimpleDateFormat("yyyy-MM-dd").format(horario_entrada);
								Calendar cal = Calendar.getInstance(); 
								cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(data_saida)); 
								cal.add(Calendar.DATE, 1);
								java.util.Date data_saida_formatada = (java.util.Date) cal.getTime();
								diff = horario_entrada.getTime() - data_saida_formatada.getTime();
						        TimeUnit time = TimeUnit.MINUTES; 
						        long difference = time.convert(diff, TimeUnit.MILLISECONDS);
						        horas =  horas + Math.abs(difference);
							}
							contador_while++;
				        }

				        
				        if(horas == 0) {
				        	permanencia.add(0.0);
				        } else {
					        permanencia.add((horas/contador_while) / 60);
				        }
				        
					} catch (SQLException | ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			}
			
			return permanencia;
		} catch(SQLException e) {
			throw new DataAccessException(e);
		}
	}
	
}
