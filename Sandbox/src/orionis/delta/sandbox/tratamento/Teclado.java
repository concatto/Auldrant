 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package orionis.delta.sandbox.tratamento;

import java.util.Date;
import java.util.Scanner;

public class Teclado {
	private static Scanner leitura = new Scanner(System.in);
    
    public static String lerTexto(){
        System.out.println("Digite um texto.");
        return leitura.nextLine();
    }
    
    public static int lerInteiro(){
        System.out.println("Digite um inteiro.");
        return leitura.nextInt();
    }
    
    public static float lerNumero(){
        System.out.println("Digite um numero.");
        return leitura.nextFloat();
    }
    
    public static boolean lerBoleano(){
        System.out.println("Digite um valor booleano.");
        return leitura.nextBoolean();
    }
    
    public static Date lerData() {
    	System.out.println("Digite uma data. Formato: dd/mm/aaaa");
    	return Conversor.paraData(leitura.nextLine());
    }
}
