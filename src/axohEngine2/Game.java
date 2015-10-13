/******************************************************************************
 * @author Travis R. Dewitt
 * @version 1.1
 * Date: June 14, 2015
 * 
 * Title: Axoh Engine
 * Description: This class contains all of the algorithms necessary for constructing a 2D video game.
 * 
 * 
 * This work is licensed under a Attribution-NonCommercial 4.0 International
 * CC BY-NC-ND license. http://creativecommons.org/licenses/by-nc/4.0/
 ******************************************************************************/

//Packages
package axohEngine2;

//Imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;

import javax.swing.JFrame;

import axohEngine2.data.Data;
import axohEngine2.data.Save;
import axohEngine2.entities.AnimatedSprite;
import axohEngine2.entities.Mob;
import axohEngine2.map.Map;
import axohEngine2.map.Tile;
import axohEngine2.project.STATE;
import axohEngine2.util.Point2D;

//Interface setup which implements needed java libraries
abstract class Game extends JFrame implements Runnable, KeyListener, MouseListener, MouseMotionListener {
	//For serializing(Saving system)
	private static final long serialVersionUID = 1L;

	/*********************
	 *     Variables
	 *********************/
	
	//Game loop and Thread variable(Transient means it wont be serialized, certain data types cant be serialized)
	private transient Thread gameloop;
	
	//Game lists to keep track of game specific data as well as their accessible method counterparts
	private LinkedList<AnimatedSprite> _sprites;
	public LinkedList<AnimatedSprite> sprites() { return _sprites; }
	private LinkedList<Tile> _tiles;
	public LinkedList<Tile> tiles() { return _tiles; }
	
	//Set up graphics, synchronizing, screenwidth and height
	private transient BufferedImage backBuffer;
	private transient Graphics2D g2d;
	private transient Toolkit tk;
	private int screenWidth, screenHeight;
	
	//Placeholder variable that is updated in your game, it is for saving later
	private STATE state;
	public void setGameState(STATE state) { this.state = state; }
	
	//Mouse variables
	private transient Point2D mousePos = new Point2D(0, 0);
	private boolean mouseButtons[] = new boolean[4];
	protected char currentChar;
	
	//File variables
	private Data data;
	protected Save save;
		
	//Time and frame rate variables
	private int _frameCount = 0;
	private int _frameRate = 0;
	private int desiredRate;
	private long startTime = System.currentTimeMillis();
	
	//Pause game state
	private boolean _gamePaused = false;
	public boolean gamePaused() { return _gamePaused; }
	public void pauseGame() { _gamePaused = true; }
	public void resumeGame() { _gamePaused = false; }
		
	//Game event methods - All of these will be inherited by a child class
	abstract void gameStartUp();
	abstract void gameTimedUpdate();
	abstract void gameRefreshScreen();
	abstract void gameShutDown();
	abstract void gameKeyDown(int keyCode);
    abstract void gameKeyUp(int keyCode);
    abstract void gameMouseDown();
    abstract void gameMouseUp();
    abstract void gameMouseMove();
	abstract void spriteUpdate(AnimatedSprite sprite);
	abstract void spriteDraw(AnimatedSprite sprite);
	abstract void spriteDying(AnimatedSprite sprite);
	abstract void spriteCollision(AnimatedSprite spr1, AnimatedSprite spr2, int hitDir, int hitDir2);
	abstract void tileCollision(AnimatedSprite spr, Tile tile, int hitDir, int hitDir2);
	
	/***************************************************************
	 * Constructor - Initialize the frame, the backBuffer, the game lists, and any othervariables
	 * 
	 * @param frameRate - An Int to give a desired framrate for the game
	 * @param width - An Int defining the width of the window
	 * @param height - An Int defining the height of the window
	 ****************************************************************/
	public Game(int frameRate, int width, int height) {
		//Set up JFrame window
		Dimension size = new Dimension(width, height);
		setPreferredSize(size);
		setSize(size);
		pack();
		
		//Store parameters in a variables
		desiredRate = frameRate;
		screenWidth = width;
		screenHeight = height;
		
		//Set up backbuffer and graphics and synchronization
		backBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
		g2d = backBuffer.createGraphics();
        tk = Toolkit.getDefaultToolkit();
        
        state = null;

        //Create the internal lists
        _sprites = new LinkedList<AnimatedSprite>();
        _tiles = new LinkedList<Tile>();
        
        //Initialize data related variables
        data = new Data();
		save = new Save();
        
		//Add the listeners to the frame
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
       
        
        //Start the game
        gameStartUp();
	}
	
	/********************************************************
	 * Get the graphics used in-game for use in child-classes
	 * 
	 * @return Graphics2D - Graphics object
	 ********************************************************/
	public Graphics2D graphics() { return g2d; }
	
	/*******************************************************
	 * @return framerate - An Int pertaining to your games framerate
	 ******************************************************/
	public int frameRate() { return _frameRate; }
	
	//Mouse events
	/*******************************************************
	 * @param btn - Each mouse button is labeled with an Int, this number picks that button
	 * @return boolean - Is the mouse button specified being pressed
	 *******************************************************/
	public boolean mouseButton(int btn) { return mouseButtons[btn]; }
	
	/*******************************************************
	 * @return Point2D - Retrive an x and y datatype of the mouse position
	 * 
	 * Currently may not work, using built in java methods may work better.
	 * Unused
	 ******************************************************/
	public Point2D mousePosition() { return mousePos; }
	
	/******************************************************
	 * @param g - Graphics used to render objects
	 * Override the JFrames update method and insert custom updating methods
	 ******************************************************/
	public void update(Graphics g) {
		//Make sure the game renders as fast as possible but only runs the framerate amount of updates per second
		_frameCount++;
		if(System.currentTimeMillis() > startTime + 1000) {
			startTime = System.currentTimeMillis();
			_frameRate = _frameCount;
			_frameCount = 0;
			
			purgeSprites(); 
		}
			drawSprites();
			paint(g);
			gameRefreshScreen();
	}
	
	/******************************************************
	 * @param g - The Systems Graphics object
	 * 
	 * Override the frames Paint method, draw the backBuffer and sync with the system
	 * The purpose of this is to solve any strange rendering glitches, doing it this way
	 * allows for an image to be designed in the background and then brought forward all at once.
	 ******************************************************/
	public void paint(Graphics g) {
		g.drawImage(backBuffer, 8, 30, this); // 8x30 does whole thing idk whatever
		tk.sync();
	}
	

	
	//Start the game loop - initialize the Thread
	public void start() {
		gameloop = new Thread(this);
		gameloop.start();
	}
	
	//Using Runnable, run a loop which calls update methods for specific actions including graphics and collisions
	public void run() {
		Thread t = Thread.currentThread();
		//Basically - While this new thread is equal to the thread we make at startup, repeat
		while(t == gameloop) {
			try { 
				Thread.sleep(1000 / desiredRate);
			} catch(InterruptedException e) { e.printStackTrace(); }
			
			//If the game is not paused, run specific update methods
		//	if(!gamePaused()) {    // Unfortunately gamePaused isn't implemented correctly
			if(isActive()) { // checking if window isActive will only allow updating when window has focus (otherwise it pauses
				gameTimedUpdate();
				updateSprites();
		//		spriteCollision();
		//		tileCollision();
				
				//Render the graphics
				update(graphics());
				repaint();
			}
			
			
			//Render the graphics
			
			// I moved the Update Graphics and Repaint into the actual game loop (where they should be)
			// Just in case anything messes up though, we'll put them back, but it normal games they should be in the game loop
			// This is because if the game is paused, the game should stop drawing
		}
	}
	
	//End the game with this method call
	public void stop() {
		gameloop = null;
		gameShutDown();
	}
	
	/***********************************************************
	 * @param fileName - A string filename
	 * The file given is the loaded as the current game state
	 * This method never needs to be touched as the only thing that is serialized is the 'data.java' class
	 * To access the loaded data, use 'data' the variable
	 ***********************************************************/
	public void loadData(String fileName) {
		FileInputStream file_in = null;
		ObjectInputStream reader = null;
		Object obj = null;
		try {
			//file_in = new FileInputStream("C:/gamedata/saves/" + fileName);
			file_in = new FileInputStream(System.getProperty("user.dir") + "\\bin\\saves\\" + fileName);
			
			reader = new ObjectInputStream (file_in);
			System.out.println("Load successful.");
			obj = reader.readObject();
		} catch(IOException | ClassNotFoundException e) {}
		if(obj instanceof Data) data = (Data) obj;
	}
	
	/**********************************************************************
	 * @return Data
	 * Get the current 'Data.java' class instance
	 *********************************************************************/
	public Data data() { return data; }
	
	/**********************************************************************
	 * @param k - A KeyEvent
	 * Key Listener Methods
	 * These methods apply the java methods to my personal more flexible ones
	 *********************************************************************/
	public void keyTyped(KeyEvent k) { setKeyChar(k.getKeyChar()); }
    public void keyPressed(KeyEvent k) { gameKeyDown(k.getKeyCode()); }
    public void keyReleased(KeyEvent k) { gameKeyUp(k.getKeyCode()); }
    
    /**********************************************************************
     * Mouse Listener events
     * Inherited Method
     * @param e - A MouseEvent action which will change a number that coordinates with having pressed that button
     *********************************************************************/
    private void checkButtons(MouseEvent e) {
        switch(e.getButton()) {
        case MouseEvent.BUTTON1:
            mouseButtons[1] = true;
            mouseButtons[2] = false;
            mouseButtons[3] = false;
            break;
        case MouseEvent.BUTTON2:
            mouseButtons[1] = false;
            mouseButtons[2] = true;
            mouseButtons[3] = false;
            break;
        case MouseEvent.BUTTON3:
            mouseButtons[1] = false;
            mouseButtons[2] = false;
            mouseButtons[3] = true;
            break;
        }
	}
	
    /**********************************************************************
     * @param e -A MouseEvent that updates a mouses position after being pressed
     * Inherited Method
     *********************************************************************/
	public void mousePressed(MouseEvent e) {
	    checkButtons(e);
	    mousePos.setX(e.getX());
	    mousePos.setY(e.getY());
	    gameMouseDown();
	}
	
	/**********************************************************************
	 * @param e - MouseEvent that updates a mouses position after being released
     * Inherited Method
	 *********************************************************************/
	public void mouseReleased(MouseEvent e) {
	    checkButtons(e);
	    mousePos.setX(e.getX());
	    mousePos.setY(e.getY());
	    gameMouseUp();
	}
	
	/**********************************************************************
	 * @param e - MouseEvent that updates a mouses position after being moved
     * Inherited Method
	 *********************************************************************/
	public void mouseMoved(MouseEvent e) {
	    checkButtons(e);
	    mousePos.setX(e.getX());
	    mousePos.setY(e.getY());
	    gameMouseMove();
	}
	
	/**********************************************************************
	 * @param e - MouseEvent that updates a mouses position after being Dragged
     * Inherited Method
	 *********************************************************************/
	public void mouseDragged(MouseEvent e) {
	    checkButtons(e);
	    mousePos.setX(e.getX());
	    mousePos.setY(e.getY());
	    gameMouseDown();
	    gameMouseMove();
	}
	
	/**********************************************************************
	 * @param e - MouseEvent that updates a mouses position after being Entered
     * Inherited Method
	 *********************************************************************/
	public void mouseEntered(MouseEvent e) {
	    mousePos.setX(e.getX());
	    mousePos.setY(e.getY());
	    gameMouseMove();
	}
	
	/**********************************************************************
	 * @param e - MouseEvent that updates a mouses position after being Exited
     * Inherited Method
	 *********************************************************************/
	public void mouseExited(MouseEvent e) {
	    mousePos.setX(e.getX());
	    mousePos.setY(e.getY());
	    gameMouseMove();
	}
	
	/**********************************************************************
	 * @param e - MouseEvent that runs after being Clicked
     * Inherited Method
	 *********************************************************************/
	public void mouseClicked(MouseEvent e) { }
	
	/**********************************************************************
	 * @param index - An int 1, 2, or 3
	 * @return boolean - If that mouse button is being pressed, return true
	 *********************************************************************/
	public boolean getMouseButtons(int index) { return mouseButtons[index]; }
	
	/**********************************************************************
	 * Set the current key being pressed to the currentChar variable
	 * The purpose of this if for typeing on screen. Once the keycode's
	 * char is passed in, that currentChar variable can be accessed at that
	 * time for typing, instead of accessing hundreds of keycodes and chars.
	 * 
	 * @param keyChar - A char of what is being pressed down
	 *********************************************************************/
	public void setKeyChar(char keyChar) { currentChar = keyChar; }

	/**********************************************************************
	 * Return an angle of X or Y value based on a degree and return it in radians
	 * @param angle - A Double from 0 to 360
	 * @return - A Double defining an angle in Radians
	 *********************************************************************/
	protected double calcAngleMoveX(double angle) { return (double) (Math.cos(angle * Math.PI / 180)); }
	protected double calcAngleMoveY(double angle) { return (double) (Math.sin(angle * Math.PI / 180)); }
	
	//update all the sprites in the current list if they are alive
	protected void updateSprites() {
		for(int i = 0; i < _sprites.size(); i++) {
			AnimatedSprite spr = (AnimatedSprite) _sprites.get(i); //The sprite type must be cast because many kinds of sprites can be stored in the list
			if(spr.alive()) {
				spriteUpdate(spr);
				if(state == STATE.GAME) if(spr instanceof Mob) ((Mob) spr).updateMob(); //When the game is running, update Mobs
			}
			spriteDying(spr);
		}
	}
	
	/*****************************************************************************
	 * Update the data object with all of the currently needed variables.
	 * This can be updated in the future after the 'Data.java' class has been
	 * correctly configured to allow for more parameters. The purpose of this
	 * is for storing data in a file for reloading an old file with that info.
	 * 
	 * This method is called in your game for saving
	 * 
	 * @param currentMap - A Map
	 * @param currentOverlay - A Map
	 * @param playerX - An Int
	 * @param playerY - An Int
	 *******************************************************************************/
	protected void updateData(Map currentMap, Map currentOverlay, int playerX, int playerY) {
		data.update(currentMap.mapName(), currentOverlay.mapName(), playerX, playerY);
	}
	
	/***********************************************************************
	 * Detect when a sprite intersects a sprite and call a handling method
	 * currently only rectangles are used for detection.
	 * 
	 * spr1 - The first sprite (Most of the time the player)
	 * spr2 - The second sprite (Most of the time a random npc or enemy)
	 * hitDir - The bound which is being intersected on spr1
	 * hitDir2 - The bound which is being intersected on spr2
	**************************************************************************/
	protected void spriteCollision() {
		for(int i = 0; i < _sprites.size(); i++) {
			AnimatedSprite spr1 = _sprites.get(i);
			for(int j = 0; j < _sprites.size(); j++) {
				if(_sprites.get(j) == spr1) continue;
				AnimatedSprite spr2 = _sprites.get(j);
				if(!spr1.hasMultBounds() && !spr2.hasMultBounds()){
					if(spr1.getBounds().intersects(spr2.getBounds())) spriteCollision(spr1, spr2, -1, -1); //spr1 and spr2 have one bound
				} else {
					if(spr1.hasMultBounds() && !spr2.hasMultBounds()){ //spr1 has multiple bounds but not spr2
						if(spr1.checkLeftBound(spr2.getBounds())) spriteCollision(spr1, spr2, 0, -1);
				   		if(spr1.checkRightBound(spr2.getBounds())) spriteCollision(spr1, spr2, 1, -1);
				   		if(spr1.checkHeadBound(spr2.getBounds())) spriteCollision(spr1, spr2, 2, -1);
				   		if(spr1.checkLegBound(spr2.getBounds())) spriteCollision(spr1, spr2, 3, -1);
					}
					if(spr2.hasMultBounds() && !spr1.hasMultBounds()){ //spr2 has multiple bounds but not spr1
						if(spr2.checkLeftBound(spr1.getBounds())) spriteCollision(spr1, spr2, -1, 0);
				   		if(spr2.checkRightBound(spr1.getBounds())) spriteCollision(spr1, spr2, -1, 1);
				   		if(spr2.checkHeadBound(spr1.getBounds())) spriteCollision(spr1, spr2, -1, 2);
				   		if(spr2.checkLegBound(spr1.getBounds())) spriteCollision(spr1, spr2, -1, 3);
					}
					if(spr2.hasMultBounds() && spr1.hasMultBounds()){ //spr2 has multiple bounds as well as spr1
						if(spr1.checkLeftBound(spr2.getLeftBound())) spriteCollision(spr1, spr2, 0, 0);
						if(spr1.checkLeftBound(spr2.getRightBound())) spriteCollision(spr1, spr2, 0, 1);
						if(spr1.checkLeftBound(spr2.getHeadBound())) spriteCollision(spr1, spr2, 0, 2);
						if(spr1.checkLeftBound(spr2.getLegBound())) spriteCollision(spr1, spr2, 0, 3);

						if(spr1.checkRightBound(spr2.getLeftBound())) spriteCollision(spr1, spr2, 1, 0);
						if(spr1.checkRightBound(spr2.getRightBound())) spriteCollision(spr1, spr2, 1, 1);
						if(spr1.checkRightBound(spr2.getHeadBound())) spriteCollision(spr1, spr2, 1, 2);
						if(spr1.checkRightBound(spr2.getLegBound())) spriteCollision(spr1, spr2, 1, 3);
						
						if(spr1.checkHeadBound(spr2.getLeftBound())) spriteCollision(spr1, spr2, 2, 0);
						if(spr1.checkHeadBound(spr2.getRightBound())) spriteCollision(spr1, spr2, 2, 1);
						if(spr1.checkHeadBound(spr2.getHeadBound())) spriteCollision(spr1, spr2, 2, 2);
						if(spr1.checkHeadBound(spr2.getLegBound())) spriteCollision(spr1, spr2, 2, 3);
						
						if(spr1.checkLegBound(spr2.getLeftBound())) spriteCollision(spr1, spr2, 3, 0);
						if(spr1.checkLegBound(spr2.getRightBound())) spriteCollision(spr1, spr2, 3, 1);
						if(spr1.checkLegBound(spr2.getHeadBound())) spriteCollision(spr1, spr2, 3, 2);
						if(spr1.checkLegBound(spr2.getLegBound())) spriteCollision(spr1, spr2, 3, 3);
					}
				}//end mult bounds checks
			}//end inner for
		}//end outer for
	}
	
	/**********************************************************************
	 * Same as the above spriteCollision() method but instead the collision is between
	 * a sprite and a Tile. Also, currently only with rectangles. 
	 * 
	 * The method gets a sprite and then gets each tile, if either objects intersects 
	 * any bounds made for either object the method calls a handling method 
	 * for dealing with very specific properties that are relative to each game
	 ***********************************************************************/
	protected void tileCollision() {
		for(int i = 0; i < _sprites.size(); i++) {
			AnimatedSprite spr = _sprites.get(i);
			for(int j = 0; j < _tiles.size(); j++) {
				Tile tile = _tiles.get(j);
				if(!spr.hasMultBounds() && !tile.hasMultBounds()) { //tile and spr have only one bound
					if(tile.getTileBounds().intersects(spr.getBounds())) tileCollision(spr, tile, -1, -1);
				} else {
					if(spr.hasMultBounds() && !tile.hasMultBounds()){ //spr has multiple bounds, not tile
				   		if(spr.checkLeftBound(tile.getTileBounds())) tileCollision(spr, tile, 0, -1);
				   		if(spr.checkRightBound(tile.getTileBounds())) tileCollision(spr, tile, 1, -1);
				   		if(spr.checkHeadBound(tile.getTileBounds())) tileCollision(spr, tile, 2, -1);
				   		if(spr.checkLegBound(tile.getTileBounds())) tileCollision(spr, tile, 3, -1);
					}
					if(tile.hasMultBounds() && !spr.hasMultBounds()){ //tile has multiple bounds, not spr
						if(tile.checkLeftBound(spr.getBounds())) tileCollision(spr, tile, -1, 0);
				   		if(tile.checkRightBound(spr.getBounds())) tileCollision(spr, tile, -1, 1);
				   		if(tile.checkHeadBound(spr.getBounds())) tileCollision(spr, tile, -1, 2);
				   		if(tile.checkLegBound(spr.getBounds())) tileCollision(spr, tile, -1, 3);
					}
					if(tile.hasMultBounds() && spr.hasMultBounds()){ //spr has multiple bounds as well as tile
						if(spr.checkLeftBound(tile.getLeftBound())) tileCollision(spr, tile, 0, 0);
						if(spr.checkLeftBound(tile.getRightBound())) tileCollision(spr, tile, 0, 1);
						if(spr.checkLeftBound(tile.getHeadBound())) tileCollision(spr, tile, 0, 2);
						if(spr.checkLeftBound(tile.getLegBound())) tileCollision(spr, tile, 0, 3);

						if(spr.checkRightBound(tile.getLeftBound())) tileCollision(spr, tile, 1, 0);
						if(spr.checkRightBound(tile.getRightBound())) tileCollision(spr, tile, 1, 1);
						if(spr.checkRightBound(tile.getHeadBound())) tileCollision(spr, tile, 1, 2);
						if(spr.checkRightBound(tile.getLegBound())) tileCollision(spr, tile, 1, 3);
						
						if(spr.checkHeadBound(tile.getLeftBound())) tileCollision(spr, tile, 2, 0);
						if(spr.checkHeadBound(tile.getRightBound())) tileCollision(spr, tile, 2, 1);
						if(spr.checkHeadBound(tile.getHeadBound())) tileCollision(spr, tile, 2, 2);
						if(spr.checkHeadBound(tile.getLegBound())) tileCollision(spr, tile, 2, 3);
						
						if(spr.checkLegBound(tile.getLeftBound())) tileCollision(spr, tile, 3, 0);
						if(spr.checkLegBound(tile.getRightBound())) tileCollision(spr, tile, 3, 1);
						if(spr.checkLegBound(tile.getHeadBound())) tileCollision(spr, tile, 3, 2);
						if(spr.checkLegBound(tile.getLegBound())) tileCollision(spr, tile, 3, 3);
					}
				}
		} //end _tiles for loop
		} //end _sprites for loop
	}
	
	//Draw animated sprites automatically, they must be in the list (Includes tiles)
	protected void drawSprites() {
		for(int i = 0; i < _sprites.size(); i++) {
			AnimatedSprite spr = (AnimatedSprite) _sprites.get(i);
			if(spr.alive()) {
				spr.updateFrame();
				spriteDraw(spr);
			}
		}
		for(int i = 0; i < _tiles.size(); i++) _tiles.get(i).updateFrame();
	}
	
	//Delete the sprite that has been killed from the system
	private void purgeSprites() {
		for(int i = 0; i < _sprites.size(); i++) {
			AnimatedSprite spr = _sprites.get(i);
			if(spr.alive() == false) _sprites.remove(i);
		}
	}
	
	/*********************************************************************
	 * @param tile - A Tile to be added in to the system
	 * 
	 * Instead of just adding all of the tiles in a Map to the system for updating,
	 * use this method to add a layer of choice(filter). This method currently only 
	 * allows Tiles which have properties - solid, event, breakable, etc..
	 * 
	 * The purpose of this is because there could be thousands of tiles in a single map
	 * not all of these tiles have properties that need updateing like animations.
	 * This allows for a much faster, smoother game experience as well as larger maps.
	 *********************************************************************/
	void addTile(Tile tile) {
		if(tile.hasProperty()) tiles().add(tile);
	}
	
	/**********************************************************************
	 * @param g2d - Gaphics used to display to the JFrame
	 * @param text - The String of text to alter
	 * @param x - An Int position relating to the X position the text will be rendered on screen
	 * @param y - An Int position relating to the Y position the text will be rendered on screen
	 * 
	 * Special drawString method which takes an extra parameter. This allows for a
	 * newLine character to be used which removes the need for seperate drawString
	 * method calls in your code. Other special actions could be added here in the future.
	 * 
	 * '\n' makes a new line
	 **********************************************************************/
	void drawString(Graphics2D g2d, String text, int x, int y) {
        for(String line : text.split("\n")) g2d.drawString(line, x, y += g2d.getFontMetrics().getHeight());
    }
	
}