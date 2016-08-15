package main;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class Alterar implements ActionListener {
	private JFrame dialogo;
	private JPanel tudo;
	private JPanel botoes;
	private JButton alterarNome;
	private JButton alterarPermissao;
	private JButton conceder;
	private JButton remover;
	private JButton cancelar;
	private Icon icone;
	private JLabel texto;
	
	private int index;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		dialogo = new JFrame("Alteração");
		tudo = new JPanel();
		botoes = new JPanel();
		alterarNome = new JButton("Nome");
		alterarPermissao = new JButton("Permissões");
		conceder = new JButton("Conceder");
		remover = new JButton("Remover");
		cancelar = new JButton("Cancelar");
		icone = UIManager.getIcon("OptionPane.informationIcon");
		texto = new JLabel("Deseja alterar o nome ou as permissões?", icone, SwingConstants.LEADING);
		
		
		texto.setAlignmentX(JFrame.CENTER_ALIGNMENT);
		dialogo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialogo.add(tudo);
		tudo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		tudo.setLayout(new BoxLayout(tudo, BoxLayout.Y_AXIS));
		tudo.add(texto);
		tudo.add(botoes);
		botoes.setBorder(BorderFactory.createEmptyBorder(5, 5, 1, 5));
		botoes.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		botoes.add(alterarNome);
		botoes.add(alterarPermissao);
//		botoes.add(conceder);
//		botoes.add(remover);
		botoes.add(cancelar);
		dialogo.pack();
		dialogo.setLocationRelativeTo(null);
		dialogo.setVisible(true);
		
		alterarNome.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String novoNome = JOptionPane.showInputDialog("Digite o novo nome:");
				Gerenciador.reescreverArquivo(novoNome, index);
				JOptionPane.showMessageDialog(dialogo, "Nome alterado.");
				dialogo.dispose();
			}
		});
		
//		conceder.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Gerenciador.reescreverArquivo("true", index);
//				JOptionPane.showMessageDialog(dialogo, "Permissão concedida.");
//				dialogo.dispose();
//			}
//		});
//		
//		remover.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Gerenciador.reescreverArquivo("false", index);
//				JOptionPane.showMessageDialog(dialogo, "Permissão removida.");
//				dialogo.dispose();
//			}
//		});
		
		cancelar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialogo.dispose();
			}
		});
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
}
