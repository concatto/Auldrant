
package jantar;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author João Felipe Gonçalves <joaofelipe.rode@gmail.com>
 */
public class Filosofo {

    private int indice;         // Identificador. Filosofos identificados por indice.
    private int garfoDireita;   
    private int garfoEsquerda;  
    private Random tempoAleatorio = new Random();   // Tempo aleatorio entre 0 e 2 segundos
    private Mesa mesa;          // Recebe por parametro a mesa em uso no experimento
    
    public Filosofo(int indice, Mesa mesa) {
        setIndice(indice);
        setGarfoDireita(indice);
        setGarfoEsquerda(indice + 1);
        setMesa(mesa);
    }
    
    public void iniciarJanta() {
        while (mesa.hasComida()) {
            pensar();                       // Pensa
            pegaGarfo(this.indice+1);       // Pega garfo da esquerda
            pegaGarfo(this.indice);         // Pega garfo da direita
            comer();                        // Come
            poeGarfo(this.indice+1);        // Devolve garfo da esquerda
            poeGarfo(this.indice);          // Devolve garfo da direita
        }
    }
    
    public void pensar() {
        mesa.setPensando(mesa.getPensando() + 1);
        try {
            Thread.currentThread().sleep(tempoAleatorio.nextInt(Jantar.tempoPensando));
        } catch (InterruptedException ex) {
            Logger.getLogger(Filosofo.class.getName()).log(Level.SEVERE, null, ex);
        }
        mesa.setPensando(mesa.getPensando() - 1);
        System.out.println("Filosofo " + getIndice() + " > Pensou");// LOG
    }
    
    public void comer() {
        mesa.setComendo(mesa.getComendo() + 1);
        try {
            Thread.currentThread().sleep(tempoAleatorio.nextInt(Jantar.tempoComendo));
        } catch (InterruptedException ex) {
            Logger.getLogger(Filosofo.class.getName()).log(Level.SEVERE, null, ex);
        }
        mesa.setComendo(mesa.getComendo() + 1);
        mesa.publicar("Filosofo " + getIndice() + " > Terminou de comer");
//        System.out.println("Filosofo " + getIndice() + " > Terminou de comer");    // LOG
    }
    
    public void pegaGarfo(int i) {
        if (i == 5) {
            System.out.println("Filosofo " + getIndice() + " > Quer pegar o garfo 0");     // LOG
            mesa.garfos.get(0).pegaGarfo();
            System.out.println("Filosofo " + this.indice + " > Pegou o garfo " + i);
        } else {
            System.out.println("Filosofo " + getIndice() + " > Quer pegar o garfo " + i);     // LOG
            mesa.garfos.get(i).pegaGarfo();
            System.out.println("Filosofo " + this.indice + " > Pegou o garfo " + i);
        }
        mesa.setComendo(mesa.getComendo() - 1);
    }
    
    public void poeGarfo(int i) {
        if (i == 5) {
            mesa.garfos.get(0).poeGarfo();
        } else {
            mesa.garfos.get(i).poeGarfo();
        }
        
        System.out.println("Filosofo " + getIndice() + " > Devolveu garfo");       // LOG
    }
    
    
    // Getters e Setters
    
    public void setIndice(int indice) {
        this.indice = indice;
    }

    public void setGarfoDireita(int garfoDireita) {
        this.garfoDireita = garfoDireita;
    }

    public void setGarfoEsquerda(int garfoEsquerda) {
        this.garfoEsquerda = garfoEsquerda;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }
    
    public int getIndice() {
        return indice;
    }
    
    public int getGarfoDireita() {
        return garfoDireita;
    }

    public int getGarfoEsquerda() {
        return garfoEsquerda;
    }
    
}
