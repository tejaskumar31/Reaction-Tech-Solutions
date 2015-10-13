/****************************************************************************************************
 * @author Travis R. Dewitt
 * @version 1.0
 * Date: June 27, 2015
 * 
 * 
 * Title: Base Game Entity
 * Description: Basic variable class that every Sprite inherits from. This class contains various
 *  variables related to a sprites positioning and status.
 * 
 * This work is licensed under a Attribution-NonCommercial 4.0 International
 * CC BY-NC-ND license. http://creativecommons.org/licenses/by-nc/4.0/
 ****************************************************************************************************/
package axohEngine2.entities;

public class BaseGameEntity extends Object {
	
	/********************
	 * Variables
	 ********************/
	protected boolean alive; //Life boolean
	protected double x, y; //Position
	protected double velX, velY; //Physical movement
	protected double moveAngle, faceAngle; //Facing degree
	
	//Getters
	public boolean isAlive() { return alive; }
	public double getX() { return x; }
	public double getY() { return y; }
	public double getVelX() { return velX; }
	public double getVelY() { return velY; }
	
	//Setters: i is an increase
	public void setAlive(boolean alive) { this.alive = alive; }
	public void setX(double x) { this.x = x; }
	public void incX(double i) { this.x += i; }
	public void setY(double y) { this.y = y; }
	public void incY(double i) { this.y += i; }
	public void setVelX(double velX) { this.velX = velX; }
	public void incVelX(double i) { this.velX += i; }
	public void setVelY(double velY) { this.velY = velY; }
	public void incVelY(double i) { this.velY += i; }
	public void setMoveAngle(double angle) { this.moveAngle = angle; }
	public void incMoveAngle(double i) { this.moveAngle += i; }
	public void setFaceAngle(double angle) { this.faceAngle = angle; }
	public void inFaceAngle(double i) { this.faceAngle += i; }
	
	/*************************************************************************************
	 * Constructor
	 * 
	 * This constructor is never called explicitly as it only contains variables 
	 * and no useful methods. It is instead called each time an imageEntity or higher
	 * parent is instantiated.
	 **************************************************************************************/
	protected BaseGameEntity() {
		setAlive(false);
		setX(0.0);
		setY(0.0);
		setVelX(0.0);
		setVelY(0.0);
		setMoveAngle(0.0);
		setFaceAngle(0.0);
	}
}