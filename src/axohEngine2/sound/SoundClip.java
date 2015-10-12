/**********************************************************************
 * @author Travis R. Dewitt
 * @version 0.8
 * Date: June 15, 2015
 * 
 * Title: SoundClip 
 * Description: Construct a Sound Clip file object.
 * This is merely some framework and is currently unused in the given game, Judgement.
 * 
 * This class should work, however is it untested.
 * 
 * This work is licensed under a Attribution-NonCommercial 4.0 International
 * CC BY-NC-ND license. http://creativecommons.org/licenses/by-nc/4.0/
 *********************************************************************/

package axohEngine2.sound;

import javax.sound.sampled.*;
import java.io.*;
import java. net.*;

public class SoundClip {
	//WAV AIFF AU
	//Get and audio data object
	private AudioInputStream sample;
	
	//Sound clip data
	private Clip clip;
	public Clip getClip() { return clip; }
	
	//looping property for continuous playback
	private boolean looping = false;
	public void setLooping(boolean _looping) { looping = _looping; }
	public boolean getLooping() {return looping; }
	
	//Repeat sound multiple times
	private int repeat = 0;
	public void setRepeat(int _repeat) { repeat = _repeat; }
	public int getRepeat() { return repeat; }
	
	//Filename properties
	private String filename = "";
	public void setFilename(String _filename) { filename = _filename; }
	public String getFilename() { return filename; }
	
	//Check for a ready sample
	public boolean isLoaded() {
		return (boolean)(sample != null);
	}
	
	//Constructor
	public SoundClip() {
		try {
			//sound buffer
			clip = AudioSystem.getClip();
		} catch(LineUnavailableException e) {}
	}
	
	private URL getURL(String filename) {
		URL url = null;
		try {
			url = this.getClass().getResource(filename);
		} catch(Exception e) {}
		return url;
	}
	
	//Load a soundfile
	public boolean load(String audiofile) {
		try {
			//Prepare the input stream for a file
			setFilename(audiofile);
			//Set audio stream source
			sample = AudioSystem.getAudioInputStream(getURL(filename));
			//Load the file
			clip.open(sample);
			return true;
		} catch (IOException e){ return false; } catch (UnsupportedAudioFileException e) { return false; } catch (LineUnavailableException e) { return false; }
	}
	
	public void play() {
		//Exit if the sample hasn't been loaded first
		if(!isLoaded()) return;
		
		//reset the sound clip
		clip.setFramePosition(0);
		
		//play the sample with an option of looping
		if(looping)
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		else
			clip.loop(repeat);
	}
	
	public void stop() {
		clip.stop();
	}
}
