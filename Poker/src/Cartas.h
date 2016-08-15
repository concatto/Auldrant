#ifndef CARTAS_H_INCLUDED
#define CARTAS_H_INCLUDED
#include <iostream>
#include <conio2.h>
#include <time.h>
#include <stdlib.h>
#include <math.h>
using namespace std;

const int TAM = 52;

struct TCarta {
	int valor;
	string naipe;
};

struct TElementoDE {
	TCarta carta;
	TElementoDE *anterior;
	TElementoDE *proximo;

};

struct TListaCartas {
	int qtd;
	TElementoDE *inicio;
	TElementoDE *fim;
};

void inicializaLDE(TListaCartas &lc) { //Inicializa a quantidade de cartas e posi√ß√£o como zero
	lc.inicio = lc.fim = NULL;
	lc.qtd = 0;
}

void insereFimCarta(TListaCartas &l, TCarta c) {
	TElementoDE *novo = new TElementoDE;
	TElementoDE *antigo = l.fim;

	novo->carta = c;
	novo->proximo = NULL;
	novo->anterior = l.fim;

	if (antigo != NULL) {
		antigo->proximo = novo;
	} else {
		l.inicio = novo;
	}

	l.fim = novo;         //O novo passa a ser o ultimo
	l.qtd++;
}

void removeNoMeio(TListaCartas &l, int posicaoRemover) {
	if (l.qtd > 0) {
		TElementoDE *aux, *aux2, *nav;
		nav = l.inicio; //Nav pega o inÌcio da lista, para poder percorrÍ-la
		for (int i = 0; i < posicaoRemover; i++) { //i percorrer· atÈ a posiÁ„o que deseja ser removida.
			aux = nav->anterior; //Uma vari·vel auxiliar receber· o anterior de nav.
			nav = nav->proximo; //Nav receber· seu prÛximo atÈ chegar a posiÁ„o desejada.
			aux2 = nav->proximo; //Uma outra vari·vel receber· o prÛximo de nav.
		}
		aux->proximo = aux2;
		/*Aux, que continha o valor que vinha antes de nav, receber· em seu atributo "prÛximo"
		 * o auxiliar 2, que contÈm o prÛximo valor de nav.

		 */
		aux2->anterior = aux; //O mesmo ocorre aqui, o anterior de aux2 receber· o valor anterior de nav.
		delete nav; //a memÛria est· sendo desalocada na ·rea onde nav est· (a posiÁ„o que a pessoa queria)

		l.qtd--;
	}
}

void removerFimLDE(TListaCartas &lc) { //Fun√ß√£o Remove carta pelo final do array
	if (lc.qtd > 0) {
		TElementoDE *antigo = lc.fim;
		lc.fim = antigo->anterior;

		delete antigo;
		lc.qtd--;

		if (lc.fim != NULL) {
			lc.fim->proximo = NULL;
		} else {
			lc.fim = NULL;
		}
	}
}

void insereNoMeio(TListaCartas &l, TCarta c, int index) {
	TElementoDE *nav = new TElementoDE;
	TElementoDE *prox = new TElementoDE;
	nav = l.fim;
	for (int i = 0; i < index - 2; i++) {
		nav = nav->anterior;
	}

	prox->anterior = nav->anterior;
	prox->proximo = nav;
	prox->carta = c;

	nav->anterior->proximo = prox->anterior;
	nav->anterior = prox;

	l.qtd++;
}

void insereInicioCarta(TListaCartas &l, TCarta c) {
	TElementoDE *novo = new TElementoDE;
	TElementoDE *antigo = l.inicio;

	novo->carta = c;   //Acomoda a musica
	novo->anterior = NULL;
	novo->proximo = l.inicio;

	l.qtd++;
	l.inicio = novo;

	if (l.qtd > 1) {
		antigo->anterior = novo;
	} else {
		l.fim = novo;
	}
}

/*void inserePosLE(TListaCartas &lc, TCarta c, int pos){  //Fun√ß√£o de inserir a carta na posi√ß√£o desejada
 if (lc.qtd < TAM and pos <= lc.qtd){
 for (int i = lc.qtd; i > pos; i--){
 lc.elemento[i] = lc.elemento[i-1];
 }
 lc.elemento[pos] = c;
 lc.qtd++;

 }
 }*/

void imprimirLDE(TListaCartas l, bool inverso = false) {
	TElementoDE *nav;

	if (inverso) {
		nav = l.fim;
	} else {
		nav = l.inicio;
	}

	while (nav != NULL) {
		cout << nav->carta.valor << " de " << nav->carta.naipe << "\n";

		if (inverso) {
			nav = nav->anterior;
		} else {
			nav = nav->proximo;
		}
	}
}

void geraCartas(TListaCartas &lc) {  //Fun√ß√£o Gera todas as cartas do deck
	for (int i = 0, j = 1; i < TAM; i++, j++) {
		TElementoDE *x = new TElementoDE;
		if (j == 14) {
			j = 1;
		}
		if (i < 13) {
			x->carta.naipe = "Paus";
		} else if (i > 12 and i < 26) {
			x->carta.naipe = "Ouro";
		} else if (i > 25 and i < 39) {
			x->carta.naipe = "Copas";
		} else if (i > 38 and i < TAM) {
			x->carta.naipe = "Espadas";
		}

		x->carta.valor = j;
		insereInicioCarta(lc, x->carta);
	}
}

void embaralhar(TListaCartas &lc) {
	TListaCartas listaTemporaria;
	inicializaLDE(listaTemporaria);
	insereInicioCarta(listaTemporaria, lc.fim->carta);
	while(listaTemporaria.qtd < 52) {
		int r = rand() % 51;
		TElementoDE *navegador;
		navegador = lc.fim;
		for (int j = 0; j <= r; j++) {
			navegador = navegador->anterior;
		}
		bool igual = false;
		TElementoDE *navegadorRepeticao;
		navegadorRepeticao = listaTemporaria.fim;
		for (int m = 0; m < listaTemporaria.qtd - 1; m++) {
			navegadorRepeticao = navegadorRepeticao->anterior;
			if (navegadorRepeticao->carta.valor == navegador->carta.valor and navegadorRepeticao->carta.naipe == navegador->carta.naipe) {
				igual = true;
				break;
			}
		}
		if (!igual) {
			insereInicioCarta(listaTemporaria, navegador->carta);
		}
	}
	lc = listaTemporaria;
}

void distribuiCartas(TListaCartas &crupie, TListaCartas &jogadorCartas,	int limite) {
	TElementoDE *aux = new TElementoDE;
	for (int i = 0; i < limite; i++) {
		aux->carta = crupie.fim->carta;
		insereInicioCarta(jogadorCartas, aux->carta);
		removerFimLDE(crupie);
	}
}

void removeInicioLDE(TListaCartas &l) {
	if (l.qtd > 0) {
		TElementoDE *antigo = l.inicio;

		l.inicio = antigo->proximo;
		delete antigo;
		l.qtd--;

		if (l.inicio != NULL)
			l.inicio->anterior = NULL;
		else
			l.fim = NULL;
	}
}

int valorDoNaipe(string naipe) {
	int expoente;
	if (naipe == "Paus") {
		expoente = 0;
	} else if (naipe == "Ouro") {
		expoente = 1;
	} else if (naipe == "Copas") {
		expoente = 2;
	} else {
		expoente = 3;
	}
	return pow(2, expoente);
}

bool arrayContem(int array[20], int valor) {
	for (int i = 0; i < 20; i++) {
		if (array[i] == valor) {
			return true;
		}
	}
	return false;
}

void compara(TListaCartas mesa, TListaCartas jogador){
	TListaCartas temp = mesa;
	insereFimCarta(temp, jogador.inicio->carta);
	insereFimCarta(temp, jogador.inicio->proximo->carta);
	int indiceArray = 0;
	int iguais[20] = {0};

	TElementoDE *navegador = temp.inicio;
	cout << "-----\n";
	int contadorPares = 0;
	int contadorTrincas = 0;
	for (int i = 0; i < temp.qtd; i++) {
		TElementoDE *navegadorSecundario = temp.inicio;
		int contador = 0;
		for (int j = 0; j < temp.qtd; j++) {
//			cout << navegador->carta.valor << "\t" << navegadorSecundario->carta.valor << endl;
			if (navegador->carta.valor == navegadorSecundario->carta.valor and navegador->carta.naipe != navegadorSecundario->carta.naipe) {
				int codigo = valorDoNaipe(navegador->carta.naipe) + valorDoNaipe(navegadorSecundario->carta.naipe);
//				cout << "Codigo:" << codigo << endl;
				if (!arrayContem(iguais, codigo)) {
					iguais[indiceArray] = codigo;
					indiceArray++;
					contador++;
				} else {
					break;
				}
			}
			navegadorSecundario = navegadorSecundario->proximo;
		}
		if (contador > 0) {
			contador++;
		}
		if (contador == 2) {
			contadorPares++;
		}
		if (contador == 3) {
			contadorTrincas++;
		}
		navegador = navegador->proximo;
	}
	if (contadorPares > 0) {
		cout << "Pares encontrados: " << contadorPares << endl;
	} else if (contadorTrincas > 0) {
		cout << "Trincas encontradas: " << contadorTrincas << endl;
	} else {
		cout << "Nenhum par ou trinca foram encontrados.\n";
	}
	cout << "-----\n";
}

#endif // CARTAS_H_INCLUDED
