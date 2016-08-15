package epsilon.orionis.yates.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Principal {
	private List<Integer> lista = new ArrayList<>();
	private List<Integer> novaLista = new ArrayList<>();
	private int[] numerosGerados = new int[52];
	public Principal() {
		for (int i = 1; i <= 53; i++) {
			int a = (i % 13) + 1;
			lista.add(a);
		}
		
		Random rand = new Random();
		
		for (int i = 1; i <= 52; i++) {
			int r = rand.nextInt(52 - (i - 1));
			for (int j = 0; j <= r; j++) {
				if (contains(j, 52, numerosGerados)) {
					r++;
				}
			}
			novaLista.add(lista.get(r));
			numerosGerados[i-1] = r;
		}
		
		for (int valor : novaLista) {
			System.out.println(valor);
		}
	}
	
	public boolean contains(int o, int size, int[] elementData) {
		for (int i = 0; i < size; i++) {
			if (o == elementData[i]) {
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		new Principal();
	}
}
