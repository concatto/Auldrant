
package jantar;

import java.util.function.Consumer;

/**
 *
 * @author João Felipe Gonçalves <joaofelipe.rode@gmail.com>
 */
public class Jantar {

    public static int qtdFilosofos;
    public static int tempoPensando;
    public static int tempoComendo;
    private Mesa mesa;     // Criado uma mesa para o experimento.
    private Thread[] threads;
    
    public Jantar(int qtdFilosofos, int tempoPensando, int tempoComendo) {        
        this.qtdFilosofos = qtdFilosofos;
        this.tempoPensando = tempoPensando;
        this.tempoComendo = tempoComendo;
        
        mesa = new Mesa();
        System.out.println(mesa.garfos);
        
        threads = new Thread[qtdFilosofos];
        int indice = 0;
        while (indice < qtdFilosofos) { 
            final Filosofo f = new Filosofo(indice, mesa); // Passado mesa por parametro para uso do filosofo
            
            threads[indice] = new Thread(new Runnable() {
                
                @Override
                public void run() {
                    f.iniciarJanta();
                }
            });
            
            threads[indice].start();
            indice++;
        }
    }
    
    public void parar() {
    	mesa.setComida(false);
    	for (Thread thread : threads) {
			thread.interrupt();
		}
    }
    
    public Mesa getMesa() {
		return mesa;
	}
    
    public void addMensagemConsumer(Consumer<String> consumer) {
    	mesa.setConsumer(consumer);
    }
}