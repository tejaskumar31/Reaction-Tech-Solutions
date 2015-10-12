/**********************************************************************
 * @author Travis R. Dewitt
 * @version 0.8
 * Date: June 15, 2015
 * 
 * Title: Bullet 
 * Description: Construct a 'Bullet' This is merely some framework and is 
 * currently unused in the given game, Judgement.
 * 
 * This work is licensed under a Attribution-NonCommercial 4.0 International
 * CC BY-NC-ND license. http://creativecommons.org/licenses/by-nc/4.0/
 *********************************************************************/
package axohEngine2.util;

import java.awt.Rectangle;

public class Bullet extends VectorEntity {
	
	//Bounding rectangle
	public Rectangle getBounds() {
		Rectangle r;
		r = new Rectangle((int)getX(), (int)getY());
		return r;
	}
	
	//Constructor
	Bullet() {
		//Bullet shape
		setShape(new Rectangle(0, 0, 1, 1));
		setAlive(false);
	}
}