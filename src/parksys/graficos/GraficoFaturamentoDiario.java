package parksys.graficos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import org.knowm.xchart.*;

import parksys.dao.DataAccessException;
import parksys.dao.EntradaSaidaDAO;



/*
 * Implementação baseada no exemplo disponível em:
 * https://github.com/knowm/XChart/blob/develop/xchart-demo/src/main/java/org/knowm/xchart/demo/charts/date/DateChart05.java
 */
public class GraficoFaturamentoDiario {
	
	/*public void execute() {
		XYChart chart = buildChart();
		new SwingWrapper<>(chart).displayChart();
	}*/
	
	public XYChart buildChart(Date dtInicio, Date dtFinal) throws DataAccessException, ParseException {
		XYChart chart = new XYChartBuilder()
				.width(600)
				.height(400)
				.title("Faturamento Diário")
				.yAxisTitle("R$")
				.build();
		
		List<Date> xs = new ArrayList<>();
		List<Double> ys = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(dtInicio);
        for (Date dt = dtInicio; dt.compareTo(dtFinal) <= 0; ) {
            xs.add(dt);
            cal.add (Calendar.DATE, +1);
            dt = cal.getTime();
        }

  
		ys = new EntradaSaidaDAO().getFaturamento(xs);
        
		
		chart.addSeries("Faturamento diário", xs, ys);

		// Aparência
		chart.getStyler().setLegendVisible(false);
		
		/*
		 * Alterando marcações (ticks) no eixo X, para exibir nomes dos dias da semana.
		 * Implementação baseada no exemplo disponível em:
		 * https://github.com/knowm/XChart/blob/36977a3dc5be67085a086528b84dae67856ba38b/xchart-demo/src/main/java/org/knowm/xchart/demo/charts/date/DateChart06.java#L63
		 */
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM");
		chart.getStyler().setxAxisTickLabelsFormattingFunction(date -> formatador.format(date));
		
		return chart;
	}

	/*public static void main(String[] args) {
		new GraficoFaturamentoDiario().execute();
	}*/
}
