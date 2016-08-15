package orionis.delta.renamer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Renamer {
	public static void main(String[] args) {
		File directory = new File("D:/Games/Guild Wars 2/extracted/Sounds/mp3");
		List<File> files = new ArrayList<>(Arrays.asList(directory.listFiles()));
		
		
		files.stream().filter(Renamer::isOgg).forEach(file -> {
			Path path = file.toPath();
			try {
				Files.move(path, path.resolveSibling(convertName(file.getName())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	private static String convertName(String name) {
		return name.substring(0, name.lastIndexOf(".")).concat(".ogg");
	}
	
	private static boolean isOgg(File file) {
		try {
			FileInputStream in = new FileInputStream(file);
			byte[] bytes = new byte[95];
			in.read(bytes);
			in.close();
			return (bytes[92] == 79 && bytes[93] == 103 && bytes[94] == 103);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
