
package leitor;

import java.util.LinkedList;
import java.util.Stack;
import javax.swing.JOptionPane;

public class DFS {
  
    
    private Grafo g;
    private Stack<Vertice> pilhaDeVertices;
    private int matriz[][];
    
    
    public boolean isConexo(){
        Vertice v;
        Vertice adj;
        int aux = 0;
        if (pilhaDeVertices.isEmpty()){
            if (percorreVisitados() == false){
                System.out.println("falso"); 
            }else return true;
        }else {
            v = pilhaDeVertices.peek();
            for(int i = 0; i < g.getListaDeVertices().size(); i++){
               adj = g.getListaDeVertices().get(i);
               if (matriz[v.getIdentificador()][i] == 1){
                   if(adj.getVisitado() == false){
                       v = v.buscaVertice(i, g);
                       v.setVisitado(true);
                       pilhaDeVertices.push(v);
                       aux = 0;
                       break;
                   }
               }
               aux = i;
            }
            if (aux == (g.getListaDeVertices().size()-1)){
                pilhaDeVertices.pop();
            }
        }
        return isConexo();
    }

    public DFS(Grafo g) {
        this.g = g;
        matriz = g.iniciaMatriz();
        pilhaDeVertices = new Stack<>();
        pilhaDeVertices.push(g.getListaDeVertices().get(0)); // Pega o elemento inicial da lista para setá-lo como inicial;
        pilhaDeVertices.peek().setVisitado(true);
        
              
    }
    
    
  
    private boolean percorreVisitados() { //Algoritmo para definir se todos foram visitados;
        for (int i = 0; i < g.getListaDeVertices().size(); i++) {
            Vertice vertice = g.getListaDeVertices().get(i);
            if (!vertice.getVisitado()) {
                return false;
            }
        }
        return true;
    }
    
    
    
    
    
}


