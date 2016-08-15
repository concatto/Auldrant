package epsilon.orionis.aether.modelo;

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class ModeloTabela extends DefaultTableModel {
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	public void clearTable() {
		int contagem = super.getRowCount() - 1;
		for (int i = contagem; i >= 0; i--) {
			super.removeRow(i);
		}
	}
}
