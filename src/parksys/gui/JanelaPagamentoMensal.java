package parksys.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import parksys.dao.ConfiguracaoDAO;
import parksys.dao.DataAccessException;
import parksys.dao.MensalistaDAO;
import parksys.dao.PagamentoDAO;
import parksys.modelo.Configuracao;
import parksys.modelo.Mensalista;
import parksys.modelo.Pagamento;
import parksys.utilidades.Funcoes;
import parksys.utilidades.JMoneyField;

public class JanelaPagamentoMensal extends JFrame{
	
	private JPanel mainPanel, panelMensalista, panelDados, btnPanel;
	private JPanel panelCPF, panelTelefone, panelInfo1, panelInfo2;
	private JPanel panel1;
	
	private JLabel lblDadoCPF, lblDadoTelefone; 
	private JLabel lblNome, txtNome, lblTelefone, txtTelefone;
	private JLabel lblSaldo, txtSaldo, lblSaldoBloco, txtSaldoBloco;
	
	private JLabel lblTarifa, txtTarifa, lblValor, txtConversao;
	
	private JFormattedTextField txtDadoCPF, txtDadoTelefone, txtValor;
	
	private JButton btnPesquisar, btnPagar, btnCancelar;
	
	private Mensalista mensalista;
	private Configuracao config;
	private MensalistaDAO daoM;
	private PagamentoDAO daoP;
	
	double valor;
	int blocos;
	
	public JanelaPagamentoMensal () {
		daoM = new MensalistaDAO();
		daoP = new PagamentoDAO();
		
		try {
			config = new ConfiguracaoDAO().getConfiguracaoSistema();
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
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
	
	private void telaNone() {
		txtDadoCPF.setText("");
		txtDadoTelefone.setText("");
		
		txtNome.setText("---");
		txtTelefone.setText("(--) xxxxx-xxxx");
		txtSaldoBloco.setText("-- Blocos");
		txtSaldo.setText("R$ 0,00");
		setEnabledAll(false);
		
		txtValor.setText("");
		txtConversao.setText(" = ");
		
		this.repaint();
	}
	
	private void setEnabledAll(boolean bool) {
		Component[] componentes = panelDados.getComponents();
		for(int i = 0; i < componentes.length; i++) {
			componentes[i].setEnabled(bool);
		}
		btnPagar.setEnabled(bool);
	}
	
	private void adaptarTela() {
		mensalista = null;
		String dado = txtDadoCPF.getText();
		if(Funcoes.charCount(dado,' ') != 0) {
			dado = txtDadoTelefone.getText();
			if(Funcoes.charCount(dado,' ') != 1) {
				JOptionPane.showMessageDialog(null, "CPF ou Telefone deve ser informado.");
				return;
			}
		}
		try {
			mensalista = daoM.localizarMensalista(dado);
			if (mensalista == null) {
				JOptionPane.showMessageDialog(null, "Mensalista não encontrado.");
				return;
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
		txtNome.setText(mensalista.getNome());
		txtTelefone.setText(mensalista.getTelefone());
		txtSaldoBloco.setText(String.format("%d Blocos", (int) (mensalista.getSaldo()/config.getTarifa())));
		txtSaldo.setText(String.format("R$ %.2f", mensalista.getSaldo()));
		setEnabledAll(true);
		
		this.pack();
		this.repaint();
	}
	
	private void calcularBlocos(PropertyChangeEvent e) {
		valor = Funcoes.stringToDecimal(txtValor.getText());
		blocos = (int) (valor/config.getTarifa());
		txtConversao.setText(String.format(" = %d Blocos", blocos));
		this.repaint();
	}
	
	private void pagar() {
		int answer = JOptionPane.showConfirmDialog(this,
				String.format("Mensalista: %s\nBlocos: %d\nValor: R$ %.2f\nDeseja continuar com o pagamento?", mensalista.getNome(), blocos, valor),
				"Continuar",
				JOptionPane.YES_NO_OPTION);

		if (answer == JOptionPane.NO_OPTION) {
			return;
		}
		SwingWorker<Void, Void> worker = new SwingWorker<>() {
			
			@Override
			protected Void doInBackground() throws DataAccessException {
				Pagamento pagamento = new Pagamento(daoM.getID(mensalista.getCPF()), valor, blocos);
				daoP.registrar(pagamento);
				daoM.adicionarSaldo(mensalista, valor);
				return null;
			}
			
			protected void done() {
				try {
					get();
					btnPagar.setEnabled(true);
					JOptionPane.showMessageDialog(mainPanel, "Pagamento cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
					telaNone();
				} catch (InterruptedException | ExecutionException e) {
					JOptionPane.showMessageDialog(mainPanel, e, "Erro ao realizar o registro!", JOptionPane.ERROR_MESSAGE);
					telaNone();
				}
			}
		};
		
		btnPagar.setEnabled(false);
		worker.execute();
	}
	
	private void criarComponentes() {
		
		mainPanel = new JPanel();
		panelMensalista = new JPanel();
		panelDados = new JPanel();
		btnPanel = new JPanel();
		
		panelCPF = new JPanel();
		panelTelefone = new JPanel();
		panelInfo1 = new JPanel();
		panelInfo2 = new JPanel();
		
		panel1 = new JPanel();
		
		lblDadoCPF = new JLabel("CPF: ");
		lblDadoTelefone = new JLabel("Telefone: ");
		
		lblNome = new JLabel("Nome: ");
		lblTelefone = new JLabel("Telefone: ");
		lblSaldoBloco = new JLabel("Blocos: ");
		lblSaldo = new JLabel("Saldo: ");
		lblValor = new JLabel("Informe o valor:");
		
		lblTarifa = new JLabel("Tarifa:");
		txtTarifa = new JLabel(String.format("R$ %.2f", config.getTarifa()));
		
		txtNome = new JLabel();
		txtTelefone = new JLabel();		
		txtSaldoBloco = new JLabel();
		txtSaldo = new JLabel();
		txtConversao = new JLabel();
		
		try {
			txtDadoCPF = new JFormattedTextField(new DefaultFormatterFactory(
                    new MaskFormatter("###.###.###-##")));
            txtDadoCPF.setColumns(10);
            
            txtDadoTelefone = new JFormattedTextField(new DefaultFormatterFactory(
                    new MaskFormatter("(##) #####-####")));
            txtDadoTelefone.setColumns(10);
            
            txtValor = new JMoneyField();
            txtValor.addPropertyChangeListener("value", this::calcularBlocos);
		} catch (ParseException e) {
			System.out.println("Erro na máscara.");
		}
		
		btnPesquisar = new JButton();
		btnPesquisar.addActionListener(e -> adaptarTela());
		
		btnPagar = new JButton("Pagar");
		btnPagar.addActionListener(e -> pagar());
		
		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(e -> confirmExit());
		
		ajustarComponentes();
	}
	
	private void ajustarComponentes() {
		
		btnPesquisar.setIcon(new ImageIcon(getClass().getResource("/imagens/pesquisar.png")));
		btnPagar.setIcon(new ImageIcon(getClass().getResource("/imagens/pagar.png")));
		btnCancelar.setIcon(new ImageIcon(getClass().getResource("/imagens/cancelar.png")));
		
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
		panelInfo1.setLayout(new BoxLayout(panelInfo1, BoxLayout.Y_AXIS));
		panelInfo2.setLayout(new BoxLayout(panelInfo2, BoxLayout.Y_AXIS));
		
		Font fonte = new Font("Tahoma", Font.PLAIN, 20);
		TitledBorder borda;
		
		panelMensalista.setBorder(new TitledBorder("  Dados do Mensalista  "));
		borda = (TitledBorder) panelMensalista.getBorder();
		borda.setTitleFont(fonte);
		borda.setTitleColor(new Color (255,255,255));
		
		mainPanel.setBackground(new Color(50, 50, 50));
		mainPanel.setLayout(new BorderLayout());
		
		adicionarComponentes();
	}

	private void adicionarComponentes() {
		
		panelCPF.add(Box.createRigidArea(new Dimension(50,0)));
		panelCPF.add(lblDadoCPF);
		panelCPF.add(txtDadoCPF);
		panelTelefone.add(lblDadoTelefone);
		panelTelefone.add(txtDadoTelefone);
		
		panel1.add(panelCPF);
		panel1.add(panelTelefone);
		
		panelInfo1.add(lblNome);
		panelInfo1.add(lblTelefone);
		panelInfo1.add(lblSaldoBloco);
		panelInfo1.add(lblSaldo);
		
		panelInfo2.add(txtNome);
		panelInfo2.add(txtTelefone);
		panelInfo2.add(txtSaldoBloco);
		panelInfo2.add(txtSaldo);
		
		panelMensalista.add(panel1);
		panelMensalista.add(Box.createRigidArea(new Dimension(10, 0)));
		panelMensalista.add(btnPesquisar);
		panelMensalista.add(Box.createRigidArea(new Dimension(20, 0)));
		panelMensalista.add(panelInfo1);
		panelMensalista.add(panelInfo2);
		panelMensalista.add(Box.createRigidArea(new Dimension(10, 0)));
		
		panelDados.add(lblTarifa);
		panelDados.add(txtTarifa);
		panelDados.add(Box.createRigidArea(new Dimension(55, 0)));
		panelDados.add(lblValor);
		panelDados.add(txtValor);
		panelDados.add(txtConversao);
		
		btnPanel.add(btnPagar);
		btnPanel.add(Box.createRigidArea(new Dimension(30, 0)));
		btnPanel.add(btnCancelar);
		
		mainPanel.add(panelMensalista, BorderLayout.NORTH);
		mainPanel.add(panelDados, BorderLayout.WEST);
		mainPanel.add(btnPanel, BorderLayout.SOUTH);
		
		this.add(mainPanel);
	}
	
	private void ajustarJanela() {    
        this.setIconImage(new ImageIcon(getClass().getResource("/imagens/pagar.png")).getImage());
		this.setVisible(true);
		this.setTitle("Fazer Pagamento Mensal");
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
