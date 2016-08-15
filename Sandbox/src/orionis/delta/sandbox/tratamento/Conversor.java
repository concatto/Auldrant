/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package orionis.delta.sandbox.tratamento;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Conversor {
	private static SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
	
    public static String paraTexto(Date data) {
    	return formato.format(data);
    }

    public static Date paraData(String texto) {
    	try {
			return formato.parse(texto);
		} catch (ParseException e) {
			return null;
		}
    }
}
