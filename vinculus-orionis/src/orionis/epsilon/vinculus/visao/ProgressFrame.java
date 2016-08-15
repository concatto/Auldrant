package orionis.epsilon.vinculus.visao;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

@SuppressWarnings("serial")
public class ProgressFrame extends JFrame {
	private JPanel panel = new JPanel();
	private JLabel label = new JLabel();
	private JProgressBar bar = new JProgressBar();
	
	public ProgressFrame() {
		add(panel);
		panel.add(label);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(bar);
		
		panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 6, 8));
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		label.setAlignmentX(CENTER_ALIGNMENT);
		bar.setPreferredSize(new Dimension(190, 19));
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	public void display() {
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void close() {
		setVisible(false);
		dispose();
	}
	
	public void updateProgress(int value) {
		bar.setValue(value);
		bar.setString(value + "%");
	}

	public int getMaximumValue() {
		return bar.getMaximum();
	}
	
	public void prepare(String title, String text, boolean determinate) {
		bar.setMaximum(100);
		
		//False for indeterminate
		bar.setStringPainted(determinate);
		//True for indeterminate
		bar.setIndeterminate(!determinate);
		
		setTitle(title);
		label.setText(text);
	}
}
