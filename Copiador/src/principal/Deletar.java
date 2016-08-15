package principal;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Deletar extends Comandos implements ActionListener {
	private JFrame optDeletar = new JFrame("Deletar");
	private JPanel panelDeletar = new JPanel();
	private JPanel panelCheckDeletar = new JPanel();
	private JPanel panelBotoesDeletar = new JPanel();
	private JButton delConfirmar = new JButton("OK");
	private JButton delCancelar = new JButton("Cancelar");
	private JCheckBox deleteOrigem = new JCheckBox("Deletar arquivos do computador hospedeiro");
	private JCheckBox deleteLocal = new JCheckBox("Deletar arquivos do Flash Drive");
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		optDeletar.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		optDeletar.setResizable(false);
		optDeletar.add(panelDeletar);
		panelDeletar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panelDeletar.setLayout(new BoxLayout(panelDeletar, BoxLayout.Y_AXIS));
		panelCheckDeletar.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelBotoesDeletar.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelDeletar.add(panelCheckDeletar);
		panelDeletar.add(panelBotoesDeletar);
		panelBotoesDeletar.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
		panelBotoesDeletar.add(delConfirmar);
		panelBotoesDeletar.add(delCancelar);
		panelCheckDeletar.setLayout(new BoxLayout(panelCheckDeletar, BoxLayout.Y_AXIS));
		panelCheckDeletar.add(deleteLocal);
		panelCheckDeletar.add(deleteOrigem);
		optDeletar.pack();
		optDeletar.setLocationRelativeTo(null);
		optDeletar.setVisible(true);
		
		delConfirmar.addActionListener(new ActionListener() {
			//TODO Consertar isto aqui. Está acontecendo várias vezes.
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					if (deleteLocal.isSelected()) emptyFolder(new File(Janela.local));
					if (deleteOrigem.isSelected()) emptyFolder(new File(Janela.origem));
					if (!deleteOrigem.isSelected() && !deleteLocal.isSelected()) {
						JOptionPane.showMessageDialog(optDeletar, "Nenhuma opção foi selecionada!", "Erro", JOptionPane.WARNING_MESSAGE);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		delCancelar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				optDeletar.dispose();
				return;
			}
		});
	}
}
