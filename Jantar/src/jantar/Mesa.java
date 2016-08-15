
package jantar;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


/**
 *
 * @author João Felipe Gonçalves <joaofelipe.rode@gmail.com>
 */
public class Mesa {
    private int pensando = 0;
    private int comendo = 0;
    public static List<Garfo> garfos = new ArrayList<>();  // Lista de Garfos
    private boolean comida = true;
	private Consumer<String> consumer;

    public Mesa() {
        for (int i=0; i < Jantar.qtdFilosofos;i++)  
            garfos.add(new Garfo());
    }
    
    
    public int getPensando() {
        return pensando;
    }

    public void setPensando(int pensando) {
        this.pensando = pensando;
    }

    public int getComendo() {
        return comendo;
    }

    public void setComendo(int comendo) {
        this.comendo = comendo;
    }
    
    public boolean hasComida() {
		return comida;
	}
    
    public void setComida(boolean comida) {
		this.comida = comida;
	}

    public void publicar(String mensagem) {
    	consumer.accept(mensagem);
    }

	public void setConsumer(Consumer<String> consumer) {
		this.consumer = consumer;
	}
}
