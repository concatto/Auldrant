//============================================================================
// Name        : Hello.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================
#include <iostream>
using namespace std;

int main(){

	int tamanho;

    cout << "Por favor, informe o tamanho que deseja ter o seu triângulo de Pascal.";
    cin >> tamanho;
//    int tamanho2 = tamanho;
//    int **tamanhomatriz = new int*[tamanho]; // Estou alocando e criando uma matriz nova.
//
//    for (int i = 0; i < tamanho; i++){
//        tamanhomatriz[i] = new int[tamanho2];
//    }
//
//    for (int i = 0; i < tamanho; i++){
//        tamanhomatriz[i] = new int[tamanho];
//    }
    int tamanhomatriz[][tamanho];

    //Neste bloco, o triângulo de pascal é formado.
     for (int i = 0; i<tamanho; tamanho++){
        for (int j = 0; j<= tamanho; j++){
            if ((j == 0) || (tamanho == i)){
                tamanhomatriz[i][j] = 1;
            }else{
                tamanhomatriz[i][j] = tamanhomatriz[i-1][j] + tamanhomatriz[i-1][j-1];
            }
        }
    }

    //Exibindo na tela
    for (int i = 0; i<tamanho; i++){
        for (int j = 0; (j<=tamanho); j++){
            cout << tamanhomatriz[i][j] << " ";
        }
        cout << "\n";
    }
}
