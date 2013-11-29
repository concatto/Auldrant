package principal;

import java.awt.image.*;

public class Animacao {
	private BufferedImage[] frames;
	
	private int frameAtual;
	private int frameInicial;
	private int frameFinal;
	private int frameCiclosPosteriores;
	
	private long tempoInicio;
	private long intervalo;
	
	private boolean primordio=true;
	private boolean iniciando;
	private boolean animacaoAcontecendo=false;
	
	public Animacao(){
		tempoInicio = System.nanoTime();
	}
	
	public int getFrameInicial() {
		return frameInicial;
	}

	public int getFrameFinal() {
		return frameFinal;
	}

	public int getFrameCiclosPosteriores() {
		return frameCiclosPosteriores;
	}
	
	public int getFrameAtual() {
		return frameAtual;
	}
	
	public boolean isPrimordio() {
		return primordio;
	}
	
	public boolean isAcontecendo() {
		return animacaoAcontecendo;
	}
	
	public void setTempo(long tempo){
		this.tempoInicio = tempo;
	}
	
	public void setFrameInicial(int frameInicial) {
		this.frameInicial = frameInicial;
	}

	public void setFrameFinal(int frameFinal) {
		this.frameFinal = frameFinal;
	}

	public void setPrimordio(boolean primordio) {
		this.primordio = primordio;
	}
	
	public void setFrameCiclosPosteriores(int frameCiclosPosteriores) {
		this.frameCiclosPosteriores = frameCiclosPosteriores;
	}

	public void setFrameAtual(int frame) {
		this.frameAtual = frame;
	}
	
	public void antiBug() {
		iniciando = true;
		this.tempoInicio = System.nanoTime();
		this.frameAtual = 0;
	}
	
	public void setFrame(BufferedImage[] images) {
		frames = images;
		this.frameInicial = 0;
		this.frameFinal = frames.length;
		this.frameCiclosPosteriores = 0;
		animacaoAcontecendo=true;
		if (frameAtual >= frames.length){
			frameAtual = 0;
		}
	}
	
	public void setFrame(BufferedImage[] images, int frameInicial, int frameFinal) {
		frames = images;
		this.frameInicial = frameInicial;
		this.frameFinal = frameFinal;
		this.frameCiclosPosteriores = 0;
		animacaoAcontecendo=true;
		if (frameAtual >= frames.length){
			frameAtual = 0;
		}
	}
	
	public void setFrame(BufferedImage[] images, int frameInicial, int frameFinal, int frameCiclosPosteriores) {
		frames = images;
		this.frameInicial = frameInicial;
		this.frameFinal = frameFinal;
		this.frameCiclosPosteriores = frameCiclosPosteriores;
		animacaoAcontecendo=true;
		if (frameAtual >= frameFinal){
			frameAtual = 0;
		}
	}
	
	public void setIntervalo(long inter) {
		intervalo = inter;
	}
	
	public void atualizarAnim(){
		if (intervalo == -1){
			frameAtual=0;
			return;
		}
		long tempoDecorrido = (System.nanoTime() - tempoInicio) / 1000000L;
		if (tempoDecorrido > intervalo) {
			if (!iniciando) {
				frameAtual++;
			} else {
				iniciando = false;
			}
			tempoInicio = System.nanoTime();
		}
		if (frameAtual == frameFinal) {
			animacaoAcontecendo = false;
			frameAtual = frameCiclosPosteriores;
		}
	}
		
	public BufferedImage getImage() {
		return frames[frameAtual];
	}
	
}
