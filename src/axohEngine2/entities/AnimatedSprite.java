/****************************************************************************************************
 * @author Travis R. Dewitt
 * @version 1.4
 * Date: June 19, 2015
 * 
 * Title: Animated Sprite
 * Description: A class used to hold the logic related to objects which update the images over time to
 * simulate a moving image. 
 *  
 * This work is licensed under a Attribution-NonCommercial 4.0 International
 * CC BY-NC-ND license. http://creativecommons.org/licenses/by-nc/4.0/
 ****************************************************************************************************/
//Package
package axohEngine2.entities;

//Import
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class AnimatedSprite extends Sprite {

	/*************************
	 * Variables
	 *************************/
	//animImage - Object that holds variouse image related data like x, y and size
	private ImageEntity animImage;
	
	//image - A BufferedImage java class object which is needed to display images to the window
	//tempSurface - A surface used to render a graphic before displaying it to the user
	//_name - A String indicating the name of the animating image
    BufferedImage image;
    Graphics2D tempSurface;
    public String _name;
    
    //currFrame - The current frame being displayed in the string of total possible images in the animation
    //totFrames - The total number of images used in the animation
    //delay - Time in between each frame change
    //tempDelay - A variable that can be subtracted from which when it hits zero can change the frame and revert back to the delay number
    //animating - Boolean that when false, stops the image from animating to the next frame
    //playOnce - Boolean which makes an animation play only once and then stop
    //nextAnim - The animation to switch to after playOnce has run
    //nextDelay - The next Delay for the next animation after playOnce
    //endFrame - The frame playOnce hits to switch to a different animation
    //nextTotal - Next total amount of frames for the next animation to switch to after playOnce
    private int currFrame;
    private int totFrames;
    private int delay;
    private int tempDelay;
    private boolean animating;
    public boolean playOnce = false;
    public int nextAnim;
    public int nextDelay;
    public int endFrame;
    public int nextTotal;
    
    //Moving specific animation variables (Starting, total and delay frames)
	public int leftAnim;
	public int rightAnim;
	public int upAnim;
	public int downAnim;
	public int walkFrames;
	public int walkDelay;
	
	//For unsheathing TODO:(Needs to be moved to the Attack class)
	public int unshLeft;
	public int unshRight;
	public int unshUp;
	public int unshDown;
	public int unshFrames;
	public int unshDelay;

	/***********************************************************************
	 * Constructor
	 * Detail out a personal Animated Sprite
	 * 
	 * @param frame - The JFrame window to display animations
	 * @param g2d - The Graphics2D object which displays images
	 * @param sheet - The spriteSheet which holds the image frames
	 * @param spriteNumber - The number pertaining to a frame on the spriteSheet
	 * @param name - The name of the animation, not used in logic
	 ************************************************************************/
    public AnimatedSprite(JFrame frame, Graphics2D g2d, SpriteSheet sheet, int spriteNumber, String name) {
        super(frame, g2d);
        animImage = new ImageEntity(frame);
        currFrame = 0;
        totFrames = 0;
        animating = false;
        _name = name;
        delay = 1;
        tempDelay = 1;
        
        setSheet(sheet);
        setSpriteNumber(spriteNumber);
        setAnimSprite();
    }
    
    /*********************************************************
     * Load an image in to the system for display
     * This method isn't currently used anywhere, instead there is one 
     * in the imageEntity class which loads images.
     * TODO: Is this method needed? Should this be phased out?
     * 
     * @param filename - A String file name for the image location 
     * @param width - Int width in pixels of the image
     * @param height - Int height in pixels of the image
     *********************************************************/
    public void load(String filename, int width, int height) {
        //load animation bitmap
        animImage.load(filename);

        //frame image is passed to parent class for drawing
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        tempSurface = image.createGraphics();
        setImage(image);
    }
    
    /*****************************************
     * Load a default animation for a tile or sprite
     * starting at the current sprites spritenumber
     * 
     * @param frames - Total frames in the animation
     * @param delay - Delay between when a frame advances
     *****************************************/
    public void loadAnim(int frames, int delay) {
    	currFrame = getSpriteNumber();
        if(frames > 0) {
        	setTotalFrames(frames);
        	animating = true;
        }
       	if(delay > 0) setDelay(delay);
        tempDelay = delay;
    }

    /****************************************************************
     * Set movement animations for an animated Mob or object
     * and loads the animation so loadAnim() doesn't need to be run as well
     * for each animation
     * 
     * @param spriteNumLeft - Starting frame for this animation
     * @param spriteNumRight - Starting frame for this animation
     * @param spriteNumUp - Starting frame for this animation
     * @param spriteNumDown - Starting frame for this animation
     * @param frames - Total frames in the aniamtions
     * @param delay - Delay between frames for these animations
     ****************************************************************/
    public void setMoveAnim(int spriteNumLeft, int spriteNumRight, int spriteNumUp, int spriteNumDown, int frames, int delay) {
		leftAnim = spriteNumLeft;
		rightAnim = spriteNumRight;
		upAnim = spriteNumUp;
		downAnim = spriteNumDown;
		walkFrames = frames;
		walkDelay = delay;
		
		currFrame = getSpriteNumber();
        if(frames > 0) {
        	setTotalFrames(frames);
        	animating = true;
        }
       	if(delay > 0) setDelay(delay);
        tempDelay = delay;
	}
    
    /*********************************************************************
     * This is specifically for animations pertaining to taking out weapons
     * TODO: Move this to Attack
     * 
     * @param left - Starting animation frame
     * @param right - Starting animation frame
     * @param up - Starting animation frame
     * @param down - Starting animation frame
     * @param frames - Total frames
     * @param delay - Delay between frames
     *********************************************************************/
    public void loadUnsheathAnim(int left, int right, int up, int down, int frames, int delay){
    	unshLeft = left;
    	unshRight = right;
    	unshUp = up;
    	unshDown = down;
    	unshFrames = frames;
    	unshDelay = delay;
    }
    
    /*************************************************************************
     * Load an animation from a specific point to a specific point without any additional method calls
     * 
     * @param startFrame - Self explained
     * @param totalFrames - Self explained
     * @param delay - Delay between frames
     **************************************************************************/
    public void setFullAnim(int startFrame, int totalFrames, int delay){
    	setAnimTo(startFrame);
		setTotalFrames(totalFrames);
		setDelay(delay);
		setTempDelay(delay);
    }
    
    /***********************************************************
     * Chnage the animation to a specific frame in the sequence
     * 
     * @param frame - The Int frame in the animation sequence to change to
     ***********************************************************/
    public void setFrame(int frame) { 
    	currFrame = frame; 
		animImage.setImage(setSprite(getSheet(), currentFrame()));
    }
    
    /*********************************************************
     * Change the current animation to another animation with all of the same
     * properties like delay and total frames
     * 
     * @param frame - The starting frame of the new animation
     *********************************************************/
    public void setAnimTo(int frame) {
    	currFrame = frame;
    	setSpriteNumber(frame);
		animImage.setImage(setSprite(getSheet(), currentFrame()));
    }
    
    //Stop the animation from moving on from the current frame
    public void stopAnim() {
    	if(!playOnce){
	    	animating = false; 
	    	currFrame = getSpriteNumber();
			animImage.setImage(setSprite(getSheet(), currentFrame()));
    	}
    }
    
    //Start the animation moving
    public void startAnim() { animating = true; }
    
    /*************************************************************
     * @return animating - Boolean of if an animation is running
     *************************************************************/
    public boolean animating() { return animating; }
    
    /*****************************************************************
     * Method which makes sure an animation plays once and then switches to a new one
     * 
     * @param nextAnimFrame - The next animation starting frame
     * @param nextAnimTotal - The next animation total frames
     * @param nextAnimDelay - The next animation delay
     * @param endingFrame - The final frame to hit in the current animation which triggers the change
     *****************************************************************/
    public void playOnce(int nextAnimFrame, int nextAnimTotal, int nextAnimDelay, int endingFrame) { 
    	playOnce = true; 
    	nextAnim = nextAnimFrame;
    	endFrame = endingFrame - 1;
    	nextDelay = nextAnimDelay;
    	nextTotal = nextAnimTotal;
    	animating = true;
    }
    
    //Setters for frames and images
    public void setDelay(int delay) { this.delay = delay; }
    public void setTempDelay(int delay) { tempDelay = delay; }
    public void setTotalFrames(int total) { totFrames = total; }
    public void setAnimating(boolean state) { animating = state; }
    public void setAnimImage(Image image) { animImage.setImage(image); }
    
    //Image and frame Getters
    public int delay() { return delay; }
    public int totalFrames() { return totFrames; }
    public int currentFrame() { return currFrame; }
    public Image getAnimImage() { return animImage.getImage(); }
    
    //Sprite and image data getters
    public SpriteSheet getSheet() { return super.sheet; }
    public int getSpriteNumber() { return super.spriteNumber; }
    public int getSpriteSize() { return super.spriteSize; }
    public int getScale() { return super.scale; }
    
    //Setters for image data and sprites
    public void setSheet(SpriteSheet sheet) { super.sheet = sheet; }
    public void setSpriteSize(int spriteSize) { super.spriteSize = spriteSize; }
    public void setScale(int scale) { super.scale = scale; }
    public void setSpriteNumber(int spriteNumber) { super.spriteNumber = spriteNumber; }
    
    //Used in the constructor to set an animated sprite's data using other given data
    private void setAnimSprite() {
    	animImage.setImage(setSprite(getSheet(), getSpriteNumber())); 
    	setScale(getSheet().getScale());
    	setSpriteSize(getSheet().getSpriteSize() * getScale());
    	boundSize = spriteSize;
    	currFrame = getSpriteNumber();
    }

    /***********************************************************
     * Render the current image in the animation sequence
     * 
     * @param frame - JFrame the image is rendered on to (Window)
     * @param g2d - Graphiics2D object used for showing that image
     * @param x -int position on the x axis in the room
     * @param y - int position on the y axis in the room
     ************************************************************/
    public void render(JFrame frame, Graphics2D g2d, int x, int y) {
    	entity.setX(x);
    	entity.setY(y);
		g2d.drawImage(getAnimImage(), x, y, getSpriteSize(), getSpriteSize(), frame);	
    }
    
    /**********************************************************************************
     * The core method which updates the current image in the animation sequence
     * 
     * A variable, tempDelay counts down each system update, when it hits 0, the 
     * next image in the sequence becomes the image being shown as the sprites image.
     * tempDelay then reverts back to the variable Delay and keeps running unless excplicitly 
     * told not to. If another variable, animating, is set to false, this whole loop stops.
     * A check is also run if playOnce is set to true to find if the sequence has reached
     * its desired end and should now revert to another animation.
     **********************************************************************************/
    public void updateFrame() {
    	if(animating) {
	    	tempDelay--;
	    	if(tempDelay == 0) {
	    		if(playOnce && currentFrame() == endFrame) {
	    			playOnce = false;
	    			setAnimTo(nextAnim);
	    			delay = nextDelay;
	    			setTotalFrames(nextTotal);
	    		}
	    		if(currentFrame() == getSpriteNumber() - 1 + totalFrames()) {
	    			currFrame = getSpriteNumber();
	    			animImage.setImage(setSprite(getSheet(), currentFrame()));
			    	tempDelay = delay;
	    			return;
	    		}
		    	currFrame++;
		    	tempDelay = delay;
		    	animImage.setImage(setSprite(getSheet(), currentFrame())); 
	    	}
    	}//end animating boolean check
    }//end updateFrame()
}