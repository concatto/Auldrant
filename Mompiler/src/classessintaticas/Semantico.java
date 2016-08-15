package classessintaticas;

import interfacegrafica.PainelPrincipal;
import java.util.Optional;
import tabela.Simbolos;

public class Semantico implements Constants
{
    private String ultimoNome = null;
    
    public void executeAction(int action, Token token)	throws SemanticError
    {
        System.out.println("Action: " + action + ", Token: " + token);
        switch(action){
            case 1:
                PainelPrincipal.tabelaPrincipal.criaVariavel(token.getLexeme(), token.getPosition(), token.getId());
                break;
            case 2:
                ultimoNome = token.getLexeme();
                if (PainelPrincipal.tabelaPrincipal.contemSimbolo()) {
                    PainelPrincipal.tabelaPrincipal.adicionarNomeAVariavel(token.getLexeme());
                }
                break;
            case 3:
                PainelPrincipal.tabelaPrincipal.buscarSimbolo(ultimoNome).ifPresent(s -> s.vetor = true);
                //PainelPrincipal.tabelaPrincipal.criarVetor(token.getLexeme(), token.getPosition(), token.getId());
                break;
            case 4:
                PainelPrincipal.tabelaPrincipal.Funcao();
                PainelPrincipal.tabelaPrincipal.empilharEscopo(PainelPrincipal.tabelaPrincipal.getUltimoBloco().nomeDaVariavel);
                break;
            case 5:
                if (PainelPrincipal.tabelaPrincipal.contemSimbolo()) {
                    PainelPrincipal.tabelaPrincipal.adicionarNomeAVariavel(token.getLexeme(), true);
                }
                break;
            case 6:
                //Fecha parenteses
             //   PainelPrincipal.tabelaPrincipal.definirEscopo("Fim de Função");
                break;
            case 7:
                //abre chaves
                Simbolos ultimo = PainelPrincipal.tabelaPrincipal.getUltimoBloco();
                System.out.println("Ultimo: " + ultimo.nomeDaVariavel);
                if (ultimo != null && !ultimo.funcao) {
                    PainelPrincipal.tabelaPrincipal.empilharEscopo(ultimo.nomeDaVariavel);
                }
                break;
            case 8:
                //fecha chaves
                PainelPrincipal.tabelaPrincipal.desempilharEscopo();
                break;
            case 9:
                //--
                break;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                PainelPrincipal.tabelaPrincipal.criarBloco(token.getId(), token.getLexeme(), token.getPosition());
                break;
            case 17:
                //escreva
                PainelPrincipal.tabelaPrincipal.iniciarComandoEscrita(token.getPosition(), token.getId());
                break;
            case 18:
                //leia
                PainelPrincipal.tabelaPrincipal.iniciarComandoLeitura(token.getPosition(), token.getId());
                break;
            case 19:
                //para inicializar variavel na declaração
                Simbolos declarada = PainelPrincipal.tabelaPrincipal.getUltimoSimbolo();
                PainelPrincipal.tabelaPrincipal.buscarSimbolo(declarada.nomeDaVariavel).ifPresent(s -> s.inicializada = true);
                break;
            case 20:
                //para atribuir valores
                //Optional contém um valor ou está vazio
                Optional<Simbolos> o = PainelPrincipal.tabelaPrincipal.buscarSimboloPercorrendoPilha(ultimoNome);
                if (o.isPresent()) {
                    Simbolos s = o.get();
                    s.inicializada = true;
                    s.usada = true;
                } else {
                    throw new SemanticError("A variável " + ultimoNome + " não existe!", token.getPosition());
                }
                break;
                
        }
    }	
}
