package parksys.gui;

import java.lang.reflect.InvocationTargetException;

import java.awt.*;

import javax.swing.*;

import parksys.dao.ConfiguracaoDAO;
import parksys.dao.DataAccessException;
import parksys.modelo.Configuracao;
import parksys.modelo.GerenciadorVagas;

public class JanelaPrincipal extends JFrame {
	
	private JPanel mainPanel, panel0, panel1, panel2, panel3, panel4;
	private JButton btnCadastrarMensalista;
	private JButton btnCadastrarVeiculos;
	private JButton btnEntradaSaida;
	private JButton btnPagamentoMensal;
	private JButton btnVisualizarSaldo;
	private JButton btnPermanencia;
	private JButton btnFaturamento;
	private JButton btnConfig;
	
	private JLabel ocupacao;
	
	GerenciadorVagas gerenciadorVagas;
	
	public JanelaPrincipal() {
		
		ajustarTema();
		
		gerenciadorVagas = new GerenciadorVagas(this);
		try {
			Configuracao config = new ConfiguracaoDAO().getConfiguracaoSistema();
			if (config == null)
				configurarSistema();
			else
				gerenciadorVagas.updateDados();
		} catch (DataAccessException e1) {
			e1.printStackTrace();
		}
			
		criarComponentes();
		ajustarJanela();
	}
	
	private void ajustarTema() {
		ajustarFontes();
		
		UIManager.put("Panel.background", new Color(0, 0, 0, 0));
		
		UIManager.put("Label.foreground", new Color(255, 255, 255));
		
		UIManager.put("Button.foreground", new Color(255, 255, 255));
		UIManager.put("Button.background", new Color(100, 100, 100));
	}
	
	private void ajustarFontes(){
        Font fonte = new Font("Tahoma", Font.PLAIN, 25);
        UIManager.put("Button.font", fonte);
        UIManager.put("Label.font", fonte);
        UIManager.put("TextField.font", fonte);
        UIManager.put("TextArea.font", fonte);
        UIManager.put("FormattedTextField.font", fonte);
        UIManager.put("RadioButton.font", fonte);
    }
	
	public void updateLabelCount() {
		int vagasOcupadas = gerenciadorVagas.getVagasOcupadas();
		int vagasTotais = gerenciadorVagas.getVagasTotais();
		
		ocupacao.setText(String.format("%d/%d", vagasOcupadas, vagasTotais));
		if (gerenciadorVagas.cheio())
			ocupacao.setForeground(Color.red);
		else	
			ocupacao.setForeground(Color.white);

		this.repaint();
	}
	
	private void cadastrarMensalista() {
		new JanelaCadastrarMensalista(this);
	}
	
	public void cadastrarVeiculos(String cpf) {
		new JanelaCadastrarVeiculos(cpf);
	}
	
	private void EntradaSaida() {
		new JanelaEntradaSaida(gerenciadorVagas);
	}
	
	private void visualizarSaldo() {
		new JanelaVisualizarSaldo();
	}
	
	private void pagamentoMensal() {
		new JanelaPagamentoMensal();
	}
	
	private void exibirTempoPermanencia() {
		new JanelaPermanencia();
	}
	
	private void exibirFaturamento(){
		new JanelaFaturamentoDiario();
	}
	
	private void configurarSistema() {
		new JanelaConfiguracao(gerenciadorVagas);
	}
	
	private void criarComponentes() {
		
		mainPanel = new JPanel(){
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image imagem = new ImageIcon(getClass().getResource("/imagens/background.jpg")).getImage();
                g.drawImage(imagem, 0, 0, this);
            }
        };
		
        panel0 = new JPanel();
		panel1 = new JPanel();
		panel2 = new JPanel();
		panel3 = new JPanel();
		panel4 = new JPanel();
		
		btnCadastrarMensalista = new JButton(" Cadastrar Mensalista");
		btnCadastrarMensalista.addActionListener(e -> cadastrarMensalista());
		
		btnCadastrarVeiculos = new JButton("  Cadastrar Veículos");
		btnCadastrarVeiculos.addActionListener(e -> cadastrarVeiculos(""));
		
		btnEntradaSaida = new JButton(" Registrar Entrada/Saída ");
		btnEntradaSaida.addActionListener(e -> EntradaSaida());
		
		btnVisualizarSaldo = new JButton("   Visualizar Saldo    ");
		btnVisualizarSaldo.addActionListener(e -> visualizarSaldo());
		
		btnPagamentoMensal = new JButton(" Pagamento Mensal ");
		btnPagamentoMensal.addActionListener(e -> pagamentoMensal());
		
		btnPermanencia = new JButton("Gráfico Permanência");
		btnPermanencia.addActionListener(e -> exibirTempoPermanencia());
		
		btnFaturamento = new JButton("Gráfico Faturamento");
		btnFaturamento.addActionListener(e -> exibirFaturamento());
		
		btnConfig = new JButton("");
		btnConfig.addActionListener(e -> configurarSistema());
		
		int vagasOcupadas = gerenciadorVagas.getVagasOcupadas();
		int vagasTotais = gerenciadorVagas.getVagasTotais();
		ocupacao = new JLabel(String.format("%d/%d", vagasOcupadas, vagasTotais));
		
		ajustarComponentes();
	}
	
	private void ajustarComponentes() {
		
		btnCadastrarMensalista.setIcon(new ImageIcon(getClass().getResource("/imagens/add_mensalista.png")));
		btnCadastrarVeiculos.setIcon(new ImageIcon(getClass().getResource("/imagens/carro.png")));
		btnEntradaSaida.setIcon(new ImageIcon(getClass().getResource("/imagens/entrada_saida.png")));
		btnVisualizarSaldo.setIcon(new ImageIcon(getClass().getResource("/imagens/show.png")));
		btnPagamentoMensal.setIcon(new ImageIcon(getClass().getResource("/imagens/pagar.png")));
		btnPermanencia.setIcon(new ImageIcon(getClass().getResource("/imagens/tempo.png")));
		btnFaturamento.setIcon(new ImageIcon(getClass().getResource("/imagens/faturamento.png")));
		btnConfig.setIcon(new ImageIcon(getClass().getResource("/imagens/config.png")));

		btnEntradaSaida.setFont(new Font("Verdana", Font.BOLD, 38));
		
		ocupacao.setFont(new Font("Impact", Font.PLAIN, 80));
		ocupacao.setForeground(Color.white);
		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		adicionarComponentes();
	}
	
	private void adicionarComponentes() {
		
		panel0.add(btnEntradaSaida);
		
		panel1.add(btnCadastrarMensalista);
		panel1.add(Box.createRigidArea(new Dimension(30, 0)));
		panel1.add(btnCadastrarVeiculos);
		
		panel2.add(btnVisualizarSaldo);
		panel2.add(Box.createRigidArea(new Dimension(20, 0)));
		panel2.add(btnPagamentoMensal);
		
		panel3.add(btnPermanencia);
		panel3.add(Box.createRigidArea(new Dimension(20, 0)));
		panel3.add(btnFaturamento);	
		
		panel4.add(Box.createRigidArea(new Dimension(490, 0)));
		panel4.add(ocupacao);
		panel4.add(Box.createRigidArea(new Dimension(360, 0)));
		panel4.add(btnConfig);
		
		mainPanel.add(Box.createRigidArea(new Dimension(0, 200)));
		mainPanel.add(panel1);
		mainPanel.add(panel0);		
		mainPanel.add(panel2);
		mainPanel.add(panel3);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		mainPanel.add(panel4);
		
		this.add(mainPanel);
	}
	
	private void ajustarJanela() {    
        this.setIconImage(new ImageIcon(getClass().getResource("/imagens/IFSP.png")).getImage());
		this.setTitle("ParkSys - DSIS4");
        this.setSize(1200,667);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
    }
	
	public static void main(String[] args) {
		try {
			SwingUtilities.invokeAndWait(JanelaPrincipal::new);
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}


