/****************************************************************************************************
 * @author Travis R. Dewitt
 * @version 1.1
 * Date: June 18, 2015
 * 
 * 
 * Title: Map
 * Description: This class takes in an array of tile blueprints and then returns back a working "map"
 * in the design you made it with the graphics you told each tile to have.
 * 
 * This work is licensed under a Attribution-NonCommercial 4.0 International
 * CC BY-NC-ND license. http://creativecommons.org/licenses/by-nc/4.0/
 ****************************************************************************************************/
//Package
package axohEngine2.map;

//Imports
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

import javax.swing.JFrame;

public class Map {
	
	/*******************
	 * Variables
	 *******************/
	//mapHeight - In tiles, how tall the map is
	//mapWIdth - In tiles, how wide the map is
	//mapTiles - The array that holds all of the finished tiles
	//spriteSize - The size in pixels of each tile
	//_name - The name of the given map, does nothing more than to give the use and idea of what map they are looking at
	private int mapHeight;
	private int mapWidth;
	private Tile[] mapTiles;
	private int spriteSize;
	private String _name;
		
	/************************************************************************
	 * Constructor
	 * 
	 * The constructor automatically duplicates all of the blueprinted tiles given in the Tile[]
	 * in to the needed shape of a map. The spriteSize is also taken from the first Tile in the array.
	 * The prupose of all of this is because having tons of tile data means nothing, by instantiating
	 * each individual tile, each tile becomes unique and can be accessed with its own specific properties.
	 * 
	 * @param frame - A JFrame that the graphics will be dsiplayed on (The window)
	 * @param g2d - The Graphics2D object that will be used to disaplay images on the JFrame
	 * @param tiles - An array of tile blueprints which will be set up in the map for display in game.
	 * @param mapWidth - An int identifying the width of the map in tiles
	 * @param mapHeight - An int identifying the height of an array in tiles
	 * @param name - A String which identifies the map, this is strictly a user identifier, it isn't used in any logic 
	 *************************************************************************/
	public Map(JFrame frame, Graphics2D g2d, Tile[] tiles, int mapWidth, int mapHeight, String name) {
		this.mapHeight = mapHeight;
		this.mapWidth = mapWidth;
		mapTiles = tiles;
		_name = name;
		
		for(int i = 0; i < mapTiles.length; i++) {
			mapTiles[i] = new Tile(mapTiles[i], frame, g2d);
		}
		spriteSize = tiles[0].getSpriteSize();
	}
	
	/****************************************************************************************
	 * The Render method goes to each tile in the specified map and renders it at a specific location.
	 * It also renders any mobs from each tile. These mobs start at the tile position, but when their x or y
	 * moves it is relative to the tile and does appear moving on the world map. 
	 * 
	 * About the 1D Array:
	 * The map is a one dimensional array of individual unique Tiles. In order to put these tiles in the
	 * correct spot, relative to how it was designed in the map database, a special way in moving around
	 * the array was needed. Using this specific way, a one dimensional array can be used instead of 2D, which
	 * saves space and optimizes the framerate of the game, allowing for larger more complicated maps.
	 * x + y * width
	 * 
	 * @param frame - The JFrame which the map will be rendered in (The window)
	 * @param g2d - The Graphics2D object wich is used to display images
	 * @param xx - An offset in the x direction which moves the map around
	 * @param yy - An offset in the y direction which moves the map around.
	 * 
	 * Offsets are used to render a piece of a map in the window at a time to indicate placement
	 * in the world.
	 ********************************************************************************************/	
	
	 /*********************************************************************************************************************
	 * ***** REACTION TECH SOLUTIONS EDIT ***** 
	 * ****************************************
	 * MODIFICATIONS TO THIS UP THERE ^^ 
	 * THE CODE FOR UP THERE IS BELOW THIS NEW RENDER METHOD, I LEFT IT COMMENTED OUT 
	 * I LIKE MINE BETTER but either will work 
	 *
	 * The entire reason that this needed revisions is because the old render method drew the entire map each frame 
	 * Instead, it should draw only the part of the map that is viewable on the screen 
	 * This makes the map drawing always a constant time, rather than a variable time, which means maps can be as big as they want without any difference to the FPS
	 * Also, it just makes sense, why render what the user can't see? Waste not want not.
	 * ***********************************************************************************************************************/	
	// shiftOffset variables, are changed depending on which direction player is moving in
	int shiftOffsetX = 0; 
	int shiftOffsetY = 0;
	int tileSize = 64; // The size of each tile (16x16, but the scale of the game is 4, so it's 64).
	public void render(JFrame frame, Graphics2D g2d, int mapX, int mapY, int currentTileX, int currentTileY,
			int previousMapX, int previousMapY, int previousTileX, int previousTileY, int mapOffsetX, int mapOffsetY) 
	{
		// mapOffsets are multiplied by -1 because the camera position and screen position are inverses...
		// don't ask me why just trust me, it makes sense when you think about it but it sucks to think about
		mapOffsetX *= -1;
		mapOffsetY *= -1;
		
		// checks if you moved to a new tile or not
		// if you did, a shift is needed in order to maintain the correct placement of tiles when drawn
		// the shift is always given/taken a tileSize (which in this case is 64)
		if (previousTileX != currentTileX) // Left/Right shifts
		{
			if (previousMapX < mapX) // Left
			{
				shiftOffsetX -= tileSize;
			}
			if (previousMapX > mapX) // Right
			{
				shiftOffsetX += tileSize;
			}
		}
		if (previousTileY != currentTileY) // Down/Up shifts
		{
			if (previousMapY > mapY) // Down
			{
				shiftOffsetY += tileSize;
			}
			if (previousMapY < mapY) // Up
			{
				shiftOffsetY -= tileSize;
			}
		}
		
		// mapX and mapY are the top left camera location
		// mapOffsetX and Y are how much the map is moved at the start of the game
		// shiftOffsetX and Y are described above
		// the subtraction of tileSize is necessary to start drawing the map one tile above where the player can see, which is necessary in cases where you can see a part of the next tile before moving tile locations
		int startX = mapX + mapOffsetX + shiftOffsetX - tileSize;
		int startY = mapY + mapOffsetY + shiftOffsetY - tileSize;
		
		// loops through each map tile that can possibly be visible on screen (plus the one layer not visible) and draws them
		// it looks at your currentTile and then draws all of the stuff above, below, and to the sides of it that the camera can see
		for(int y = currentTileY - 6; y < currentTileY + 7; y++) {
			for(int x = currentTileX - 10; x < currentTileX + 11; x++) {
				if (x >= 0 && y >= 0 && x < mapWidth && y < mapHeight) // if tile exists, draw it (if not leave it black)
				{
					Tile tile = mapTiles[x + y * mapWidth];
					// because map is a one dimensional array, x + y * mapWidth will give you the tile you want
					tile.renderTile(startX, startY, g2d, frame);
					//try
				//	{
//						if (tile._name.equals("chest"))
//						{
							tile.setLocation(new Rectangle(startX, startY, tileSize, tileSize));
							
						//	tile.setSolidBounds(new Rectangle(startX + (1 * 4), startY + (2 * 4), 14 * 4 - 1, 14 * 4));
							Rectangle bounds = tile.getBounds();
							tile.setSolidBounds(new Rectangle(startX + bounds.x, startY + bounds.y, bounds.width, bounds.height));
//							g2d.setColor(Color.white);
//							float thickness = 1;
//							Stroke oldStroke = g2d.getStroke();
//							g2d.setStroke(new BasicStroke(thickness));
//							g2d.drawRect(startX + bounds.x, startY + bounds.y, bounds.width, bounds.height);
//							g2d.setStroke(oldStroke);

					//	}
				//	}
			//	catch (Exception e)
			//	{
//						
			//	}
					
				}
				startX += tileSize; // increase X by a tileSize to draw the next tile so they won't overlap
			}	
			startX = mapX + mapOffsetX + shiftOffsetX - tileSize; // reset startX for the next layer
			startY += tileSize; // increase Y by a tilesize to draw the next layer of tiles below the old layer so they won't overlap
		}
	}
	
	

	
/* *** OLD WAY of rendering the map, saved just in case I need to see how something is done
*** NEW WAY is above ***/	
//	public void render(JFrame frame, Graphics2D g2d, int xx, int yy) {
//		int xt = xx;
//		for(int y = 0; y < mapHeight; y++) {
//			for(int x = 0; x < mapWidth; x++) {
//				mapTiles[x + y * mapWidth].renderTile(xx, yy, g2d, frame);
//				if(mapTiles[x + y * mapWidth].hasMob()) mapTiles[x + y * mapWidth].mob().renderMob(xx, yy);
//				xx = xx + spriteSize;
//			}
//			xx = xt;
//			yy = yy + spriteSize;
//		}
//	}
	
	/*****************************************************************************
	 * As the method name implies, access the tile at a specific index in the map
	 * The map is in a 1D array, so think x + y * width of the map when accessing the tile.
	 * 
	 * @param index - An int of total x + y * width of the map to access a tile
	 * @return - A Tile which is accessed and can be changed
	 *****************************************************************************/
	public Tile accessTile(int index) {
		return mapTiles[index];
	}
	
	/*****************************************************************
	 * Get the maximum x value the player could move to in the map.
	 * maxX is the right border, unlike y, when X goes up, x goes right.
	 * Always gives back the opposite number found
	 * 
	 * @param screenWidth - The width of the window screen given at the start of the Game
	 * @return - The maximum or minimum int x value 
	 ******************************************************************/
	public int getMaxX(int screenWidth) { return -1 * (mapWidth * spriteSize - screenWidth); }
	public int getMinX() { return 0; }
	
	/*****************************************************************
	 * Get the maximum y value the player could move to in the map.
	 * maxY is the bottom edge, as Y goes down, the y value goes up.
	 * Always gives back the opposite number found
	 * 
	 * @param screenHeight - The height of the window screen given at the start of the Game
	 * @return - The maximum or minimum int y value 
	 ******************************************************************/
	public int getMaxY(int screenHeight) { return -1 * (mapHeight * spriteSize - screenHeight); }
	public int getMinY() { return 0; }
	
	/************************************************************
	 * Currently an unused section of code which obtains the tile in the map based on an X and Y parameter
	 * 
	 * Does not work yet but should!
	 ************************************************************/
	//Get a tile based on a location and direction of a mob
	//playerX and playerY only matter if the mob in the first parameter is a player, otherwise they dont matter
	//TODO: This method doesn't actually work, don't use it
	/*public Tile getFrontTile(Mob mob, int playerX, int playerY, int centerX, int centerY){
		int xx = (int) Math.floor(Math.abs(mob.getXLoc())/spriteSize);
		int yy = (int) Math.floor(Math.abs(mob.getYLoc())/spriteSize);
		if(mob.getType() == TYPE.PLAYER){
			if(playerX < 0) xx = (int) Math.floor(Math.abs(playerX - centerX)/spriteSize); //width what about the black spaces
			if(playerX > 0) xx = (int) Math.floor((playerX + centerX)/spriteSize);
			if(playerY < 0) yy = (int) Math.floor(Math.abs(playerY - centerY)/spriteSize); //height
			if(playerY > 0) yy = (int) Math.floor((playerY + centerY)/spriteSize); //height
		}
		System.out.println((xx + " xx " + yy + " yy ") + " ufiusfhsidu " + spriteSize);
		if(mob.facingLeft) return mapTiles[(xx - 1) + yy * mapWidth]; //left tile
		if(mob.facingRight) return mapTiles[(xx + 1) + yy * mapWidth]; //right
		if(mob.facingUp) return mapTiles[xx + (yy - 1) * mapWidth]; //up
		if(mob.facingDown) return mapTiles[xx + (yy + 1) * mapWidth]; //down
		return mapTiles[xx + yy * mapWidth]; //This line should never run, it's here for formality
	}*/
	
	/**********************************************************
	 * Getters which return the width of the map, the height, or the name of the map
	 * 
	 * @return - An int of height or width or a String name
	 **********************************************************/
	public int getWidth() { return mapWidth; }
	public int getHeight() { return mapHeight; }
	public String mapName() { return _name; }
}