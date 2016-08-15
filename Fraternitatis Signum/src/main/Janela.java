package main;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Janela extends JFrame{
	private static final long serialVersionUID = 1L;
	private JPanel painel = new JPanel();
	private JPanel painelBotoesInferiores = new JPanel();
	private JPanel painelBotoesSuperiores = new JPanel();
	private JButton atualizar = new JButton("Atualizar");
	private JButton adicionar = new JButton("Adicionar");
	private JButton remover = new JButton("Remover");
	private JButton alterar = new JButton("Alterar");
	private JButton verificar = new JButton("Verificar status");
	private JComboBox<Object> box;
	
	private Leitor leitor = new Leitor();
	
	public Janela() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Fraternitatis Signum");
		
		box = new JComboBox<>();
		leitor.atualizar(box);
		
		atualizar.setAlignmentX(CENTER_ALIGNMENT);
		add(painel);
		painel.setBorder(BorderFactory.createEmptyBorder(5, 5, 1, 5));
		painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
		painel.add(painelBotoesSuperiores);
		painel.add(Box.createRigidArea(new Dimension(0, 4)));
		painel.add(box);
		painel.add(painelBotoesInferiores);
		
		painelBotoesSuperiores.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		painelBotoesSuperiores.add(atualizar);
		painelBotoesSuperiores.add(verificar);
		
		painelBotoesInferiores.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		painelBotoesInferiores.add(adicionar);
		painelBotoesInferiores.add(remover);
		painelBotoesInferiores.add(alterar);
		
		configurarListeners();
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void configurarListeners() {
		atualizar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				leitor.atualizar(box);
			}
		});
		
		final Alterar acaoAlterar = new Alterar();
		alterar.addActionListener(acaoAlterar);
		alterar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				acaoAlterar.setIndex(box.getSelectedIndex());
			}
		});
		
		verificar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				leitor.atualizar(box);
				int index = box.getSelectedIndex();
				String estado = ((leitor.getStatus().get(index)) == true) ? " " : " não ";
				String texto = leitor.getMembros().get(index) + estado + "pode saquear o item.";
				JOptionPane.showMessageDialog(painel, texto);
			}
		});
	}
}
