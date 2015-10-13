/****************************************************************************************************
 * @author Travis R. Dewitt
 * @version 1.0
 * Date: July 5, 2015
 * 
 * Title: SpriteSheet
 * Description: Take an image and cut it up in to smaller pieces used for individual sprites.
 * The purpose of this is for more optimized resource management, less HD space is needed, less code.
 * 
 * This work is licensed under a Attribution-NonCommercial 4.0 International
 * CC BY-NC-ND license. http://creativecommons.org/licenses/by-nc/4.0/
 ****************************************************************************************************/
//Package
package axohEngine2.entities;

//Imports
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet extends Object {
	
	/*****************
	 * Variables
	 *****************/
	//sprites - An array containing many images cut from the spritesheet for use
	//fileName - String directory location of the spriteSheet
	BufferedImage[] sprites;
    String fileName;
    
    //sheetHeight/width - In pixels, the width and height of the whole sprite sheet
    //spriteSize - width * height of each smaller cut image
    //scale - Int number used to multiply the cut images width and height by for display on screen
    public int sheetHeight;
    public int sheetWidth;
    private int spriteSize;
    private int scale;
    
    /*********************************************************************************
     * Constructor
     * Also automatically loads and splits up the sheet for use
     * 
     * @param filename - String directory location of the sprite sheet image file
     * @param sheetWidth - int sheet widthin pixels
     * @param sheetHeight - int sheet height in pixels
     * @param spriteSize - int individual sprite size(width * height) of the cut up smaller images
     * @param scale - int to be multiplied by the sprite size for correct display
     **********************************************************************************/
    public SpriteSheet(String filename, int sheetWidth, int sheetHeight, int spriteSize, int scale) {
    	this.scale = scale;
    	this.spriteSize = spriteSize;
    	this.sheetHeight = sheetHeight;
    	this.sheetWidth = sheetWidth;
    	setSheet(filename, sheetWidth, sheetHeight, spriteSize);
    }
    
    /*****************************************************************
     * Cut up the larger sprite sheet image in to smaller manageable parts
     * using a bufferedImage array and another helper method.
     * 
     * @param filename - Directory location of the sprite sheet
     * @param sheetWidth - int in pixels of the sheet width
     * @param sheetHeight - int in pixels of the sheet height
     * @param spriteSize - int in pixels of the cut up smaller images 
     ******************************************************************/
    private void setSheet(String filename, int sheetWidth, int sheetHeight, int spriteSize) {
    	fileName = filename;
    	try{
    		BufferedImage spriteSheet = ImageIO.read(getClass().getResource(filename));
    		buildSprites(spriteSheet, sheetWidth, sheetHeight, spriteSize);
    	}catch(IOException e){}
    }
    
    /*****************************************************************************
     * Cut up a larger image in to smaller parts and adding them in to an array for special access
     * 
     * @param spriteSheet - Large image called a spriteSheet to seperate
     * @param sheetWidth - int in pixels of the sprite sheets width
     * @param sheetHeight - int in pixels of the sprite sheets height
     * @param spriteSize - int in pixels of the individual sprite widths * heights
     ******************************************************************************/
    private void buildSprites(BufferedImage spriteSheet, int sheetWidth, int sheetHeight, int spriteSize){
        sprites = new BufferedImage[sheetHeight * sheetWidth];
	    for(int x = 0; x < sheetWidth; x++){
	        for(int y = 0; y < sheetHeight; y++){
	          	sprites[x + y * sheetWidth] = spriteSheet.getSubimage(x * (spriteSize), y * (spriteSize), spriteSize, spriteSize);
            }
	    }
    }
    
    //@return - The image in the array of images cut from the spritesheet to be returned using the sprite number
    public BufferedImage getSprite(int imageNumber){ return sprites[imageNumber]; }

    //Gettersfor the many sprites spriteSize and scale
    public int getSpriteSize() { return spriteSize; }
    public int getScale() { return scale; }
}