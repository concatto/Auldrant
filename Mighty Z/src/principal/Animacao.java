package principal;

import java.awt.image.*;

public class Animacao {
	private BufferedImage[] frames;
	
	private int frameAtual;
	private int frameFinal;
	private int frameCiclosPosteriores;
	
	private long tempoInicio;
	private long intervalo;
	
	private boolean iniciando;
	private boolean animacaoAcontecendo=false;
	
	public Animacao(){
		tempoInicio = System.nanoTime();
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
	
	public boolean isAcontecendo() {
		return animacaoAcontecendo;
	}
	
	public void setTempo(long tempo){
		this.tempoInicio = tempo;
	}
	
	public void setFrameFinal(int frameFinal) {
		this.frameFinal = frameFinal;
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
		this.frameFinal = frames.length;
		this.frameCiclosPosteriores = 0;
		animacaoAcontecendo=true;
		if (frameAtual >= frames.length){
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
