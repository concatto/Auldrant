//Universidade do Vale do Itajaí
//Ciência da Computação - 3º Periodo  2014/1
//Professor: Rafael de Santiago
//Acadêmicos: Guilherme Gustavo Gohr Darosci &
//            João Felipe Gonçalves

#include "Cartas.h"

using namespace std;

int main() {
	srand(time(NULL));
	TListaCartas jogadorUm, jogadorDois, jogadorTres, jogadorQuatro;
	TListaCartas mesa;
	inicializaLDE(jogadorUm);
	inicializaLDE(jogadorDois);
	inicializaLDE(jogadorTres);
	inicializaLDE(jogadorQuatro);
	inicializaLDE(mesa);

	//Gerando os deck
	TListaCartas deckCartas;

	inicializaLDE(deckCartas);
	geraCartas(deckCartas);

	embaralhar(deckCartas);

	distribuiCartas(deckCartas, jogadorUm, 2);
	distribuiCartas(deckCartas, jogadorDois, 2);
	distribuiCartas(deckCartas, jogadorTres, 2);
	distribuiCartas(deckCartas, jogadorQuatro, 2);
	distribuiCartas(deckCartas, mesa, 5);
	imprimirLDE(mesa);

	//organizarMesa(mesa);

	cout << "\nMesa:\n";
	imprimirLDE(mesa);


	cout << "\nJogador 1:\n";
	imprimirLDE(jogadorUm);
	compara(mesa, jogadorUm);

	cout << "\n\nJogador 2:\n";
	imprimirLDE(jogadorDois);
	compara(mesa, jogadorDois);

	cout << "\n\nJogador 3:\n";
	imprimirLDE(jogadorTres);
	compara(mesa, jogadorTres);

	cout << "\n\nJogador 4:\n";
	imprimirLDE(jogadorQuatro);
	compara(mesa, jogadorQuatro);

	cout << "\n\nPressione ENTER para continuar...";
	getch();
}
