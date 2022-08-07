package parksys.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.text.*;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.text.*;

import org.knowm.xchart.*;

import parksys.dao.DataAccessException;
import parksys.graficos.GraficoFaturamentoDiario;

public class JanelaFaturamentoDiario extends JFrame{
	private JPanel mainPanel, panel1, panel2, btnPanel;
	private JButton btnGrafico, btnCancelar;
	private JFormattedTextField txtDataInicio, txtDataFinal;
	private JLabel lblDe, lblAte;
	private JanelaPrincipal mainJanela;
	
	public JanelaFaturamentoDiario(){
		criarComponentes();
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
	
	private void gerarGrafico() {
		
	    SwingWorker<Void, Void> worker = new SwingWorker<>() {
			
			@Override
			protected Void doInBackground() throws ParseException, DataAccessException {

		        DateFormat df = new SimpleDateFormat ("dd/MM/yyyy");
				Date dt1 = df.parse(txtDataInicio.getText());
			    Date dt2 = df.parse(txtDataFinal.getText());
			    XYChart chart = new GraficoFaturamentoDiario().buildChart(dt1, dt2);
			    JFrame frame = new SwingWrapper<XYChart>(chart).displayChart();
			    javax.swing.SwingUtilities.invokeLater(
			    	    ()->frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE)
			    	);
				return null;
			}
			
			protected void done() {
				try {
					get();
				} catch (InterruptedException | ExecutionException e) {
					JOptionPane.showMessageDialog(mainPanel, e, "Es!", JOptionPane.ERROR_MESSAGE);

				}
			}
		};

		worker.execute();

		this.dispose();
	}
	
	private void criarComponentes() {
		mainPanel = new JPanel();
		panel1 = new JPanel();
		panel2 = new JPanel();
		btnPanel = new JPanel();	
		
		lblAte = new JLabel("Até: ");
		lblDe = new JLabel("De: ");
		
		try {
			txtDataInicio = new JFormattedTextField(new DefaultFormatterFactory(
                    new MaskFormatter("##/##/####")));
			txtDataInicio.setColumns(10);
            
            txtDataFinal = new JFormattedTextField(new DefaultFormatterFactory(
                    new MaskFormatter("##/##/####")));
            txtDataFinal.setColumns(10);
		} catch (ParseException e) {
			System.out.println("Erro na máscara.");
		}
		
		btnGrafico = new JButton("Gerar Gráfico");
		btnGrafico.addActionListener(e -> gerarGrafico());
		
		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(e -> confirmExit());

		ajustarComponentes();
	}
	
	private void ajustarComponentes() {
		mainPanel.setBackground(new Color(50, 50, 50));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		btnCancelar.setIcon(new ImageIcon(getClass().getResource("/imagens/cancelar_mini.png")));
		
		adicionarComponentes();
	}
	
	private void adicionarComponentes() {

		btnPanel.add(btnGrafico);
		btnPanel.add(Box.createRigidArea(new Dimension(30,0)));
		btnPanel.add(btnCancelar);
		
		panel1.add(lblDe);
		panel1.add(txtDataInicio);
		panel2.add(lblAte);
		panel2.add(txtDataFinal);
		
		mainPanel.add(panel1);
		mainPanel.add(panel2);
		mainPanel.add(btnPanel);
		
		this.add(mainPanel);
	}
	
	private void ajustarJanela() {  
		this.setVisible(true);
		this.setTitle("Gráfico faturamento");
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
