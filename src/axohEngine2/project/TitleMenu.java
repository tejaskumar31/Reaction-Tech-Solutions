/****************************************************************************************************
 * @author Travis R. Dewitt
 * @version 1.0
 * Date: July 5, 2015
 * 
 * Title: Title Menu
 * Description: Create a title menu with a graphic and options to load/ssave/delete a file
 * 
 * TODO: Create option of deleting a file from the menu
 * 
 * This work is licensed under a Attribution-NonCommercial 4.0 International
 * CC BY-NC-ND license. http://creativecommons.org/licenses/by-nc/4.0/
 ****************************************************************************************************/
//Package
package axohEngine2.project;

//Imports
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;

import axohEngine2.entities.AnimatedSprite;
import axohEngine2.entities.ImageEntity;

public class TitleMenu {
	
	/********************
	 * Variables
	 ********************/
	//files - An array of at most size 3 which contain all of the currently existing files
	//existingFiles - The File object which points to the initial directory location where the save will be located
	//location - Which slot is currently being highlighted by the user
	//_fileName - String which contains the character the user is typing on screen to be used as a file name
	//getName - Boolean that when true, set the system to collect keypresses and display them on screen for file names
	private String[] files;
	private File existingFiles;
	private int location;
	private String _fileName = "";
	private boolean getName = false;
	
	//_mainImage - The initial background image of the title screen
	//_secondary - The image which appear after choosing load game
	//titleArrow - The AnimatedSprite which indicates which option the user is currently hovering over
	//_option - A choice the player might make like load or newGame or delete
	private ImageEntity _mainImage;
	private ImageEntity _secondary;
	private AnimatedSprite _titleArrow;
	private AnimatedSprite _titleEraseBomb;
	private OPTION _option;
	
	//Fonts to be used to display text, variouse ones for various uses
	private Font _simple;
	private Font _bold;
	private Font _bigBold;
	
	//SCREENWIDTH, SCREENHEIGHT - width and height of the game JFrame window in pixels
	private int SCREENWIDTH;
	private int SCREENHEIGHT;
	
	// Not part of constructor
	// Checks if erase is pressed or not...if it isn't, uses arrow, if it is, uses bomb
	private String titleSymbol = "arrow";

	
	/*******************************************************************
	 * Constructor
	 * 
	 * @param mainImage - ImageEntity background
	 * @param secondary - ImageEntity load game background
	 * @param titleArrow - AnimatedSprite for currently selected option
	 * @param screenWidth - width of the window in pixels
	 * @param screenHeight - height of the window in pixels
	 * @param simple - The font to use for normal text
	 * @param bold - The font to use for bold text
	 * @param bigBold - The font to use for big loud remarks, very bold
	 *******************************************************************/
	public TitleMenu(ImageEntity mainImage, ImageEntity secondary, AnimatedSprite titleArrow, int screenWidth, int screenHeight, Font simple, Font bold, Font bigBold,
			AnimatedSprite titleEraseBomb) {
		// existingFiles = new File("C:/gamedata/saves/");
		

		existingFiles = new File(System.getProperty("user.dir") + "\\bin\\saves\\"); // WHERE THE SAVE FILES ARE SAVED TO and loaded from
		_mainImage = mainImage;
		_secondary = secondary;
		_titleArrow = titleArrow;
		SCREENWIDTH = screenWidth;
		SCREENHEIGHT = screenHeight;
		_simple = simple;
		_bold = bold;
		_bigBold = bigBold;
		_option = OPTION.NONE;
		_titleEraseBomb = titleEraseBomb;
	}
	
	/****************************************************************************
	 * Render the title screen and change what is being shown based on options the user chooses
	 * 
	 * @param frame - JFrame window where the images will be rendered
	 * @param g2d - Graphics2D object needed to render images
	 * @param titleX - x position of the arrow
	 * @param titleY - y position of the arrow
	 * @param titleX2 - x position of the arrow once a choice is made
	 * @param titleY2 - y position of the arrow once a choice has been made
	 ****************************************************************************/
	public void render(JFrame frame, Graphics2D g2d, int titleX, int titleY, int titleX2, int titleY2) {
		String text;
		Font f;
		Shape shape;
		TextLayout t1;
		
		// if on title screen (where you can only press new game or load game)
		if (_option == OPTION.NONE)
		{
			g2d.drawImage(_mainImage.getImage(), 0, 0, SCREENWIDTH, SCREENHEIGHT, frame); // draws background image

			// for outline text...basically g2d.draw(shape) is what draws the outline and then g2d.fill(shape) fills it in
			// "new game"
			text = "New Game";		
			f = new Font("Helvetica", Font.PLAIN, 62);
			t1 = new TextLayout(text, f, g2d.getFontRenderContext());
			shape = t1.getOutline(null);
			g2d.setColor(Color.black);
			g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2d.translate(660, 500);
			g2d.draw(shape);
			g2d.translate(-660, -500);
			// checks if there are already 3 files existing -- if there are, it will gray out the New Game drawstring
			// the code to not allow New Game to be selected however is in Judgement.java
			if (isFull())
			{
				g2d.setColor(Color.gray);
			}
			else
			{
				g2d.setColor(Color.white);
			}	
			g2d.translate(660, 500);
			g2d.fill(shape);
			g2d.translate(-660, -500);
			
			// "load game"
			text = "Load Game";		
			f = new Font("Helvetica", Font.PLAIN, 62);
			t1 = new TextLayout(text, f, g2d.getFontRenderContext());
			shape = t1.getOutline(null);
			g2d.setColor(Color.black);
			g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2d.translate(660, 600);
			g2d.draw(shape);
			g2d.translate(-660, -600);	
			// checks if there are files to load -- if there are none, gray out Load Game screen
			// the code to not allow Load Game to be selected however is in Judgement.java
			if (isEmpty())
			{
				g2d.setColor(Color.gray);
			}
			else
			{
				g2d.setColor(Color.white);
			}
			g2d.translate(660, 600);
			g2d.fill(shape);
			g2d.translate(-660, -600);
			
			// "judgement"
			text = "Judgement";		
			f = new Font("Helvetica", Font.PLAIN, 72);
			t1 = new TextLayout(text, f, g2d.getFontRenderContext());
			shape = t1.getOutline(null);
			g2d.setColor(Color.black);
			g2d.translate(200, 120);
			g2d.draw(shape);
			g2d.translate(-200, -120);
			
			g2d.setColor(Color.white);
			g2d.translate(200, 120);
			g2d.fill(shape);
			g2d.translate(-200, -120);
			
			// this draws the moving red arrow that appear in front of "new game" and "load game" when you select it
			g2d.drawImage(_titleArrow.getImage(), titleX, titleY, _titleArrow.getSpriteSize(), _titleArrow.getSpriteSize(), frame);
		}
		
		// this draws the file names AFTER you select new game OR load game
		if(_option == OPTION.NEWGAME || _option == OPTION.LOADGAME){
			g2d.clearRect(0, 0, SCREENWIDTH, SCREENHEIGHT); // clear old stuff
			g2d.drawImage(_mainImage.getImage(), 0, 0, SCREENWIDTH, SCREENHEIGHT, frame); // reload background image
			
			// IDK WHAT THESE DO, but I left them "just in case" as comment...probably going to delete it soon 
			// I just hate deleting stuff and then you know...yeah
			//	g2d.setColor(Color.WHITE);
			//   g2d.setFont(_simple);
			//	g2d.drawImage(_secondary.getImage(), 0, 0, SCREENWIDTH, SCREENHEIGHT, frame);
			
			// as long as there are file names existing, this will drawstring the file names to screen
			// I changed it to only draw files when "load game" is pressed
			if (_option == OPTION.LOADGAME)
			{
				text = "Load Game";	
				f = new Font("Helvetica", Font.PLAIN, 72);
				t1 = new TextLayout(text, f, g2d.getFontRenderContext());
				shape = t1.getOutline(null);
				g2d.setColor(Color.black);
				g2d.translate(200, 120);
				g2d.draw(shape);
				g2d.translate(-200, -120);
				
				g2d.setColor(Color.white);
				g2d.translate(200, 120);
				g2d.fill(shape);
				g2d.translate(-200, -120); 
				
				if(files != null){
					
					int x = 660; // x position
					int y = 280; // y position (is incremented in the loop)
					int rectx = 640; // x position of rectangle
					int recty = 210; // y position of rectangle (is incremented in the loop)
					
					// this loop adds file names to "Load game" screen, incrementing y value each loop
					// also draws a rectangle around them 
					for(int i = 0; i < files.length; i++){ // loop through list
						// rectangle is drawn first so file name will draw over it
						g2d.drawRect(640, recty, 500, 100);  
				        g2d.setColor(Color.black);  
				        g2d.fillRect(640, recty, 500, 100);
				        
				        recty += 120;
						
				        // draw file name
						text = files[i];	
						f = new Font("Helvetica", Font.PLAIN, 62);
						t1 = new TextLayout(text, f, g2d.getFontRenderContext());
						shape = t1.getOutline(null);
						g2d.setColor(Color.black);
						g2d.translate(x, y);
						g2d.draw(shape);
						g2d.translate(x * -1, y * -1);
						
						g2d.setColor(Color.white);
						g2d.translate(x, y);
						g2d.fill(shape);
						g2d.translate(x * -1, y * -1);
						
					
						
						y += 120; // add to y position so next iteration, the file name is drawn below the previous one
					}
					
					// draws a string for either "erase file" or "cancel" (if you want to cancel erasing file)
					if (titleSymbol.equals("arrow")) 
					{
						// "erase file" string (on Load Game screen)
						text = "Erase File";		
						f = new Font("Helvetica", Font.PLAIN, 32);
						t1 = new TextLayout(text, f, g2d.getFontRenderContext());
						shape = t1.getOutline(null);
						g2d.setColor(Color.black);
						g2d.translate(x, (y - 20));
						g2d.draw(shape);
						g2d.translate(-x, -(y - 20));
						
						g2d.setColor(Color.white);
						g2d.translate(x, (y - 20));
						g2d.fill(shape);
						g2d.translate(-x, -(y - 20));
					}
					else if (titleSymbol.equals("bomb")) // if bomb is out (meaning Delete File has been pressed), show a cancel string
					{
						text = "Cancel";		
						f = new Font("Helvetica", Font.PLAIN, 32);
						t1 = new TextLayout(text, f, g2d.getFontRenderContext());
						shape = t1.getOutline(null);
						g2d.setColor(Color.black);
						g2d.translate(x, (y - 20));
						g2d.draw(shape);
						g2d.translate(-x, -(y - 20));
						
						g2d.setColor(Color.white);
						g2d.translate(x, (y - 20));
						g2d.fill(shape);
						g2d.translate(-x, -(y - 20));
					}
				}
			}
			if(_option == OPTION.NEWGAME) { // draws "New Game" at top right corner when Load Game is selected 
				text = "New Game";	
				f = new Font("Helvetica", Font.PLAIN, 72);
				t1 = new TextLayout(text, f, g2d.getFontRenderContext());
				shape = t1.getOutline(null);
				g2d.setColor(Color.black);
				g2d.translate(200, 120);
				g2d.draw(shape);
				g2d.translate(-200, -120);
				
				g2d.setColor(Color.white);
				g2d.translate(200, 120);
				g2d.fill(shape);
				g2d.translate(-200, -120);
				
				// draws "Enter new file" drawstring
				text = "Enter File Name";	
				f = new Font("Helvetica", Font.PLAIN, 42);
				t1 = new TextLayout(text, f, g2d.getFontRenderContext());
				shape = t1.getOutline(null);
				g2d.setColor(Color.black);
				g2d.translate(250, 380);
				g2d.draw(shape);
				g2d.translate(-250, -380);
				
				g2d.setColor(Color.white);
				g2d.translate(250, 380);
				g2d.fill(shape);
				g2d.translate(-250, -380);
				
				// draws rectangle where you can enter the file name for new game
				 g2d.drawRect(250, 400, 700, 100);  
		         g2d.setColor(Color.black);  
		         g2d.fillRect(250, 400, 700, 100);
				
				
				// This writes the file name as you are typing it in for a new game
				text = _fileName + " ";	
				f = new Font("Helvetica", Font.PLAIN, 62);
				t1 = new TextLayout(text, f, g2d.getFontRenderContext());
				shape = t1.getOutline(null);
				g2d.setColor(Color.black);
				// 660, 399 + location * 165
				g2d.translate(260, 470);
				g2d.draw(shape);
				g2d.translate(-260, -470);
				
				g2d.setColor(Color.white);
				g2d.translate(260, 470);
				g2d.fill(shape);
				g2d.translate(-260, -470);
				g2d.setColor(Color.white);
				
			}
		//	if(_option == OPTION.LOADGAME) { // draws "Load Game" at top right corner when Load Game is selected 
				
		//	}
			// draws title arrow (position is not set here -- position is set in Judgement class.
			// ctrl + F and type in "Special Actions for the Title" and you will get to the part where the position can be changed (in Judgement.java)
			// eventually hoping to put anything related to title screen in this class, but for not I'm leaving it in Judgement.java
			if (titleSymbol.equals("arrow"))
			{
				g2d.drawImage(_titleArrow.getImage(), titleX2, titleY2, _titleArrow.getSpriteSize(), _titleArrow.getSpriteSize(), frame);
			}
			else if (titleSymbol.equals("bomb"))
			{
				g2d.drawImage(_titleEraseBomb.getImage(), titleX2, titleY2, _titleEraseBomb.getSpriteSize(), _titleEraseBomb.getSpriteSize(), frame);
			}
		}
	}
	
	/******************************************************************
	 * Update the title screen variables from outside of this class
	 * 
	 * @param option - The new currently selected option
	 * @param location - The file which the arrow is pointing to
	 ******************************************************************/
	public void update(OPTION option, int location) {
		_option = option;
		files = existingFiles.list();
		this.location = location;
	}
	
	/*********************************************************************************
	 * This method updates the current filename variable and sorts out
	 * any non valid characters. This also makes sure the the file name
	 * is always 10 characters or less.
	 * 
	 * This method is best used when checking for whatever the user is 
	 * currently pressing on the keyboard, then converting that keycode 
	 * in to a char and passing that in to this method. 
	 * 
	 * @param currentChar - The character to be checked and added in to the file name
	 **********************************************************************************/
	public void setFileName(char currentChar) {
		if(currentChar == '\0') return;
		if(currentChar == '\b') return;
		if(currentChar == '\n') return;
		if(_fileName.length() < 11) _fileName += currentChar;
	}
	
	/******************************************************************************
	 * Method used to delete the last character when typing a file name (Backspace)
	 ******************************************************************************/
	public void deleteChar() {
		if(_fileName.length() > 0) _fileName = _fileName.substring(0, _fileName.length() - 1);
	}
	
	/*****************************************************************************
	 * Method which decides what happens when the enter key is pressed, what
	 * happens depends on many different variables. This method also
	 * returns the filename if it is over one, for loading purposes.
	 * 
	 * @return - Either an empty string or the filename being chosen
	 ******************************************************************************/
	public String enter() {
		if(_option == OPTION.NEWGAME) {
			getName = true;
			return "";
		}
		if(_option == OPTION.LOADGAME) {
			if(files != null){
				if(location <= files.length  - 1){
					if(files.length == 3) return (files[location]); 
					if(files.length == 2 && location <= 1) return (files[location]); 
					if(files.length == 1 && location == 0) return (files[location]); 
				}
			}
		}
		return "";
	}
	
	public void deleteFile(int location)
	{
	
		String filename = files[location];
		File file = new File(System.getProperty("user.dir") + "\\bin\\saves\\" + filename);
		file.delete();
		files = existingFiles.list();
		for (int i = 0; i < files.length - 1; i++)
		{
			if (files[i].equals("") && !files[i + 1].equals(""))
			{
				files[i] = files[i + 1];
			}
		}
		
	}
	// for arrow keys on title screen > load screen, it makes sure that you can't select a file that isn't there
	public boolean checkFilesNumber(int number, String direction)
	{	
		if (direction.equals("Down"))
		{
			if (number > files.length - 1)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else if (direction.equals("Up"))
		{
			System.out.println(number + " " + (files.length - 1));
			if (number <= 0)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
			
		return false;
		
	}
			
				
	public boolean isFull()
	{
		if (files.length == 3)
		{
			return true;
		}
		else
		{
			return false;
		}	
	}
	
	public boolean isEmpty()
	{
		if (files.length == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//Getters for _fileName, files(The array), and is the syetm is in getName state
	public String getFileName() { return _fileName; }
	public String[] files() { return files; }
	public boolean isGetName() { return getName; }
	public String getTitleSymbol() { return titleSymbol; }
	
	//Setter for the boolean getName
	public void setGetName(boolean onOrOff) { getName = onOrOff; }
	public void setTitleSymbol(String symbol) { titleSymbol = symbol; }
	
	/***************************************************************************************************
	 * A special drawString method which takes in a string to be displayed and constructs it in 
	 * special ways based on certain characters it may encounter, like '\n' which is the equivalent 
	 * of the enter key
	 * 
	 * @param g2d - Graphics2D object needed to display images
	 * @param text - String to check
	 * @param x - x position for the text to display
	 * @param y - y position for the text to display
	 ****************************************************************************************************/
	void drawString(Graphics2D g2d, String text, int x, int y) {
       for (String line : text.split("\n"))
           g2d.drawString(line, x, y += g2d.getFontMetrics().getHeight());
    }
}