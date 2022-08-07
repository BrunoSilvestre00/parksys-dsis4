package parksys.gui;

import javax.swing.*;
import javax.swing.text.*;

import parksys.modelo.Mensalista;
import parksys.utilidades.Funcoes;
import parksys.dao.DataAccessException;
import parksys.dao.MensalistaDAO;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

public class JanelaCadastrarMensalista extends JFrame {
	
	private JPanel mainPanel, panel1, panel2, btnPanel;
	private JPanel panelCPF, panelNome, panelTelefone, panelObservacoes;
	private JLabel lblCPF, lblNome, lblTelefone, lblObservacoes;
	private JFormattedTextField txtCPF, txtTelefone, txtNome;
	private JTextArea txtObservacoes;
	private JButton btnCadastrar, btnCancelar;
	
	private JanelaPrincipal mainJanela;
	
	public JanelaCadastrarMensalista(JanelaPrincipal janela) {
		this.mainJanela = janela;
		criarComponentes();
		ajustarJanela();
	}
	
	private void apagarCampos() {
		txtCPF.setText("");
		txtNome.setText("");
		txtTelefone.setText("");
		txtObservacoes.setText("");
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
	
	private void cadastrar() {
		MensalistaDAO dao = new MensalistaDAO();
		
		String CPF = txtCPF.getText();
		if (Funcoes.charCount(CPF,' ') != 0) {
			JOptionPane.showMessageDialog(null, "Um CPF válido é obrigatório!");
            return;
		}
		
		try {
			if (dao.existeMensalista(CPF)) {
				JOptionPane.showMessageDialog(null, "Este CPF já foi cadastrado!");
	            return;
			}
		} catch(DataAccessException e) {
			JOptionPane.showMessageDialog(null, e, "Erro!", JOptionPane.ERROR_MESSAGE);
		}
		
		
		String nome = txtNome.getText().toUpperCase().trim();
		if (nome.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Não é possível adicionar um Mensalista sem nome!");
            return;
		}
		
		String telefone = txtTelefone.getText();
		String observacoes = txtObservacoes.getText().trim();
		
		Mensalista mensalista = new Mensalista(CPF, nome, telefone);
		if (observacoes.isEmpty())
			mensalista.setObservacoes("-");
		else if (observacoes.length() > 100)
			mensalista.setObservacoes(observacoes.substring(0,99));
		else
			mensalista.setObservacoes(observacoes);
		
		SwingWorker<Void, Void> worker = new SwingWorker<>() {
			
			@Override
			protected Void doInBackground() throws DataAccessException {
				dao.cadastrar(mensalista);
				return null;
			}
			
			protected void done() {
				try {
					get();
					JOptionPane.showMessageDialog(mainPanel, "Mensalista cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
					mainJanela.cadastrarVeiculos(CPF);
				} catch (InterruptedException | ExecutionException e) {
					JOptionPane.showMessageDialog(mainPanel, e, "Erro ao realizar o cadastro!", JOptionPane.ERROR_MESSAGE);
					btnCadastrar.setEnabled(true);
				}
			}
		};
		
		btnCadastrar.setEnabled(false);
		worker.execute();
		this.dispose();
	}
	
	private void criarComponentes() {
		
		mainPanel = new JPanel();
		btnPanel = new JPanel();
		panel1 = new JPanel();
		panel2 = new JPanel();
		
		panelCPF = new JPanel();
		panelNome = new JPanel();
		panelTelefone = new JPanel();
		panelObservacoes = new JPanel();
		
		lblCPF = new JLabel("CPF: ");
		lblNome = new JLabel("Nome: ");
		lblTelefone = new JLabel("Telefone: ");
		lblObservacoes = new JLabel("Observações:");
		
		try {
			txtCPF = new JFormattedTextField(new DefaultFormatterFactory(
                    new MaskFormatter("###.###.###-##")));
            txtCPF.setColumns(10);
            
            txtNome = new JFormattedTextField(new DefaultFormatterFactory(
                    new MaskFormatter("**************************************************")));
            txtNome.setColumns(10);
            
            txtTelefone = new JFormattedTextField(new DefaultFormatterFactory(
                    new MaskFormatter("(##) #####-####")));
            txtTelefone.setColumns(10);
		} catch (ParseException e) {
			System.out.println("Erro na máscara.");
		}
		
		txtObservacoes = new JTextArea(5,15);
		
		btnCadastrar = new JButton("Cadastrar Mensalista");
		btnCadastrar.addActionListener(e -> cadastrar());
		
		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(e -> confirmExit());
		
		ajustarComponentes();
	}
	
	private void ajustarComponentes() {
		
		btnCadastrar.setIcon(new ImageIcon(getClass().getResource("/imagens/add_mensalista.png")));
		btnCancelar.setIcon(new ImageIcon(getClass().getResource("/imagens/cancelar.png")));
		
		panelCPF.setLayout(new BoxLayout(panelCPF, BoxLayout.LINE_AXIS));
		panelCPF.setBorder(BorderFactory.createEmptyBorder(20,15,20,15));
		
		panelNome.setLayout(new BoxLayout(panelNome, BoxLayout.X_AXIS));
		panelNome.setBorder(BorderFactory.createEmptyBorder(20,15,20,15));
		
		panelTelefone.setLayout(new BoxLayout(panelTelefone, BoxLayout.X_AXIS));
		panelTelefone.setBorder(BorderFactory.createEmptyBorder(20,15,20,15));
		
		panelObservacoes.setLayout(new BorderLayout());
		panelObservacoes.setBorder(BorderFactory.createEmptyBorder(10,15,20,15));

		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
		
		mainPanel.setBackground(new Color(50, 50, 50));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		adicionarComponentes();
	}
	
	private void adicionarComponentes() {
		
		panelCPF.add(lblCPF);
		panelCPF.add(txtCPF);
		
		panelNome.add(lblNome);
		panelNome.add(txtNome);
		
		panelTelefone.add(lblTelefone);
		panelTelefone.add(txtTelefone);
		
		panelObservacoes.add(lblObservacoes, BorderLayout.NORTH);
		panelObservacoes.add(txtObservacoes, BorderLayout.SOUTH);
		
		panel1.add(panelCPF);
		panel1.add(panelNome);
		panel1.add(panelTelefone);
		
		panel2.add(panel1);
		panel2.add(panelObservacoes);
		
		btnPanel.add(btnCadastrar);
		btnPanel.add(btnCancelar);
		
		mainPanel.add(panel2);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		mainPanel.add(btnPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		
		this.add(mainPanel);
	}
	
	private void ajustarJanela() {    
        this.setIconImage(new ImageIcon(getClass().getResource("/imagens/add_mensalista.png")).getImage());
		this.setVisible(true);
		this.setTitle("Cadastrar Mensalista");
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
