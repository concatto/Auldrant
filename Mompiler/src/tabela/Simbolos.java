
package tabela;


public class Simbolos {
    
   public int idPrincipal;
   public int chavePrimaria;
   public int fk;
   public String nomeDaVariavel;
   public String tipoDaVariavel;
   public boolean inicializada;
   public boolean usada;
   public String escopo;
   public boolean parametro = false;
   public int posicao = -1;
   public boolean vetor = false;
   public boolean funcao = false;

    public Simbolos() {
    }

    public Simbolos(int idPrincipal, String nomeDaVariavel, String tipoDaVariavel, boolean inicializada, boolean usada, int posicao) {
        this.idPrincipal = idPrincipal;
        this.nomeDaVariavel = nomeDaVariavel;
        this.tipoDaVariavel = tipoDaVariavel;
        this.inicializada = inicializada;
        this.usada = usada;
        this.posicao = posicao;
    }

    @Override
    public String toString() {
        return nomeDaVariavel;
    }
   
        
    
    
}
