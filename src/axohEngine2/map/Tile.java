/****************************************************************************************************
 * @author Travis R. Dewitt
 * @version 1.2
 * Date: June 19, 2015
 * 
 * Title: Tile
 * Description: A class which contains an image, a possibility for animation and other special properties
 * 
 * This work is licensed under a Attribution-NonCommercial 4.0 International
 * CC BY-NC-ND license. http://creativecommons.org/licenses/by-nc/4.0/
 ****************************************************************************************************/
//Packages
package axohEngine2.map;

//Imports
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.LinkedList;

import javax.swing.JFrame;

import axohEngine2.entities.AnimatedSprite;
import axohEngine2.entities.Mob;
import axohEngine2.entities.SpriteSheet;
import axohEngine2.project.TYPE;

public class Tile extends AnimatedSprite {
	
	/**************************
	 * Variables
	 **************************/
	//_solid, _slippery, _breakable - Boolean properties that can be checked in a tile to perform special actions
	//hasEvent and hasMob - vey special booleans that check for the possibility for an event or a mob from that tile
	private boolean _solid;
	private boolean _slippery;
	private boolean _breakable;
	private boolean hasEvent = false;
	private boolean hasMob = false;
	
	//event and mob - Variables that hold objects stored in the tile which can be accessed and/or changed in that tile
	private Event event;
	private Mob mob;
	
	/*hasProperty - boolean to check if a tile has a property like solidity, this is here to make sure so many multiple checks 
	are done on each tile. So if a plain tile is checked in the game loop, it can be passed over without mny additional checks.*/
	private boolean hasProperty = false;
	
	// **************************************************************************************************************
	// Stuff Reaction Tech Solutions Added (some repeats...I just wanted to keep my stuff away from Travis')
	
	// Variables for Tiles
	private boolean isSolid;  // tile is fully solid
	private boolean isPassable; // tile is fully passable
	private boolean isPartial; // tile is solid at one point and passable at another
	private Rectangle location; // location of tile on map
	private Rectangle solidBounds; // solidBounds of tile (where tile is solid) [for isPartial]
	private TileEventType eventType; // type of event it will have
	
	// Constructor
	public Tile(JFrame frame, Graphics2D g2d, String name, SpriteSheet sheet, int spriteNumber, boolean isSolid,
			boolean isPassable, boolean isPartial, Rectangle location, Rectangle solidBounds, TileEventType eventType)
	{
		super(frame, g2d, sheet, spriteNumber, name); // needed...
		this.isSolid = isSolid;
		this.isPassable = isPassable;
		this.isPartial = isPartial;
		this.location = location;
		this.solidBounds = solidBounds;
		this.eventType = eventType;	
		setSprite(sheet, spriteNumber); //Set the object image to 
	}
		
	// Getters
	public boolean getIsSolid() { return isSolid; }
	public boolean getIsPassable() { return isPassable; }
	public boolean getIsPartial() { return isPartial; }
	public Rectangle getLocation() { return location; }
	public Rectangle getSolidBounds() { return solidBounds; }
	public TileEventType getEventType() { return eventType; }
	
	// Setters
	public void setIsSolid(boolean isSolid) { this.isSolid = isSolid; }
	public void setIsPassable(boolean isPassable) { this.isPassable = isPassable; }
	public void setIsPartial(boolean isPartial) { this.isPartial = isPartial; }
	public void setLocation(Rectangle location) { this.location = location; }
	public void setSolidBounds(Rectangle solidBounds) { this.solidBounds = solidBounds; }
	public void setEventType(TileEventType eventType) { this.eventType = eventType; }
	
	
	// End Reaction Tech Solutions
	// **************************************************************************************************************
	
	/******************************************************************************************
	 * Default constructor
	 * 
	 * The purpose of multiple constructors is to cut down on possible parameters for special properties the tile may have.
	 *
	 * @param frame - JFrame the tile will be displayed on (The window)
	 * @param g2d - Graphics2D object which displays graphics to a JFrame
	 * @param name - String name of a tile which is strictly a user identifier, not used in logic
	 * @param sheet - The spriteSheet object which the tile graphic can be found on
	 * @param spriteNumber - The number in the spriteSheet that the graphic can be found on
	 ******************************************************************************************/
	public Tile(JFrame frame, Graphics2D g2d, String name, SpriteSheet sheet, int spriteNumber) {
		super(frame, g2d, sheet, spriteNumber, name);
		_solid = false;
		_slippery = false;
		_breakable = false;
		
		setSprite(sheet, spriteNumber); //Set the object image to 
	}
	
	/**************************************************************************
	 * Second constructor used for the most common element: solid
	 * 
	 * @param frame - Reference Constructor 1
	 * @param g2d - Reference Constructor 1
	 * @param name - Reference Constructor 1
	 * @param sheet - Reference Constructor 1
	 * @param spriteNumber - Reference Constructor 1
	 * @param solid - Property which the system checks to stop objects from moving on it
	 **************************************************************************/
	public Tile(JFrame frame, Graphics2D g2d, String name, SpriteSheet sheet, int spriteNumber, boolean solid) {
		super(frame, g2d, sheet, spriteNumber, name);
		_solid = solid;
		_slippery = false;
		_breakable = false;
		
		if(solid) setSpriteType(TYPE.WALL);
		if(solid) hasProperty = true;
		setSolid(solid); //In Sprite super class, set solid
		setSprite(sheet, spriteNumber);
	}
	
	
	/******************************************************************
	 * Third constructor for less commn elements
	 * 
	 * @param frame - Reference Constructor 1
	 * @param g2d - Reference Constructor 1
	 * @param name - Reference Constructor 1
	 * @param sheet - Reference Constructor 1
	 * @param spriteNumber - Reference Constructor 1
	 * @param solid - Reference Constructor 1
	 * @param slippery - Property which the system checks to run special handling methods
	 * @param breakable - Property which the system checks to run special handling methods
	 ******************************************************************/
	public Tile(JFrame frame, Graphics2D g2d, String name, SpriteSheet sheet, int spriteNumber, boolean solid, boolean slippery, boolean breakable) {
		super(frame, g2d, sheet, spriteNumber, name);
		_solid = solid;
		_slippery = slippery;
		_breakable = breakable;
		
		setSolid(solid);
		if(solid || slippery || breakable) hasProperty = true;
		setSprite(getSheet(), getSpriteNumber());
	}
	
	/*********************************************************************
	 * Special constructor for making a new tile from an existing tile for recreation
	 * Blueprint tile
	 * 
	 * @param tile - A tile object which is a blueprint to design additional unique tiles
	 * @param frame - Reference Constructor 1
	 * @param g2d - Reference Constructor 1
	 *********************************************************************/
	public Tile(Tile tile, JFrame frame, Graphics2D g2d) {
		super(frame, g2d, tile.getSheet(), tile.getSpriteNumber(), tile._name);
		_solid = tile.isSolid();
		_slippery = tile.isSlippery();
		_breakable = tile.isBreakable();
		hasEvent = tile.hasEvent();
		hasProperty = tile.hasProperty();
		event = tile.event();
		
		setSolid(_solid);
		setSprite(tile.getSheet(), tile.getSpriteNumber());
	}
	
	/*******************************************************************
	 * Add an event of special properties to the tile for use later
	 * 
	 * @param event - An Event which can be accessed for use later
	 *******************************************************************/
	public void addEvent(Event event) {
		hasProperty = true;
		this.event = event;
		hasEvent = true;
	}
	
	/**************************************************************************
	 * Event related methods which can be used to access the event, check for
	 * the possibility of an event, and to end the event, making sure it doesn't play twice.
	 * 
	 * @return - Event object
	 **************************************************************************/
	public Event event() { return event; }
	public boolean hasEvent() { return hasEvent; }
	public void endEvent() {
		event = null; 
		hasEvent = false;
	}
	
	/******************************************************************************
	 * Mob related methods used for accessing the mob for update, checking to see if
	 * a mob exists from that Tile or to add a tile to that Tile
	 * @return - Mob object
	 *******************************************************************************/
	public Mob mob() { return mob; }
	public boolean hasMob() { return hasMob; }
	public void addMob(Mob mob) {
		hasMob = true;
		this.mob = mob;
	}
	
	/****************************************
	 * Property accessing methods
	 * 
	 * @return - A property boolean
	 ****************************************/
	public boolean isSolid() { return _solid; }
	public boolean isSlippery() { return _slippery; }
	public boolean isBreakable() { return _breakable; }
	public boolean hasProperty() { return hasProperty; }

	/*******************************************************
	 * Load the tile animation
	 * Inherited Method
	 * 
	 * @param frames - The frames to use in the animation
	 * @param delay - The delay between frame advances
	 ********************************************************/
	public void loadAnim(int frames, int delay) {
		super.loadAnim(frames, delay);
	}
	
	/***************************************************************************
	 * Render the tile on screen and cgange x and y position of the tile each update
	 * 
	 * @param x - An int specifying the x location of the tile on screen.
	 * @param y - An int specifying the y location of the tile on screen.
	 * @param g2d - Graphics2D object that renders the tile
	 * @param frame - JFrame which the tile graphic will be displayed on (The window)
	 ***************************************************************************/
	public void renderTile(int x, int y, Graphics2D g2d, JFrame frame) {
		g2d.drawImage(getImage(), x, y, getSpriteSize(), getSpriteSize(), frame);
		getEntity().setX(x);
		getEntity().setY(y);
	}
	
	/***************************************************************************
	 * Special method which can be used for debugging to show the tile box bounds
	 * 
	 * @param c - Color of the line around the bound
	 * @param g2d - The Graphics2D object used for displaying graphics
	 ****************************************************************************/
	public void drawTileBounds(Color c, Graphics2D g2d) {
		g2d.setColor(c);
		g2d.draw(getTileBounds());
	}
	
	/****************************************************************************
	 * Get the rectangle bound used for interactions on that tile
	 * 
	 * @return - A Rectangle used for a bounding box in the tile
	 ****************************************************************************/
	public Rectangle getTileBounds() {
		Rectangle r;
		r = new Rectangle((int)entity.getX(), (int)entity.getY(), getSpriteSize(), getSpriteSize());
		return r;
	}
}