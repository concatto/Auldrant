package jantar;

import java.util.concurrent.Semaphore;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Jantar2 {
	public static Garfo[] garfos = {new Garfo(), new Garfo(), new Garfo(), new Garfo(), new Garfo()};
	
	static class Garfo {
		private Semaphore s = new Semaphore(1);
		
		public void get() {
			s.acquireUninterruptibly();
		}
		
		public void drop() {
			s.release();
		}
	}
	
	static class Filosofo {
		public void pensar() {
			try {
				Thread.sleep((long) (Math.random() * 30));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void pegar(int garfo) {
			Jantar2.garfos[garfo].get();
		}
		
		public void comer() {
			pensar();
		}
		
		public void soltar(int garfo) {
			Jantar2.garfos[garfo].drop();
		}
	}
	
	public Jantar2() {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		for (int i = 0; i < 5; i++) {
			Filosofo f = new Filosofo();
			JLabel l = new JLabel();
			panel.add(l);
			int index = i;
			new Thread(() -> {
				while (true) {
					l.setText("Pensando");
					f.pensar();
					l.setText("Tentando pegar " + (index + 1) % 5);
					f.pegar((index + 1) % 5);
					l.setText("Tentando pegar " + index);
					f.pegar(index);
					l.setText("Comendo");
					f.comer();
					l.setText("Soltando " + (index + 1) % 5);
					f.soltar((index + 1) % 5);
					l.setText("Soltando " + index);
					f.soltar(index);
				}
			}).start();
		}
		
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
	//	new Jantar2();
	}
}
