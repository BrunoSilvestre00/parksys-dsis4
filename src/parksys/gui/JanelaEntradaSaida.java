package parksys.gui;

import java.lang.Math;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import parksys.dao.ConfiguracaoDAO;
import parksys.dao.DataAccessException;
import parksys.dao.EntradaSaidaDAO;
import parksys.dao.MensalistaDAO;
import parksys.dao.VeiculoDAO;
import parksys.modelo.Configuracao;
import parksys.modelo.EntradaSaida;
import parksys.modelo.GerenciadorVagas;
import parksys.modelo.Mensalista;
import parksys.modelo.Veiculo;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;

public class JanelaEntradaSaida extends JFrame{
	
	private static final int NONE = 0; 	  // Placa não informada ou inválida
	private static final int ENTRADA = 1; // Placa válida sem registro de entrada
	private static final int SAIDA = 2;	  // Placa válida com registro de entrada
	
	private JPanel mainPanel, panelPlacaCarro, panelDados, btnPanel, panelTotal;
	
	private JLabel lblPlaca, lblCarro, lblBlocos1, lblBlocos2, lblPreco1, lblPreco2;
	private JLabel lblEntrada, lblSaida, lblPermanencia, lblCliente, lblQtdBlocos, lblTotal;
	
	private JLabel txtEntrada, txtSaida, txtPermanencia, txtCliente, txtQtdBlocos, txtTotal, txtDesc;
	
	private JFormattedTextField txtPlaca, txtDescricao;
	
	private JButton btnPesquisar, btnRegistrar, btnCancelar;
	
	EntradaSaidaDAO dao;
	ConfiguracaoDAO daoConfig;
	Configuracao config;
	
	private int estado; //Variável que vai determinar em qual situação a tela estará
	private GerenciadorVagas gerenciadorVagas;
	private Date agora;
	private double valor;
	private Mensalista mens;
	
	public JanelaEntradaSaida (GerenciadorVagas gerenciadorVagas) {
		try {
			dao =  new EntradaSaidaDAO();
			daoConfig = new ConfiguracaoDAO();
			config = daoConfig.getConfiguracaoSistema();
		} catch(DataAccessException e) {
			JOptionPane.showMessageDialog(null, e, "Erro!", JOptionPane.ERROR_MESSAGE);
		}
		
		estado = NONE;
		
		this.gerenciadorVagas = gerenciadorVagas;
		
		criarComponentes();
		telaNone();
		ajustarJanela();
	}
	
	private void confirmExit() {
		int answer = JOptionPane.showConfirmDialog(this,
				"Se você continuar, o registro atual será cancelado. Deseja continuar?",
				"Cancelar",
				JOptionPane.YES_NO_OPTION);

		if (answer == JOptionPane.YES_OPTION) {
			this.dispose();
		}
	}
	
	public void apagarCampos() {
		txtPlaca.setText("");
		txtDescricao.setText("");
	}
	
	private int calcularBlocos(long minutes) {
		int nblocos = (int) Math.ceil(minutes/(config.getDuracao_bloco()*60));
		return nblocos;
	}
	
	private int identificarEstado(String placa) {
		if (placa.equals("-"))
			return NONE;
		
		try {
			if(dao.procurarEntrada(placa)) {
				return SAIDA;
			} else {
				return ENTRADA;
			}
		} catch (DataAccessException e) {
			JOptionPane.showMessageDialog(null, e, "Erro!", JOptionPane.ERROR_MESSAGE);
		}
		
		return NONE;
	}
	
	private void telaNone() {
		apagarCampos();
		txtDescricao.setEditable(false);
		
		btnRegistrar.setIcon(new ImageIcon(getClass().getResource("/imagens/entrada_saida_mini.png")));
		btnRegistrar.setEnabled(false);
		
		txtEntrada.setText("---:---:---");
		txtSaida.setText("---:---:---");
		txtPermanencia.setText("00 min");
		txtQtdBlocos.setText("0 Blocos");
		txtCliente.setText("Comum");
		txtTotal.setText(String.format("%.2f", 0d));
		txtDesc.setText("");
		
		TitledBorder borda = (TitledBorder) panelDados.getBorder();
		borda.setTitle("Dados de Entrada e Saída");
		borda.setTitleColor(new Color (200,200,200));
		
		Font bold = lblTotal.getFont();
		txtTotal.setFont(bold);
		
		this.repaint();
	}
	
	private void telaEntrada(String placa) {
		txtDescricao.setText("");
		txtDescricao.setEditable(true);
		
		btnRegistrar.setText("Registrar Entrada");
		btnRegistrar.setEnabled(true);
		btnRegistrar.setIcon(new ImageIcon(getClass().getResource("/imagens/entrada.png")));
		
		Veiculo veic = null;
		mens = null;
		
		try {
			veic = new VeiculoDAO().getVeiculo(placa);
			if (veic != null)
				mens = new MensalistaDAO().localizarID(veic.getMensalistaID());
		} catch (DataAccessException e) {
			JOptionPane.showMessageDialog(null, e, "Erro!", JOptionPane.ERROR_MESSAGE);
		}
		
		
		txtEntrada.setText(new SimpleDateFormat("HH:mm:ss").format(agora));
		txtSaida.setText("---:---:---");
		txtPermanencia.setText("00 min");
		txtTotal.setText(String.format("%.2f", 0d));
		txtDesc.setText("");
		
		TitledBorder borda = (TitledBorder) panelDados.getBorder();
		borda.setTitle("Dados de Entrada");
		borda.setTitleColor(new Color (0,200,0));
		
		if(mens != null) {
			txtDescricao.setEditable(false);
			txtCliente.setText("Mensalista");
			txtDescricao.setText(veic.getDescricao());
		}
		
		this.repaint();
	}
	
	private void telaSaida(String placa) {
		txtDescricao.setEditable(false);
		
		btnRegistrar.setText("Registrar Saída");
		btnRegistrar.setEnabled(true);
		btnRegistrar.setIcon(new ImageIcon(getClass().getResource("/imagens/saida.png")));
		
		EntradaSaida ent_sai = null;
		Veiculo veic = null;
		mens = null;
		
		try {
			ent_sai = dao.getEntradaSaida(placa);
			veic = new VeiculoDAO().getVeiculo(placa);
			if (veic != null)
				mens = new MensalistaDAO().localizarID(veic.getMensalistaID());
		} catch (DataAccessException e) {
			JOptionPane.showMessageDialog(null, e, "Erro!", JOptionPane.ERROR_MESSAGE);
		}
		
		Date entrada = ent_sai.getHora_entrada();
		long minutes = TimeUnit.MILLISECONDS.toMinutes(agora.getTime() - entrada.getTime());
		
		txtDescricao.setText(ent_sai.getDescricao());
		txtEntrada.setText(new SimpleDateFormat("HH:mm:ss").format(entrada));
		txtSaida.setText(new SimpleDateFormat("HH:mm:ss").format(agora));
		txtPermanencia.setText(String.format("%d min", minutes));
		
		int qtdBlocos = calcularBlocos(minutes);
		double total = qtdBlocos*config.getTarifa();
		
		txtQtdBlocos.setText(Integer.toString(qtdBlocos));
		txtTotal.setText(String.format("%.2f", total));
		
		
		TitledBorder borda = (TitledBorder) panelDados.getBorder();
		borda.setTitle("Dados de Saída");
		borda.setTitleColor(new Color (250,0,0));
		
		if(mens != null) {
			txtCliente.setText("Mensalista");
			txtDescricao.setText(veic.getDescricao());
			
			
			Font font = lblEntrada.getFont();
			
			Hashtable<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
			map.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		    Font stk = font.deriveFont(map);
		    
		    txtTotal.setFont(stk);
		    valor = total*(1-config.getDesconto());
			txtDesc.setText(String.format(" %.2f", valor));
		}else {
			valor = total;
		}
		
		this.repaint();
	}
	
	private void adaptarTela() {
		
		String placa = txtPlaca.getText().toUpperCase();
		txtPlaca.setText(placa);
		
		estado = identificarEstado(placa);
		if(estado == NONE) {
			telaNone();
			return;
		}
		
		agora = new Date();
		
		if (estado == ENTRADA) {
			telaEntrada(placa);
		} else if (estado == SAIDA) {
			telaSaida(placa);
		}
	}
	
	private void registrar() {
		
		String placa = txtPlaca.getText().toUpperCase().trim();
		String descricao = txtDescricao.getText().trim();
		
		if(estado == ENTRADA) {
			
			if (gerenciadorVagas.cheio()) {
				JOptionPane.showMessageDialog(mainPanel, "O estacionamento já atingiu o\nlimite máximo de veículos!", "LOTADO!", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			if(descricao.isEmpty()) {
				descricao = "-";
			}
			else if(descricao.length() > 100) {
				descricao = descricao.substring(0,99);
			}
			
			EntradaSaida ent_sai = new EntradaSaida(placa, descricao, agora);
			SwingWorker<Void, Void> worker = new SwingWorker<>() {
				
				@Override
				protected Void doInBackground() throws DataAccessException {
					dao.registrarEntrada(ent_sai);
					return null;
				}
				
				protected void done() {
					try {
						get();
						JOptionPane.showMessageDialog(mainPanel, "Entrada registrada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
						estado = NONE;
						gerenciadorVagas.addOcupadas();
						telaNone();
					} catch (InterruptedException | ExecutionException e) {
						JOptionPane.showMessageDialog(mainPanel, e, "Erro ao realizar o registro!", JOptionPane.ERROR_MESSAGE);
					}
				}
			};
			
			btnRegistrar.setEnabled(false);
			worker.execute();
			
		}else if (estado == SAIDA) {
			
			try {
				EntradaSaida ent_sai = dao.getEntradaSaida(placa);
				ent_sai.setHora_saida(agora);
				Veiculo veic = new VeiculoDAO().getVeiculo(placa);
				if (veic != null)
					mens = new MensalistaDAO().localizarID(veic.getMensalistaID());

				Date entrada = ent_sai.getHora_entrada();
				long minutes = TimeUnit.MILLISECONDS.toMinutes(agora.getTime() - entrada.getTime());

				int qtdBlocos = calcularBlocos(minutes);
				SwingWorker<Void, Void> worker = new SwingWorker<>() {
					
					@Override
					protected Void doInBackground() throws DataAccessException {
						
						if(mens != null && valor > mens.getSaldo()) {
							new JanelaPagamentoMensal();
							JOptionPane.showMessageDialog(null, "Saldo insuficiente! Saldo atual de blocos: " + (mens.getSaldo()));
						}else {
							dao.registrarSaida(ent_sai);
						}
						return null;
					}
					
					protected void done() {
						try {
							if(mens != null && valor > mens.getSaldo()) {
								//JOptionPane.showMessageDialog(mainPanel, "Saldo do mensalista insuficiente!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
							} else {
								if(mens != null) {
									new MensalistaDAO().subtrairSaldo(mens, valor);
								}
								get();
								JOptionPane.showMessageDialog(mainPanel, "Saida registrada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
								estado = NONE;
								gerenciadorVagas.subOcupadas();
								telaNone();
							}
						} catch (InterruptedException | ExecutionException | DataAccessException e) {
							JOptionPane.showMessageDialog(mainPanel, e, "Erro ao realizar o registro!", JOptionPane.ERROR_MESSAGE);
						}
					}
				};
				
				btnRegistrar.setEnabled(false);
				worker.execute();
				
			} catch (DataAccessException e) {
				JOptionPane.showMessageDialog(null, e, "Erro!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void criarComponentes() {
		
		mainPanel = new JPanel();
		panelPlacaCarro = new JPanel();
		panelDados = new JPanel();
		btnPanel = new JPanel();
		panelTotal = new JPanel();

		lblPlaca = new JLabel("Placa: ");
		lblCarro = new JLabel("Carro: ");
		
		lblEntrada = new JLabel("Entrada: ");	
		lblSaida =  new JLabel("Saída: ");		
		lblPermanencia = new JLabel("Permanência: ");
		lblQtdBlocos = new JLabel("Qtd. Blocos: ");
		
		lblCliente = new JLabel("Cliente: ");
		
		lblBlocos1 = new JLabel("Blocos: ");
		lblBlocos2 = new JLabel(String.format("%.1f Hora", config.getDuracao_bloco()));

		lblPreco1 = new JLabel("Preço: ");
		lblPreco2 = new JLabel(String.format("R$ %.2f", config.getTarifa()));
		
		lblTotal = new JLabel("Total: ");
		
		try {
			txtPlaca = new JFormattedTextField(new DefaultFormatterFactory(
                    new MaskFormatter("***-####")));
			txtPlaca.setColumns(5);
			
			txtDescricao = new JFormattedTextField(new DefaultFormatterFactory(
                    new MaskFormatter("**************************************************")));
			txtDescricao.setColumns(10);
		} catch (ParseException e) {
			System.out.println("Erro na máscara.");
		}
		
		txtEntrada = new JLabel();
		txtSaida = new JLabel();
		txtPermanencia = new JLabel();
		txtQtdBlocos = new JLabel();
		txtCliente = new JLabel();
		txtTotal = new JLabel();
		txtDesc = new JLabel();
		
		btnPesquisar = new JButton();
		btnPesquisar.addActionListener(e -> adaptarTela());
		
		btnRegistrar = new JButton("Registrar");
		btnRegistrar.addActionListener(e -> registrar());
		
		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(e -> confirmExit());
		
		ajustarComponentes();
	}
	
	private void ajustarComponentes() {
			
		Font font = lblTotal.getFont();
		Font bold = font.deriveFont(font.getStyle() | Font.BOLD);
		lblTotal.setFont(bold);
		txtTotal.setFont(bold);
		txtDesc.setFont(bold);
		
		btnPesquisar.setIcon(new ImageIcon(getClass().getResource("/imagens/pesquisar.png")));
		btnCancelar.setIcon(new ImageIcon(getClass().getResource("/imagens/cancelar.png")));
		
		panelPlacaCarro.setLayout(new BoxLayout(panelPlacaCarro, BoxLayout.LINE_AXIS));
		panelPlacaCarro.setBorder(BorderFactory.createEmptyBorder(20,15,20,15));
		
		panelDados.setLayout(new GridLayout(4, 4));
		panelDados.setBorder(new TitledBorder("Dados de Entrada e Saída"));
		
		Font fonte = new Font("Tahoma", Font.PLAIN, 20);
		TitledBorder borda = (TitledBorder) panelDados.getBorder();
		borda.setTitleFont(fonte);
		borda.setTitleColor(new Color (200,200,200));
		
		btnPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		panelTotal.setLayout(new BorderLayout());
		
		mainPanel.setBackground(new Color(50, 50, 50));
		mainPanel.setLayout(new BorderLayout());
		
		adicionarComponentes();
	}
	
	private void adicionarComponentes() {
		
		panelTotal.add(new JLabel("R$ "), BorderLayout.WEST);
		panelTotal.add(txtTotal, BorderLayout.CENTER);
		panelTotal.add(txtDesc, BorderLayout.EAST);
		
		panelPlacaCarro.add(lblPlaca);
		panelPlacaCarro.add(txtPlaca);
		panelPlacaCarro.add(Box.createRigidArea(new Dimension(10,0)));
		panelPlacaCarro.add(btnPesquisar);
		panelPlacaCarro.add(Box.createRigidArea(new Dimension(30,0)));
		panelPlacaCarro.add(lblCarro);
		panelPlacaCarro.add(txtDescricao);
		
		panelDados.add(lblEntrada);
		panelDados.add(txtEntrada);
		
		panelDados.add(lblCliente);
		panelDados.add(txtCliente);
		
		panelDados.add(lblSaida);
		panelDados.add(txtSaida);
		
		panelDados.add(lblBlocos1);
		panelDados.add(lblBlocos2);
		
		panelDados.add(lblPermanencia);
		panelDados.add(txtPermanencia);
		
		panelDados.add(lblPreco1);
		panelDados.add(lblPreco2);
		
		panelDados.add(lblQtdBlocos);
		panelDados.add(txtQtdBlocos);
		
		panelDados.add(lblTotal);
		panelDados.add(panelTotal);
		
		btnPanel.add(btnRegistrar);
		btnPanel.add(Box.createRigidArea(new Dimension(30,0)));
		btnPanel.add(btnCancelar);	
		
		mainPanel.add(panelPlacaCarro, BorderLayout.NORTH);
		mainPanel.add(Box.createRigidArea(new Dimension(20,0)), BorderLayout.WEST);
		mainPanel.add(panelDados, BorderLayout.CENTER);
		mainPanel.add(Box.createRigidArea(new Dimension(20,0)), BorderLayout.EAST);
		mainPanel.add(btnPanel, BorderLayout.SOUTH);
		
		this.add(mainPanel);
	}
	
	private void ajustarJanela() {    
        this.setIconImage(new ImageIcon(getClass().getResource("/imagens/entrada_saida.png")).getImage());
		this.setVisible(true);
		this.setTitle("Entrada/Saída");
		this.pack();
		//this.setSize(1040,550);
		this.setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				confirmExit();
			}			
		});
    }
	
	
	
	
}
