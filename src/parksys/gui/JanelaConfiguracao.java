package parksys.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import parksys.dao.ConfiguracaoDAO;
import parksys.dao.DataAccessException;
import parksys.modelo.Configuracao;
import parksys.modelo.GerenciadorVagas;
import parksys.modelo.Pagamento;
import parksys.utilidades.Funcoes;
import parksys.utilidades.JMoneyField;

public class JanelaConfiguracao extends JFrame {
	
	private JPanel mainPanel, panel1, radioPanel, btnPanel;
	
	private JLabel lblDuracaoBloco, lblTarifa, lblDesconto, lblQtdMinHoras, lblVagasMax;
	
	private JFormattedTextField txtTarifa, txtDesconto;
	private JTextField txtQtdMinHoras, txtVagasMax;
	
	private JRadioButton radioMeiaHora, radioUmaHora;
	private ButtonGroup group;
	
	private JButton btnAplicar, btnCancelar;
	
	private Configuracao config;
	private ConfiguracaoDAO dao;
	private GerenciadorVagas gerenciador;
	
	public JanelaConfiguracao(GerenciadorVagas gerenciador) {
		
		this.gerenciador = gerenciador;
		dao = new ConfiguracaoDAO();
		try {
			config = dao.getConfiguracaoSistema();
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
		criarComponentes();
		preencherCampos();
		ajustarJanela();
	}
	
	private void confirmExit() {
		int answer = JOptionPane.showConfirmDialog(this,
				"Se você sair agora, nenhuma alteração será feita. Deseja continuar?",
				"Cancelar",
				JOptionPane.YES_NO_OPTION);

		if (answer == JOptionPane.YES_OPTION) {
			this.dispose();
		}
	}
	
	private void preencherCampos() {
		if (config ==  null) return;
		
		if(config.getDuracao_bloco() == 1)
			radioUmaHora.setSelected(true);
		else
			radioMeiaHora.setSelected(true);
		
		txtTarifa.setText(String.format("R$ %.2f", config.getTarifa()));
		txtDesconto.setText(String.format("%d %%", (int)(config.getDesconto()*100)));
		txtQtdMinHoras.setText(String.format("%d", config.getHoras_minimas()));
		txtVagasMax.setText(String.format("%d", config.getVagas_max()));		
	}
	
	private void aplicar() {
		
		int answer = JOptionPane.showConfirmDialog(this,
				"Deseja aplicar as alterações?",
				"Cancelar",
				JOptionPane.YES_NO_OPTION);

		if (answer == JOptionPane.NO_OPTION) {
			return;
		}
		
		double duracao_bloco = radioUmaHora.isSelected() ? 1 : 0.5;
		double tarifa = Funcoes.stringToDecimal(txtTarifa.getText());;
		double desconto = Funcoes.StringPctToDouble(txtDesconto.getText())/100;
		int horas_minimas = Integer.parseInt(txtQtdMinHoras.getText());
		int vagas_max = Integer.parseInt(txtVagasMax.getText());
		
		SwingWorker<Void, Void> worker = new SwingWorker<>() {
			@Override
			protected Void doInBackground() throws DataAccessException {
				Configuracao newConfig = new Configuracao(duracao_bloco, tarifa, desconto, horas_minimas, vagas_max);
				dao.setConfiguracaoSistema(newConfig);
				return null;
			}
			
			protected void done() {
				try {
					get();
					btnAplicar.setEnabled(true);
					gerenciador.updateDados();
					gerenciador.update();
					JOptionPane.showMessageDialog(mainPanel, "Configuração do sistema atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
				} catch (InterruptedException | ExecutionException | DataAccessException e) {
					JOptionPane.showMessageDialog(mainPanel, e, "Erro ao aplicar as alterações!\nTente novamente mais tarde.", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		
		btnAplicar.setEnabled(false);
		worker.execute();
	}
	
	public void criarComponentes() {
		
		mainPanel = new JPanel();		
		radioPanel = new JPanel();
		btnPanel = new JPanel();
		panel1 = new JPanel();		
		
		lblDuracaoBloco = new JLabel("Duração do Bloco:");
		lblTarifa = new JLabel("Valor da Tarifa:");
		lblDesconto = new JLabel("Desconto Mensalistas:");
		lblQtdMinHoras = new JLabel("Qtd. Mínima de Horas:");
		lblVagasMax = new JLabel("Qtd. de Vagas Máxima:");
		
		txtVagasMax = new JTextField(5);
		txtQtdMinHoras = new JTextField(5);
		try {
			txtTarifa = new JMoneyField();
			
			txtDesconto = new JFormattedTextField(new DefaultFormatterFactory(
                    new MaskFormatter("***%")));
			txtDesconto.setColumns(4);
		} catch (ParseException e) {
			System.out.println("Erro na máscara.");
		}
		
		radioMeiaHora = new JRadioButton("30 min");
		radioUmaHora = new JRadioButton("1 hora");
		
		group = new ButtonGroup();
		group.add(radioMeiaHora);
		group.add(radioUmaHora);
		
		btnAplicar = new JButton("Aplicar");
		btnAplicar.addActionListener(e -> aplicar());
		
		btnCancelar = new JButton("Sair");
		btnCancelar.addActionListener(e -> confirmExit());
		
		ajustarComponentes();
	}
	
	public void ajustarComponentes() {
		
		radioMeiaHora.setSelected(true);
		
		btnAplicar.setIcon(new ImageIcon(getClass().getResource("/imagens/config_mini.png")));
		btnCancelar.setIcon(new ImageIcon(getClass().getResource("/imagens/cancelar.png")));
		
		panel1.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
		btnPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		
		panel1.setLayout(new GridLayout(5,2,5,10));
		mainPanel.setLayout(new BorderLayout());
		
		mainPanel.setBackground(new Color(50, 50, 50));
		
		adicionarComponentes();
	}
	
	public void adicionarComponentes() {
		
		radioPanel.add(radioMeiaHora);
		radioPanel.add(Box.createRigidArea(new Dimension(50, 0)));
		radioPanel.add(radioUmaHora);
		
		panel1.add(lblDuracaoBloco);
		panel1.add(radioPanel);
		
		panel1.add(lblTarifa);
		panel1.add(txtTarifa);
		
		panel1.add(lblDesconto);
		panel1.add(txtDesconto);
		
		panel1.add(lblQtdMinHoras);
		panel1.add(txtQtdMinHoras);
		
		panel1.add(lblVagasMax);
		panel1.add(txtVagasMax);
		
		btnPanel.add(btnAplicar);
		btnPanel.add(Box.createRigidArea(new Dimension(30, 0)));
		btnPanel.add(btnCancelar);
		
		mainPanel.add(panel1, BorderLayout.NORTH);
		mainPanel.add(btnPanel, BorderLayout.SOUTH);
		
		this.add(mainPanel);
	}
	
	private void ajustarJanela() {    
        this.setIconImage(new ImageIcon(getClass().getResource("/imagens/config_mini.png")).getImage());
		this.setVisible(true);
		this.setTitle("Configuração do Sistema");
		this.pack();
		this.setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				confirmExit();
			}			
		});
    }
}
