package principal;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Jogador{
	private BufferedImage[] teleport;
	private BufferedImage[] materialize;
	private BufferedImage[] dash;
	private BufferedImage[] stand;
	private BufferedImage[] walk;
	private BufferedImage[] jump;
	private BufferedImage[] fall;
	private BufferedImage[] saber;
	private BufferedImage[] saberJumping;
	private BufferedImage[] reachGround;
	private BufferedImage[] shootWalking;
	private BufferedImage[] shootDashing;
	private BufferedImage[] shootJumping;
	private BufferedImage[] shootFalling;
	private BufferedImage[] shoot;
	private final int largura = 124;
	private final int altura = 77;
	private final int hitLargura = 40;
	private final int hitAltura = 48;
	
	Animacao anim = new Animacao();
	
	private int nivelCarga=0;
	private int grafX = 0;
	private int x = 0;
	private int y = 0;
	private int extremidadeEsquerda;
	private int extremidadeDireita;
	private int extremidadeInferior;
	private int extremidadeSuperior;
	
	private boolean teleportando=true;
	private boolean materializando=false;
	private boolean dashing=false;
	private boolean movendo=false;
	private boolean moveAtirando=false;
	private boolean pulando=false;
	private boolean caindo=false;
	private boolean sabre=false;
	private boolean atirando=false;
	private boolean carregando=false;
	private boolean chegandoChao=false;
	
	private static boolean moveDireita=true;
	private boolean relativoCenario=false;
	
	private long tempoInicio;
	
	public Jogador(){
		try {
			teleport = new BufferedImage[1];
			materialize = new BufferedImage[6];
			dash = new BufferedImage[7];
			stand = new BufferedImage[1];
			walk = new BufferedImage[10];
			jump = new BufferedImage[4];
			fall = new BufferedImage[3];
			saber = new BufferedImage[13];
			saberJumping = new BufferedImage[13];
			reachGround = new BufferedImage[2];
			shootWalking = new BufferedImage[10];
			shootDashing = new BufferedImage[7];
			shootJumping = new BufferedImage[4];
			shootFalling = new BufferedImage[3];
			shoot = new BufferedImage[4];
			
			teleport[0] = ImageIO.read(new File("resources\\start.1.new.gif"));
			
			BufferedImage superMaterialize = ImageIO.read(new File("resources\\start.2.new.gif"));
			for (int i = 0; i < materialize.length; i++) {
				materialize[i] = superMaterialize.getSubimage(i*largura, 0, largura, altura);
			}
			
			BufferedImage superDash = ImageIO.read(new File("resources\\dashing.new.gif"));
			for (int i = 0; i < dash.length; i++) {
				dash[i] = superDash.getSubimage(i*largura, 0, largura, altura);
			}
			
			stand[0] = ImageIO.read(new File("resources\\stand.new.gif"));
			
			BufferedImage superWalk = ImageIO.read(new File("resources\\walk.new.gif"));
			for (int i = 0; i < walk.length; i++) {
				walk[i] = superWalk.getSubimage(i*largura, 0, largura, altura);
			}
			
			BufferedImage superJump = ImageIO.read(new File("resources\\jumping.new.gif"));
			for (int i = 0; i < jump.length; i++) {
				jump[i] = superJump.getSubimage(i*largura, 0, largura, altura);
			}
			
			BufferedImage superFall = ImageIO.read(new File("resources\\falling.new.gif"));
			for (int i = 0; i < fall.length; i++) {
				fall[i] = superFall.getSubimage(i*largura, 0, largura, altura);
			}
			
			BufferedImage superSaber = ImageIO.read(new File("resources\\saber.new.gif"));
			for (int i = 0; i < saber.length; i++) {
				saber[i] = superSaber.getSubimage(i*largura, 0, largura, altura);
			}
			
			BufferedImage superSaberJump = ImageIO.read(new File("resources\\saber.jumping.gif"));
			for (int i = 0; i < saberJumping.length; i++) {
				saberJumping[i] = superSaberJump.getSubimage(i*largura, 0, largura, altura);
			}
			
			BufferedImage superReachGround = ImageIO.read(new File("resources\\reach.ground.gif"));
			for (int i = 0; i < reachGround.length; i++) {
				reachGround[i] = superReachGround.getSubimage(i*largura, 0, largura, altura);
			}
			
			BufferedImage superShootWalk = ImageIO.read(new File("resources\\shoot.walk.new.gif"));
			for (int i = 0; i < shootWalking.length; i++) {
				shootWalking[i] = superShootWalk.getSubimage(i*largura, 0, largura, altura);
			}
			
			BufferedImage superShootDash = ImageIO.read(new File("resources\\dashing.shooting.new.gif"));
			for (int i = 0; i < shootDashing.length; i++) {
				shootDashing[i] = superShootDash.getSubimage(i*largura, 0, largura, altura);
			}

			BufferedImage superShootJump = ImageIO.read(new File("resources\\shoot.jumping.new.gif"));
			for (int i = 0; i < shootJumping.length; i++) {
				shootJumping[i] = superShootJump.getSubimage(i*largura, 0, largura, altura);
			}
			
			BufferedImage superShootFall = ImageIO.read(new File("resources\\shoot.falling.new.gif"));
			for (int i = 0; i < shootFalling.length; i++) {
				shootFalling[i] = superShootFall.getSubimage(i*largura, 0, largura, altura);
			}
			
			BufferedImage superShoot = ImageIO.read(new File("resources\\shoot.new.gif"));
			for (int i = 0; i < shoot.length; i++) {
				shoot[i] = superShoot.getSubimage(i*largura, 0, largura, altura);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		tempoInicio = System.nanoTime();
		anim.setFrame(teleport);
		anim.setIntervalo(-1);
	}
	
	
	public void desenhar(Graphics2D g) {
		BufferedImage result = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gbi = result.createGraphics();
		BufferedImage imagem = anim.getImage();
		if (!moveDireita){
			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		    tx.translate(-imagem.getWidth(null), 0);
		    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		    imagem = op.filter(imagem, null);
		}
		gbi.drawImage(imagem, 0, 0, null);
		if ((System.nanoTime() - tempoInicio) / 1000000L < 35){
			sobrescreverCor(gbi, Color.red, 0.00f);
		} else if ((System.nanoTime() - tempoInicio) / 1000000L > 70) {
			sobrescreverCor(gbi, Color.red, 0.00f);
			tempoInicio = System.nanoTime();
		} else {
			if (nivelCarga==0) {
				sobrescreverCor(gbi, Color.red, 0.00f);
			}
			if (nivelCarga==1) {
				sobrescreverCor(gbi, Color.green, 0.60f);
			}
			if (nivelCarga==2) {
				sobrescreverCor(gbi, new Color(85,140,255), 0.60f);
			}
		}
	    gbi.fillRect(0, 0, largura, altura);
		g.drawImage(result, grafX, y-imagem.getHeight(), null);
	}
	
	private void sobrescreverCor(Graphics2D g, Color cor, Float transparencia){
		g.setColor(cor);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, transparencia));
	}
	
	public int getLargura() {
		return largura;
	}

	public int getAltura() {
		return altura;
	}
	
	public int getGrafX() {
		return grafX;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getHitLargura() {
		return hitLargura;
	}
	
	public int getHitAltura() {
		return hitAltura;
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setGrafX(int grafX) {
		this.grafX = grafX;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public void setExtremidadeEsquerda(int extremidadeEsquerda) {
		this.extremidadeEsquerda = extremidadeEsquerda;
	}
	
	public void setExtremidadeDireita(int extremidadeDireita) {
		this.extremidadeDireita = extremidadeDireita;
	}
	
	public void setExtremidadeInferior(int extremidadeInferior) {
		this.extremidadeInferior = extremidadeInferior;
	}
	
	public void setExtremidadeSuperior(int extremidadeSuperior) {
		this.extremidadeSuperior = extremidadeSuperior;
	}
	
	public int getExtremidadeEsquerda() {
		return extremidadeEsquerda;
	}
	
	public int getExtremidadeDireita() {
		return extremidadeDireita;
	}
	
	public int getExtremidadeInferior() {
		return extremidadeInferior;
	}
	
	public int getExtremidadeSuperior() {
		return extremidadeSuperior;
	}
	
	public void setRelativoCenario(boolean relativoCenario) {
		this.relativoCenario = relativoCenario;
	}
	
	public boolean isRelativoCenario() {
		return relativoCenario;
	}
	
	public int procurarChao(int tamanhoTile) {
		int i = y;
		while (true) {
			if (i % tamanhoTile == 0) {
				return i;
			}
			i++;
		}
	}
	
	public int getOffsetEsquerda() {
		return x + (largura/2) - (hitLargura/2);
	}
	
	public int getOffsetDireita() {
		return x + (largura/2) + (hitLargura/2);
	}
	
	public void setTeleportando(boolean teleportando) {
		this.teleportando = teleportando;
	}
	
	public void setMaterializando(boolean materializando) {
		this.materializando = materializando;
	}
	
	public void setNivelCarga(int nivelCarga) {
		this.nivelCarga = nivelCarga;
	}
	
	public int getNivelCarga() {
		return nivelCarga;
	}

	public boolean isTeleportando() {
		return teleportando;
	}
	
	public boolean isMaterializando() {
		return materializando;
	}
	
	public boolean isMovendo() {
		return movendo;
	}
	
	public boolean isPulando() {
		return pulando;
	}
	
	public boolean isCaindo() {
		return caindo;
	}
	
	public boolean isChegandoChao() {
		return chegandoChao;
	}
	
	public static boolean isMoveDireita() {
		return moveDireita;
	}

	public boolean isAtirando() {
		return atirando;
	}
	
	public boolean isCarregando() {
		return carregando;
	}
	
	public boolean isMoveAtirando() {
		return moveAtirando;
	}
	
	public boolean isSabre() {
		return sabre;
	}
	
	public boolean isDashing() {
		return dashing;
	}
	
	public void setDashing(boolean dashing) {
		this.dashing = dashing;
	}
	
	public void setCaindo(boolean caindo) {
		this.caindo = caindo;	
	}
	
	public void setMovendo(boolean movendo) {
		this.movendo = movendo;
	}
	
	public void setPulando(boolean pulando) {
		this.pulando = pulando;
	}

	public void setChegandoChao(boolean chegandoChao) {
		this.chegandoChao = chegandoChao;
	}
	
	public void setMoveDireita(boolean moveDireita) {
		Jogador.moveDireita = moveDireita;
	}
	
	public void setAtirando(boolean atirando) {
		this.atirando = atirando;
	}
	
	public void setCarregando(boolean carregando) {
		this.carregando = carregando;
	}
	
	public void setMoveAtirando(boolean moveAtirando) {
		this.moveAtirando = moveAtirando;
	}
	
	public void setSabre(boolean sabre) {
		this.sabre = sabre;
	}

	public void setAniMaterializando(){
		anim.setFrame(materialize);
		anim.setTempo(System.nanoTime());
		anim.setIntervalo(50);
	}
	
	public void setAniDashing(){
		anim.setFrame(dash);
		if (!dashing) anim.antiBug();
		anim.setFrameCiclosPosteriores(3);
		anim.setIntervalo(30);
	}
	
	public void setAniParado(){
		anim.setFrame(stand);
		anim.setIntervalo(-1);
	}
	
	public void setAniMovendo(){
		anim.setFrame(walk);
		anim.setIntervalo(42);
	}
	
	public void setAniMoveAtirando(){
		int frame = anim.getFrameAtual();
		anim.setFrame(shootWalking);
		anim.setFrameAtual(frame);
		anim.setIntervalo(42);
	}
	
	public void setAniDashAtirando(){
		anim.setFrame(shootDashing);
		anim.setFrameCiclosPosteriores(3);
		anim.setIntervalo(30);
	}
	
	public void setAniSabre(){
		anim.setFrame(saber);
		anim.setIntervalo(55);
	}
	
	public void setAniSabrePulando(){
		anim.setFrame(saberJumping);
		anim.setIntervalo(55);
	}
	
	public void setAniChegaChao(){
		anim.setFrame(reachGround);
		anim.setIntervalo(60);
	}
	
	public void setAniSubindo(){
		anim.setFrame(jump);
		if (!pulando) anim.antiBug();
		anim.setFrameCiclosPosteriores(2);
		anim.setIntervalo(50);
	}
	
	public void setAniCaindo(){
		anim.setFrame(fall);
		if (!caindo) anim.antiBug();
		anim.setFrameCiclosPosteriores(1);
		anim.setIntervalo(50);
	}
	
	public void setAniSobeAtirando(){
		anim.setFrame(shootJumping);
		if (!pulando) anim.antiBug();
		anim.setFrameCiclosPosteriores(2);
		anim.setIntervalo(50);
	}
	
	public void setAniCaiAtirando(){
		anim.setFrame(shootFalling);
		if (!caindo) anim.antiBug();
		anim.setFrameCiclosPosteriores(1);
		anim.setIntervalo(50);
	}
	
	public void setAniAtirando(){
		anim.setFrame(shoot);
		anim.setTempo(System.nanoTime());
		anim.setIntervalo(200);
	}
	
	public void atualizar(){
		anim.atualizarAnim();
	}
}