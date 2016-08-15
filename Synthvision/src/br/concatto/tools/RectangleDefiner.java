package br.concatto.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class RectangleDefiner extends JFrame {
	private static final int INSET = 3;
	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	
	private JPanel content = new JPanel(new BorderLayout());
	private JPanel mover = new JPanel();
	private ComponentResizer resizer = new ComponentResizer();
	private int x;
	private int y;
	private Rectangle previousBounds;
	
	public RectangleDefiner() {
		setUndecorated(true);
		setBackground(TRANSPARENT);
		setSize(600, resizer.getMinimumSize().height);
		setLocationRelativeTo(null);
		
		resizer.setDragInsets(new Insets(INSET, INSET, INSET, INSET));
		content.add(mover);
		content.setBackground(TRANSPARENT);
		content.setBorder(new LineBorder(new Color(0, 0, 0, 0.6f), INSET));
		
		mover.setBackground(new Color(0, 0, 0, 0.3f));
		mover.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				x = e.getX();
				y = e.getY();
			}
		});
		
		mover.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				setLocation(getLocation().x + e.getX() - x, getLocation().y + e.getY() - y);
			}
		});
		
		resizer.registerComponent(this);
		setContentPane(content);
	}

	public Rectangle getBounding() {
		Rectangle bounds = mover.getBounds();
		Point parent = getLocation();
		bounds.x += parent.x;
		bounds.y += parent.y;
		return bounds;
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (b) previousBounds = getBounds();
	}
	
	public void restoreBounding() {
		setBounds(previousBounds);
	}
}
