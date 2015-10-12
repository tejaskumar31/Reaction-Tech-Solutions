/****************************************************************************************************
 * @author Travis R. Dewitt
 * @version 0.53
 * Date: June 14, 2015
 * 
 * 
 * Title: Judgement(The Game)
 * Description: This class extends 'Game.java' in order to run a 2D game with specificly defined
 *  sprites, animatons, and actions.
 *  
 * 
 * This work is licensed under a Attribution-NonCommercial 4.0 International
 * CC BY-NC-ND license. http://creativecommons.org/licenses/by-nc/4.0/
 ****************************************************************************************************/
//Package name
package axohEngine2;

//Imports
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JFrame;

import axohEngine2.entities.AnimatedSprite;
import axohEngine2.entities.DIRECTION;
import axohEngine2.entities.ImageEntity;
import axohEngine2.entities.Mob;
import axohEngine2.entities.SpriteSheet;
import axohEngine2.map.Map;
import axohEngine2.map.Tile;
import axohEngine2.player.Player;
import axohEngine2.project.InGameMenu;
import axohEngine2.project.MapDatabase;
import axohEngine2.project.OPTION;
import axohEngine2.project.STATE;
import axohEngine2.project.TYPE;
import axohEngine2.project.Textbox;
import axohEngine2.project.TitleMenu;

//Start class by also extending the 'Game.java' engine interface
public class Judgement extends Game {
	//For serializing (The saving system)
	private static final long serialVersionUID = 1L;
	
	/****************** Variables **********************/
	//--------- Screen ---------
	//SCREENWIDTH - Game window width
	//SCREENHEIGHT - Game window height
	//CENTERX/CENTERY - Center of the game window's x/y
	static int SCREENWIDTH = 1200;
	static int SCREENHEIGHT = 700;

	// basically keeps player in center of screen
	static int CENTERX = 530;
	static int CENTERY = 270;
	
	//--------- Miscelaneous ---------
	//booleans - A way of detecting a pushed key in game
	//random - Use this to generate a random number
	//state - Game states used to show specific info ie. pause/running
	//option - In game common choices at given times
	//Fonts - Variouse font sizes in the Arial style for different in game text
	boolean keyLeft, keyRight, keyUp, keyDown, keyInventory, keyAction, keyBack, keyEnter, keySpace, keyChange;
	Random random = new Random();
	STATE state; 
	OPTION option;
	private Font simple = new Font("Arial", Font.PLAIN, 72);
	private Font bold = new Font("Arial", Font.BOLD, 72);
	private Font bigBold = new Font("Arial", Font.BOLD, 96);
	
	//--------- Player and scale ---------
	//scale - All in game art is 16 x 16 pixels, the scale is the multiplier to enlarge the art and give it the pixelated look
	//mapX/mapY - Location of the camera on the map
	//playerX/playerY - Location of the player on the map
	//startPosX/startPosY - Starting position of the player in the map
	//playerSpeed - How many pixels the player moves in a direction each update when told to
	private int scale;
	private int mapX;
	private int mapY;
	private int playerX;
	private int playerY;
	private int startPosX;
	private int startPosY;
	private int currentPosX;
	private int currentPosY;
	private int playerSpeed;
	
	//----------- Map and input --------
	//currentMap - The currently displayed map the player can explore
	//currentOverlay - The current overlay which usually contains houses, trees, pots, etc.
	//mapBase - The database which contains all variables which pertain to specific maps(NPCs, monsters, chests...)
	//inputWait - How long the system waits for after an action is done on the keyboard
	//confirmUse - After some decisions are made, a second question pops up, true equals continue action from before.
	private Map currentMap;
	private Map currentOverlay;
	private MapDatabase mapBase;
	private int inputWait = 5;
	private boolean confirmUse = false;
	
	//----------- Menus ----------------
	//inX/inY - In Game Menu starting location for default choice highlight
	//inLocation - Current choice in the in game menu represented by a number, 0 is the top
	//sectionLoc - Current position the player could choose after the first choice has been made in the in game menu(Items -> potion), 0 is the top.
	//titleX, titleY, titleX2, titleY2 - Positions for specific moveable sprites at the title screen (arrow/highlight).
	//titleLocation - Current position the player is choosing in the title screen(File 1, 2, 3) 0 is top
	//currentFile - Name of the currently loaded file
	//wasSaving/wait/waitOn - Various waiting variables to give the player time to react to whats happening on screen
	private int inX = 90, inY = 90;
	private int inLocation;
	private int sectionLoc;
	private int titleX = 520, titleY = 416;
	private int titleX2 = 520, titleY2 = 510;
	private int titleLocation;
	private String currentFile;
	private boolean wasSaving = false;
	private int wait;
	private boolean waitOn = false;
	
	//----------- Game  -----------------
	//SpriteSheets (To be split in to multiple smaller sprites)
	SpriteSheet extras1;
	SpriteSheet mainCharacter;
	
	//ImageEntitys (Basic pictures)
	ImageEntity inGameMenu;
	ImageEntity titleMenu;
	ImageEntity titleMenu2;
	
	//Menu classes
	TitleMenu title;
	InGameMenu inMenu;
	
	//Animated sprites
	AnimatedSprite titleArrow;
	AnimatedSprite titleEraseBomb;
	
	//Player and NPCs
	Mob playerMob;
	Mob randomNPC;
	
	int playerNumber;
	public static boolean playerUpdate;
	int cameraWidth;
	int cameraHeight;
	
	int currentTileX; 
	int currentTileY; 
	
	int mapOffsetX;
	int mapOffsetY;
	
	int previousMapX;
	int previousMapY;
	
	int previousTileX;
	int previousTileY;
	
	//***Initialize textbox***
	Textbox textbox = new Textbox();
	boolean renderTextbox;
	
	/*********************************************************************** 
	 * Constructor
	 * 
	 * Set up the super class Game and set the window to appear
	 **********************************************************************/
	public Judgement() {
		//super(130, 1200, 700); // the 130 is frame rate!!, 1200 is screen width, 700 is screen height
		super(130, SCREENWIDTH, SCREENHEIGHT);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/****************************************************************************
	 * Inherited Method
	 * This method is called only once by the 'Game.java' class, for startup
	 * Initialize all non-int variables here
	 *****************************************************************************/
	void gameStartUp() {
		/****************************************************************
		 * The "camera" is the mapX and mapY variables. These variables 
		 * can be changed in order to move the map around, simulating the
		 * camera. The player is moved around ONLY when at an edge of a map,
		 * otherwise it simply stays at the center of the screen as the "camera"
		 * is moved around.
		 ****************************************************************/
		//****Initialize Misc Variables
		state = STATE.TITLE;
		option = OPTION.NONE;
		
		// start position of player 
		// this is different than the camera position (mapX and mapY)
		// these will be used to get which tile the player is on
		// Basically CENTERX and CENTERY should have done the trick for putting the player in the center of the map.
		// but it doesn't account for the player's actual size and I'm just not sure how to do that math correctly.
		// I'll figure it out later in the case that we add more than one playable character, for now here's some addition and subtraction.
		
		startPosX = 530 + 16; //CENTERX + 16    546
		startPosY = 270 - 42; //CENTERY - 42    228
	
		mapOffsetX = -128;
		mapOffsetY = -128;
		
		// (is needed to keep track of so we know where the player is at all times)
		// where the player is currently positioned on map (default equal to start position)
		currentPosX = startPosX;
		currentPosY = startPosY;
		
		// gets the tile the player is currently standing on
		// figures it out by using your currentPosition, dividing it by 64 (which is the tile size after they are scaled), and it auto rounds down
		currentTileX = (currentPosX + 64 + (mapOffsetX * -1)) / 64;
		currentTileY = (currentPosY + 128 + (mapOffsetY * -1)) / 64;
		System.out.println(currentTileX + " " + currentTileY);
		
		// start camera position
		mapX = 0 + mapOffsetX;
		mapY = 0 + mapOffsetY;
		
		previousMapX = mapX;
		previousMapY = mapY;
		previousTileX = currentTileX;
		previousTileY = currentTileY;
		
		scale = 4;
		playerSpeed = 4;
		
		cameraWidth = 1200;
		cameraHeight = 700;
		
		
		
		//****Initialize spriteSheets*********************************************************************
		extras1 = new SpriteSheet("/textures/extras/extras1.png", 8, 8, 32, scale);
		//mainCharacter = new SpriteSheet("/textures/characters/mainCharacter.png", 8, 8, 32, scale);
		
		//****Initialize and setup AnimatedSprites*********************************************************
		titleArrow = new AnimatedSprite(this, graphics(), extras1, 0, "arrow");
		titleArrow.loadAnim(4, 10);
		sprites().add(titleArrow);
		
		titleEraseBomb = new AnimatedSprite(this, graphics(), extras1, 4, "bomb");
		titleEraseBomb.loadAnim(4, 10);
		sprites().add(titleEraseBomb);
			
		//****Initialize and setup image entities**********************************************************
		inGameMenu = new ImageEntity(this);
		titleMenu = new ImageEntity(this);
		titleMenu2 = new ImageEntity(this);
		inGameMenu.load("/menus/ingamemenu.png");
	//	titleMenu.load("/menus/titlemenu1.png");
		titleMenu.load("/menus/PurpleBackground.png");
	//	titleMenu2.load("/menus/titlemenu2.png");
		titleMenu2.load("/menus/titlemenu.png");

	
		//*****Initialize Menus***************************************************************************
		title = new TitleMenu(titleMenu, titleMenu2, titleArrow, SCREENWIDTH, SCREENHEIGHT, simple, bold, bigBold, titleEraseBomb);
		inMenu = new InGameMenu(inGameMenu, SCREENWIDTH, SCREENHEIGHT);

		//****Initialize and setup Mobs*********************************************************************
		
		// Class for initializing player + checking which player you will be
		Player player = new Player();
		playerMob = player.getPlayerMobStart(mainCharacter, playerMob, graphics(), this, sprites(), playerNumber);
		playerNumber++;
	//	playerUpdate = true;
		//*****Initialize and setup first Map******************************************************************
		mapBase = new MapDatabase(this, graphics(), scale);
		//Get Map from the database
		for(int i = 0; i < mapBase.maps.length; i++){
			if(mapBase.getMap(i) == null) continue;
			if(mapBase.getMap(i).mapName() == "city") currentMap = mapBase.getMap(i);
			if(mapBase.getMap(i).mapName() == "cityO") currentOverlay = mapBase.getMap(i);
		}
		//Add the tiles from the map to be updated each system cycle
		for(int i = 0; i < currentMap.getWidth() * currentMap.getHeight(); i++){
			addTile(currentMap.accessTile(i));
			addTile(currentOverlay.accessTile(i));
			if(currentMap.accessTile(i).hasMob()) sprites().add(currentMap.accessTile(i).mob());
			if(currentOverlay.accessTile(i).hasMob()) sprites().add(currentOverlay.accessTile(i).mob());
			currentMap.accessTile(i).getEntity().setX(-300);
			currentOverlay.accessTile(i).getEntity().setX(-300);
		}
		
		requestFocus(); //Make sure the game is focused on
		start(); //Start the game loop
	}
	
	/**************************************************************************** 
	 * Inherited Method
	 * Method that updates with the default 'Game.java' loop method
	 * Add game specific elements that need updating here
	 *****************************************************************************/
	void gameTimedUpdate() {
		checkInput(); //Check for user input
		//Update certain specifics based on certain game states
		if(state == STATE.TITLE) title.update(option, titleLocation); //Title Menu update
		if(state == STATE.INGAMEMENU) inMenu.update(option, sectionLoc, playerMob.health()); //In Game Menu update
		updateData(currentMap, currentOverlay, playerX, playerY); //Update the current file data for saving later
		System.out.println(frameRate()); //Print the current framerate to the console
		if(waitOn) wait--;
	}
	
	/**
	 * Inherited Method
	 * Obtain the 'graphics' passed down by the super class 'Game.java' and render objects on the screen
	 */
	void gameRefreshScreen() {		
		/*********************************************************************
		* Rendering images uses the java class Graphics2D
		* Each frame the screen needs to be cleared and an image is setup as a back buffer which is brought 
		* to the front as a full image at the time it is needed. This way the screen is NOT rendered pixel by 
		* pixel in front of the user, which would have made a strange lag effect.
		* 
		* 'graphics' objects have parameters that can be changed which effect what it renders, two are font and color
		**********************************************************************/
		Graphics2D g2d = graphics();
		g2d.clearRect(0, 0, SCREENWIDTH, SCREENHEIGHT); 
		g2d.setFont(simple);
		
		//GUI rendering for when a specific state is set, only specific groups of data is drawn at specific times
		if(state == STATE.GAME) { 
			
			//Render the map, the player, any NPCs or Monsters and the player health or status
			currentMap.render(this, g2d, mapX, mapY, currentTileX, currentTileY, previousMapX, previousMapY, previousTileX, previousTileY, mapOffsetX, mapOffsetY);
		//	currentOverlay.render(this, g2d, mapX, mapY, currentTileX, currentTileY, previousMapX, previousMapY, previousTileX, previousTileY, mapOffsetX, mapOffsetY);
			
			if (renderTextbox)
			{
				textbox.renderTextBox(this, graphics());
			}
				
			// BECAUSE of a mistake somewhere either in the sprite class, Animated sprite, or Mob...or even sprite sheet...
			// the starting sprite when drawing the player will always mess up, and for some reason it will walk in place at first
			// I tried...idk how to fix it, so here's a work around yay
			// this if statement checks a variable I added into the Mob for which frame to start on
			// playerUpdate is only set to true when first drawing a player into the map, so this if statement happens once
			if (playerUpdate)
			{
				playerMob.stopAnim(); // stops the moving in place right when player loads
				playerMob.setAnimTo(playerMob.getStartFrame()); // stops the odd animation loading and sets player to start on its wanted start frame
				playerUpdate = false; 
			}
			
			// STARTING POSITION OF PLAYER 
			playerMob.renderMob(startPosX, startPosY);
			Rectangle bounds = playerMob.getLeftBounds();
			float thickness = 1;
			Stroke oldStroke = g2d.getStroke();
			g2d.setStroke(new BasicStroke(thickness));
			g2d.drawRect(bounds.x + startPosX, bounds.y + startPosY, bounds.width, bounds.height);
			g2d.setStroke(oldStroke);
			
//			playerMob.setBounds_(new Rectangle(startPosX + bounds.x + 1, startPosY + bounds.y, bounds.width, bounds.height));
//			// Set Bounds of player
//			if (playerMob.direction == DIRECTION.DOWN)
//			{
//				Rectangle bounds = playerMob.getDownBounds();
//				playerMob.setBounds_(new Rectangle(bounds.x + startPosX, bounds.y + startPosY, bounds.width, bounds.height));
//			
//				float thickness = 1;
//				Stroke oldStroke = g2d.getStroke();
//				g2d.setStroke(new BasicStroke(thickness));
//				g2d.drawRect(bounds.x + startPosX, bounds.y + startPosY, bounds.width, bounds.height);
//				g2d.setStroke(oldStroke);
//			}
//		    if (playerMob.direction == DIRECTION.UP)
//			{
//				Rectangle bounds = playerMob.getUpBounds();
//				playerMob.setBounds_(new Rectangle(bounds.x + startPosX, bounds.y + startPosY, bounds.width, bounds.height));
//
//				float thickness = 1;
//				Stroke oldStroke = g2d.getStroke();
//				g2d.setStroke(new BasicStroke(thickness));
//				g2d.drawRect(bounds.x + startPosX, bounds.y + startPosY, bounds.width, bounds.height);
//				g2d.setStroke(oldStroke);
//			}
//			if (playerMob.direction == DIRECTION.LEFT)
//			{
//				Rectangle bounds = playerMob.getLeftBounds();
//				playerMob.setBounds_(new Rectangle(bounds.x + startPosX, bounds.y + startPosY, bounds.width, bounds.height));
//			
//				float thickness = 1;
//				Stroke oldStroke = g2d.getStroke();
//				g2d.setStroke(new BasicStroke(thickness));
//				g2d.drawRect(bounds.x + startPosX, bounds.y + startPosY, bounds.width, bounds.height);
//				g2d.setStroke(oldStroke);
//			}
//			if (playerMob.direction == DIRECTION.RIGHT)
//			{
//				Rectangle bounds = playerMob.getRightBounds();
//				playerMob.setBounds_(new Rectangle(bounds.x + startPosX, bounds.y + startPosY, bounds.width, bounds.height));
//		
//				float thickness = 1;
//				Stroke oldStroke = g2d.getStroke();
//				g2d.setStroke(new BasicStroke(thickness));
//				g2d.drawRect(bounds.x + startPosX, bounds.y + startPosY, bounds.width, bounds.height);
//				g2d.setStroke(oldStroke);
//			}

		}
		if(state == STATE.INGAMEMENU){
			//Render the in game menu and specific text
			inMenu.render(this, g2d, inX, inY);
			g2d.setColor(Color.red);
			if(confirmUse) g2d.drawString("Use this?", CENTERX, CENTERY);
		}
		if(state == STATE.TITLE) {
			//Render the title screen
			title.render(this, g2d, titleX, titleY, titleX2, titleY2);
		}
		
		//Render save time specific writing
		if(option == OPTION.SAVE){
			drawString(g2d, "Are you sure you\n      would like to save?", 660, 400);
		}
		if(wasSaving && wait > 0) {
			g2d.drawString("Game Saved!", 700, 500);
		}
	}
	
	/*******************************************************************
	 * The next four methods are inherited
	 * Currently these methods are not being used, but they have
	 * been set up to go off at specific times in a game as events.
	 * Actions that need to be done during these times can be added here.
	 ******************************************************************/
	void gameShutDown() {		
	}

	void spriteUpdate(AnimatedSprite sprite) {		
	}

	void spriteDraw(AnimatedSprite sprite) {		
	}

	void spriteDying(AnimatedSprite sprite) {		
	}

	/*************************************************************************
	 * @param AnimatedSprite
	 * @param AnimatedSprite
	 * @param int
	 * @param int
	 * 
	 * Inherited Method
	 * Handling for when a SPRITE contacts a SPRITE
	 * 
	 * hitDir is the hit found when colliding on a specific bounding box on spr1 and hitDir2
	 * is the same thing applied to spr2
	 * hitDir is short for hit direction which can give the data needed to move the colliding sprites
	 * hitDir is a number between and including 0 and 3, these assignments are taken care of in 'Game.java'.
	 * What hitDir is actually referring to is the specific hit box that is on a multi-box sprite.
	 *****************************************************************************/
	void spriteCollision(AnimatedSprite spr1, AnimatedSprite spr2, int hitDir, int hitDir2) {
		//Get the smallest possible overlap between the two problem sprites
		double leftOverlap = (spr1.getBoundX(hitDir) + spr1.getBoundSize() - spr2.getBoundX(hitDir2));
		double rightOverlap = (spr2.getBoundX(hitDir2) + spr2.getBoundSize() - spr1.getBoundX(hitDir));
		double topOverlap = (spr1.getBoundY(hitDir) + spr1.getBoundSize() - spr2.getBoundY(hitDir2));
		double botOverlap = (spr2.getBoundY(hitDir2) + spr2.getBoundSize() - spr1.getBoundY(hitDir));
		double smallestOverlap = Double.MAX_VALUE; 
		double shiftX = 0;
		double shiftY = 0;

		if(leftOverlap < smallestOverlap) { //Left
			smallestOverlap = leftOverlap;
			shiftX -= leftOverlap; 
			shiftY = 0;
		}
		if(rightOverlap < smallestOverlap){ //right
			smallestOverlap = rightOverlap;
			shiftX = rightOverlap;
			shiftY = 0;
		}
		if(topOverlap < smallestOverlap){ //up
			smallestOverlap = topOverlap;
			shiftX = 0;
			shiftY -= topOverlap;
		}
		if(botOverlap < smallestOverlap){ //down
			smallestOverlap = botOverlap;
			shiftX = 0;
			shiftY = botOverlap;
		}

		//Handling very specific collisions
		if(spr1.spriteType() == TYPE.PLAYER && state == STATE.GAME){
			if(spr2 instanceof Mob) ((Mob) spr2).stop(); // stops an NPC from walking through you
			
			//This piece of code is commented out because I still need the capability of getting a tile from an xand y position
			/*if(((Mob) spr1).attacking() && currentOverlay.getFrontTile((Mob) spr1, playerX, playerY, CENTERX, CENTERY).getBounds().intersects(spr2.getBounds())){
				((Mob) spr2).takeDamage(25);
				//TODO: inside of take damage should be a number dependant on the current weapon equipped, change later
			}*/
			
			//WORMS
			//Handle simple push back collision
		//	if(playerX != 0) playerX -= shiftX; // Unnecessary now that the game camera has been changed to move at all times
		//	if(playerY != 0) playerY -= shiftY;
		
			// this checks to make sure the player has been loaded before checking for sprite collisions
			// otherwise, when the game first loads, it automatically counts a collision for some reason...idk why and then moves you as if you collided
			// once again idk why it does this but like everything in this game engine, nothing makes any sense, so here's a work around, which is basically what this entire engine has become
			if (playerUpdate == false) 
			{
				if(playerX == 0) mapX -= shiftX; // if you collide with a sprite, move back
				if(playerY == 0) mapY -= shiftY;
			}
		}
	}
	
	/***********************************************************************
	* @param AnimatedSprite
	* @param Tile
	* @param int
	* @param int
	* 
	* Inherited Method
	* Set handling for when a SPRITE contacts a TILE, this is handy for
	* dealing with Tiles which contain Events. When specifying a new
	* collision method, check for the type of sprite and whether a tile is
	* solid or breakable, both, or even if it contains an event. This is
	* mandatory because the AxohEngine finds details on collision and then 
	* returns it for specific handling by the user.
	* 
	* For more details on this method, refer to the spriteCollision method above
	*************************************************************************/
	void tileCollision(AnimatedSprite spr, Tile tile, int hitDir, int hitDir2) {
		double leftOverlap = (spr.getBoundX(hitDir) + spr.getBoundSize() - tile.getBoundX(hitDir2));
		double rightOverlap = (tile.getBoundX(hitDir2) + tile.getBoundSize() - spr.getBoundX(hitDir));
		double topOverlap = (spr.getBoundY(hitDir) + spr.getBoundSize() - tile.getBoundY(hitDir2));
		double botOverlap = (tile.getBoundY(hitDir2) + tile.getBoundSize() - spr.getBoundY(hitDir));
		double smallestOverlap = Double.MAX_VALUE; 
		double shiftX = 0;
		double shiftY = 0;

		if(leftOverlap < smallestOverlap) { //Left
			smallestOverlap = leftOverlap;
			shiftX -= leftOverlap; 
			shiftY = 0;
		}
		if(rightOverlap < smallestOverlap){ //right
			smallestOverlap = rightOverlap;
			shiftX = rightOverlap;
			shiftY = 0;
		}
		if(topOverlap < smallestOverlap){ //up
			smallestOverlap = topOverlap;
			shiftX = 0;
			shiftY -= topOverlap;
		}
		if(botOverlap < smallestOverlap){ //down
			smallestOverlap = botOverlap;
			shiftX = 0;
			shiftY = botOverlap;
		}
		
		//Deal with a tiles possible event property
		if(tile.hasEvent()){
			if(spr.spriteType() == TYPE.PLAYER) {
				//Warp Events(Doors)
				if(tile.event().getEventType() == TYPE.WARP) {
					tiles().clear();
					sprites().clear();
					sprites().add(playerMob);
					//Get the new map
					for(int i = 0; i < mapBase.maps.length; i++){
						 if(mapBase.getMap(i) == null) continue;
						 if(tile.event().getMapName() == mapBase.getMap(i).mapName()) currentMap = mapBase.getMap(i);
						 if(tile.event().getOverlayName() == mapBase.getMap(i).mapName()) currentOverlay = mapBase.getMap(i);
					}
					//Load in the new maps Tiles and Mobs
					for(int i = 0; i < currentMap.getWidth() * currentMap.getHeight(); i++){
						addTile(currentMap.accessTile(i));
						addTile(currentOverlay.accessTile(i));
						if(currentMap.accessTile(i).hasMob()) sprites().add(currentMap.accessTile(i).mob());
						if(currentOverlay.accessTile(i).hasMob()) sprites().add(currentOverlay.accessTile(i).mob());
					}
					//Move the player to the new position
					playerX = tile.event().getNewX();
					playerY = tile.event().getNewY();
				}	
			} //end warp
			//Item exchange event
			if(spr.spriteType() == TYPE.PLAYER && tile.event().getEventType() == TYPE.ITEM && keyAction){
				if((tile._name).equals("chest")) tile.setFrame(tile.getSpriteNumber() + 1); //Chests should have opened and closed version next to each other
				inMenu.addItem(tile.event().getItem()); //Add item to inventory
				tile.endEvent();
			}
		}//end check events
		
		//If the tile is solid, move the player off of it and exit method immediately
		if(spr.spriteType() == TYPE.PLAYER && tile.solid() && state == STATE.GAME) {
			//WORMS
			if(playerX != 0) playerX -= shiftX;
			if(playerY != 0) playerY -= shiftY;
			if(playerX == 0) mapX -= shiftX;
			if(playerY == 0) mapY -= shiftY;
			return;
		}
		//If an npc is intersecting a solid tile, move it off
		if(spr.spriteType() != TYPE.PLAYER && tile.solid() && state == STATE.GAME){
			if(spr instanceof Mob) {
				((Mob) spr).setLoc((int)shiftX, (int)shiftY);
				((Mob) spr).resetMovement();
			}
		}
	}//end tileCollision method
	
	// Reaction Tech Solutions tile collision method
	int checkTileCollisionsX(int xa)
	{
		int futurePosX;
		int futureTileX;
		Tile tile;
		Tile tile2;
		Rectangle bounds;
		
		try
		{
		if(xa > 0) // Left	
		{
			futurePosX = currentPosX + (xa * -1);
			futureTileX = (futurePosX + 64 + (mapOffsetX * -1)) / 64; 
			tile = currentMap.accessTile(futureTileX + currentTileY * currentMap.getWidth());
			tile2 = currentMap.accessTile(futureTileX + (currentTileY - 1) * currentMap.getWidth());
			
			bounds = playerMob.getLeftBounds();
			
			playerMob.setBounds_(new Rectangle(startPosX + bounds.x - 1, startPosY + bounds.y, bounds.width, bounds.height));

			if (playerMob.getBounds_().intersects(tile.getSolidBounds()) || playerMob.getBounds_().intersects(tile2.getSolidBounds()))
			{
				xa = 0;
			}
			
			playerMob.setBounds_(new Rectangle(startPosX + bounds.x + 1, startPosY + bounds.y, bounds.width, bounds.height));
		}
		else if(xa < 0) // right
		{
			futurePosX = currentPosX + (xa * -1);
			futureTileX = (futurePosX + 64 + (mapOffsetX * -1)) / 64; 
			tile = currentMap.accessTile(futureTileX + currentTileY * currentMap.getWidth());
			
			bounds = playerMob.getRightBounds();
			playerMob.setBounds_(new Rectangle(startPosX + bounds.x + 1, startPosY + bounds.y, bounds.width, bounds.height));
			
			if (playerMob.getBounds_().intersects(tile.getSolidBounds()))
			{
				xa = 0;
			}
			playerMob.setBounds_(new Rectangle(startPosX + bounds.x - 1, startPosY + bounds.y, bounds.width, bounds.height));
		}
//		System.out.println(bounds.x + " " + bounds.y + " " + bounds.width + " " + bounds.height);	
		}
		catch(Exception e){}
		return xa;
	}
	
	int checkTileCollisionsY(int ya)
	{
	//	currentPosY += ya * -1;
	//	currentTileY = (currentPosY + 128 + (mapOffsetY * -1)) / 64;
	//	Tile tile = currentMap.accessTile(5 + 4 * currentMap.getWidth());
	//	Tile tile = currentMap.accessTile(currentTileX + currentTileY * currentMap.getWidth());
		int futurePosY;
		int futureTileY;
		Tile tile;
		Rectangle bounds;
		try
		{
		if(ya > 0) // up	
		{
			futurePosY = currentPosY + (45 * -1);
			futureTileY = (futurePosY + 128 + (mapOffsetY * -1)) / 64; 
			tile = currentMap.accessTile(currentTileX + futureTileY * currentMap.getWidth());
			
			bounds = playerMob.getUpBounds();
			playerMob.setBounds_(new Rectangle(startPosX + bounds.x, startPosY + bounds.y - 1, bounds.width, bounds.height));
			
			if (playerMob.getBounds_().intersects(tile.getSolidBounds()))
			{
				ya = 0;
			}
			playerMob.setBounds_(new Rectangle(startPosX + bounds.x, startPosY + bounds.y + 1, bounds.width, bounds.height));
		}
		else if(ya < 0) // down
		{
			futurePosY = currentPosY + (-5 * -1);
			futureTileY = (futurePosY + 128 + (mapOffsetY * -1)) / 64; 
			tile = currentMap.accessTile(currentTileX + futureTileY * currentMap.getWidth());
			
			bounds = playerMob.getDownBounds();
			playerMob.setBounds_(new Rectangle(startPosX + bounds.x, startPosY + bounds.y + 1, bounds.width, bounds.height));
			
			if (playerMob.getBounds_().intersects(tile.getSolidBounds()))
			{
				ya = 0;
			}
			playerMob.setBounds_(new Rectangle(startPosX + bounds.x, startPosY + bounds.y - 1, bounds.width, bounds.height));
		}
		}
		catch(Exception e) {}
		return ya;
	}
	
	/*****************************************************************
	 * @param int
	 * @param int
	 * 
	 *Method to call which moves the player. The player never moves apart from the map
	 *unless the player is at an edge of the generated map. Also, to simulate the movement
	 *of the space around the player like that, the X movement is flipped. 
	 *Which means to move right, you subtract from the X position.
	 ******************************************************************/
	// CHANGED from above ^^ now ONLY moves the map as you move
	void movePlayer(int xa, int ya) 
	{		
		// check tile collisions
		if (xa != 0) // Check tile collisions X as long as you are moving left or right
		{
			xa = checkTileCollisionsX(xa);
		}
		if (ya != 0) // Check tile collisions X as long as you are moving up or down
		{
			ya = checkTileCollisionsY(ya);
		}
		
		if (xa == 0) // Not moving horizontal
		{
			previousMapX = mapX;
			previousTileX = currentTileX;
		}
		if (ya == 0) // Not moving vertical
		{
			previousMapY = mapY;
			previousTileY = currentTileY;
		}
		if(xa > 0) // Left	
		{ 	
			previousMapX = mapX; // sets previousMap to Map before Map changes
			previousTileX = currentTileX; // sets previousTile to currentTile before currentTile possibly changes
			mapX += xa; // moves the map the correct direction by the correct amount
			currentPosX += xa * -1;	// gets the player's current position on map (since it's the player's position and not the camera, we need the opposite value, so that's why it's multiplied by 1)
			currentTileX = (currentPosX + 64 + (mapOffsetX * -1)) / 64; // gets the player's current tile, it works don't question it
		}
		if(xa < 0)  // Right
		{
			previousMapX = mapX;
			previousTileX = currentTileX;
			mapX += xa;
			currentPosX += xa * -1;
			currentTileX = (currentPosX + 64 + (mapOffsetX * -1)) / 64;
		}
		if(ya > 0) // up
		{ 
			previousMapY = mapY;
			previousTileY = currentTileY;
			mapY += ya;
			currentPosY += ya * -1;
			currentTileY = (currentPosY + 128 + (mapOffsetY * -1)) / 64;
		}
		if(ya < 0)  // down
		{
			previousMapY = mapY;
			previousTileY = currentTileY;
			mapY += ya;
			currentPosY += ya * -1;
			currentTileY = (currentPosY + 128 + (mapOffsetY * -1)) / 64;
		}

		System.out.println(currentTileX + " " + currentTileY);
	}
	
	/**********************************************************
	 * Main
	 * 
	 * @param args
	 ********************************************************/
	public static void main(String[] args) { new Judgement(); }
	
	/**********************************************************
	 * The Depths of Judgement Lies Below
	 * 
	 *             Key events - Mouse events
	 *                            
	 ***********************************************************/

	/****************************************************************
	 * Check specifically defined key presses which do various things
	 ****************************************************************/
	// Begin movement
	// It might be best to just not touch movement ever again after this
	String firstKey = "";
	String secondKey = "";
	String thirdKey = "";
	String rightOrLeft = "";
	String upOrDown = "";
	boolean canMove = true;
	public void checkInput() {
		int xa = 0;
		int ya = 0;
		
		/********************************************
		 * Special actions for In Game
		 *******************************************/
		if(state == STATE.GAME && inputWait < 0) 
		{
			if (canMove)
			{
				//A or left arrow(move left)
				if(keyLeft && !keyRight && !keyUp && !keyDown) {
					xa = xa + 1 + playerSpeed;
					playerMob.updatePlayer(keyLeft, keyRight, keyUp, keyDown);
					firstKey = "Left";
					
				}
				//D or right arrow(move right)
				if(keyRight && !keyLeft && !keyUp && !keyDown) {
					xa = xa - 1 - playerSpeed;
					playerMob.updatePlayer(keyLeft, keyRight, keyUp, keyDown);
					firstKey = "Right";
				}
				//W or up arrow(move up)
			    if(keyUp && !keyLeft && !keyRight && !keyDown) {
					ya = ya + 1 + playerSpeed;
					playerMob.updatePlayer(keyLeft, keyRight, keyUp, keyDown);
					firstKey = "Up";
				}
				//S or down arrow(move down)
				if(keyDown && !keyLeft && !keyRight && !keyUp) {
					ya = ya - 1 - playerSpeed;
					playerMob.updatePlayer(keyLeft, keyRight, keyUp, keyDown);
					firstKey = "Down";
				}
				
				// Diagonal
				if(keyLeft && keyDown && !keyRight && !keyUp)
				{
					xa = xa + 1 + playerSpeed;
					ya = ya - 1 - playerSpeed;
					playerMob.updatePlayer(false, keyRight, keyUp, keyDown);
					
					if (firstKey.equals("Left"))
					{
						secondKey = "Down";
					}
					else if (firstKey.equals("Down"))
					{
						secondKey = "Left";
					}
				}
				
				if(keyLeft && keyUp && !keyRight && !keyDown)
				{
					xa = xa + 1 + playerSpeed;
					ya = ya + 1 + playerSpeed;
					playerMob.updatePlayer(false, keyRight, keyUp, keyDown);
					
					if (firstKey.equals("Left"))
					{
						secondKey = "Up";
					}
					else if (firstKey.equals("Up"))
					{
						secondKey = "Left";
					}
				}
				
				if(keyRight && keyDown && !keyLeft && !keyUp)
				{
					xa = xa - 1 - playerSpeed;
					ya = ya - 1 - playerSpeed;
					playerMob.updatePlayer(keyLeft, false, keyUp, keyDown);
					
					if (firstKey.equals("Right"))
					{
						secondKey = "Down";
					}
					else if (firstKey.equals("Down"))
					{
						secondKey = "Right";
					}
				}
				
				if(keyRight && keyUp && !keyLeft && !keyDown)
				{
					xa = xa - 1 - playerSpeed;
					ya = ya + 1 + playerSpeed;
					playerMob.updatePlayer(keyLeft, false, keyUp, keyDown);
					
					if (firstKey.equals("Right"))
					{
						secondKey = "Up";
					}
					else if (firstKey.equals("Up"))
					{
						secondKey = "Right";
					}
				}
				
				// Solves what happens if you press opposite keys (example, if you hold left and then press right)
				// The second key will always overwrite the first
				if(keyLeft && keyRight && !keyUp && !keyDown) {
					if (firstKey.equals("Left"))
					{
						xa = xa - 1 - playerSpeed;
						playerMob.updatePlayer(false, true, keyUp, keyDown);
						secondKey = "Right";
					}
				    if (firstKey.equals("Right"))
					{
						xa = xa + 1 + playerSpeed;
						playerMob.updatePlayer(true, false, keyUp, keyDown);
						secondKey = "Left";
					}
				    if (firstKey.equals("Up") || firstKey.equals("Down"))
				    {
				    	if (rightOrLeft.equals("Left"))
				    	{
				    		xa = xa + 1 + playerSpeed;
							playerMob.updatePlayer(true, false, keyUp, keyDown);
				    	}
				    	if (rightOrLeft.equals("Right"))
				    	{
				    		xa = xa - 1 - playerSpeed;
							playerMob.updatePlayer(false, true, keyUp, keyDown);
				    	}
				    }
				}
				
				if(keyUp && keyDown && !keyLeft && !keyRight) {
					if (firstKey.equals("Up"))
					{
						ya = ya - 1 - playerSpeed;
						playerMob.updatePlayer(keyLeft, keyRight, false, true);
						secondKey = "Down";
					}
				    if (firstKey.equals("Down"))
					{
				    	ya = ya + 1 + playerSpeed;
						playerMob.updatePlayer(keyLeft, keyRight, true, false);
						secondKey = "Up";
					}
				    if (firstKey.equals("Left") || firstKey.equals("Right"))
				    {
				    	if (upOrDown.equals("Up"))
				    	{
				    		ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, false);
				    	}
				    	if (upOrDown.equals("Down"))
				    	{
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, false, true);
				    	}
				    }
				}
				
				// Solves what happens when three keys are pressed at once
				// The third key will overwrite 
				// Will always lead to a diagonal movement
				if(keyLeft && keyRight && keyUp && !keyDown) {
					if (firstKey.equals("Left"))
					{
						if (secondKey.equals("Right"))
						{
							xa = xa - 1 - playerSpeed;
							ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, keyDown);
							thirdKey = "Up";
						}
						if (secondKey.equals("Up"))
						{
							xa = xa - 1 - playerSpeed;
							ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, keyDown);
							thirdKey = "Right";
						}
						if (secondKey.equals("Down"))
						{						
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Down"))
					    	{
						    	xa = xa - 1 - playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Up"))
					    	{
					    		xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Up"))
					    	{
					    		xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Down"))
					    	{
					    		xa = xa + 1 + playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}						    
						}
	
					}
				    if (firstKey.equals("Right"))
					{
				    	if (secondKey.equals("Left"))
						{
				    		xa = xa + 1 + playerSpeed;
				    		ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, keyDown);
							thirdKey = "Up";
						}
						if (secondKey.equals("Up"))
						{
							xa = xa + 1 + playerSpeed;
							ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, keyDown);
							thirdKey = "Left";
						}
						if (secondKey.equals("Down"))
						{						
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Down"))
					    	{
						    	xa = xa - 1 - playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Up"))
					    	{
					    		xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Up"))
					    	{
					    		xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Down"))
					    	{
					    		xa = xa + 1 + playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}						    
						}
					}
				    
				    if (firstKey.equals("Up"))
					{
				    	if (secondKey.equals("Left"))
						{
				    		xa = xa - 1 - playerSpeed;
				    		ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, keyDown);
							thirdKey = "Right";
						}
						if (secondKey.equals("Right"))
						{
							xa = xa + 1 + playerSpeed;
							ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, keyDown);
							thirdKey = "Left";
						}
						if (secondKey.equals("Down"))
						{						
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Down"))
					    	{
						    	xa = xa - 1 - playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Up"))
					    	{
					    		xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Up"))
					    	{
					    		xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Down"))
					    	{
					    		xa = xa + 1 + playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}						    
						}
					}
				    if (firstKey.equals("Down")) 
				    {
				    	if (rightOrLeft.equals("Right") && upOrDown.equals("Down"))
				    	{
					    	xa = xa - 1 - playerSpeed;
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, false, true);
				    	}
				    	if (rightOrLeft.equals("Left") && upOrDown.equals("Up"))
				    	{
				    		xa = xa + 1 + playerSpeed;
				    		ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, false);
				    	}
				    	if (rightOrLeft.equals("Right") && upOrDown.equals("Up"))
				    	{
				    		xa = xa - 1 - playerSpeed;
				    		ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, false);
				    	}
				    	if (rightOrLeft.equals("Left") && upOrDown.equals("Down"))
				    	{
				    		xa = xa + 1 + playerSpeed;
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, false, true);
				    	}
				    }
				}
				if(keyLeft && keyRight && !keyUp && keyDown) {
					if (firstKey.equals("Left"))
					{
						if (secondKey.equals("Right"))
						{
							xa = xa - 1 - playerSpeed;
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, keyUp, true);
							thirdKey = "Down";
						}
						if (secondKey.equals("Down"))
						{
							xa = xa - 1 - playerSpeed;
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, keyUp, true);
							thirdKey = "Right";
						}
						if (secondKey.equals("Up"))
						{						
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Down"))
					    	{
						    	xa = xa - 1 - playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Up"))
					    	{
					    		xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Up"))
					    	{
					    		xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Down"))
					    	{
					    		xa = xa + 1 + playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}						    
						}
	
					}
				    if (firstKey.equals("Right"))
					{
				    	if (secondKey.equals("Left"))
						{
				    		xa = xa + 1 + playerSpeed;
				    		ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, keyUp, true);
							thirdKey = "Down";
						}
						if (secondKey.equals("Down"))
						{
							xa = xa + 1 + playerSpeed;
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, keyUp, true);
							thirdKey = "Left";
						}
						if (secondKey.equals("Up"))
						{						
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Down"))
					    	{
						    	xa = xa - 1 - playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Up"))
					    	{
					    		xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Up"))
					    	{
					    		xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Down"))
					    	{
					    		xa = xa + 1 + playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}						    
						}
					}
				    
				    if (firstKey.equals("Down"))
					{
				    	if (secondKey.equals("Left"))
						{
				    		xa = xa - 1 - playerSpeed;
				    		ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, keyUp, true);
							thirdKey = "Right";
						}
						if (secondKey.equals("Right"))
						{
							xa = xa + 1 + playerSpeed;
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, keyUp, true);
							thirdKey = "Left";
						}
						if (secondKey.equals("Up"))
						{						
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Down"))
					    	{
						    	xa = xa - 1 - playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Up"))
					    	{
					    		xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Up"))
					    	{
					    		xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Down"))
					    	{
					    		xa = xa + 1 + playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}						    
						}
					}
				    if (firstKey.equals("Up")) 
				    {
				    	if (rightOrLeft.equals("Right") && upOrDown.equals("Down"))
				    	{
					    	xa = xa - 1 - playerSpeed;
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, false, true);
				    	}
				    	if (rightOrLeft.equals("Left") && upOrDown.equals("Up"))
				    	{
				    		xa = xa + 1 + playerSpeed;
				    		ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, false);
				    	}
				    	if (rightOrLeft.equals("Right") && upOrDown.equals("Up"))
				    	{
				    		xa = xa - 1 - playerSpeed;
				    		ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, false);
				    	}
				    	if (rightOrLeft.equals("Left") && upOrDown.equals("Down"))
				    	{
				    		xa = xa + 1 + playerSpeed;
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, false, true);
				    	}
				    }
				}
				if(!keyLeft && keyRight && keyUp && keyDown) {
					if (firstKey.equals("Right"))
					{
						if (secondKey.equals("Up"))
						{
							xa = xa - 1 - playerSpeed;
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, false, true);
							thirdKey = "Down";
						}
						if (secondKey.equals("Down"))
						{
							xa = xa - 1 - playerSpeed;
							ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, false);
							thirdKey = "Up";
						}
						if (secondKey.equals("Left"))
						{						
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Down"))
					    	{
						    	xa = xa - 1 - playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Up"))
					    	{
					    		xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Up"))
					    	{
					    		xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Down"))
					    	{
					    		xa = xa + 1 + playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}						    
						}
	
					}
				    if (firstKey.equals("Up"))
					{
				    	if (secondKey.equals("Right"))
						{
				    		xa = xa - 1 - playerSpeed;
				    		ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, false, true);
							thirdKey = "Down";
						}
						if (secondKey.equals("Down"))
						{
							xa = xa - 1 - playerSpeed;
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, false, true);
							thirdKey = "Right";
						}
						if (secondKey.equals("Left"))
						{						
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Down"))
					    	{
						    	xa = xa - 1 - playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Up"))
					    	{
					    		xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Up"))
					    	{
					    		xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Down"))
					    	{
					    		xa = xa + 1 + playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}						    
						}
					}
				    
				    if (firstKey.equals("Down"))
					{
				    	if (secondKey.equals("Right"))
						{
				    		xa = xa - 1 - playerSpeed;
				    		ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, false);
							thirdKey = "Up";
						}
						if (secondKey.equals("Up"))
						{
							xa = xa - 1 - playerSpeed;
							ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, false);
							thirdKey = "Right";
						}
						if (secondKey.equals("Left"))
						{						
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Down"))
					    	{
						    	xa = xa - 1 - playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Up"))
					    	{
					    		xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Up"))
					    	{
					    		xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Down"))
					    	{
					    		xa = xa + 1 + playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}						    
						}
					}
				    if (firstKey.equals("Left")) 
				    {
				    	if (rightOrLeft.equals("Right") && upOrDown.equals("Down"))
				    	{
					    	xa = xa - 1 - playerSpeed;
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, false, true);
				    	}
				    	if (rightOrLeft.equals("Left") && upOrDown.equals("Up"))
				    	{
				    		xa = xa + 1 + playerSpeed;
				    		ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, false);
				    	}
				    	if (rightOrLeft.equals("Right") && upOrDown.equals("Up"))
				    	{
				    		xa = xa - 1 - playerSpeed;
				    		ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, false);
				    	}
				    	if (rightOrLeft.equals("Left") && upOrDown.equals("Down"))
				    	{
				    		xa = xa + 1 + playerSpeed;
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, false, true);
				    	}
				    }
				}
				if(keyLeft && !keyRight && keyUp && keyDown) {
					if (firstKey.equals("Left"))
					{
						if (secondKey.equals("Up"))
						{
							xa = xa + 1 + playerSpeed;
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, false, true);
							thirdKey = "Down";
						}
						if (secondKey.equals("Down"))
						{
							xa = xa + 1 + playerSpeed;
							ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, false);
							thirdKey = "Up";
						}
						if (secondKey.equals("Right"))
						{						
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Down"))
					    	{
						    	xa = xa - 1 - playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Up"))
					    	{
					    		xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Up"))
					    	{
					    		xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Down"))
					    	{
					    		xa = xa + 1 + playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}						    
						}
					}
				    if (firstKey.equals("Up"))
					{
				    	if (secondKey.equals("Left"))
						{
				    		xa = xa + 1 + playerSpeed;
				    		ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, false, true);
							thirdKey = "Down";
						}
						if (secondKey.equals("Down"))
						{
							xa = xa + 1 + playerSpeed;
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, false, true);
							thirdKey = "Left";
						}
						if (secondKey.equals("Right"))
						{						
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Down"))
					    	{
						    	xa = xa - 1 - playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Up"))
					    	{
					    		xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Up"))
					    	{
					    		xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Down"))
					    	{
					    		xa = xa + 1 + playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}						    
						}
					}
				    
				    if (firstKey.equals("Down"))
					{
				    	if (secondKey.equals("Left"))
						{
				    		xa = xa + 1 + playerSpeed;
				    		ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, false);
							thirdKey = "Up";
						}
						if (secondKey.equals("Up"))
						{
							xa = xa + 1 + playerSpeed;
							ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, false);
							thirdKey = "Left";
						}
						if (secondKey.equals("Right"))
						{						
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Down"))
					    	{
						    	xa = xa - 1 - playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Up"))
					    	{
					    		xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Right") && upOrDown.equals("Up"))
					    	{
					    		xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
					    	}
					    	if (rightOrLeft.equals("Left") && upOrDown.equals("Down"))
					    	{
					    		xa = xa + 1 + playerSpeed;
								ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
					    	}						    
						}
					}
				    if (firstKey.equals("Right")) 
				    {
				    	if (rightOrLeft.equals("Right") && upOrDown.equals("Down"))
				    	{
					    	xa = xa - 1 - playerSpeed;
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, false, true);
				    	}
				    	if (rightOrLeft.equals("Left") && upOrDown.equals("Up"))
				    	{
				    		xa = xa + 1 + playerSpeed;
				    		ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, false);
				    	}
				    	if (rightOrLeft.equals("Right") && upOrDown.equals("Up"))
				    	{
				    		xa = xa - 1 - playerSpeed;
				    		ya = ya + 1 + playerSpeed;
							playerMob.updatePlayer(false, false, true, false);
				    	}
				    	if (rightOrLeft.equals("Left") && upOrDown.equals("Down"))
				    	{
				    		xa = xa + 1 + playerSpeed;
							ya = ya - 1 - playerSpeed;
							playerMob.updatePlayer(false, false, false, true);
				    	}
				    }
				}
				
				// Solves what happens if all four keys are pressed at the same time
				// last two opposites will overwrite previous two (will always result in diagonal movement)
				if(keyLeft && keyRight && keyUp && keyDown &&
						!firstKey.equals("") && !secondKey.equals("") && !thirdKey.equals(""))
				{
					if (firstKey.equals("Left"))
					{
						if (secondKey.equals("Right"))
						{
							if (thirdKey.equals("Up"))
							{
								xa = xa - 1 - playerSpeed;
					    		ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
								rightOrLeft = "Right";
								upOrDown = "Down";
							}
							if (thirdKey.equals("Down"))
							{
								xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
								rightOrLeft = "Right";
								upOrDown = "Up";
	
							}
						}
						if (secondKey.equals("Up"))
						{
							if (thirdKey.equals("Right"))
							{
								xa = xa - 1 - playerSpeed;
					    		ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
								rightOrLeft = "Right";
								upOrDown = "Down";
							}
							if (thirdKey.equals("Down"))
							{
								xa = xa - 1 - playerSpeed;
					    		ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
								rightOrLeft = "Right";
								upOrDown = "Down";
							}
						}
						if (secondKey.equals("Down"))
						{
							if (thirdKey.equals("Right"))
							{
								xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
								rightOrLeft = "Right";
								upOrDown = "Up";
							}
							if (thirdKey.equals("Up"))
							{
								xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
								rightOrLeft = "Right";
								upOrDown = "Up";
							}
						}
					}
					if (firstKey.equals("Right"))
					{
						if (secondKey.equals("Left"))
						{
							if (thirdKey.equals("Up"))
							{
								xa = xa + 1 + playerSpeed;
					    		ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
								rightOrLeft = "Left";
								upOrDown = "Down";
							}
							if (thirdKey.equals("Down"))
							{
								xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
								rightOrLeft = "Left";
								upOrDown = "Up";
							}
						}
						if (secondKey.equals("Up"))
						{
							if (thirdKey.equals("Left"))
							{
								xa = xa + 1 + playerSpeed;
					    		ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
								rightOrLeft = "Left";
								upOrDown = "Down";
							}
							if (thirdKey.equals("Down"))
							{
								xa = xa + 1 + playerSpeed;
					    		ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
								rightOrLeft = "Left";
								upOrDown = "Down";
							}
						}
						if (secondKey.equals("Down"))
						{
							if (thirdKey.equals("Left"))
							{
								xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
								rightOrLeft = "Left";
								upOrDown = "Up";
							}
							if (thirdKey.equals("Up"))
							{
								xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
								rightOrLeft = "Left";
								upOrDown = "Up";
							}
						}
					}
					if (firstKey.equals("Up"))
					{
						if (secondKey.equals("Left"))
						{
							if (thirdKey.equals("Down"))
							{
								xa = xa - 1 - playerSpeed;
					    		ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
								rightOrLeft = "Right";
								upOrDown = "Down";
							}
							if (thirdKey.equals("Right"))
							{
								xa = xa - 1 - playerSpeed;
					    		ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
								rightOrLeft = "Right";
								upOrDown = "Down";
							}
						}
						if (secondKey.equals("Right"))
						{
							if (thirdKey.equals("Left"))
							{
								xa = xa + 1 + playerSpeed;
					    		ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
								rightOrLeft = "Left";
								upOrDown = "Down";
							}
							if (thirdKey.equals("Down"))
							{
								xa = xa + 1 + playerSpeed;
					    		ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
								rightOrLeft = "Left";
								upOrDown = "Down";
							}
						}
						if (secondKey.equals("Down"))
						{
							if (thirdKey.equals("Left"))
							{
								xa = xa - 1 - playerSpeed;
					    		ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
								rightOrLeft = "Right";
								upOrDown = "Down";
							}
							if (thirdKey.equals("Right"))
							{
								xa = xa + 1 + playerSpeed;
					    		ya = ya - 1 - playerSpeed;
								playerMob.updatePlayer(false, false, false, true);
								rightOrLeft = "Left";
								upOrDown = "Down";
							}
						}
					}
					if (firstKey.equals("Down"))
					{
						if (secondKey.equals("Left"))
						{
							if (thirdKey.equals("Up"))
							{
								xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
								playerMob.updatePlayer(false, false, true, false);
								rightOrLeft = "Right";
								upOrDown = "Up";
							}
							if (thirdKey.equals("Right"))
							{
								xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
					    		playerMob.updatePlayer(false, false, true, false);
					    		rightOrLeft = "Right";
								upOrDown = "Up";
							}
						}
						if (secondKey.equals("Right"))
						{
							if (thirdKey.equals("Left"))
							{
								xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
					    		playerMob.updatePlayer(false, false, true, false);
					    		rightOrLeft = "Left";
								upOrDown = "Up";
							}
							if (thirdKey.equals("Up"))
							{
								xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
					    		playerMob.updatePlayer(false, false, true, false);
					    		rightOrLeft = "Left";
								upOrDown = "Up";
							}
						}
						if (secondKey.equals("Up"))
						{
							if (thirdKey.equals("Left"))
							{
								xa = xa - 1 - playerSpeed;
					    		ya = ya + 1 + playerSpeed;
					    		playerMob.updatePlayer(false, false, true, false);
					    		rightOrLeft = "Right";
								upOrDown = "Up";
							}
							if (thirdKey.equals("Right"))
							{
								xa = xa + 1 + playerSpeed;
					    		ya = ya + 1 + playerSpeed;
					    		playerMob.updatePlayer(false, false, true, false);
					    		rightOrLeft = "Left";
								upOrDown = "Up";
							}
						}
					}
				}
				else if (keyLeft && keyRight && keyUp && keyDown &&
					(firstKey.equals("") || secondKey.equals("") || thirdKey.equals("")))
				{
					firstKey = "Left";
					secondKey = "Right";
					thirdKey = "Up";
					xa = xa - 1 - playerSpeed;
		    		ya = ya - 1 - playerSpeed;
					playerMob.updatePlayer(false, false, false, true);
					rightOrLeft = "Right";
					upOrDown = "Down";
				}
				
				//No keys are pressed
				if(!keyLeft && !keyRight && !keyUp && !keyDown) {
					playerMob.updatePlayer(keyLeft, keyRight, keyUp, keyDown);
				}
				movePlayer(xa, ya);
				// End Movement
			}
			//I(Inventory)
			if(keyInventory) {
				state = STATE.INGAMEMENU;
				inputWait = 10;
			}
			
			// QUIT BUTTON (TEMP)
			if(keyBack)
			{
				state = STATE.TITLE;
				option = OPTION.LOADGAME;
				sprites().add(titleArrow);
				sprites().add(titleEraseBomb);

			}
			
			if(keyAction)
			{
				if (!renderTextbox)
				{
					renderTextbox = true;
				}
				else
				{
					renderTextbox = false;
				}
				inputWait = 10;
			}
				
			//SpaceBar(action button)
			if(keySpace) 
			{
				canMove = false;
				playerMob.inOutItem();
				inputWait = 20;
			}
			else
			{
				canMove = true;
			}
			
			

			
			if(keyChange)
			{
				Player player = new Player();
				playerMob = player.getNewPlayer(mainCharacter, playerMob, graphics(), this, sprites(), playerNumber);
				inputWait = 10;
				if (playerNumber == 1)
				{
					playerNumber++;
				}
				else if (playerNumber == 2)
				{
					playerNumber--;
				}	
			}
				
		}//end in game choices
		
		/*****************************************
		 * Special actions for the Title Menu
		 *****************************************/
		if(state == STATE.TITLE && inputWait < 0) {
			//For when no initial choice has been made
			if(option == OPTION.NONE)
			{
				//S or down arrow(Change selection)
				if(keyDown && titleLocation < 1) {
					titleX = 520;
					titleY = 510;
					titleLocation++;
					inputWait = 5;
				}
				//W or up arrow(Change selection
				if(keyUp && titleLocation > 0) {
					titleX = 520;
					titleY = 416;
					titleLocation--;
					inputWait = 5;
				}
				//Enter key(Make a choice)
				if(keyEnter) {
					if(titleLocation == 0 && !title.isFull()) // checks to make sure there is a save slot left for a new game (3 max)
					{
						option = OPTION.NEWGAME;
						titleLocation = 0;
						inputWait = 10;
						keyEnter = false;
						titleX2 = 1000; // hide arrow far off screen forever away
						titleY2 = 1000; // bye
					}
					if(titleLocation == 1 && !title.isEmpty()) // checks to make sure there is a game to load
					{
						option = OPTION.LOADGAME;
						titleLocation = 0;
						inputWait = 5;
						keyEnter = false;
						
						titleX2 = 520; // sets location of arrow to file
						titleY2 = 194; // y value
					}

				}
			}//end option none
			
			if(option == OPTION.NEWGAME)
			{
				//Backspace(Exit choice)
				if(keyBack && !title.isGetName() && title.getFileName().length() == 0)
				{
					titleLocation = 0;
					inputWait = 5;
					titleX2 = 340;
					titleY2 = 310;
					option = OPTION.NONE;
				}
				
				//The following is for when a new file needs to be created - Typesetting
				title.setFileName(currentChar);
				currentChar = '\0'; //null
				//Back space(Delete last character)
				if(keyBack) {
					title.deleteChar();
					inputWait = 5;
				}
				//Back space(exit name entry if name has no characters)
				if(keyBack && title.getFileName().length() == 0) {
					title.setGetName(false);
					titleX2 -= 40;
					inputWait = 5;
				}
				//Enter key(Write the file using the currently typed name and save it)
				if(keyEnter && title.getFileName().length() > 0) {
					save.newFile(title.getFileName());
					title.setGetName(false);
					currentFile = title.getFileName();
					state = STATE.GAME;
					option = OPTION.NONE;
					setGameState(STATE.GAME);
				}
					
					
				//end get name
			}
			
			if (option == OPTION.LOADGAME)
			{
				//Backspace(Exit choice)
				if(keyBack && !title.isGetName())
				{
					if (title.getTitleSymbol().equals("arrow"))
					{
						titleLocation = 1;
						inputWait = 5;
						titleX2 = 340;
						titleY2 = 310;
						option = OPTION.NONE;
					}
					else if (title.getTitleSymbol().equals("bomb"))
					{
						title.setTitleSymbol("arrow");
						inputWait = 8;
					}
				}
				
				//S or down arrow(Change selection)
				if(keyDown && !title.isGetName() && title.checkFilesNumber(titleLocation, "Down")) {
					titleLocation++;
				//	if (titleLocation != 3) // checks to see if arrow is about to be on erase game (if it is, it increments less)
					if (titleLocation != title.files().length)
					{
						titleY2 += 120;
					}
					else
					{
						titleY2 += 110;
					}
					inputWait = 7;
				}
				//W or up arrow(Change selection)
				if(keyUp && !title.isGetName() && title.checkFilesNumber(titleLocation, "Up")) {
					titleLocation--;
					if (titleLocation != title.files().length - 1) // checks to see if arrow is on erase game or not (if it is, it deccrements less)
					{
						titleY2 -= 120;
					}
					else
					{	
						titleY2 -= 110;
					}
						inputWait = 7;
				}
				
				//Enter key(Make a choice)
				// If one of the three files is selected (not the erase file)
				if(keyEnter && titleLocation != title.files().length) 
				{
					if (title.getTitleSymbol().equals("arrow")) // if not erasing a file, load the file and the game
					{
				//	Load the currently selected file
						currentFile = title.enter();
						if(currentFile != "") { //File is empty
							loadGame();
							inputWait = 5;
							option = OPTION.NONE;
							state = STATE.GAME;
							setGameState(STATE.GAME);
						}
					}
					else if(title.getTitleSymbol().equals("bomb")) // DELETE THE FILE
					{
						title.deleteFile(titleLocation); // delete
						inputWait = 10; // wait
						if (title.files().length == 0) // if all files are deleted, go back to title screen
						{

							titleLocation = 1;
							inputWait = 7;
							titleX2 = 340;
							titleY2 = 310;
							option = OPTION.NONE;
							title.setTitleSymbol("arrow");
						}
					}
				}				
				 // if Erase file is selected
				else if(keyEnter && titleLocation == title.files().length)
				{
					if (title.getTitleSymbol().equals("arrow"))
					{
						title.setTitleSymbol("bomb");
					}
					else if(title.getTitleSymbol().equals("bomb"))
					{
						title.setTitleSymbol("arrow");
					}
					inputWait = 11;
				}
	
			}
			
		}//end title state
		
		
		/******************************************
		 * Special actions for In Game Menu
		 ******************************************/
		if(state == STATE.INGAMEMENU && inputWait < 0) {
			//I(Close inventory)
			if(keyInventory) {
				state = STATE.GAME;
				option = OPTION.NONE;
				inLocation = 0;
				inY = 90;
				inputWait = 8;
			}
			//No option is chosen yet
			if(option == OPTION.NONE){ 
				if(wait == 0) wasSaving = false;
				//W or up arrow(Move selection)
				if(keyUp) {
					if(inLocation > 0) {
						inY -= 108;
						inLocation--;
						inputWait = 10;
					}
				}
				//S or down arrow(move selection)
				if(keyDown) {
					if(inLocation < 4) {
						inY += 108;
						inLocation++;
						inputWait = 10;
					}
				}
				//Enter key(Make a choice)
				if(keyEnter) {
					if(inLocation == 0){
						option = OPTION.ITEMS;
						inputWait = 5;
					}
					if(inLocation == 1){
						option = OPTION.EQUIPMENT;
						inputWait = 5;
					}
					if(inLocation == 2){
						option = OPTION.MAGIC;
						inputWait = 5;
					}
					if(inLocation == 3){
						option = OPTION.STATUS;
						inputWait = 5;
					}
					if(inLocation == 4){
						option = OPTION.SAVE;
						inputWait = 20;
					}
					keyEnter = false;
				}
			}
			
			//Set actions for specific choices in the menu
			//Items
			if(option == OPTION.ITEMS) {
				//W or up arrow(move selection)
				if(keyUp){
					if(sectionLoc == 0) inMenu.loadOldItems();
					if(sectionLoc - 1 != -1) sectionLoc--;
					inputWait = 8;
				}
				//S or down arrow(move selection)
				if(keyDown) {
					if(sectionLoc == 3) inMenu.loadNextItems();
					if(inMenu.getTotalItems() > sectionLoc + 1 && sectionLoc < 3) sectionLoc++;
					inputWait = 8;
				}
				//Enter key(Make a choice)
				if(keyEnter){
					if(confirmUse) {
						inMenu.useItem(); //then use item
						confirmUse = false;
						keyEnter = false;
					}
					if(inMenu.checkCount() > 0 && keyEnter) confirmUse = true;
					inputWait = 10;
				}
				//Back space(Go back on your last choice)
				if(keyBack) confirmUse = false;
			}
			
			//Equipment
			if(option == OPTION.EQUIPMENT) {
				//W or up arrow(move selection)
				if(keyUp){
					if(sectionLoc == 0) inMenu.loadOldItems();
					if(sectionLoc - 1 != -1) sectionLoc--;
					inputWait = 8;
				}
				//S or down arrow(move selection)
				if(keyDown) {
					if(sectionLoc == 3) inMenu.loadNextEquipment();
					if(inMenu.getTotalEquipment() > sectionLoc + 1 && sectionLoc < 3) sectionLoc++;
					inputWait = 8;
				}
			}
			
			//Saving
			if(option == OPTION.SAVE){
				//Key enter(Save the file)
				if(keyEnter){
					save.saveState(currentFile, data());
					inputWait = 20;
					wait = 200;
					waitOn = true;
					wasSaving = true;
					option = OPTION.NONE;
				}
			}
			
			//Backspace(if a choice has been made, this backs out of it)
			if(keyBack && option != OPTION.NONE) {
				option = OPTION.NONE;
				inMenu.setItemLoc(0);
				sectionLoc = 0;
				inputWait = 8;
				keyBack = false;
			}
			//Backspace(if a choice has not been made, this closes the inventory)
			if(keyBack && option == OPTION.NONE) {
				state = STATE.GAME;
				option = OPTION.NONE;
				inLocation = 0;
				sectionLoc = 0;
				inY = 90;
				inputWait = 8;
			}
		}
		inputWait--;
	}
	
	/**
	 * Inherited method
	 * @param keyCode
	 * 
	 * Set keys for a new game action here using a switch statement
	 * dont forget gameKeyUp
	 */
	void gameKeyDown(int keyCode) {
		switch(keyCode) {
	        case KeyEvent.VK_LEFT:
	            keyLeft = true;
	            break;
	        case KeyEvent.VK_A:
	        	keyLeft = true;
	        	break;
	        case KeyEvent.VK_RIGHT:
	            keyRight = true;
	            break;
	        case KeyEvent.VK_D:
	        	keyRight = true;
	        	break;
	        case KeyEvent.VK_UP:
	            keyUp = true;
	            break;
	        case KeyEvent.VK_W:
	        	keyUp = true;
	        	break;
	        case KeyEvent.VK_DOWN:
	            keyDown = true;
	            break;
	        case KeyEvent.VK_S:
	        	keyDown = true;
	        	break;
	        case KeyEvent.VK_I:
	        	keyInventory = true;
	        	break;
	        case KeyEvent.VK_F:
	        	keyAction = true;
	        	break;
	        case KeyEvent.VK_ENTER:
	        	keyEnter = true;
	        	break;
	        case KeyEvent.VK_BACK_SPACE:
	        	keyBack = true;
	        	break;
	        case KeyEvent.VK_SPACE:
	        	keySpace = true;
	        	break;
	        case KeyEvent.VK_Y:
	        	keyChange = true;
	        	break;
        }
	}

	/**
	 * Inherited method
	 * @param keyCode
	 * 
	 * Set keys for a new game action here using a switch statement
	 * Dont forget gameKeyDown
	 */
	void gameKeyUp(int keyCode) {
		switch(keyCode) {
        case KeyEvent.VK_LEFT:
            keyLeft = false;
            break;
        case KeyEvent.VK_A:
        	keyLeft = false;
        	break;
        case KeyEvent.VK_RIGHT:
            keyRight = false;
            break;
        case KeyEvent.VK_D:
        	keyRight = false;
        	break;
        case KeyEvent.VK_UP:
            keyUp = false;
            break;
        case KeyEvent.VK_W:
        	keyUp = false;
        	break;
        case KeyEvent.VK_DOWN:
            keyDown = false;
            break;
        case KeyEvent.VK_S:
        	keyDown = false;
        	break;
        case KeyEvent.VK_I:
	    	keyInventory = false;
	    	break;
	    case KeyEvent.VK_F:
	    	keyAction = false;
	    	break;
	    case KeyEvent.VK_ENTER:
	    	keyEnter = false;
	    	break;
	    case KeyEvent.VK_BACK_SPACE:
	    	keyBack = false;
	    	break;
	    case KeyEvent.VK_SPACE:
	    	keySpace = false;
	    	break;
	    case KeyEvent.VK_Y:
	    	keyChange = false;
	    	break;
		}
	}

	/**
	 * Inherited method
	 * Currently unused
	 */
	void gameMouseDown() {	
	}

	/**
	 * Inherited method
	 * Currently if the game is running and the sword is out, the player attacks with it
	 */
	void gameMouseUp() { 
		if(getMouseButtons(1) == true && playerMob.isTakenOut() && inputWait < 0) {
			playerMob.attack();
			inputWait = 24;
		}
		inputWait--;
	}

	/**
	 * Inherited Method
	 * Currently unused
	 */
	void gameMouseMove() {
	}
	 
	 //From the title screen, load a game file by having the super class get the data,
	 // then handling where the pieces of the data will be assigned here.
	/**
	 * Inherited Method
	 * 
	 * The title screen calls this method when a currently existing file is chosen
	 * Add new saved game details here as well as in the 'Data.java' class
	 * 
	 * Currently only the player x and y location and the current map is saved
	 */
	 void loadGame() {
		 if(currentFile != "") {
			 System.out.println("Loading...");
			 loadData(currentFile);
			 tiles().clear();
			 sprites().clear();
			 for(int i = 0; i < mapBase.maps.length; i++){
				 if(mapBase.getMap(i) == null) continue;
				 if(data().getMapName() == mapBase.getMap(i).mapName()) currentMap = mapBase.getMap(i);
				 if(data().getOverlayName() == mapBase.getMap(i).mapName()) currentOverlay = mapBase.getMap(i);
			 }
			 playerX = data().getPlayerX();
			 playerY = data().getPlayerY();
			 sprites().add(playerMob);
			 for(int i = 0; i < currentMap.getWidth() * currentMap.getHeight(); i++){
					addTile(currentMap.accessTile(i));
					addTile(currentOverlay.accessTile(i));
					if(currentMap.accessTile(i).hasMob()) sprites().add(currentMap.accessTile(i).mob());
					if(currentOverlay.accessTile(i).hasMob()) sprites().add(currentOverlay.accessTile(i).mob());
			}//end for
			System.out.println("Load Successful");
		 } //end file is not empty check
	 } //end load method
	 

} //end class