package orionis.delta.vision.main;

import java.io.ByteArrayInputStream;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	private static boolean isWhite(double[] pixelData) {
		double sum = 0;
		for (double d : pixelData) {
			sum += d;
		}
		return sum != 0;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat matrix = Imgcodecs.imread("C:/kar4.jpg", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Mat edges = new Mat(matrix.size(), matrix.type());
		
		Imgproc.Canny(matrix, edges, 1, 15);
		vertical(matrix, edges);
		
		MatOfByte buf = new MatOfByte();
		
		Imgcodecs.imencode(".png", matrix, buf);
		ImageView display = new ImageView(new Image(new ByteArrayInputStream(buf.toArray())));
		
		primaryStage.setScene(new Scene(new Group(display)));
		primaryStage.show();
	}

	private void vertical(Mat matrix, Mat edges) {
		final int SCAN_WIDTH = 7;
		for (int i = 0, j = SCAN_WIDTH; i < edges.cols(); i += SCAN_WIDTH, j += SCAN_WIDTH) {
			if (j > edges.cols()) j = edges.cols() - 1;
			Mat cols = edges.colRange(i, j);
			double sum = 0;
			double whites = 0;
			double blacks = 0;
			boolean hasWhite;
			for (int k = 0; k < cols.rows(); k++) {
				hasWhite = false;
				for (int l = 0; l < cols.cols(); l++) {
					if (isWhite(cols.get(k, l))) {
						if (blacks > 0) {
							whites -= blacks * 8;
							sum += whites;
							whites = 0;
							blacks = 0;
						}
						whites += 5;
						hasWhite = true;
						break;
					}
				}
				if (!hasWhite) {
					blacks++;
				}
			}
			if (sum > 0) Imgproc.line(matrix, new Point((i + j) / 2, 0), new Point((i + j) / 2, matrix.rows()), new Scalar(255, 255, 255), 1);
		}
	}
}
