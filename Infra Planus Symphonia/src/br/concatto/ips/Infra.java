package br.concatto.ips;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Infra extends Application {
	private ExecutorService executor;
	private Future<?> consumeTask;
	private int sampleRate = 0;
	private boolean safeStrats = false;

	public static void main(String[] args) {
		launch(args);
	}
	
	private WritableImage transform(File file, int width) throws IOException {
		InputStream stream = new FileInputStream(file);
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int read;
		sampleRate = 0;
		while ((read = stream.read(buffer, 0, buffer.length)) > 0) {
			if (sampleRate == 0) {
				byte[] four = {buffer[27], buffer[26], buffer[25], buffer[24]};
				for (int j = 0; j < four.length; j++) {
					sampleRate += (four[j] & 0xFF) << (8 * (four.length - j - 1));
				}
			}
			byteStream.write(buffer, 0, read);
		}
		
		byte[] bytes = byteStream.toByteArray();
		WritableImage image = new WritableImage(width, bytes.length / width);
		PixelWriter writer = image.getPixelWriter();
		
		int[] colors = IntStream.iterate(0xFF000000, i -> i + 0x10101).limit(256).toArray();
		PixelFormat<ByteBuffer> format = PixelFormat.createByteIndexedInstance(colors);
		
		writer.setPixels(0, 0, width, bytes.length / width, format, bytes, 0, width);
		
		stream.close();
		return image;
	}
	
	private Future<?> consume(WritableImage image, ExecutorService executor) throws LineUnavailableException {
		PixelWriter writer = image.getPixelWriter();
		PixelReader reader = image.getPixelReader();
		AudioFormat f = new AudioFormat(sampleRate, 16, 2, true, false);
		SourceDataLine line = AudioSystem.getSourceDataLine(f);
		line.open();
		line.start();
		
		
		return executor.submit(() -> {
			int width = (int) image.getWidth();
			for (int i = 0; i < image.getHeight(); i++) {
				int[] ints = new int[width];
				reader.getPixels(0, i, width, 1, PixelFormat.getIntArgbInstance(), ints, 0, width);
				byte[] buffer = new byte[ints.length];
				for (int j = 0; j < buffer.length; j++) {
					buffer[j] = (byte) (((ints[j] & 0xFF) + ((ints[j] >> 8) & 0xFF)) / 2);
				}
				
				line.write(buffer, 0, buffer.length);

				Arrays.fill(ints, 0);
				int y = i;
				Platform.runLater(() -> writer.setPixels(0, y, width, 1, PixelFormat.getIntArgbInstance(), ints, 0, width));
				
				if (Thread.interrupted() || safeStrats) break;
			}
		});
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		int width = 1024;
		
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("Arquivos .wav", "*.wav"));
		chooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Music"));
		File file = chooser.showOpenDialog(primaryStage);
		if (file == null) return;
		WritableImage image = transform(file, width);
		
		ImageView imageView = new ImageView(image);
		ScrollPane scrollPane = new ScrollPane(imageView);
		scrollPane.setMaxWidth(width + 16);
		Button consumeButton = new Button("Consume!");
		VBox root = new VBox(20, consumeButton, scrollPane);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(20));
		
		executor = Executors.newSingleThreadExecutor();
		consumeButton.setOnAction(e -> {
			try {
				consumeTask = consume(image, executor);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		primaryStage.setTitle(file.getName());
		primaryStage.setScene(new Scene(root, scrollPane.getWidth(), 768));
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> {
			if (consumeTask != null) {
				safeStrats = true;
				consumeTask.cancel(true);
				executor.shutdownNow();
			}
		});
	}
}
