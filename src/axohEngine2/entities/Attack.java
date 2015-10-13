/****************************************************************************************************
 * @author Travis R. Dewitt
 * @version 0.9
 * Date: June 20, 2015
 * 
 * 
 * Title: Attack
 * Description: Using this class, all attack related variables can be grouped together as one which can be checked
 * in that specific attack object at any time. This class also has the potential to be used for holding items.
 *  
 * 
 * This work is licensed under a Attribution-NonCommercial 4.0 International
 * CC BY-NC-ND license. http://creativecommons.org/licenses/by-nc/4.0/
 ****************************************************************************************************/
//Package
package axohEngine2.entities;

//Imports
import java.awt.Rectangle;

public class Attack {

	/******************************
	 * Variables
	 * 
	 * For explanations on how animation works or what these variables do,
	 * consult 'Animation.java'
	 ******************************/
	//Moving around with weapon or item out
	private int animUp;
	private int animDown;
	private int animLeft;
	private int animRight;
	private int totalAnimFrames;
	private int animDelay;
	
	//Taking out or putting away the weapon or item
	private int inOutAnimUp;
	private int inOutAnimDown;
	private int inOutAnimLeft;
	private int inOutAnimRight;
	private int totalInOutFrames;
	private int inOutDelay;
	
	//attacking
	private int attackUp;
	private int attackDown;
	private int attackLeft;
	private int attackRight;
	private int totalAttackFrames;
	private int attackDelay;
	
	//Item specific damage modifiers
	private int strengthDamage;
	private int magicDamage;
	
	//Four different hitboxes for possible attacks, 1 is the normal amount used
	//hitBoxX - left/rigth Related to player
	//hitboxY - up/down Related to player
	//boundSize - If 5 then box is 5 x 5
	private int hitBoxX, hitBoxY, boundSize;
	private int hitBoxX2, hitBoxY2, boundSize2;
	private int hitBoxX3, hitBoxY3, boundSize3;
	private int hitBoxX4, hitBoxY4, boundSize4;
	private int totalPossible = 4; //Update this if more boxes are added
	private int hitBoxesUsed;
	
	//Differentiate one attack from another, name it
	private String attackName;
	
	//Booleans for if an attack has certain animations, so redundant checks are lowered.
	private boolean hasMoveAnim = false;
	private boolean hasAttackAnim = false;
	private boolean hasInOutAnim = false;
	
	/************************************************
	 * Constructor
	 * 
	 * Use 0's for any damage type you dont want to use.
	 * Attacks cannot use magic and strength damage at the same time
	 * This could be changed later to add for even more in game possibilities
	 * 
	 * @param name - String name identifier
	 * @param magicDam - The amount of magic damage a weapon might have
	 * @param strengthDam - The amount of physical damage a weapon may have
	 **************************************************/
	public Attack(String name, int magicDam, int strengthDam){
		attackName = name;
		magicDamage = magicDam;
		strengthDamage = strengthDam;
	}
	
	/*****************************************************************
	 * If the total hitBoxes possible needs to be increased in the code, add handling for it here
	 * as well as above in the variable section. The number of hitboxes will need to be edited 
	 * under getHitBoxes() and under the variable, totalPossible.
	 * 
	 * This method is used in order to add a hit box to an attack. Up to 4 are currently possible. 
	 * Hit boxes, when added do not need to be specified as box 1 or box 2, this method runs a 
	 * check for how many boxes are possible and how many are used and puts the indicated
	 * parameters in the next available box if possible.
	 * 
	 * @param x - Int location on the x axis relative to the player
	 * @param y - Int location on the y axis relative to the player
	 * @param size - If 5 then the bounding hit box size is 5 x 5
	 ******************************************************************/
	public String addHitBox(int x, int y, int size){
		if(hitBoxesUsed < totalPossible){
			if(hitBoxesUsed == 0){
				hitBoxX = x;
				hitBoxY = y;
				boundSize = size;
			}
			if(hitBoxesUsed == 1){
				hitBoxX2 = x;
				hitBoxY2 = y;
				boundSize2 = size;
			}
			if(hitBoxesUsed == 2){
				hitBoxX3 = x;
				hitBoxY3 = y;
				boundSize3 = size;			
			}
			if(hitBoxesUsed == 3){
				hitBoxX4 = x;
				hitBoxY4 = y;
				boundSize4 = size;
			}
			hitBoxesUsed++;
			return "Hit box added to " + attackName;
		} else return "No more hitBoxes available in " + attackName;
	}
	
	/********************************************************************
	 * The next three methods are used seperately in order to add specific types of animations to an attack
	 * For details on the parameters and what they mean, consult the 'Animation.java' class.
	 * 
	 * @param up
	 * @param down
	 * @param left
	 * @param right
	 * @param total
	 * @param delay
	 *********************************************************************/
	public void addMovingAnim(int up, int down, int left, int right, int total, int delay){
		animUp = up;
		animDown = down;
		animLeft = left;
		animRight = right;
		totalAnimFrames = total;
		animDelay = delay;
		hasMoveAnim = true;
	}
	public void addInOutAnim(int up, int down, int left, int right, int total, int delay){
		inOutAnimUp = up;
		inOutAnimDown = down;
		inOutAnimLeft = left;
		inOutAnimRight = right;
		totalInOutFrames = total;
		inOutDelay = delay;
		hasInOutAnim = true;
	}
	public void addAttackAnim(int up, int down, int left, int right, int total, int delay){
		attackUp = up;
		attackDown = down;
		attackLeft = left;
		attackRight = right;
		totalAttackFrames = total;
		attackDelay = delay;
		hasAttackAnim = true;
	}
	
	/*****************************************************************
	 * Get the damage this item or weapon does
	 * Currently only one type of attack can be used for an Attack.
	 * If both is desired, then two getter methods will be needed as well as both methods
	 * will need to be run every time this weapon intersects with something damageable.
	 * 
	 * @return - An int representing the strength or magic damage of an Attack
	 *****************************************************************/
	public int getDamage() {
		if(strengthDamage != 0) return strengthDamage;
		if(magicDamage != 0) return magicDamage;
		return 0;
	}
	
	/************************************************************************
	 * Takes a number for which box to check as a parameter and returns the bounds of that hit box
	 * Under Construction, not tested
	 * 
	 * @param mob - Owner mob of this attack (For relocation)
	 * @param boxNumber - Int number of the box to check
	 * @return - Rectangle bound of a hit box
	 ************************************************************************/
	public Rectangle getHitBox(Mob mob, int boxNumber){
		Rectangle r = null;
		if(boxNumber == 1) r = new Rectangle((int)mob.getXLoc() + hitBoxX, (int)mob.getYLoc() + hitBoxY, boundSize, boundSize);
		if(boxNumber == 2) r = new Rectangle((int)mob.getXLoc() + hitBoxX2, (int)mob.getYLoc() + hitBoxY2, boundSize2, boundSize2);
		if(boxNumber == 3) r = new Rectangle((int)mob.getXLoc() + hitBoxX3, (int)mob.getYLoc() + hitBoxY3, boundSize3, boundSize3);
		if(boxNumber == 4) r = new Rectangle((int)mob.getXLoc() + hitBoxX4, (int)mob.getYLoc() + hitBoxY4, boundSize4, boundSize4);
		return r;
	}
	
	/*************************************************************
	 * Use these three getters to return info on the MOVING animation
	 * 
	 * @return - Variouse Ints relating to the animation and its frames
	 *************************************************************/
	public int getMoveTotal() { return totalAnimFrames; }
	public int getMoveDelay() { return animDelay; }
	public int getMoveAnim(DIRECTION direction){
		if(direction == DIRECTION.UP){
			return animUp;
		}
		if(direction == DIRECTION.DOWN){
			return animDown;
		}
		if(direction == DIRECTION.LEFT){
			return animLeft;
		}
		if(direction == DIRECTION.RIGHT){
			return animRight;
		}
		return -1;
	}
		
	/************************************************************
	 * Use these three getters to return info on the IN AND OUT animation
	 * 
	 * @return - Various Ints relating to animation data
	 ************************************************************/
	public int getInOutTotal() { return totalInOutFrames; }
	public int getInOutDelay() { return inOutDelay; }
	public int getInOutAnim(DIRECTION direction){
		if(direction == DIRECTION.UP){
			return inOutAnimUp;
		}
		if(direction == DIRECTION.DOWN){
			return inOutAnimDown;
		}
		if(direction == DIRECTION.LEFT){
			return inOutAnimLeft;
		}
		if(direction == DIRECTION.RIGHT){
			return inOutAnimRight;
		}
		return -1;
	}
	
	/*****************************************************
	 * Use these three getters to return info on the ATTACK animation
	 * 
	 * @return - Various Ints relating to animation data
	 *****************************************************/
	public int getAttackTotal() { return totalAttackFrames; }
	public int getAttackDelay() { return attackDelay; }
	public int getAttackAnim(DIRECTION direction){
		if(direction == DIRECTION.UP){
			return attackUp;
		}
		if(direction == DIRECTION.DOWN){
			return attackDown;
		}
		if(direction == DIRECTION.LEFT){
			return attackLeft;
		}
		if(direction == DIRECTION.RIGHT){
			return attackRight;
		}
		return -1;
	}
	
	//Check to see if an attack has a specific animation
	public boolean hasMoveAnim() { return hasMoveAnim; }
	public boolean hasInOutAnim() { return hasInOutAnim; }
	public boolean hasAttackAnim() { return hasAttackAnim; }
	
	//Return the name of the attack to be used
	public String getName() { return attackName; }
}
