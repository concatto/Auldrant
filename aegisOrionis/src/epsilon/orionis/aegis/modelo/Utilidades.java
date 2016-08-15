package epsilon.orionis.aegis.modelo;

import java.util.Observable;

public class Utilidades extends Observable {
	public void filtrarBytes(long bytes) {
		String valor;
	    int unit = 1024;
	    if (bytes < unit) {
	    	valor = bytes + " B";
	    } else {
		    int exp = (int) (Math.log(bytes) / Math.log(unit));
		    char pre;
			pre = ("KMGTPE").charAt(exp - 1);
		    valor = String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	    }
	    valor = String.format(" (%s)", valor);
	    
	    setChanged();
	    notifyObservers(valor);
	}
}
