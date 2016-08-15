
package jantar;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author João Felipe Gonçalves <joaofelipe.rode@gmail.com>
 */
public class Garfo {

    private int indice;
    private boolean isLivre = true;
    private Semaphore semaforo = new Semaphore(1);
    
    public Garfo() {
        
    }
    
    public void pegaGarfo() {
        try {
            semaforo.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Garfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        setIsLivre(false);
        
    }
    
    public void poeGarfo() {
        setIsLivre(true);
        semaforo.release();
    }

    public void setIsLivre(boolean isLivre) {
        this.isLivre = isLivre;
    }

    public boolean isIsLivre() {
        return isLivre;
    }
    
    
}
