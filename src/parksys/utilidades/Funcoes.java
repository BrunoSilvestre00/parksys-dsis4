package parksys.utilidades;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Funcoes {

	public static int charCount(String string, char c) {	
		int count = 0;
		for(int i = 0; i < string.length(); i++)
			if (string.charAt(i) == c)
				count++;
		return count;
	}
	
	public static double StringPctToDouble(String str_valor){
		return Double.parseDouble(str_valor.replace("%", ""));
	}
	
	public static String DoubleToString(double valor) {
		String str_valor = Double.toString(valor).replace(',', '.');
		return str_valor;
	}
	
	//Essa função foi criada unicamente pra funcionar em conjunto com a classe JMoneyField
	public static double stringToDecimal(String str_valor) {
		return Double.parseDouble(str_valor.replace("R$ ", "").replace(".", "").replace(",", "."));
	}
	
	public int getWeek(String date){ //ex 07/03/2017
	    int dayWeek = 0;
	    GregorianCalendar gc = new GregorianCalendar();
	    try {
	        gc.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(date));
	        switch (gc.get(Calendar.DAY_OF_WEEK)) {
	            case Calendar.SUNDAY:
	                dayWeek = 1;
	                break;
	            case Calendar.MONDAY:
	                dayWeek = 2;
	                break;
	            case Calendar.TUESDAY:
	                dayWeek = 3;
	            break;
	            case Calendar.WEDNESDAY:
	                dayWeek = 4;
	                break;
	            case Calendar.THURSDAY:
	                dayWeek = 5;
	                break;
	            case Calendar.FRIDAY:
	                dayWeek = 6;
	                break;
	            case Calendar.SATURDAY:
	                dayWeek = 7;

	        }
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }
	    return dayWeek;
	}
	
}
