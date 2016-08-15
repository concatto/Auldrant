
package leitor;

import java.awt.Dimension;
import java.awt.List;
import java.util.LinkedList;
import java.util.Stack;


public class Grafo {
    
  private LinkedList<Vertice> listaDeVertices;
  private LinkedList<Arco> listaDeArcos;
  private int[][] matriz;

    public Grafo() {
    	listaDeArcos = new LinkedList<Arco>();
        listaDeVertices = new LinkedList<Vertice>();
    }
  
  
  
    public int[][] iniciaMatriz() {
        int verticeA;
        int verticeB;
        int tamanhoMatriz;
        tamanhoMatriz = listaDeVertices.size();
        matriz = new int[tamanhoMatriz][tamanhoMatriz];

        for (int i = 0; i < tamanhoMatriz; i++) {
            for (int j = 0; j < tamanhoMatriz; j++) {
                matriz[i][j] = 0;
            }
        }
        
        for(int i = 0; i < listaDeArcos.size(); i++){
            verticeA = listaDeArcos.get(i).getIdVertice1();
            verticeB = listaDeArcos.get(i).getIdVertice2();
            matriz[verticeA][verticeB] = 1; //Deu liga
        }

        return matriz;

    }
  
        public void adicionarArco(int id1, int id2){
            Arco arco = new Arco(id1, id2);
            listaDeArcos.add(arco);
        }
        
        public void adicionarVertice(String nome, int id){
            Vertice v = new Vertice(id, nome);
            listaDeVertices.add(v);
            
        }

    public LinkedList<Arco> getListaDeArcos() {
        return listaDeArcos;
    }

    public LinkedList<Vertice> getListaDeVertices() {
        return listaDeVertices;
    }

    public int[][] getMatriz() {
        return matriz;
    }
        
     
      
  
}
