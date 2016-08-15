package orionis.epsilon.vinculus.visao;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Scrollable;

@SuppressWarnings("serial")
public class ScrollableImage extends JLabel implements Scrollable, MouseMotionListener {
	private static double SCALING = 0.25;
	private BufferedImage source;
	
	public ScrollableImage(BufferedImage image) {
		super();
		
		setImage(image);
		configure();
	}
	
	public ScrollableImage(String temporaryText) {
		super();
		
		setText(temporaryText);
		configure();
	}
	
	private void configure() {
		setAutoscrolls(true);
		addMouseMotionListener(this);
	}
	
	@Override
	public void setText(String text) {
		if (text != null) {
			setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 0));
			setImage(null);
		}
		
		this.source = null;
		super.setText(text);
	}
	
	public void setImage(BufferedImage image) {
		if (image != null) {
			setBorder(null);
			setText(null);
		}
		
		this.source = image;
		super.setIcon(transform(image));
	}
	
	private static ImageIcon transform(BufferedImage source) {
		if (source == null) return null;
		
		int width = (int) (source.getWidth() * SCALING);
		int height = (int) (source.getHeight() * SCALING);
		
		BufferedImage destination = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		AffineTransform transform;
		AffineTransformOp op;
		
		transform = new AffineTransform();
		transform.scale(SCALING, SCALING);
		
		op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
		destination = op.filter(source, destination);
		
		return new ImageIcon(destination);
	}
	
	public BufferedImage getSource() {
		return source;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		Rectangle rectangle = new Rectangle(e.getX(), e.getY(), 1, 1);
        scrollRectToVisible(rectangle);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		/* Nothing */
	}

	@Override
	public Dimension getPreferredSize() {
		return super.getPreferredSize();
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

}
