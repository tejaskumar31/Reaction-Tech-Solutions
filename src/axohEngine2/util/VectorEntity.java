/**********************************************************************
 * @author Travis R. Dewitt
 * @version 0.8
 * Date: June 15, 2015
 * 
 * Title: Vectors 
 * Description: Construct an Entity only using lines. This is merely some framework and is 
 * currently unused in the given game, Judgement.
 * 
 * This work is licensed under a Attribution-NonCommercial 4.0 International
 * CC BY-NC-ND license. http://creativecommons.org/licenses/by-nc/4.0/
 *********************************************************************/
package axohEngine2.util;

import java.awt.Shape;

import axohEngine2.entities.BaseGameEntity;

public class VectorEntity extends BaseGameEntity {
	
	VectorEntity() {
		super();
		setShape(null);
	}

	private Shape shape;
	
	public Shape getShape() { return shape; }
	
	public void setShape(Shape shape) { this.shape = shape; }
	
}
