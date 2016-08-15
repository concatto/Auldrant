package orionis.epsilon.vinculus.visao;

import java.awt.Rectangle;

import javax.swing.JList;

import orionis.epsilon.vinculus.modelo.SocketInformation;

@SuppressWarnings("serial")
public class ConnectionList extends JList<SocketInformation> {
	@Override
	public SocketInformation getPrototypeCellValue() {
		return SocketInformation.getPrototype();
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 4;
	}
	
	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 4;
	}
	
	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
	@Override
	public int getVisibleRowCount() {
		return 4;
	}
}
