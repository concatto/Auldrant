package principal;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.imageio.ImageIO;

public class TileMap {
	private int tamanhoTile;
	private int mapa[][];
	
	private BufferedImage[] tileFrames;
	
	private int x = 0;
	private int y = 0;
	
	private int largMapa;
	private int altuMapa;
	private BufferedReader br;
	
	public TileMap(String caminho, int tamanhoTile){
		tileFrames = new BufferedImage[7];
		this.tamanhoTile = tamanhoTile;
		
		try {
			
			BufferedImage superTileFrames = ImageIO.read(new File("resources\\TileFrames.gif"));
			for (int i = 0; i < tileFrames.length; i++) {
				tileFrames[i] = superTileFrames.getSubimage(i*tamanhoTile, 0, tamanhoTile, tamanhoTile);
			}
			
			br = new BufferedReader(new FileReader(caminho));
			
			largMapa = Integer.parseInt(br.readLine());
			altuMapa = Integer.parseInt(br.readLine());
			mapa = new int[altuMapa][largMapa];
			
			String espacador = " ";
			for (int l = 0; l < altuMapa; l++) {
				String linha = br.readLine();
				String[] numeros = linha.split(espacador);
				for (int c = 0; c < largMapa; c++) {
					mapa[l][c] = Integer.parseInt(numeros[c]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getTamanhoTile(){
		return tamanhoTile;
	}
	
	public boolean hasColisaoAbaixo(int linha, int esquerda, int direita) {
		linha+=1;
		if (mapa[linha][esquerda] <= 2 || mapa[linha][direita] <= 2) {
			return true;
		}
		return false;
	}
	
	public boolean hasColisaoDireita(int linha, int coluna) {
		if (mapa[linha][coluna] <= 2) {
			return true;
		}
		return false;
	}
	
	public boolean hasColisaoEsquerda(int linha, int coluna) {
		if (mapa[linha][coluna] <= 2) {
			return true;
		}
		return false;
	}
	
	public int getAltura(){
		return altuMapa;
	}
	
	public int getLargura(){
		return largMapa;
	}
	
	public int getLargPixels() {
		return largMapa * tamanhoTile;
	}
	
	public void desenhar(Graphics2D g){
		for (int l = 0; l < altuMapa; l++) {
			for (int c = 0; c < largMapa; c++) {
				int valor = mapa[l][c];
				switch (valor){
				case 0:
					g.drawImage(tileFrames[1], x + c * tamanhoTile, y + l * tamanhoTile, null);
					break;
				case 1:
					g.drawImage(tileFrames[0], x + c * tamanhoTile, y + l * tamanhoTile, null);
					break;
				case 2:
					g.drawImage(tileFrames[6], x + c * tamanhoTile, y + l * tamanhoTile, null);
					break;
				case 3:
					g.drawImage(tileFrames[6], x + c * tamanhoTile, y + l * tamanhoTile, null);
					break;
				case 4:
					g.drawImage(tileFrames[2], x + c * tamanhoTile, y + l * tamanhoTile, null);
					break;
				case 5:
					g.drawImage(tileFrames[4], x + c * tamanhoTile, y + l * tamanhoTile, null);
					break;
				case 6:
					g.drawImage(tileFrames[5], x + c * tamanhoTile, y + l * tamanhoTile, null);
					break;
				case 7:
					g.drawImage(tileFrames[3], x + c * tamanhoTile, y + l * tamanhoTile, null);
					break;
				}
			}
		}
	}
}