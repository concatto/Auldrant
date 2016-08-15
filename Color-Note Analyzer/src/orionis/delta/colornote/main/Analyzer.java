package orionis.delta.colornote.main;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Analyzer {
	enum NoteColor {
		BLUE, RED, PURPLE, YELLOW, GREEN
	}
	
	public static void main(String[] args) {
		try {
			BufferedImage image = ImageIO.read(new File("C:/science.png"));
			Raster data = image.getData();
			int[] colors = new int[4];
			int y = 393;
			int threshold = 160;
			
			/* Percorrer todos os pixels no eixo X, exceto os últimos 20 (barra de rolagem) */
			for (int x = 0; x < data.getWidth() - 20; x++) {
				data.getPixel(x, y, colors);
				
				/* Obter média das cores */
				int average = 0;
				for (int i = 0; i < 3; i++) average += colors[i];
				average /= 3;
				
				/* Se a média for clara o suficiente, significa o início de uma nota */
				if (average > threshold) {
					System.out.printf("Match found at x = %d, average: %d\n", x, average);
					/* Largura da nota em pixels */
					int width = 14;
					int[] mix = new int[3];
					
					/* Obter a média de todos os 14 pixels */
					for (int startingX = x; x < startingX + width; x++) {
						data.getPixel(x, y, colors);
						for (int i = 0; i < mix.length; i++) {
							mix[i] += colors[i];
						}
					}
					
					for (int i = 0; i < mix.length; i++) {
						mix[i] = mix[i] / width;
					}
					System.out.printf("Mix of %d pixels: %d red, %d green, %d blue. Possible color: %s\n",
							width, mix[0], mix[1], mix[2],
							findColor(mix[0], mix[1], mix[2]));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static NoteColor findColor(int r, int g, int b) {
		if (testRange(r, 130) && testRange(g, 170) && testRange(b, 210)) {
			return NoteColor.BLUE;
		} else if (testRange(r, 200) && testRange(g, 150) && testRange(b, 200)) {
			return NoteColor.PURPLE;
		} else if (testRange(r, 230) && testRange(g, 125) && testRange(b, 125)) {
			return NoteColor.RED;
		} else if (testRange(r, 150) && testRange(g, 200) && testRange(b, 90)) {
			return NoteColor.GREEN;
		} else {
			return NoteColor.YELLOW;
		}
	}
	
	private static boolean testRange(int number, int comparison) {
		int tolerance = 30;
		return (number > comparison - tolerance) && (number < comparison + tolerance);
	}
}