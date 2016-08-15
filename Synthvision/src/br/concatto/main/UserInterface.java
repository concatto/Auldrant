package br.concatto.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import java.util.StringJoiner;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class UserInterface extends JFrame {
	private JPanel root = new JPanel();
	private JPanel buttonContainer = new JPanel();
	private JPanel statusContainer = new JPanel(new BorderLayout());
	private JButton define = new JButton("Define bounds");
	private JButton start = new JButton("Start");
	private JButton stop = new JButton("Stop");
	private JLabel status = new JLabel("Undefined bounds.");
	private DefinerControls definerControls = new DefinerControls(this);
	
	//TODO Controller
	private Watcher watcher = new Watcher();
	
	public UserInterface() {
		super("Synthvision");
		addComponents();
		configureListeners();
		
		setContentPane(root);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}
	
	private void addComponents() {
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		root.add(buttonContainer);
		root.add(new JSeparator());
		root.add(statusContainer);
		
		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setPreferredSize(new Dimension(1, define.getPreferredSize().height));
		
		buttonContainer.add(define);
		buttonContainer.add(separator);
		buttonContainer.add(start);
		buttonContainer.add(stop);
		
		statusContainer.add(status);
		statusContainer.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4, 5, 5, 5),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)
		));
		
		status.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 0));
		
		start.setEnabled(false);
		stop.setEnabled(false);
	}
	
	private void configureListeners() {
		define.addActionListener(e -> {
			setVisible(false);
			definerControls.setVisible(true);
			
			StringJoiner joiner = new StringJoiner(", ");
			Rectangle bounding = definerControls.getBounding();
			
			if (bounding != null) {
				joiner.add("X=" + bounding.x);
				joiner.add("Y=" + bounding.y);
				joiner.add("W=" + bounding.width);
				joiner.add("H=" + bounding.height);
				status.setText(joiner.toString());
			}
			
			start.setEnabled(true);
			setVisible(true);
			toFront();
		});
		
		start.addActionListener(e -> {
			changeButtonState(true);
			watcher.start(definerControls.getBounding());
		});
		
		stop.addActionListener(e -> {
			changeButtonState(false);
			watcher.stop();
			System.out.println(watcher.getNotes());
		});
	}
	
	private void changeButtonState(boolean clickedStart) {
		stop.setEnabled(clickedStart);
		start.setEnabled(!clickedStart);
		define.setEnabled(!clickedStart);
	}
}
