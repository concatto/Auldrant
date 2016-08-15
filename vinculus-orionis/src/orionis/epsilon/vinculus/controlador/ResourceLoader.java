package orionis.epsilon.vinculus.controlador;

import java.net.URL;

public class ResourceLoader {
	public static URL loadResource(String path) {
		return ResourceLoader.class.getResource(path);
	}
}
