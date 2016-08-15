package br.concatto.main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import br.concatto.tools.RectangleDefiner;

@SuppressWarnings("serial")
public class DefinerControls extends JDialog {
	private JPanel root = new JPanel(new GridBagLayout());
	private JPanel container = new JPanel();
	private JButton cancel = new JButton("Cancel");
	private JButton confirm = new JButton("Confirm");
	private RectangleDefiner definer = new RectangleDefiner();
	private Rectangle bounding;
	
	public DefinerControls(Window owner) {
		super(owner, "Bounds", ModalityType.APPLICATION_MODAL);
		setContentPane(root);
		definer.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		definer.setAlwaysOnTop(true);
		
		//Small hack to centralize vertically
		root.add(container, new GridBagConstraints());
		container.add(cancel);
		container.add(confirm);
		
		pack();
		setLocationRelativeTo(null);
		
		Rectangle b = getBounds();
		b.y -= owner.getHeight() + 30;
		setBounds(b);
		setResizable(false);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cancel.doClick();
				super.windowClosing(e);
			}
		});
		
		cancel.addActionListener(e -> {
			definer.restoreBounding();
			bounding = null;
			setVisible(false);
		});
		
		confirm.addActionListener(e -> {
			bounding = definer.getBounding();
			setVisible(false);
		});
	}
	
	@Override
	public void setVisible(boolean b) {
		definer.setVisible(b);
		bounding = definer.getBounding();
		super.setVisible(b);
		toFront();
	}
	
	public Rectangle getBounding() {
		return bounding;
	}
}
