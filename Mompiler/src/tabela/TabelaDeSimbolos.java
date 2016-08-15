
package tabela;


import classessintaticas.SemanticError;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;
/**
 *
 * @author Guilherme e Salazar
 */


public class TabelaDeSimbolos {

    //Cada linha da tabela será uma lista com diversos elementos.
    
    private List<Simbolos> listaDeSimbolos;
    
   
    private Simbolos ultimoBloco;
    private Simbolos simbolo;
    
    private int chavePrimaria = 0;
    
    private Stack<String> pilhaDeEscopos;

    public TabelaDeSimbolos() {
        
  
        listaDeSimbolos = new ArrayList<>();
        pilhaDeEscopos = new Stack<>();
        pilhaDeEscopos.push("Global");
        
    }
    
    public void criaVariavel(String tipo, int posicao, int id){
        simbolo = new Simbolos();
        simbolo.tipoDaVariavel = tipo;
        simbolo.posicao = posicao;
        simbolo.idPrincipal = id;
        simbolo.chavePrimaria += chavePrimaria++; 
    }
    
    public boolean contemSimbolo() {
        return simbolo != null;
    }
    
    public void adicionarNomeAVariavel(String nome) throws SemanticError{
        simbolo.nomeDaVariavel = nome;
        AddSimboloTabela();
    }
    
    public void adicionarNomeAVariavel(String nome, boolean parametro) throws SemanticError{
        simbolo.parametro = parametro;
        adicionarNomeAVariavel(nome);
    }
    
    public void criarVetor(String tipo, int posicao, int id) throws SemanticError{
        
        simbolo.vetor = true;
        AddSimboloTabela();
        
    }
    
    public void definirEscopo(){ 
        definirEscopo("Global");
    }
    
    public void iniciarParametro(String parenteses){
        
    }
    
    private boolean ValidarInclusao() {
        for (String escopo : pilhaDeEscopos) {
            long iguais = listaDeSimbolos.stream().filter(simboloteste -> simboloteste.nomeDaVariavel.equals(simbolo.nomeDaVariavel) && simboloteste.escopo.equals(escopo)).count();
            if (iguais != 0) {
                return false;
            }
        }
        
        return true;
        
        
        //return listaDeSimbolos.stream().filter(simboloteste -> simboloteste.nomeDaVariavel.equals(simbolo.nomeDaVariavel) && simboloteste.escopo.equals(simbolo.escopo)).collect(Collectors.toList()).isEmpty();
    }
    
    public void definirEscopo(String escopo){
        if (!escopo.equals("Global")){
            pilhaDeEscopos.push(escopo);
        }
    }
   
    public void removerEscopo(){
        if (!pilhaDeEscopos.peek().equals("Global")){
            pilhaDeEscopos.pop();
        }
    }
    
    public Simbolos getUltimoBloco() {
        return ultimoBloco;
    }
    
    public List<Simbolos> getListaDeSimbolos() {
        return listaDeSimbolos;
    }

    private void AddSimboloTabela() throws SemanticError {
        simbolo.escopo = pilhaDeEscopos.peek();
        
        if (ValidarInclusao()){
            listaDeSimbolos.add(simbolo);
            //removerEscopo();
            simbolo = null;
        }else{
            throw new SemanticError("A variável " + simbolo.nomeDaVariavel + " já existe.", simbolo.posicao);
        }
    }
    
    public void verificaVariaveis(String nome) throws SemanticError{
        
        if (simbolo == null){
            if (listaDeSimbolos.stream().filter(simbolotaste -> simbolotaste.nomeDaVariavel.equals(nome) && simbolotaste.escopo.equals(pilhaDeEscopos.peek())).collect(Collectors.toList()).isEmpty()){
                throw new SemanticError("A variável ainda não foi declarada!");
            }else{
                simbolo = listaDeSimbolos.stream().filter(simbolotast -> simbolotast.nomeDaVariavel.equals(nome) && simbolotast.escopo.equals(pilhaDeEscopos.peek())).findFirst().get();
                simbolo.usada = true;
            }
        }
    }
    
    public String buscarNaoUsadas(){
        StringBuilder mensagemRetorno = new StringBuilder();
        for(Simbolos simbolo:listaDeSimbolos){
            if (!simbolo.usada){
                mensagemRetorno.append("A variável '").append(simbolo.nomeDaVariavel).append("' não está sendo utilizada em nenhum momento.").append("\n");
            }
            
        }
        return mensagemRetorno.toString();
    }
    
    public void atribuirValor(){
        if (simbolo != null){
            simbolo.usada = true;
        }
    }
    
    public void criarSimbolo(int idPrincipal, String nomeDaVariavel, String tipoDaVariavel, boolean inicializada, boolean usada, int posicao) throws SemanticError {
        simbolo = new Simbolos(idPrincipal, nomeDaVariavel, tipoDaVariavel, inicializada, usada, posicao);
        AddSimboloTabela();
    }
    
    public void criarBloco(int idPrincipal, String nomeDaVariavel, int posicao) throws SemanticError {
        simbolo = new Simbolos(idPrincipal, nomeDaVariavel, "void", true, true, posicao);
        ultimoBloco = simbolo;
        AddSimboloTabela();
    }
    
    public void Funcao() throws SemanticError{
        if (simbolo != null){
            simbolo.funcao = true;
            simbolo.usada = true;
            simbolo.inicializada = true;
            AddSimboloTabela();
        }else{
            listaDeSimbolos.get(listaDeSimbolos.size()-1).funcao = true;
            listaDeSimbolos.get(listaDeSimbolos.size()-1).usada = true;
            listaDeSimbolos.get(listaDeSimbolos.size()-1).inicializada = true;
            ultimoBloco = listaDeSimbolos.get(listaDeSimbolos.size()-1);
        }
    }
    
    public void iniciarComandos(String nome, int posicao, int ID) throws SemanticError{
        simbolo = new Simbolos();
        simbolo.escopo = pilhaDeEscopos.peek();
        simbolo.inicializada = true;
        simbolo.idPrincipal = ID;
        simbolo.posicao = posicao;
        simbolo.chavePrimaria = chavePrimaria++;
        simbolo.tipoDaVariavel = "Comando";
        
        AddSimboloTabela();
    }
    
    
    public void iniciarComandoLeitura(int posicao, int id) throws SemanticError{
            iniciarComandos("Leitura", posicao, id);
    }
    
    public void iniciarComandoEscrita(int posicao, int id) throws SemanticError{
        iniciarComandos("Escrita", posicao, id);
    }

    public void empilharEscopo(String escopo) {
        pilhaDeEscopos.push(escopo);
    }
    
    public void desempilharEscopo() {
        pilhaDeEscopos.pop();
    }

    public Simbolos getUltimoSimbolo() {
        return listaDeSimbolos.get(listaDeSimbolos.size() - 1);
    }
    
    public Optional<Simbolos> buscarSimbolo(String nome) {
        String escopoAtual = pilhaDeEscopos.peek();
        return listaDeSimbolos.stream().filter(s -> s.nomeDaVariavel.equals(nome) && s.escopo.equals(escopoAtual)).findFirst();
    }
    
    public Optional<Simbolos> buscarSimboloPercorrendoPilha(String nome) {
        for (String escopoAtual : pilhaDeEscopos) {
        	Optional<Simbolos> o = listaDeSimbolos.stream().filter(s -> s.nomeDaVariavel.equals(nome) && s.escopo.equals(escopoAtual)).findFirst();
        	if (o.isPresent()) return o;
        }
        return Optional.empty();
    }
}
