/****************************************************************************************************
 * @author Travis R. Dewitt
 * @version 1.2
 * Date: July 5, 2015
 * 
 * Title: InGameMenu
 * Description: A class which contains all relative player information. With this, a menu can be displayed,
 * items and descriptions can be chosen, the game can be saved and items can be stored. Player stats are
 * carried here as well. Think of this class as a backpack.
 * 
 * Much of this class is still yet to be populated. As new items are created for your game, this class needs
 * to be updated to handle those items. Item.java will also have methods which need to be updated for correct handling.
 * 
 * Methods that will need constant updating: countItems(), setItems(), levelUp() and useItem()
 * 
 * This work is licensed under a Attribution-NonCommercial 4.0 International
 * CC BY-NC-ND license. http://creativecommons.org/licenses/by-nc/4.0/
 ****************************************************************************************************/
//Package
package axohEngine2.project;

//Imports
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;

import axohEngine2.entities.ImageEntity;

public class InGameMenu {

	/**************
	 * Variables
	 **************/
	//_background - Image used to display the menu background
	private ImageEntity _background;
	
	//_option - An enum which holds the choice made at certain times
	//SCREENWIDTH, SCREENHEIGHT - width and height of the JFrame window
	//random - A random number generator
	private OPTION _option;
	private int SCREENWIDTH;
	private int SCREENHEIGHT;
	private Random random = new Random();
	
	//items - A list of items contained in the menu
	//equipment - A list of equipment contained in the menu
	//counts - An array of totals of items grouped together currently in inventory
	//shownEquipment - An array of items, specifically equipment, which is being shown on screen
	//shownItems - An array of items which are being shown on screen
	//itemLocation - Reference below comment
	//sectionLoc - Reference below comment
	//totalItems - A number that counts the total different items in the inventory, not duplicates
	//totalEquipment - same as totalItems but for equipment types
	private LinkedList<Item> items;
	private LinkedList<Item> equipment;
	private int[] counts;
	private Item[] shownEquipment;
	private Item[] shownItems;
	private int itemLocation;
	private int sectionLoc;
	private int totalItems;
	private int totalEquipment;
	
	//Starting stat variables
	private int level = 1;
	private int maxHealth = 35;
	private int currHealth = maxHealth;
	private int magic = 5;
	private int attack = 8;
	private int defense = 4;
	private int experience;
	private int nextLevel = 20;
	private int nextExp = 25;
	
	/********************************************************************************************
	 * The Items algorithms need an explanation, starting with:
	 *  
	 *  countItems(): The purpose
	 *  of this method is to count how many of each item is in the list of avaiable items to the 
	 *  player. Each index coincides with an item count of your choice. Because this count method is
	 *  updated each time an item is obtained, the count for every item is set to 0 first, Then 
	 *  populated by checking the name of an item in the list and adding 1 to it's index location.
	 *  
	 *  setItems(): First, since this is checked every time an item is obtained, the totalItems variable
	 *  which counts the currently showing items, is set to 0 and the repopulated. The purpose of
	 *  this method is to make sure only specific items are being shown in the menu and specifically not
	 *  duplicates. It populates an array of showable items if it exists, otherwise it skips over 
	 *  repeated names. The totalItems is only added to when a shownItems index is not null. This is
	 *  because some indexes may not be used because items can be obtained out of order.
	 *  
	 *  loadNextItems(): itemLocation, is the variable that is the start number for the items to be
	 *  displayed on screen, used in the main FOR loop. This method runs a check, starting wherever
	 *  the fourth item + 1 is found in the showItems array. This way the fourth item isn't counted for
	 *  when the check to make sure a fifth item exists to be displayed. 
	 *  
	 *  loadOldItems(): Simply subtract one from the current itemLocation if it isn't 0. These last two
	 *  methods are only called when the current cursor location, kept track by the game itself, is near
	 *  the top or the bottom of the screen. 0 or 3.
	 *  
	 *  Main Rendering algorithm: A for loop begins at the current itemLocation and ends when it reaches
	 *  start + 4. Start begins equaling getStart(itemLocation) but is added to if the current index in 
	 *  shownItems returns null, thereby skipping that index. The first check is done to make sure that
	 *  no index out of bounds errors occur and stops the loop if they may. Any time the loop is stopped,
	 *  'y' is set back to 0 for the next rendering. The highlight line is then drawn only if the items
	 *  list is not empty. This starts at the current sectionLoc which is calculated based on up and
	 *  down movement in the menu and ends at the end of the length of the current item name. Then the
	 *  current item name is drawn along with its count and image next to it. The count is rendered
	 *  by changing the count[] index and changing it to an INTEGER, then making it in to a STRING. The count
	 *  number render location is dependant on the length of the name of the item. The reason why a 
	 *  seperate variable 'y' is used to move down the rendered text is because 'i' could be very large
	 *  depending on if some items aren't obtained yet, which would mean their index would be null and 
	 *  'i' would increase because the item would be skipped. The last check is done in order to make
	 *  sure 'y' is correctly reset just in case 4 items are found immediately and the loop would end.
	 *  
	 *  itemLocation: The starting point in the list of items that will be shown, for example, if it is 2,
	 *  then you have 6 items, the first two arent being shown.
	 *  
	 *  sectionLoc: The item section you are highlighting currently. For example, if it is 3, then you
	 *  are highlighting the third item on screen.
	 *  
	 *  Using the above two variables, adding itemLocation and sectionLoc together will always net you the
	 *  correct item to be managed
	 *  
	 ********************************************************************************************/
	
	/*****************************************************************
	 * Constructor
	 * Change count[] and shownItem[] size to adjust for even more items in game
	 * 
	 * @param background - ImageEntity to be used for the menu background
	 * @param screenWidth - Width of the Jframe
	 * @param screenHeight - Height of the JFrame
	 ******************************************************************/
	public InGameMenu(ImageEntity background, int screenWidth, int screenHeight) {
		_background = background;
		SCREENWIDTH = screenWidth;
		SCREENHEIGHT = screenHeight;
		items = new LinkedList<Item>();
		equipment = new LinkedList<Item>();
		counts = new int[100];
		shownItems = new Item[100];
		shownEquipment = new Item[100];
	}
	
	/*******************************************************************
	 * Update some variables based on choices elsewhere in the engine
	 * 
	 * @param option - OPTION enum which is the current choice
	 * @param sectionLocation - current choice, an int
	 * @param health - Player health int
	 *******************************************************************/
	public void update(OPTION option, int sectionLocation, int health){
		_option = option;
		sectionLoc = sectionLocation;
		currHealth = health;
		levelUp();
	}
	
	/*************************************************************
	 * Add an item in to the menu backpack
	 * 
	 * @param item - Item type to be added to the backpack
	 *************************************************************/
	public void addItem(Item item) {
		items.add(item);
		countItems();
		setItems();
	}
	
	//Reference addItem()
	public void addEquipment(Item item) {
		equipment.add(item);
		countItems();
		setItems();
	}
	
	//Each new item and equipment for the game needs to be added to what needs to be counted
	//Reference blue code block above methods
	private void countItems() {
		for(int i = 0; i < counts.length; i++){
			counts[i] = 0;
		}
		for(int i = 0; i < items.size(); i++){ //Add items to be counted here
			if(items.get(i).getName().equals("Potion")) counts[0]++;
			if(items.get(i).getName().equals("Mega Potion")) counts[1]++;
		}
		for(int i = 0; i < equipment.size(); i++){ //add equipment to be counted here.
			
		}
	}
	
	//Each new item will need to be added here as well.
	//Reference blue code block above methods
	private void setItems() {
		totalItems = 0;
		totalEquipment = 0;
		for(int i = 0; i < items.size(); i++){
			if(items.get(i).getName() == "Potion"){ shownItems[0] = items.get(i); }
			if(items.get(i).getName() == "Mega Potion"){ shownItems[1] = items.get(i); }
		}
		for(int i = 0; i < equipment.size(); i++){ //Set items for showEquipment[] here
			
		}
		for(int i = 0; i < shownEquipment.length; i++){
			if(shownEquipment[i] == null) continue;
			totalEquipment++;
		}
		for(int i = 0; i < shownItems.length; i++){
			if(shownItems[i] == null) continue;
			totalItems++;
		}
	}
	
	//Reference blue code block above methods
	public void render(JFrame frame, Graphics2D g2d, int inX, int inY) {
		g2d.drawImage(_background.getImage(), 0, 0, SCREENWIDTH, SCREENHEIGHT, frame);
		g2d.setColor(Color.BLACK);
		g2d.drawString("Items", 120, 170);
		g2d.drawString("Equipment", 120, 275);
		g2d.drawString("Magic", 120, 385);
		g2d.drawString("Status", 120, 490);
		g2d.drawString("Save Game", 120, 600);
		g2d.setColor(Color.YELLOW);
		g2d.drawRect(inX, inY, 435, 104);
		
		if(_option == OPTION.ITEMS){
			g2d.setColor(Color.BLACK);
			g2d.drawString("Items", 920, 200);
			renderItems(frame, g2d, shownItems, items);
		}
		
		if(_option == OPTION.EQUIPMENT){
			g2d.setColor(Color.BLACK);
			g2d.drawString("Equipment", 900, 200);
			renderItems(frame, g2d, shownEquipment, equipment);
		}
		
		if(_option == OPTION.MAGIC){
			g2d.setColor(Color.BLACK);
			g2d.drawString("Magic", 880, 200);
		}
		
		if(_option == OPTION.STATUS){
			g2d.setColor(Color.BLACK);
			g2d.drawString("Status", 920, 200);
			g2d.drawString("Level: " + level, 600, 375);
			g2d.drawString("Attack: " + attack, 600, 475);
			g2d.drawString("Defense: " + defense, 600, 575);
			g2d.drawString("Health: " + currHealth, 600, 675);
			g2d.drawString("Experience: " + experience + " / " + nextLevel, 600, 775);
		}
		
		if(_option == OPTION.SAVE){
			g2d.setColor(Color.BLACK);
			g2d.drawString("Save Game", 880, 200);
		}
	}
	
	//Reference blue code block above methods
	public void renderItems(JFrame frame, Graphics2D g2d, Item[] array, LinkedList<Item> list) {
		int start = getStart(itemLocation, array);
		int y = 0;
		for(int i = start; i < start + 4; i++){
			if(array[i] == null) {
				start++;
				if(start == array.length - 1) break;
				if(i == array.length - 1) break;
				continue;
			}
			g2d.setColor(Color.YELLOW);
			if(!list.isEmpty()) g2d.drawLine(670, 410 + sectionLoc * 110, 670 + array[i].getName().length() * 37, 410 + sectionLoc * 110);
			g2d.setColor(Color.BLACK);
			g2d.drawString(array[i].getName(), 670, 400 + y * 110);
			g2d.drawString(" x " + new Integer(counts[i]).toString(), 700 + array[i].getName().length() * 37,  400 + y * 110);
			array[i].render(frame, g2d, 600, 340 + y * 110);
			y++;
		}
	}
	
	//What needs to be done to level a character up, this is checked for at every update.
	//Edit this section to change how the stats change each time a player levels up
	private void levelUp(){
		if(experience >= nextLevel){
			level++;
			
			nextLevel = nextExp;
			if(level <= 25) nextExp = nextExp + 20 + random.nextInt(level + 10);
			if(level > 25 && level <= 50) nextExp = nextExp + 40 + random.nextInt(level + 20);
			if(level > 50 && level < 75) nextExp = nextExp + 80 + random.nextInt(level + 40);
			if(level >= 75) nextExp = nextExp + 80 + random.nextInt(level + 80);
			//Health
			if(level % 10 == 10) maxHealth += random.nextInt(level) + 1;
			if(level % 5 == 5) maxHealth += 2 + random.nextInt(4);
			if(level % 3 == 3) maxHealth += 3;
			if(level % 2 == 2) maxHealth += 1;
			if(level % 7 == 7) maxHealth += 5 + random.nextInt(4) - random.nextInt(3);
			//Attack
			if(level % 7 == 7) attack += 3;
			if(level % 8 == 8) attack += 1;
			if(level % 5 == 5) attack += 1;
			if(level % 12 == 12) attack += 6 - random.nextInt(3);
			//Defense
			if(level % 4 == 4) defense += 2;
			if(level % 3 == 3) defense += 1;
			if(level % 9 == 9) defense += 4 - random.nextInt(2);
			if(level % 6 == 6) defense += 2 + random.nextInt(3);
		}
	}
	
	//Add an int, exp to the total in the backpack
	public void getExp(int exp) { experience += exp; }
	
	/*********************************************************
	 * Return the starting location after a certain amount of items are counted
	 * 
	 * @param amount - Int pertaining to how many items to move through before returning
	 * @param array - The array to get a position in(For size)
	 * @return - position to start in the array given an offset
	 *********************************************************/
	private int getStart(int amount, Item[] array){
		int found = 0;
		for(int i = 0; i < array.length; i++){
			if(found == amount) {
				return i;
			}
			if(array[i] != null) {
				found++;
			}
		}
		return 0;
	}
	
	//Move variables around for correct rendering based on keyboard actions
	public void loadNextItems() {
		int start = 0;
		start = getStart(4, shownItems);
		for(int i = start; i < shownItems.length; i++){
			if(shownItems[i] != null) {
				itemLocation++;
				break;
			}
		}
	}
	
	//Reference loadNextEquipment()
	public void loadNextEquipment() {
		int start = 0;
		start = getStart(4, shownEquipment);
		for(int i = start; i < shownEquipment.length; i++){
			if(shownEquipment[i] != null) {
				itemLocation++;
				break;
			}
		}
	}
	
	//This works with items and equipment
	//Move back up the list of items to show already passed by items or equipment
	public void loadOldItems() {
		if(itemLocation > 0) itemLocation--;
	}
	
	//Getters for totalItems and totalEquipment
	public int getTotalItems() { return totalItems; }
	public int getTotalEquipment() { return totalEquipment; }
	
	//Setter for itemLocation - Reference blue code block above methods for description of variable
	public void setItemLoc(int location) { itemLocation = location; }
	
	//Remember to update the item check under Item.java when adding a new group of items. All are handled seperately.
	//This method uses up an item based on its characteristics
	public void useItem() { 
		for(int i = 0; i < items.size(); i++){
			if(shownItems[itemLocation + sectionLoc].getName() == items.get(i).getName()) {
				
				if(items.get(i).checkItem() == 1); //TODO: What to do with key items?
				
				if(items.get(i).checkItem() == 2) {
					if(currHealth + items.get(i).getHealth() >= maxHealth){
						currHealth = maxHealth;
						items.remove(i);
						countItems();
						setItems();
						break;
					}
					currHealth += items.get(i).getHealth();
				} //Heal Item
				
				if(items.get(i).checkItem() == 3); //TODO: Attack Item
				items.remove(i);
				countItems();
				setItems();
				break;
			}
		}
	}
	
	//Getters for currently selected item number, health and magic
	public int checkCount() { return counts[itemLocation + sectionLoc]; }
	public int getHealth() { return currHealth; }
	public int getMagic() { return magic; }
	
	//Setter for magic stat
	public void setmagic(int magic) { this.magic = magic; }
}
