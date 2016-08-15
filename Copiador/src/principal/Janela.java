package principal;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

@SuppressWarnings("serial")
public class Janela extends JFrame{
	private JPanel tudo = new JPanel();	
	private JButton selecionar = new JButton("Selecionar destino...");
	private JButton copiar = new JButton("Copiar");
	private JButton deletar = new JButton("Deletar");
	private JButton restaurar = new JButton("Restaurar");
	private JButton atualizar = new JButton("Atualizar");	
	private JTextField caminhoOriginal = new JTextField();
	private JTextField caminhoLocal = new JTextField();
	
	private Copiar copy = new Copiar();
	private Restaurar restore = new Restaurar();
	private Deletar delete = new Deletar();	
	private Selecionar choose = new Selecionar();
	
	static String driveID;
	static String local;	//Caminho do computador hospedeiro
	static String origem;	//Caminho do flash drive
	
	private BufferedReader in;
	
	public Janela(){
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File[] f = File.listRoots();
		for (int i = 1; i < f.length; i++) {
			if (fsv.getSystemDisplayName(f[i]).contains("ELYSIUM")) driveID = f[i].getPath();
		}
		setSize(260,200);
		setLocationRelativeTo(null);
		setTitle("Workspace Manager");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		
		tudo.setLayout(new GridBagLayout());
		add(tudo);
		tudo.add(Box.createRigidArea(new Dimension(5,3)));
		
		GridBagConstraints lay = new GridBagConstraints();
		lay.fill=GridBagConstraints.HORIZONTAL;
		lay.weightx=1;
		lay.weighty=1;
		lay.gridwidth=3;
		lay.insets=new Insets(3, 6, 3, 1);
		
		lay.gridx=0;
		lay.gridy=0;
		tudo.add(selecionar, lay);
		lay.gridy=1;
		lay.gridwidth=1;
		tudo.add(copiar, lay);
		lay.gridx=1;
		tudo.add(deletar, lay);
		lay.gridx=2;
		tudo.add(restaurar, lay);
		lay.gridwidth=3;
		lay.gridy=2;
		lay.gridx=0;
		caminhoOriginal.setColumns(15);
		caminhoOriginal.setEditable(false);
		caminhoOriginal.setHorizontalAlignment(JTextField.CENTER);
		tudo.add(caminhoOriginal, lay);
		lay.gridy=3;
		caminhoLocal.setColumns(15);
		caminhoLocal.setEditable(false);
		caminhoLocal.setHorizontalAlignment(JTextField.CENTER);
		tudo.add(caminhoLocal, lay);
		lay.gridy=4;
		lay.gridx=0;
		tudo.add(atualizar, lay);
		tudo.add(Box.createRigidArea(new Dimension(5,3)));
		
		setVisible(true);
		
		try {
			in = new BufferedReader(new FileReader(driveID + "Eclipse\\path.txt"));
			local = in.readLine();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		origem = (driveID + "Eclipse\\workspace");
		
		selecionar.addActionListener(choose);		
		copiar.addActionListener(copy);
		deletar.addActionListener(delete);
		restaurar.addActionListener(restore);
		atualizar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					in = new BufferedReader(new FileReader(Janela.driveID + "Eclipse\\path.txt"));
					JOptionPane.showMessageDialog(Janela.this, "Arquivos lidos com sucesso.", "Arquivos lidos!", JOptionPane.INFORMATION_MESSAGE);
					local = in.readLine();
					caminhoOriginal.setText(origem);
					caminhoLocal.setText(local);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		});
	}
}