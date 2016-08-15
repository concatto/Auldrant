import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Mover {
	public Mover() {
		try {
			Path directory = Files.createDirectories(Paths.get("C:/Users/Fernando/Music/Rockman"));
			
			Map<Character, List<Path>> map = Files.list(Paths.get("C:/Rockman"))
					.collect(Collectors.groupingBy(e -> e.toFile().getName().charAt(0)));
			
			map.forEach((number, list) -> {
				try {
					int value = Character.getNumericValue(number) + 1;
					Path subDirectory = Files.createDirectories(directory.resolve("Mega Man X" + value));
					list.parallelStream().forEach(e -> {
						String title = getTitle(e.toFile()) + ".mp3";
						try {
							Path destination = subDirectory.resolve(title);
							if (!Files.exists(destination)) Files.copy(e, destination);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					});
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Mover();
	}
	
	private String getTitle(File file) {
		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
			byte[] start = "TIT2".getBytes();
			byte[] length = new byte[4];
			
			int startCount = 0;
			int b;
			while ((b = in.read()) != -1) {
				if (b == start[startCount]) {
					startCount++;
				} else {
					startCount = 0;
					if (b == start[startCount]) startCount++;
				}
				
				if (startCount == start.length) {
					in.read(length);
					byte[] title = new byte[toLength(length)];
					in.skip(2);
					in.read(title);
					
					return new String(title).trim().replace("\"", "");
				}
			}
			
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
	private int toLength(byte[] bytes) {
		int length = 0;
		int unsigned;
		for (int i = 0; i < bytes.length; i++) {
			unsigned = (bytes[i] & 0xFF);
			length += unsigned * Math.pow(16, (bytes.length - 1 - i) * 2);
		}
		return length;
	}
}
