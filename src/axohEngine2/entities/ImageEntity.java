/****************************************************************************************************
 * @author Travis R. Dewitt
 * @version 1.0
 * Date: June 27, 2015
 * 
 * 
 * Title: Image Entity
 * Description: A class which inherits from BaseGameEntity, which contains variables about location and position
 * of an object. This class contains the actual image itself as well as the ability to display it.
 * 
 * This work is licensed under a Attribution-NonCommercial 4.0 International
 * CC BY-NC-ND license. http://creativecommons.org/licenses/by-nc/4.0/
 ****************************************************************************************************/
//Package
package axohEngine2.entities;

//Imports
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class ImageEntity extends BaseGameEntity {
	
	/************************
	 * Variables
	 ************************/
	//image - An object containing the image file
	//frame - The Window to display the image on
	//at - A transformation object which can change how an image looks
	//g2d - Graphics that the frame needs to display an object on the screen
	//width and height - Of the image in pixels
	//scale - How many times the image needs to be stretched on the screen
	protected Image image;
	protected JFrame frame;
	protected AffineTransform at;
	protected Graphics2D g2d;
	protected int width;
	protected int height;
	public int scale;
	
	/*******************************************
	 * Constructor
	 * 
	 * @param frame - The JFrame window object that the graphic will be displayed
	 ********************************************/
	public ImageEntity(JFrame frame) {
		this.frame = frame;
		setImage(null);
		setAlive(true);
	}
	
	/*************************************************
	 * @return - Get the image tied to this entity
	 *************************************************/
	public Image getImage() { return image; }
	
	/********************************************************************
	 * Use for many purposes, most notably in the animated sprite class
	 * which chnages the current image of this sprite or entity to something else.
	 * 
	 * @param image - The image object that this new entity will contain.
	 ********************************************************************/
	public void setImage(Image image) {
		this.image = image;
		if(image != null) {
			width = image.getHeight(frame);
			height = image.getWidth(frame);
			double x = frame.getSize().width / 2 - width() / 2;
			double y = frame.getSize().height / 2 - height() / 2;
			at = AffineTransform.getTranslateInstance(x, y);
		}
	}
	
	/*********************************************
	 * @return - Int width of an image in pixels
	 *********************************************/
	public int width() {
		if(image != null) 
			return image.getWidth(frame);
		else
			return 0;
	}
	
	/*********************************************
	 * @return - Int height of an image in pixels
	 *********************************************/
	public int height() {
		if(image != null) 
			return image.getHeight(frame);
		else
			return 0;
	}
	
	/*********************************************
	 * @return - Int center X position of an image
	 *********************************************/
	public double getCenterX() {
		return getX() + width() / 2;
	}
	
	/*********************************************
	 * @return - Int center Y position of an image
	 *********************************************/
	public double getCenterY() {
		return getY() + height() / 2;
	}
	
	/*********************************************
	 * This is not called outside of this class as it is automatic during the
	 * constructor however it could be used for more control if this is 
	 * ever necessary. 
	 * 
	 * @param - set the graphics object used for displaying this entity.
	 *********************************************/
	public void setGraphics(Graphics2D g) {
		g2d = g;
	}
	
	/******************************************************
	 * Load in an image from a file using a filename location
	 * 
	 * @param filename - String filename location
	 *****************************************************/
	public void load(String filename) {
		try{	
			System.out.print("Trying to load: " + filename + " ... ");
			image = ImageIO.read(getClass().getResource(filename));
			System.out.println(" succeeded!");
		} catch  (IOException e) {
			e.printStackTrace();
		  } catch (Exception e) {
			  System.err.println(" failed!");
		  }
	}
		
	/**********************************************************************************
	 * Bounding rectangle, currently only takes a square design, more parameters would
	 * be needed to change that. This is used for collision checking. This is specifically
	 * used for a collision which covers the entire image of a sprite.
	 * 
	 * @param spriteSize - Int width * height 
	 * @return - Get a rectangle back which represents the collision box
	 **********************************************************************************/
	public Rectangle getBounds(int spriteSize) {
		Rectangle r;
		r = new Rectangle((int)getX(), (int)getY(), spriteSize, spriteSize);
		return r;
	}
	
	/**********************************************************************************
	 * Bounding rectangle, currently only takes a square design, more parameters would
	 * be needed to change that. This is used for collision checking on a box in a specific
	 * position on an image using an X and Y offset.
	 * 
	 * @param boundSize - Int width * height of the bounding box
	 * @param x - Int x offset from the top left position of the image
	 * @param y - Int y offset from the top left position of the image
	 * @return - Get a rectangle back which represents the collision box
	 **********************************************************************************/
	public Rectangle getBounds(int boundSize, int x, int y) {
		Rectangle r;
		r = new Rectangle((int)getX() + x, (int)getY() + y, boundSize, boundSize);
		return r;
	}
}
