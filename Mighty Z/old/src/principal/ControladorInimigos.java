package principal;

import java.awt.Graphics2D;

import inimigos.*;

public class ControladorInimigos {
	private Inimigo zyght;
	
	private int x;
	//Os eventos são relativos ao canto direito, ou seja, ao fim do mapa.
	private int eventoZyght = 900;
	
	private boolean permissaoZyght = true;
	private boolean[] instrucoes;

	public ControladorInimigos() {
		instrucoes = new boolean[2];
	}
	
	public boolean[] deveDesenhar() {
		return instrucoes;
	}
	
	public void definirLocal(int posicao) {
		this.x = posicao;
	}
	
	public int getX() {
		return x;
	}
	
	public void verificarEventos() {
		if (x > eventoZyght && zyght == null && permissaoZyght) {
			criarZyght();
			permissaoZyght = false;
		} else if (x < eventoZyght) {
			permissaoZyght = true;
		}
		
		atualizarInstrucoes();
		atualizarVida();
	}
	
	public void verificarSabre(int jogadorX, int jogadorY, int jogadorAltura) {
		if (zyght != null) {
			int impactoDireita = 1050;
			int impactoEsquerda = 1210;
			if (jogadorY > zyght.getY() && jogadorY - jogadorAltura < zyght.getY() + zyght.getAltura()) {
				if (Jogador.isMoveDireita() && jogadorX - zyght.getX() < impactoDireita && jogadorX - zyght.getX() > impactoDireita - (zyght.getLargura() * 4)) {
					zyght.receberDano(8);
				}
				if (!Jogador.isMoveDireita() && jogadorX - zyght.getX() < impactoEsquerda && jogadorX - zyght.getX() > impactoEsquerda - (zyght.getLargura() * 4)) {
					zyght.receberDano(8);
				}
			}
		}
	}
	
	public boolean verificarProjeteis(int jogadorX, int projX, int projY, int largProjetil, int altuProjetil, int nivel, boolean moveDireita) {
		if (zyght != null) {
			int direita = projX + (largProjetil + 30);
			int esquerda = projX + (largProjetil - 30);
			if (nivel != 4) {
				zyght.cancelarRejeicoes();
			}
			if (projY + altuProjetil > zyght.getY() && projY < zyght.getY() + zyght.getAltura()) {
				if (moveDireita && direita > zyght.getX() && (jogadorX - 100) - zyght.getX() < eventoZyght && !zyght.isRejeitado()) {
					zyght.receberDano(nivel * 2);
					if (nivel == 4) {
						zyght.rejeitarDano();
					}
					return true;
				}
				if (!moveDireita && esquerda < zyght.getX() && (jogadorX - 200) - zyght.getX() > eventoZyght) {
					zyght.receberDano(nivel * 2);
					if (nivel == 4) {
						zyght.rejeitarDano();
					}
					return true;
				}
			} else {
				
			}
			return false;
		}
		return false;
	}
	
	
	
	public void desenharZyght(Graphics2D g) {
		if (zyght != null) {
			zyght.setX(x - eventoZyght);
			g.drawString(String.valueOf(zyght.getVida()), zyght.getX(), zyght.getY() - 10);
			g.drawImage(zyght.getImage(), zyght.getX(), zyght.getY(), null);
		}
	}
	
	public void atualizarVida() {
		if (zyght != null) {
			if (zyght.getVida() < 1) {
				zyght = null;
			}
		}
	}
	
	private void criarZyght() {
		zyght = new Zyght(20, x - eventoZyght, 339);
	}
	
	private void atualizarInstrucoes() {
		instrucoes[0] = (zyght != null);
	}
}
