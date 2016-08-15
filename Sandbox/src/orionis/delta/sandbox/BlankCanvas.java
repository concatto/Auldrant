package orionis.delta.sandbox;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class BlankCanvas {
	public BlankCanvas() {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		JTextArea area = new JTextArea(20, 60);
		
		OutputStream stream = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				area.append(String.valueOf((char) b));
			}
		};
		System.setOut(new PrintStream(stream));
		
		panel.add(area);
		frame.setContentPane(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		System.out.println("Hello world!");
	}
	
	public static void main(String[] args) {
		new BlankCanvas();
	}
}
