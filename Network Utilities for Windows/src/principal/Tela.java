package principal;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class Tela extends JFrame implements Runnable {
	private JPanel tudo = new JPanel();
	private JPanel superComandos = new JPanel();
	private JPanel superBotoes = new JPanel();
	private JPanel superTexto = new JPanel();
	private JButton testePing = new JButton("Teste de Ping");
	private JButton parar = new JButton("Parar");
	private JButton limpar = new JButton("Limpar");
	private JTextArea texto = new JTextArea("Pronto.", 20, 30);
	private JScrollPane scroll = new JScrollPane(texto);
	
	private Comando cmd = new Comando();
	private Thread thread = new Thread(this);
	
	public Tela() {
		setTitle("Network Utilities");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cmd.cancelarProcesso(false);
				dispose();
				System.exit(0);
			}
		});
		
		add(tudo);
		tudo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
		tudo.setLayout(new GridBagLayout());
		
		GridBagConstraints lay = new GridBagConstraints();
		lay.anchor = GridBagConstraints.NORTH;
		lay.gridx = 0;
		
		tudo.add(superComandos, lay);
		tudo.add(superTexto, lay);
		tudo.add(superBotoes, lay);
		superComandos.add(testePing);
		superTexto.add(scroll);
		superBotoes.add(parar);
		superBotoes.add(limpar);
		
		superBotoes.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		texto.setEditable(false);
		texto.setFont(new Font("Arial", Font.PLAIN, 16));
		texto.setBorder(BorderFactory.createEmptyBorder(1, 5, 5, 5));
		
		testePing.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cmd.executarComando("ping", "186.225.239.140", "-t");
				texto.setText("Ping");
			}
		});

		parar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cmd.cancelarProcesso(true);
			}
		});

		limpar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				texto.setText("Pronto.");
			}
		});
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		thread.start();
	}

	@Override
	public void run() {
		while (true) {
			if (cmd.getSaida() != null && !cmd.isCompleto()) {
				for (Iterator<String> iterator = cmd.getSaida().iterator(); iterator.hasNext();) {
					String string = (String) iterator.next();
					String tempo = new String("tempo=");
					int indexInicial = string.indexOf(tempo);
					int indexFinal = string.lastIndexOf("ms");
					if (indexInicial > 10) {
						texto.append("\n" + string.substring(indexInicial + tempo.length(), indexFinal));
						texto.setCaretPosition(texto.getDocument().getLength());
					}
				}
			}
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
