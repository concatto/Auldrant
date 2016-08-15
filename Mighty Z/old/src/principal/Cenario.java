package principal;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Cenario extends JPanel implements Runnable{
	private Dimension tamanho = new Dimension(640, 480);
	private Jogador jogador = new Jogador();
	private Projetil projetil[] = new Projetil[3];
	private Overlay overlay = new Overlay();
	private TileMap tm = new TileMap("resources\\Mapa.txt", 32);
	private ControladorInimigos ctrlInimigos = new ControladorInimigos();
	private Thread thread = new Thread(this);
	private BufferedImage imagem;
	private Graphics2D g;

	private int iProjetil=0;
	private int quantProjeteis=0;
	
	private boolean ativo = true;
	private boolean inicioQueda = false;
	private boolean comecaCarga = false;
	private boolean cargaAtiva = false;
	private boolean tiroProgramado = false;
	private boolean botaoPuloApertado = false;
	private boolean permiteAtirar = true;
	private boolean permiteDash = true;
	private boolean permiteTudo = false;
	private boolean direitaPressionada = false;
	private boolean esquerdaPressionada = false;
	
	private int metadeCenario;
	
	private boolean desenharOverlay = false;
	private boolean[] desenharProjetil = new boolean[3];
	
	private final int gravidadeInicial = 18;
	private final int gravidadeMaxima = 15;
	private final double gravidadeSecundariaInicial = 1.4;
	private double gravidade = gravidadeInicial;
	private double gravidadeSecundaria = gravidadeSecundariaInicial;
	
	private long tempoInicioTiro;
	private long tempoInicioCarga;
	private long tempoInicioDash;
	
	public Cenario() {
//		try {
//			Audio tema = new Audio("resources\\themeShort.wav", Clip.LOOP_CONTINUOUSLY);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		
		imagem = new BufferedImage(tamanho.width, tamanho.height, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) imagem.getGraphics();
		
		setFocusable(true);
		setSize(tamanho);
		setBackground(new Color(0, 255, 0, 0));
		
		this.requestFocusInWindow();
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {

			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case 39:	//Botão "RIGHT"
					if (!esquerdaPressionada) {
						if (!jogador.isPulando() && !jogador.isDashing() && !jogador.isSabre() && !jogador.isTeleportando() && !jogador.isMaterializando() && !jogador.isCaindo()) {
							jogador.setAniParado();
						}
						jogador.setMovendo(false);
					} else {
						jogador.setMoveDireita(false);
					}
					direitaPressionada = false;
					break;
				case 37:	//Botão "LEFT"
					if (!direitaPressionada) {
						if (!jogador.isPulando() && !jogador.isDashing() && !jogador.isSabre() && !jogador.isTeleportando() && !jogador.isMaterializando() && !jogador.isCaindo()) {
							jogador.setAniParado();
						}
						jogador.setMovendo(false);
					} else {
						jogador.setMoveDireita(true);
					}
					esquerdaPressionada = false;
					break;
				case 65:	//Botão "A"
					permiteAtirar=true;
					if (cargaAtiva && comecaCarga) {
						cargaAtiva=false;
						comecaCarga=false;
						desenharOverlay=false;
					}
					if (jogador.getNivelCarga() > 0) {
						try {
							Robot robot = new Robot();
							robot.keyPress(KeyEvent.VK_A);
							tiroProgramado=true;
							robot.keyRelease(KeyEvent.VK_A);
						} catch (AWTException e1) {
							e1.printStackTrace();
						}
					}
					jogador.setCarregando(false);
					break;
				case 88:	//Botão "X"
					permiteDash=true;
					if (!jogador.isPulando() && !jogador.isSabre()){
						jogador.setDashing(false);
						if (jogador.isMovendo()){
							jogador.setAniMovendo();
						} else {
							jogador.setAniParado();
						}
					}
					break;
				case 90:	//Botão "Z"
					botaoPuloApertado = false;
					break;
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (permiteTudo){
					switch (e.getKeyCode()){
					//Movimento (direita)
					case KeyEvent.VK_RIGHT:
						if(!jogador.isPulando() && !jogador.isDashing() && !jogador.isCaindo()) jogador.setAniMovendo();
						jogador.setMovendo(true);
						jogador.setMoveDireita(true);
						direitaPressionada = true;
						break;
					//Movimento (esquerda)
					case KeyEvent.VK_LEFT:
						if(!jogador.isPulando() && !jogador.isDashing() && !jogador.isCaindo()) jogador.setAniMovendo();
						jogador.setMovendo(true);
						jogador.setMoveDireita(false);
						esquerdaPressionada = true;
						break;
					//Dash
					case KeyEvent.VK_X:
						if(permiteDash && !jogador.isCaindo()){
							permiteDash=false;
							tempoInicioDash = System.nanoTime();
							if(!jogador.isPulando()){
								jogador.setDashing(true);
								jogador.anim.setFrameAtual(0);
								if (jogador.isAtirando()){
									jogador.setAniDashAtirando();
								} else {
									jogador.setAniDashing();
								}
							}
						}
						break;
					//Pulo
					case KeyEvent.VK_Z:
						botaoPuloApertado = true;
						if (!jogador.isCaindo() && !jogador.isChegandoChao()){ 
							jogador.setAniSubindo();
							jogador.setPulando(true);
						}
						break;
					//Tiro
					case KeyEvent.VK_A:
						if(permiteAtirar){
							for (int i = 0; i < projetil.length; i++) {
								if (!projetil[i].isAtivo()){
									iProjetil = i;
									quantProjeteis++;
									break;
								}
							}
							tempoInicioTiro = System.nanoTime();
							tempoInicioCarga = System.nanoTime();
							permiteAtirar = false;
							if(jogador.isPulando() && !jogador.isCaindo()){
								jogador.setAniSobeAtirando();
							} else if (jogador.isCaindo()){
								jogador.setAniCaiAtirando();
							} else {
								if(jogador.isDashing()){
									jogador.setAniDashAtirando();
								} else if (jogador.isMovendo()){
									jogador.setAniMoveAtirando();
								} else {
									jogador.setAniAtirando();
								}
							}
							if (Jogador.isMoveDireita()) {
								projetil[iProjetil].setMoveDireita(true);
							} else {
								projetil[iProjetil].setMoveDireita(false);
							}
							switch (jogador.getNivelCarga()) {
							case 0:
								projetil[iProjetil].setTiroA();
								break;
							case 1:
								projetil[iProjetil].setTiroB();
								break;
							case 2:
								projetil[iProjetil].setTiroC();
								break;
							}
							if (tiroProgramado) {
								tiroProgramado=false;
								jogador.setNivelCarga(0);
							}
							projetil[iProjetil].setAtivo(true);
							int posX = jogador.getGrafX() - 10;
							int auxPosX = (Jogador.isMoveDireita()) ? 21 : -21;
							int posY = jogador.getY() - jogador.getAltura();
							if (!jogador.isDashing()) {
								if (jogador.isPulando() || jogador.isCaindo()){
									projetil[iProjetil].setLocation(posX, posY + 29);
								} else {
									projetil[iProjetil].setLocation(posX, posY + 39);
								}
							} else {
								projetil[iProjetil].setLocation(posX + auxPosX, posY + 47);
							}
							projetil[iProjetil].desenhar(g);
							jogador.setCarregando(true);
							jogador.setAtirando(true);
						}
						break;
					//Sabre
					case KeyEvent.VK_S:
						if(!jogador.isAtirando()){
							permiteTudo=false;
							if (jogador.isPulando()){
								jogador.setAniSabrePulando();
							} else {
								jogador.setAniSabre();
								jogador.setMovendo(false);
							}
							jogador.anim.setFrameAtual(0);
							if (!jogador.isPulando()) jogador.setDashing(false);
							jogador.setSabre(true);
						}
						break;
					}
				}
			}
		});
		
		for (int i = 0; i < projetil.length; i++) {
			projetil[i] = new Projetil();
		}
		
		thread.start();
	}
	
	@Override
	public void run() {
		while (ativo){
			jogador.atualizar();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, tamanho.width, tamanho.height);
			tm.desenhar(g);
			jogador.desenhar(g);
			if (desenharOverlay) overlay.desenhar(g);
			
			metadeCenario = tm.getTamanhoTile() * 8;
			
			jogador.setExtremidadeEsquerda((int)Math.ceil(jogador.getOffsetEsquerda()/tm.getTamanhoTile()));
			jogador.setExtremidadeDireita((int)Math.ceil(jogador.getOffsetDireita()/tm.getTamanhoTile()));
			jogador.setExtremidadeInferior((int)Math.ceil((jogador.getY() - 1)/tm.getTamanhoTile()));
			jogador.setExtremidadeSuperior((int)Math.ceil(jogador.getY() - jogador.getHitAltura()/tm.getTamanhoTile()));
			
			int limiteDireito = tm.getLargPixels() - metadeCenario - jogador.getLargura();
			
			//Scrolling da tela
			if (jogador.getX() >= metadeCenario && jogador.getX() <= limiteDireito) {
				jogador.setRelativoCenario(true);
				jogador.setGrafX(metadeCenario);
				tm.setX(metadeCenario - jogador.getX());
			} else {
				jogador.setRelativoCenario(false);
				if (jogador.getX() >= limiteDireito) {
					jogador.setGrafX(jogador.getX() - (tm.getLargPixels() - (metadeCenario*2) - jogador.getLargura()));
				} else {
					jogador.setGrafX(jogador.getX());
				}
			}
			
			controle();
			
			//Triggers
			ctrlInimigos.verificarEventos();
			
			//Evento de colisão do projétil com o inimigo
			for (int i = 0; i < projetil.length; i++) {
				if (projetil[i].isAtivo()) {
					//Truque: como o método verificarProjeteis retorna true ou false dependendo do resultado do tiro, é possível acoplar ao if.
					if (ctrlInimigos.verificarProjeteis(jogador.getX(), projetil[i].getX(), projetil[i].getY(), projetil[i].getLargura(), projetil[i].getAltura(), projetil[i].getNivel(), projetil[i].isMoveDireita())) {
						projetil[i].setAtivo(false);
						desenharProjetil[i] = false;
						quantProjeteis--;
					}
				}
			}
			
			//Evento de colisão do sabre com o inimigo
			if (jogador.isSabre() && jogador.anim.getFrameAtual() > 4) {
				ctrlInimigos.verificarSabre(jogador.getX(), jogador.getY(), jogador.getHitAltura());
			}
			
			//Recebimento de instruções de desenho
			boolean[] instrucoes = ctrlInimigos.deveDesenhar();
			
			//Uso das instruções recebidas
			if (instrucoes[0]) ctrlInimigos.desenharZyght(g);
			
			//Passagem das localizações para o Controlador
			if (jogador.isRelativoCenario()) {
				ctrlInimigos.definirLocal(tm.getLargPixels() - jogador.getX());
			} else {
				ctrlInimigos.definirLocal(ctrlInimigos.getX());
			}
			
			//Os projéteis ficarão na camada do topo
			for (int i = 0; i < desenharProjetil.length; i++) {
				if (desenharProjetil[i]) projetil[i].desenhar(g);
			}
			
//			Graphics g2 = getGraphics();
//			g2.drawImage(imagem, 0, 0, null);
//			g2.dispose();
			
			repaint();
			
			requestFocusInWindow();
			
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(imagem, 0, 0, null);
	}
	
	//Método usado nos dois eventos de queda.
	private void cair() {
		jogador.setCaindo(true);
		if (!jogador.isSabre()){
			if (!jogador.isAtirando()){
				jogador.setAniCaindo();
			} else {
				jogador.setAniCaiAtirando();
			}
		}
	}
	
	private void controle() {
		//Primeira parte da sequência inicial.
		if (jogador.isTeleportando()){
			jogador.setY(jogador.getY()+15);
		}
		
		//Segunda parte da sequência inicial.
		if (!jogador.isMaterializando() && jogador.isTeleportando() && tm.hasColisaoAbaixo(jogador.getExtremidadeInferior(), jogador.getExtremidadeEsquerda(), jogador.getExtremidadeDireita())){
			jogador.setY(jogador.procurarChao(tm.getTamanhoTile()));
			jogador.setTeleportando(false);
			jogador.setMaterializando(true);
			jogador.setAniMaterializando();
		}
		
		//Término da sequência inicial.
		if (jogador.isMaterializando() && jogador.anim.getFrameAtual() == jogador.anim.getFrameFinal()-1){
			jogador.setMaterializando(false);
			permiteTudo=true;
			jogador.setAniParado();
		}
		
		//Controle de descida de Tiles
		if (!jogador.isTeleportando() && !jogador.isPulando() && !tm.hasColisaoAbaixo(jogador.getExtremidadeInferior(), jogador.getExtremidadeEsquerda(), jogador.getExtremidadeDireita())) {
			if (inicioQueda) {
				gravidadeSecundaria=1.4;
				inicioQueda = false;
			}
			cair();
			if (gravidadeSecundaria < gravidadeMaxima) {
				gravidadeSecundaria = gravidadeSecundaria + (gravidadeSecundaria*0.22);
			}
			jogador.setY(jogador.getY() + (int)gravidadeSecundaria);
		} else if (!jogador.isTeleportando() && !jogador.isPulando()){
			jogador.setY(jogador.procurarChao(tm.getTamanhoTile()));
			if (jogador.isCaindo()){
				jogador.setAniChegaChao();
				jogador.setChegandoChao(true);
				jogador.setCaindo(false);
			}
			gravidadeSecundaria = 1.4;
			inicioQueda = true;
		}
		
		//Gravidade e permissões ao pular.
		if (jogador.isPulando()){
			if (gravidade < 1 || jogador.isCaindo() || !botaoPuloApertado){		//Momento de chegada no topo do pulo
				if (inicioQueda) {
					gravidade = gravidadeSecundaria;
					inicioQueda = false;
				}
				cair();
				if (gravidade < gravidadeMaxima) {
					gravidade = gravidade + (gravidade * 0.22);
				}
				jogador.setY(jogador.getY() + (int)gravidade);
				if (tm.hasColisaoAbaixo(jogador.getExtremidadeInferior(), jogador.getExtremidadeEsquerda(), jogador.getExtremidadeDireita())){
					jogador.setCaindo(false);
					jogador.setDashing(false);
					if(!jogador.isSabre()){
						if(jogador.isMovendo()) jogador.anim.setFrameFinal(0);
						jogador.setAniChegaChao();
						jogador.setChegandoChao(true);
					} else {
						if(jogador.anim.isAcontecendo()){
							int frame=jogador.anim.getFrameAtual();
							jogador.setAniSabre();
							jogador.anim.setFrameAtual(frame);
						} else {
							permiteTudo=true;
							jogador.setSabre(false);
							jogador.setAniParado();
						}
						jogador.setMovendo(false);
					}
					gravidade = gravidadeInicial;
					jogador.setAtirando(false);
					jogador.setPulando(false);
					jogador.setY(jogador.procurarChao(tm.getTamanhoTile()));
//					try {
//						Audio jumpEnd = new Audio("resources\\jumpend.wav", 0);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
				}
			} else if (!jogador.isCaindo()){	//Enquanto está subindo
				if (jogador.isAtirando()) jogador.setAniSobeAtirando();
				gravidade = gravidade - (gravidade*0.12);
				jogador.setY(jogador.getY() - (int)gravidade);
			} else {
				inicioQueda = true;
			}
		}
		
		//Transição ao chegar no chão.
		if (jogador.isChegandoChao() && jogador.anim.getFrameAtual() == 1){
			jogador.setChegandoChao(false);
			jogador.setDashing(false);
			if (jogador.isMovendo()){
				jogador.setAniMovendo();
			} else {
				jogador.setAniParado();
			}
		}

		//Colisão e elocidade de movimento.
		if (jogador.isMovendo() && !jogador.isDashing()) {
			if (Jogador.isMoveDireita() && !tm.hasColisaoDireita(jogador.getExtremidadeInferior(), jogador.getExtremidadeDireita())) {
				jogador.setX(jogador.getX()+3);
			} 
			if (!Jogador.isMoveDireita() && !tm.hasColisaoEsquerda(jogador.getExtremidadeInferior(), jogador.getExtremidadeEsquerda())) {
				jogador.setX(jogador.getX()-3);
			}
		}

		//Colisão, limite de distância e velocidade do dash.
		if (jogador.isDashing()){
			if (((System.nanoTime() - tempoInicioDash) / 1000000L) > 450 && !jogador.isPulando()){
				if (jogador.isMovendo()){
					jogador.setAniMovendo();
				} else {
					jogador.setAniParado();
				}
				jogador.setDashing(false);
			}
			if(Jogador.isMoveDireita() && !tm.hasColisaoDireita(jogador.getExtremidadeInferior(), jogador.getExtremidadeDireita())){
				jogador.setX(jogador.getX()+10);
			}
			if (!Jogador.isMoveDireita() && !tm.hasColisaoEsquerda(jogador.getExtremidadeInferior(), jogador.getExtremidadeEsquerda())) {
				jogador.setX(jogador.getX()-10);
			}
		}
			
		//Remoção de todas as permissões ao usar o sabre.
		if (jogador.isSabre() && !jogador.anim.isAcontecendo()){
			permiteTudo=true;
			jogador.setSabre(false);
			jogador.setAniParado();
		}
		
		//Condição de desenho e níveis de carga.
		if (jogador.isCarregando()) {
			if (cargaAtiva) {
				overlay.setLocation(jogador.getGrafX()+(jogador.getLargura()-overlay.getWidth())/2, jogador.getY()-overlay.getHeight());
				overlay.atualizar();
				desenharOverlay=true;
			}
			if (((System.nanoTime() - tempoInicioCarga) / 1000000L) > 1400 && jogador.getNivelCarga() < Projetil.getCargaMaxima()) {
				if (!comecaCarga) {
					try {
						overlay.setLocation(jogador.getGrafX()+(jogador.getLargura()-overlay.getWidth())/2, jogador.getY()+jogador.getAltura()-overlay.getHeight());
						comecaCarga=true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				cargaAtiva=true;
				jogador.setNivelCarga(jogador.getNivelCarga()+1);
				tempoInicioCarga = System.nanoTime();
			}
		}
		
		//Condição de desenho e permissão dos projéteis.
		for (int i = 0; i < projetil.length; i++) {
			if (!jogador.isCarregando()) {
				if (quantProjeteis == 3) {
					permiteAtirar = false;
				} else {
					permiteAtirar = true;
				}
			}
			if (projetil[i].isAtivo()) {
				projetil[i].atualizar();
				desenharProjetil[i] = true;
				if (projetil[i].isMoveDireita()) {
					projetil[i].setLocation(projetil[i].getX() + (16 - (jogador.getNivelCarga() * 2)), projetil[i].getY());
				} else {
					projetil[i].setLocation(projetil[i].getX() - (16 - (jogador.getNivelCarga() * 2)), projetil[i].getY());
				}
				if (projetil[i].getX() > getWidth() - projetil[i].getLargura() || projetil[i].getX() < 0 - projetil[i].getLargura()) {
					projetil[i].setAtivo(false);
					desenharProjetil[i] = false;
					quantProjeteis--;
				}
			}
		}
		
		//Transição da animação de tiro.
		if (jogador.isAtirando()) {
			if (((System.nanoTime() - tempoInicioTiro) / 1000000L) > 400){
				jogador.setAtirando(false);
				if (jogador.isDashing() && !jogador.isPulando()){
					jogador.setAniDashing();
				} else if (jogador.isMovendo() && !jogador.isPulando()){
					jogador.setAniMovendo();
				} else if (jogador.isCaindo()){
					jogador.setAniCaindo();
				} else if (jogador.isPulando()){
					jogador.setAniSubindo();
				} else {
					jogador.setAniParado();
				}
			}
		}
	}
}
