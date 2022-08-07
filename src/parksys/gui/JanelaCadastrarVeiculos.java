package parksys.gui;

import java.awt.*;
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
import parksys.dao.VeiculoDAO;
import parksys.modelo.Configuracao;
import parksys.modelo.Mensalista;
import parksys.modelo.Veiculo;
import parksys.utilidades.Funcoes;

public class JanelaCadastrarVeiculos extends JFrame {
	
	private JPanel mainPanel, panelMensalista, panelDados, btnPanel;
	private JPanel panelCPF, panelTelefone, panelInfo1, panelInfo2;
	private JPanel panel1;
	
	private JLabel lblDadoCPF, lblDadoTelefone; 
	private JLabel lblNome, txtNome, lblTelefone, txtTelefone;
	private JLabel lblSaldo, txtSaldo, lblSaldoBloco, txtSaldoBloco;
	
	private JFormattedTextField txtDadoCPF, txtDadoTelefone;
	
	private JLabel lblPlaca, lblDescricao;	
	private JFormattedTextField txtPlaca, txtDescricao;
	
	private JButton btnPesquisar, btnRegistrar, btnCancelar;
	
	private Mensalista mensalista;
	private Configuracao config;
	private MensalistaDAO daoM;
	private VeiculoDAO daoV;
	
	public JanelaCadastrarVeiculos(String cpf) {
		daoM = new MensalistaDAO();
		daoV = new VeiculoDAO();
		
		try {
			config = new ConfiguracaoDAO().getConfiguracaoSistema();
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
		criarComponentes();
		telaNone();
		
		txtDadoCPF.setText(cpf);
		
		ajustarJanela();
	}
	
	private void confirmExit() {
		int answer = JOptionPane.showConfirmDialog(this,
				"Se você continuar, o cadastro atual será cancelado. Deseja continuar?",
				"Cancelar",
				JOptionPane.YES_NO_OPTION);

		if (answer == JOptionPane.YES_OPTION) {
			this.dispose();
		}
	}

	private void setEnabledAll(boolean bool) {
		Component[] componentes = panelDados.getComponents();
		for(int i = 0; i < componentes.length; i++) {
			componentes[i].setEnabled(bool);
		}
		btnRegistrar.setEnabled(bool);
	}
	
	private void telaNone() {
		txtDadoCPF.setText("");
		txtDadoTelefone.setText("");
		
		txtNome.setText("---");
		txtTelefone.setText("(--) xxxxx-xxxx");
		txtSaldoBloco.setText("-- Blocos");
		txtSaldo.setText("R$ 0,00");
		setEnabledAll(false);

		txtPlaca.setText("");
		txtDescricao.setText("");
		
		this.repaint();
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
	
	private void registrar() {
		String placa, descricao;
		placa = txtPlaca.getText().toUpperCase();
		descricao = txtDescricao.getText().trim();
		
		if (Funcoes.charCount(placa,' ') != 0) {
			JOptionPane.showMessageDialog(null, "Uma placa válida é obrigatória!");
            return;
		}
		
		Veiculo veiculo;
		try {
			veiculo = new Veiculo(placa, descricao, daoM.getID(mensalista.getCPF()));
		} catch (DataAccessException e) {
			e.printStackTrace();
			return;
		}
		
		SwingWorker<Void, Void> worker = new SwingWorker<>() {
			
			@Override
			protected Void doInBackground() throws DataAccessException {
				daoV.cadastrar(veiculo);
				return null;
			}
			
			protected void done() {
				try {
					get();
					btnRegistrar.setEnabled(true);
					telaNone();
					JOptionPane.showMessageDialog(mainPanel, "Veiculo cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
				} catch (InterruptedException | ExecutionException e) {
					JOptionPane.showMessageDialog(mainPanel, e, "Erro ao realizar o cadastro!", JOptionPane.ERROR_MESSAGE);
					btnRegistrar.setEnabled(true);
				}
			}
		};
		
		btnRegistrar.setEnabled(false);
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
		
		txtNome = new JLabel();
		txtTelefone = new JLabel();		
		txtSaldoBloco = new JLabel();
		txtSaldo = new JLabel();
		
		lblPlaca = new JLabel("Placa:");
		lblDescricao = new JLabel("Descrição:");
		
		try {
			txtDadoCPF = new JFormattedTextField(new DefaultFormatterFactory(
                    new MaskFormatter("###.###.###-##")));
            txtDadoCPF.setColumns(10);
            
            txtDadoTelefone = new JFormattedTextField(new DefaultFormatterFactory(
                    new MaskFormatter("(##) #####-####")));
            txtDadoTelefone.setColumns(10);
            
			txtPlaca = new JFormattedTextField(new DefaultFormatterFactory(
                    new MaskFormatter("***-####")));
			txtPlaca.setColumns(6);
			
			txtDescricao = new JFormattedTextField(new DefaultFormatterFactory(
                    new MaskFormatter("**************************************************")));
			txtDescricao.setColumns(10);
			
		} catch (ParseException e) {
			System.out.println("Erro na máscara.");
		}
		
		btnPesquisar = new JButton();
		btnPesquisar.addActionListener(e -> adaptarTela());
		
		btnRegistrar = new JButton("Registrar");
		btnRegistrar.addActionListener(e -> registrar());
		
		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(e -> confirmExit());
		
		ajustarComponentes();
	}
	
	private void ajustarComponentes() {
		
		btnPesquisar.setIcon(new ImageIcon(getClass().getResource("/imagens/pesquisar.png")));
		btnRegistrar.setIcon(new ImageIcon(getClass().getResource("/imagens/carro.png")));
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
		
		panelDados.setBorder(new TitledBorder("  Dados do Veículo  "));
		borda = (TitledBorder) panelDados.getBorder();
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
		
		panelDados.add(lblPlaca);
		panelDados.add(txtPlaca);
		panelDados.add(Box.createRigidArea(new Dimension(50, 0)));
		panelDados.add(lblDescricao);
		panelDados.add(txtDescricao);
		
		btnPanel.add(btnRegistrar);
		btnPanel.add(Box.createRigidArea(new Dimension(30, 0)));
		btnPanel.add(btnCancelar);
		
		mainPanel.add(panelMensalista, BorderLayout.NORTH);
		mainPanel.add(panelDados, BorderLayout.CENTER);
		mainPanel.add(btnPanel, BorderLayout.SOUTH);
		
		this.add(mainPanel);
	}
	
	private void ajustarJanela() {    
        this.setIconImage(new ImageIcon(getClass().getResource("/imagens/carro.png")).getImage());
		this.setVisible(true);
		this.setTitle("Cadastrar Veículos");
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
