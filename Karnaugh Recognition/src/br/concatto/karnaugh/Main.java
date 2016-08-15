package br.concatto.karnaugh;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {
	private static final int[][] permutations = {
		{0, 1, 2, 3}, {0, 1, 3, 2}, {0, 3, 1, 2}, {3, 0, 1, 2}, {3, 0, 2, 1}, {0, 3, 2, 1},
		{0, 2, 3, 1}, {0, 2, 1, 3}, {2, 0, 1, 3}, {2, 0, 3, 1}, {2, 3, 0, 1}, {3, 2, 0, 1},
		{3, 2, 1, 0}, {2, 3, 1, 0}, {2, 1, 3, 0}, {2, 1, 0, 3}, {1, 2, 0, 3}, {1, 2, 3, 0},
		{1, 3, 2, 0}, {3, 1, 2, 0}, {3, 1, 0, 2}, {1, 3, 0, 2}, {1, 0, 3, 2}, {1, 0, 2, 3}
	};
	private double toleranceX =  2.1;
	private double toleranceY = 2.15;
	private int toleranceDarkPixel = 115;
	private int tolerancePainted = 130;
	private int toleranceDifference = 15;
	private int vars = 4;
	private int bound = vars - 1;
	
	@SuppressWarnings("serial")
	public Main() {	
		BufferedImage originalImage;
		try {
			originalImage = toRGB(ImageIO.read(new File("C:/kar7.jpg")));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		BufferedImage image = resize(originalImage, 400);
		Dimension d = new Dimension(image.getWidth(), image.getHeight());
		
		Raster data = image.getData();
		ArrayList<Point> points = findDarkPoints(data);
		Rectangle rectangle = buildRectangle(points, data);
		
		BufferedImage map = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = map.createGraphics();
		graphics.setBackground(Color.WHITE);
		graphics.clearRect(0, 0, d.width, d.height);
		graphics.setColor(Color.BLACK);
		
		JFrame frame = new JFrame("Karnaugh Recognition");
		JPanel root = new JPanel();
		JPanel original = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				return d;
			}
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(image, 0, 0, null);
			}
		};
		JPanel drawn = new JPanel() {			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(map, 0, 0, null);
			}
			
			@Override
			public Dimension getPreferredSize() {
				return d;
			}
		};
		
		frame.setContentPane(root);
		root.add(original);
		root.add(drawn);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		Expression[][] matrix = new Expression[vars][vars];
		
		int width = rectangle.width / vars;
		int height = rectangle.height / vars;
		for (int column = 0; column < vars; column++) {
			Expression[] array = new Expression[vars];
			for (int row = 0; row < vars; row++) {
				int x = rectangle.x + row * width;
				int y = rectangle.y + column * height;
				boolean painted = isPainted(x, y, width, height, data);
				if (painted) {
					graphics.fillRect(x, y, width, height);
					array[row] = developExpression(column, row); //Inverted
				}
			}
			matrix[column] = array;
		}
		
		ArrayList<Expression> finalExpression = null;
		int smallestLength = 1 + vars * (int) Math.pow(2, vars);
		for (int[] permutation : permutations) {
			ArrayList<Expression> result = merge(matrix, permutation);
			if (countCharacters(result) < smallestLength) finalExpression = result;
		}
		
		System.out.println(finalExpression);
		
		drawn.repaint();
		graphics.dispose();
	}
	
	private int countCharacters(ArrayList<Expression> expressions) {
		int amount = 0;
		for (Expression expression : expressions) {
			amount += expression.getLength();
		}
		return amount;
	}
	
	private ArrayList<Expression> merge(Expression[][] matrix, int[] permutation) {
		clearUsed(matrix);
		ArrayList<Expression> expressions = new ArrayList<>();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				Expression expression = matrix[i][j];
				if (expression == null || expression.isUsed()) continue;
				Expression[] neighbors = neighbors(matrix, i, j, permutation);
				
				int nullAmount = 0;
				for (int k = 0; k < neighbors.length; k++) {
					Expression neighbor = neighbors[k];
					if (neighbor != null && !neighbor.isUsed()) {
						Expression merged = expression.tryMerge(neighbor);
//						System.out.printf("%s merged with %s --->  %s\n", expression, neighbor, merged);
						expressions.add(merged);
						neighbor.setUsed(true);
						expression.setUsed(true);
						break;
					} else {
						nullAmount++;
						if (nullAmount == neighbors.length) expressions.add(expression);
					}
				}
			}
		}
		
		clearUsed(matrix);
		
		for (int i = 0; i < expressions.size(); i++) {
			Expression expression = expressions.get(i);
			if (expression.isUsed()) continue;
			for (int j = 0; j < expressions.size(); j++) {
				Expression other = expressions.get(j);
				if (other.isUsed()) continue;
				Expression merge = expression.tryMerge(other);
				if (merge != null) {
					expression.setUsed(true);
					other.setUsed(true);
					expressions.add(merge);
//					System.out.printf("%s merged with %s --->  %s\n", expression, other, merge);
					break;
				}
			}
		}
		
		for (ListIterator<Expression> it = expressions.listIterator(); it.hasNext();) {
			if (it.next().isUsed()) it.remove();
		}
		
		return expressions;
	}
	
	private void clearUsed(Expression[][] matrix) {
		for (Expression[] expressions : matrix) {
			for (Expression expression : expressions) {
				if (expression != null) expression.setUsed(false);
			}
		}
	}
	
	private Expression developExpression(int row, int column) {
		boolean a = row > 1;
		boolean b = row > 0 && row < bound;
		boolean c = column > 1;
		boolean d = column > 0 && column < bound;
		return new Expression(a, b, c, d);
	}
	
	private Expression[] neighbors(Expression[][] matrix, int row, int column, int[] permutation) {
		Expression[] array = new Expression[4];
		
		array[permutation[0]] = matrix[row][verify(column + 1)];
		array[permutation[1]] = matrix[row][verify(column - 1)];
		array[permutation[2]] = matrix[verify(row + 1)][column];
		array[permutation[3]] = matrix[verify(row - 1)][column];
		return array;
	}
	
	private int verify(int number) {
		return number < 0 ? bound : (number == bound + 1 ? 0 : number);
	}

	private BufferedImage toRGB(BufferedImage image) {
		BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D rgbGraphics = rgbImage.createGraphics();
		rgbGraphics.drawImage(image, 0, 0, null);
		rgbGraphics.dispose();
		return rgbImage;
	}
	
	private BufferedImage resize(BufferedImage image, int desiredDimension) {
		int dimension = image.getWidth() + image.getHeight() / 2;
		double scale = 1;
		if (dimension > desiredDimension) scale = desiredDimension / (double) dimension;
		double denominator = 1 / scale;
		int newWidth = (int) Math.round(image.getWidth() / denominator);
		int newHeight = (int) Math.round(image.getHeight() / denominator);
		BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		AffineTransformOp scaleOp = new AffineTransformOp(new AffineTransform(new double[]{scale, 0, 0, scale}), AffineTransformOp.TYPE_BICUBIC);
		scaled = scaleOp.filter(image, scaled);
		return scaled;
	}
	
	private ArrayList<Point> findDarkPoints(Raster data) {
		ArrayList<Point> points = new ArrayList<>();
		
		for (int x = 0; x < data.getWidth(); x++) {
			for (int y = 0; y < data.getHeight(); y++) {
				int[] array = new int[3];
				data.getPixel(x, y, array);
				int average = 0;
				for (int i = 0; i < array.length; i++) {
					average += array[i];
				}
				average /= array.length;
				if (average < toleranceDarkPixel && findRange(array) <= toleranceDifference) {
					points.add(new Point(x, y));
				}
			}
		}
		
		return points;
	}
	
	private boolean isPainted(int x, int y, int width, int height, Raster data) {
		int total = 0;
		for (int tempX = x; tempX < width + x; tempX++) {
			for (int tempY = y; tempY < height + y; tempY++) {
				int[] array = new int[3];
				data.getPixel(tempX, tempY, array);
				int sum = 0;
				for (int value : array) {
					sum += value;
				}
				total += sum / 3;
			}
		}
		
		int i = total / (width * height);
		return i < tolerancePainted;
	}
	
	private Rectangle buildRectangle(ArrayList<Point> points, Raster data) {
		Wireframe wireframe = Wireframe.analyze(points, toleranceX, toleranceY, data.getWidth(), data.getHeight());
		return new Rectangle(wireframe.getX(), wireframe.getY(), wireframe.getWidth(), wireframe.getHeight());
	}
	
	private int findRange(int[] array) {
		Arrays.sort(array);
		return array[array.length - 1] - array[0];
	}
	
	public static void main(String[] args) {
		new Main();
	}
}
