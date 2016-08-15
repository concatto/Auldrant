package principal;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Audio {
	private Clip clip;
	private AudioInputStream ais;
	
	public Audio(String caminho, int loop) throws Exception{
        clip = AudioSystem.getClip();
        //getAudioInputStream() aceita File e InputStream
        ais = AudioSystem.getAudioInputStream(new File(caminho));
        clip.open(ais);
        clip.loop(loop);
    }
}
