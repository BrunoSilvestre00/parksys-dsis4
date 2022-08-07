package parksys.graficos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import parksys.dao.DataAccessException;
import parksys.dao.EntradaSaidaDAO;
import parksys.utilidades.Funcoes;

public class GraficoPermanencia {
	
	/*public void execute() {
		XYChart chart = buildChart();
		new SwingWrapper<>(chart).displayChart();
	}*/
	
	/*
	 * Implementação baseada no exemplo disponível em:
	 * https://github.com/knowm/XChart/blob/36977a3dc5be67085a086528b84dae67856ba38b/xchart-demo/src/main/java/org/knowm/xchart/demo/charts/date/DateChart06.java#L34
	 */
	public XYChart buildChart(Date dtInicio, Date dtFinal) throws DataAccessException {
		XYChart chart = new XYChartBuilder()
				.width(600)
				.height(400)
				.title("Permanência Média por Dia da Semana - 01/09/21 - 30/09/21")
				.yAxisTitle("Horas")
				.xAxisTitle("Dia da Semana")
				.build();
		
		// Series
		List<Integer> xs = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
		List<Double> ys = Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
		List<Date> datas = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(dtInicio);
        for (Date dt = dtInicio; dt.compareTo(dtFinal) <= 0; ) {
        	datas.add(dt);
            cal.add (Calendar.DATE, +1);
            dt = cal.getTime();
        }
        
        List<Double> ys_aux = new EntradaSaidaDAO().getMediaPermanencia(datas);

    	Funcoes f = new Funcoes();
        for(int i = 1; i < datas.size(); i++) {
        	
        	switch (f.getWeek(new SimpleDateFormat("dd/MM/yyyy").format(datas.get(i)))) {
				case 1:
					ys.set(0, ys.get(0) + ys_aux.get(i));
					break;
				case 2:
					ys.set(1, ys.get(1) + ys_aux.get(i));
					break;
				case 3:
					ys.set(2, ys.get(2) + ys_aux.get(i));
					break;
				case 4:
					ys.set(3, ys.get(3) + ys_aux.get(i));
					break;
				case 5:
					ys.set(4, ys.get(4) + ys_aux.get(i));
					break;
				case 6:
					ys.set(5, ys.get(5) + ys_aux.get(i));
					break;
				case 7:
					ys.set(6, ys.get(6) + ys_aux.get(i));
					break;
	
				default:
					break;
			}
        }
        		
		chart.addSeries("Tempo médio (em horas)", xs, ys);

		// Aparência
		chart.getStyler().setLegendVisible(false);
		
		/*
		 * Alterando marcações (ticks) no eixo X, para exibir nomes dos dias da semana.
		 * Implementação baseada no exemplo disponível em:
		 * https://github.com/knowm/XChart/blob/36977a3dc5be67085a086528b84dae67856ba38b/xchart-demo/src/main/java/org/knowm/xchart/demo/charts/date/DateChart06.java#L63
		 */
		String[] diasSemana = new String[] {"Dom","Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"};
		chart.getStyler().setxAxisTickLabelsFormattingFunction(d -> diasSemana[d.intValue() - 1]);
		
		return chart;
	}

	/*public static void main(String[] args) {
		new GraficoPermanencia().execute();
	}*/
}
