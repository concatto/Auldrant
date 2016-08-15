
package leitor;

import java.awt.List;
import java.util.ArrayList;
import java.util.LinkedList;


public class Vertice {
    
    int idVertice;
    String nome;
    boolean visitado;
    private LinkedList arestas;
    private LinkedList adj;
    int identificador;
   

    public Vertice(int idVertice, String nome) {
        this.nome = nome;
        this.idVertice = idVertice;
        arestas = new LinkedList();
        adj = new LinkedList();
    }

    public int getIdVertice() {
        return idVertice;
    }

    public String getNome() {
        return nome;
    }

    public void setIdVertice(int idVertice) {
        this.idVertice = idVertice;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setVisitado(boolean visitado) {
        this.visitado = visitado;
    }
    
    public boolean getVisitado(){
        return visitado;
    }
    
    
    
    public void Aresta(int id1, int id2){
        arestas.add(id1);
        arestas.add(id2);
    }

    public LinkedList getAdj() {
        return adj;
    }
    
    
    public void adicionarAdjacente(Vertice v){
        adj.add(v);
        v.adj.add(this);
        
    }
    
    
    public Vertice buscaVertice(int pos, Grafo g) {
        for (Vertice vertice : g.getListaDeVertices()) {
            if (vertice.identificador == pos) {
                return vertice;
            }
        }
        return null;
    }

    public int getIdentificador() {
        return identificador;
    }

    
    
}
