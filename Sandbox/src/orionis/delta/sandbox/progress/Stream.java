package orionis.delta.sandbox.progress;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Stream {
	public Stream(String origem, String destino) {
		try {
			File original = new File("C:\\28_6_2014_23_35_01.mp3");
			File copia = new File("C:\\testecopy.mp3");
			
			FileInputStream in = new FileInputStream(original);
			FileOutputStream out = new FileOutputStream(copia);
			System.out.println(copiar(in, out));
			in.close();
			out.close();
			
			BufferedInputStream inBuffered = new BufferedInputStream(new FileInputStream(original));
			BufferedOutputStream outBuffered = new BufferedOutputStream(new FileOutputStream(copia));
			System.out.println(copiar(inBuffered, outBuffered));
			inBuffered.close();
			outBuffered.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public long copiar(InputStream origem, OutputStream destino) throws IOException {
		long inicio = System.currentTimeMillis();
		
		int theByte;
		while ((theByte = origem.read()) != -1) {
			destino.write(theByte);
		}
		
		destino.flush();
		return System.currentTimeMillis() - inicio;
	}
}
