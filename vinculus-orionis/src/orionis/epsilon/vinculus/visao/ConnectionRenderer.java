package orionis.epsilon.vinculus.visao;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;

import orionis.epsilon.vinculus.modelo.SocketInformation;

@SuppressWarnings("serial")
public class ConnectionRenderer extends JTextArea implements ListCellRenderer<SocketInformation> {
	private static final Color BACKGROUND_SELECTED = UIManager.getColor("List.selectionBackground");
	private static final Color FOREGROUND_SELECTED = UIManager.getColor("List.selectionForeground");
	private static final Color BACKGROUND_NORMAL = UIManager.getColor("List.background");
	private static final Color FOREGROUND_NORMAL = UIManager.getColor("List.foreground");
	private static final Color BACKGROUND_HALF = UIManager.getColor("InternalFrame.inactiveTitleGradient");
	private static final Color FOREGROUND_HALF = FOREGROUND_NORMAL;
	private static final Border BORDER_EMPTY = BorderFactory.createEmptyBorder(5, 5, 5, 5);
	private static final Border BORDER_BOTTOM = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Table.gridColor")), BORDER_EMPTY);
	private static final Border BORDER_TOP = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("Table.gridColor")), BORDER_EMPTY);
	
	public ConnectionRenderer() {
		super();
	}
	
	@Override
	public Font getFont() {
		return UIManager.getFont("TextField.font");
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends SocketInformation> list, SocketInformation value, int index, boolean isSelected, boolean cellHasFocus) {
		String status = (value.isClosed()) ? "fechada" : "aberta";
		String text = value.getAddress() + "\n"
				+ value.getPort() + "\n"
				+ "Conexão " + status;
		setText(text);
		
		int visible = list.getVisibleRowCount() - 1;
		if (index < visible) {
			setBorder(BORDER_BOTTOM);
		} else if (index > visible) {
			setBorder(BORDER_TOP);
		} else {
			setBorder(BORDER_EMPTY);
		}
		if (isSelected) {
			if (cellHasFocus) {
				setForeground(FOREGROUND_SELECTED);
				setBackground(BACKGROUND_SELECTED);
			} else {
				setForeground(FOREGROUND_HALF);
				setBackground(BACKGROUND_HALF);
			}
		} else {
			setForeground(FOREGROUND_NORMAL);
			setBackground(BACKGROUND_NORMAL);
		}
		
		return this;
	}
}
