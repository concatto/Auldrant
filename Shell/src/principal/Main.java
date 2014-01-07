package principal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

public class Main {
	public static void main(String[] args) {
		Process p = null;
		ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/U", "/C", "dir");
		pb.redirectErrorStream(true);

		try {
			p = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			String saida;

			while ((saida = reader.readLine()) != null) {
				saida = saida.replace(String.valueOf(reader.readLine().charAt(0)), "");
				System.out.println(saida);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
}
