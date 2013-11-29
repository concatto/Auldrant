package principal;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class Cenario extends JFrame implements Runnable{
	private JPanel chao = new JPanel();
	private JPanel ceu = new JPanel();
	private JLabel tutorial = new JLabel("<html>As setas direcionais movem o personagem.<br/>A: Z-Buster<br/>S: Z-Saber<br/>Z: Pulo<br/>X: Dash</html>");
	private JLabel testeTransp = new JLabel("<html>Use isto para\t<br/>testar a transparência.\t</html");
	private Jogador jogador = new Jogador();
	private Projetil projetil[] = new Projetil[3];
	private Overlay overlay = new Overlay();
	private Color corCeu = new Color(121, 188, 255);
	private Thread thread = new Thread(this);

	private int iProjetil=0;
	private int quantProjeteis=0;
	
	private boolean comecaCarga = false;
	private boolean cargaAtiva = false;
	private boolean tiroProgramado = false;
	private boolean ativo = true;
	private boolean permiteAtirar = true;
	private boolean permiteDash = true;
	private boolean permiteTudo = false;

	private double gravidade = 17;
	private double removeGravidade = 2;
	
	private long tempoInicioTiro;
	private long tempoInicioCarga;
	private long tempoInicioDash;
	
	private Dimension tamanho = new Dimension(640, 480);
	
	public Cenario(){
//		try {
//			Audio tema = new Audio("resources\\themeShort.wav", Clip.LOOP_CONTINUOUSLY);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Mighty Z");
		setSize(tamanho);
		setLocationRelativeTo(null);
		setVisible(true);
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		ceu.setFocusable(false);
		ceu.setPreferredSize(new Dimension(tamanho.width, tamanho.height/2));
		ceu.setBackground(corCeu);
		ceu.setLayout(new GridBagLayout());
				
		GridBagConstraints lay = new GridBagConstraints();
		lay.anchor=GridBagConstraints.NORTHWEST;
		lay.weightx=1;
		lay.weighty=1;
		
		this.add(ceu);
		
		jogador.setFocusable(false);
		jogador.setPreferredSize(new Dimension(jogador.getLargura(), jogador.getAltura()));
		ceu.add(jogador, lay);
		
		lay.anchor=GridBagConstraints.NORTHWEST;
		ceu.add(tutorial, lay);
		
		lay.anchor=GridBagConstraints.SOUTHWEST;
		ceu.add(testeTransp, lay);
		
		chao.setFocusable(false);
		chao.setBackground(Color.DARK_GRAY);
		chao.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		chao.setPreferredSize(new Dimension(tamanho.width, tamanho.height/8));
		this.add(chao);
		
		this.requestFocusInWindow();
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case 39:
					if (!jogador.isPulando() && !jogador.isDashing() && !jogador.isSabre() && !jogador.isTeleportando() && !jogador.isMaterializando())  jogador.setParado();
					jogador.setMovendo(false);
					break;
				case 37:
					if (!jogador.isPulando() && !jogador.isDashing() && !jogador.isSabre() && !jogador.isTeleportando() && !jogador.isMaterializando()) jogador.setParado();
					jogador.setMovendo(false);
					break;
				case 65:
					permiteAtirar=true;
					if (cargaAtiva && comecaCarga) {
						cargaAtiva=false;
						comecaCarga=false;
						ceu.remove(overlay);
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
				case 88:
					permiteDash=true;
					if (!jogador.isPulando() && !jogador.isSabre()){
						jogador.setDashing(false);
						if (jogador.isMovendo()){
							jogador.setMovendo();
						} else {
							jogador.setParado();
						}
					} else {
						
					}
					break;				
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (permiteTudo){
					switch (e.getKeyCode()){
					case KeyEvent.VK_RIGHT:
						if(!jogador.isPulando() && !jogador.isDashing()) jogador.setMovendo();
						jogador.setMovendo(true);
						jogador.setMoveDireita(true);
						break;
					case KeyEvent.VK_LEFT:
						if(!jogador.isPulando() && !jogador.isDashing()) jogador.setMovendo();
						jogador.setMovendo(true);
						jogador.setMoveDireita(false);
						break;
					case KeyEvent.VK_X:
						if(permiteDash){
							permiteDash=false;
							tempoInicioDash = System.nanoTime();
							if(!jogador.isPulando()){
								jogador.setDashing(true);
								jogador.anim.setFrameAtual(0);
								if (jogador.isAtirando()){
									jogador.setDashAtirando();
								} else {
									jogador.setDashing();
								}
							}
						}
						break;
					case KeyEvent.VK_Z:
						if (!jogador.isCaindo()) jogador.setSubindo();
	//					if (!jogador.isPulando()) {
	//						try {
	//							Audio jumpStart = new Audio("resources\\jumpstart.wav", 0);
	//						} catch (Exception e1) {
	//							// TODO Auto-generated catch block
	//							e1.printStackTrace();
	//						}
	//					}
						jogador.setPulando(true);
						break;
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
							if(jogador.isCaindo()){
								jogador.setCaiAtirando();
							}
							if(jogador.isPulando()){
								jogador.setSobeAtirando();
							} else {
								if(jogador.isDashing()){
									jogador.setDashAtirando();
								} else if (jogador.isMovendo()){
									jogador.setMoveAtirando();
								} else {
									jogador.setAtirando();
								}
							}
							if (Jogador.isMoveDireita()) {
								projetil[iProjetil].setMoveDireita(true);
							} else {
								projetil[iProjetil].setMoveDireita(false);
							}
							if (jogador.getNivelCarga()==0) projetil[iProjetil].setTiroA();
							if (jogador.getNivelCarga()==1)	projetil[iProjetil].setTiroB();
							if (jogador.getNivelCarga()==2) projetil[iProjetil].setTiroC();
							if (tiroProgramado) {
								tiroProgramado=false;
								jogador.setNivelCarga(0);
							}
							projetil[iProjetil].setAtivo(true);
							if (!jogador.isDashing()) {
								if (jogador.isPulando() || jogador.isCaindo()){
									projetil[iProjetil].setLocation(jogador.getX(), jogador.getY() + 29);
								} else {
									projetil[iProjetil].setLocation(jogador.getX(), jogador.getY() + 39);
								}
							} else {
								projetil[iProjetil].setLocation(jogador.getX(), jogador.getY() + 47);
							}
							ceu.add(projetil[iProjetil]);
							jogador.setCarregando(true);
							jogador.setAtirando(true);
						}
						break;
					case KeyEvent.VK_S:
						if(!jogador.isAtirando()){
							permiteTudo=false;
							if (jogador.isPulando()){
								jogador.setSabrePulando();
							} else {
								jogador.setSabre();
								jogador.setMovendo(false);
							}
							jogador.anim.setFrameAtual(0);
							if(!jogador.isPulando()) jogador.setDashing(false);
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
			if (jogador.isTeleportando()){
				jogador.setLocation(jogador.getX(), jogador.getY()+16);
			}
			
			if (jogador.isMaterializando() && !jogador.anim.isAcontecendo()){
				jogador.setMaterializando(false);
				permiteTudo=true;
				jogador.setParado();
			}
			
			if (jogador.getY() > tamanho.height/2+chao.getHeight() && !jogador.isMaterializando() && jogador.isTeleportando()){
				jogador.setTeleportando(false);
				jogador.setMaterializando(true);
				jogador.setMaterializando();
			}
			
			if (jogador.isPulando()){
				if (gravidade < 1){
					jogador.setCaindo(true);
					if (!jogador.isSabre()){
						if (!jogador.isAtirando()){
							jogador.setCaindo();
						} else {
							jogador.setCaiAtirando();
						}
					}
					removeGravidade = removeGravidade*1.1;
					gravidade = gravidade-removeGravidade;
					jogador.setLocation(jogador.getX(), jogador.getY()-(int)gravidade);
					if (gravidade < -16){
						jogador.setCaindo(false);
						jogador.setDashing(false);
						jogador.setLocation(jogador.getX(), jogador.getY()+5);
						if(!jogador.isSabre()){
							if(jogador.isMovendo()) jogador.anim.setFrameFinal(0);
							jogador.setChegaChao();
							jogador.setChegandoChao(true);
						} else {
							if(jogador.anim.isAcontecendo()){
								int frame=jogador.anim.getFrameAtual();
								jogador.setSabre();
								jogador.anim.setFrameAtual(frame);
							} else {
								permiteTudo=true;
								jogador.setSabre(false);
								jogador.setParado();
							}
							jogador.setMovendo(false);
						}
						gravidade = 17;
						removeGravidade = 2;
						jogador.setAtirando(false);
						jogador.setPulando(false);
//						try {
//							Audio jumpEnd = new Audio("resources\\jumpend.wav", 0);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
					}
				} else{
					if (jogador.isAtirando()) jogador.setSobeAtirando();
					removeGravidade = removeGravidade/1.1;
					gravidade = gravidade-removeGravidade;
					jogador.setLocation(jogador.getX(), jogador.getY()-(int)gravidade);
				}
			}
			
			if (jogador.isChegandoChao() && jogador.anim.getFrameAtual() == 1){
				jogador.setChegandoChao(false);
				if (jogador.isMovendo()){
					jogador.setMovendo();
				} else {
					jogador.setParado();
				}
			}
			
			if (jogador.isMovendo() && !jogador.isDashing()){
				if(Jogador.isMoveDireita()){
					jogador.setLocation(jogador.getX()+2, jogador.getY());
				} else{
					jogador.setLocation(jogador.getX()-2, jogador.getY());
				}
			}
			
			if (jogador.isDashing()){
				if (((System.nanoTime() - tempoInicioDash) / 1000000L) > 450 && !jogador.isPulando()){
					if (jogador.isMovendo()){
						jogador.setMovendo();
					} else {
						jogador.setParado();
					}
					jogador.setDashing(false);
				}
				if(Jogador.isMoveDireita()){
					jogador.setLocation(jogador.getX()+10, jogador.getY());
				} else{
					jogador.setLocation(jogador.getX()-10, jogador.getY());
				}
			}
			
			if (!jogador.anim.isAcontecendo() && jogador.isSabre()){
				permiteTudo=true;
				jogador.setSabre(false);
				jogador.setParado();
			}
			
			if (jogador.isCarregando()) {
				if (cargaAtiva) {
					overlay.setLocation(jogador.getX()+(jogador.getLargura()-overlay.getWidth())/2, jogador.getY()+jogador.getAltura()-overlay.getHeight());
					overlay.atualizar();
				}
				if (((System.nanoTime() - tempoInicioCarga) / 1000000L) > 1400 && jogador.getNivelCarga() < 2) {
					if (!comecaCarga) {
						try {
							overlay.setLocation(jogador.getX()+(jogador.getLargura()-overlay.getWidth())/2, jogador.getY()+jogador.getAltura()-overlay.getHeight());
							ceu.add(overlay);
							ceu.setComponentZOrder(overlay, 0);
							comecaCarga=true;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					cargaAtiva=true;
					jogador.setNivelCarga(jogador.getNivelCarga()+1);
					tempoInicioCarga = System.nanoTime();
				}
			}
			
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
					if (projetil[i].isMoveDireita()) {
						projetil[i].setLocation(projetil[i].getX() + (16 - (jogador.getNivelCarga() * 2)), projetil[i].getY());
						if (projetil[i].getX() > tamanho.width) {
							projetil[i].setAtivo(false);
							//ceu.remove(projetil[iProjetil]);
							quantProjeteis--;
						}
					} else {
						projetil[i].setLocation(projetil[i].getX() - (16 - (jogador.getNivelCarga() * 2)), projetil[i].getY());
						if (projetil[i].getX() < 0 - projetil[i].getWidth()) {
							projetil[i].setAtivo(false);
							//ceu.remove(projetil[iProjetil]);
							quantProjeteis--;
						}
					}
				}
			}
			
			if (jogador.isAtirando()) {
				if (((System.nanoTime() - tempoInicioTiro) / 1000000L) > 400){
					jogador.setAtirando(false);
					if (jogador.isMovendo()){
						if (jogador.isPulando()){
							jogador.setSubindo();
						} else {
							jogador.setMovendo();
						}
					} else if (jogador.isDashing()){
						jogador.setDashing();
					} else {
						jogador.setParado();
					}
					if (jogador.isCaindo()){
						jogador.setCaindo();
					}
				}
			}
			
			try {
				Thread.sleep(19);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			jogador.atualizar();
			repaint();
			requestFocusInWindow();
//			jogador.setLocation(jogador.getX()+1, jogador.getY());	//R.I.P. Gambiarra
//			jogador.setLocation(jogador.getX()-1, jogador.getY());	//Força atualizações do painel a cada 20 milissegundos
		}
	}
}

