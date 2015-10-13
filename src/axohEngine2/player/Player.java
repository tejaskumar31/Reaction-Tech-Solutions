package axohEngine2.player;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.LinkedList;

import javax.swing.JFrame;

import axohEngine2.Judgement;
import axohEngine2.entities.AnimatedSprite;
import axohEngine2.entities.Mob;
import axohEngine2.entities.SpriteSheet;
import axohEngine2.project.TYPE;

import java.util.ListIterator;

public class Player
{
	private final long serialVersionUID = 1L;
	
	//int scale = 4;
	
	public Mob getPlayerMobStart(SpriteSheet mainCharacter, Mob playerMob, Graphics2D graphics, JFrame jframe,
			LinkedList<AnimatedSprite> sprites, int playerNumber)
	{
		mainCharacter = new SpriteSheet("/textures/characters/mainCharacter.png", 8, 8, 32, 4);
		Rectangle bounds = new Rectangle(11 * 4, 22 * 4, 9 * 4, 9 * 4);

//		Rectangle leftBounds = new Rectangle(11 * 4 -1, 22 * 4, 9 * 4, 9 * 4);
//		Rectangle rightBounds = new Rectangle(11 * 4 - 2, 22 * 4, 9 * 4, 9 * 4);
//		Rectangle upBounds = new Rectangle(11 * 4, 22 * 4 + 8, 9 * 4, 9 * 4 - 8);
//		Rectangle downBounds = new Rectangle(11 * 4, 22 * 4, 9 * 4, 9 * 4 + 5);
		Rectangle leftBounds = new Rectangle(11 * 4 + 1, 22 * 4 + 12, 1, 9 * 4 - 12);
		Rectangle rightBounds = new Rectangle(11 * 4 + 35, 22 * 4 + 12, 1, 9 * 4 - 12);
		Rectangle upBounds = new Rectangle(11 * 4 + 4, 22 * 4 + 8, 9 * 4 - 9, 1);
		Rectangle downBounds = new Rectangle(11 * 4 + 4, 22 * 4 + 40, 9 * 4 - 9, 1);
		playerMob = new Mob(jframe, graphics, mainCharacter, 40, TYPE.PLAYER, "Original", true, 56, bounds, leftBounds, rightBounds, upBounds, downBounds);
	//	playerMob.setMultBounds(6, 50, 95, 37, 88, 62, 92, 62, 96);
		playerMob.setMoveAnim(32, 48, 40, 56, 3, 8);
		playerMob.addAttack("sword", 0, 5);
		playerMob.getAttack("sword").addMovingAnim(17, 25, 9, 1, 3, 8);
		playerMob.getAttack("sword").addAttackAnim(20, 28, 12, 4, 3, 6);
		playerMob.getAttack("sword").addInOutAnim(16, 24, 8, 0, 1, 10);
		playerMob.setCurrentAttack("sword"); //Starting attack
		playerMob.setHealth(35); //If you change the starting max health, dont forget to change it in inGameMenu.java max health also
		sprites.add(playerMob);
		
		// 12, 12, 48 for 48x48 [sheet = 1024], 8, 8, 32 for 32x32 [sheet = 256]
		Judgement.playerUpdate = true;
		
		return playerMob;	
	}
	
	public Mob getNewPlayer(SpriteSheet mainCharacter, Mob playerMob, Graphics2D graphics, JFrame jframe,
			LinkedList<AnimatedSprite> sprites, int playerNumber)
	{
		ListIterator<AnimatedSprite> listIterator = sprites.listIterator();
		
		if (playerNumber == 1)
		{
			mainCharacter = new SpriteSheet("/textures/characters/Pokemon2.png", 8, 8, 32, 4);
			playerMob = new Mob(jframe, graphics, mainCharacter, 40, TYPE.PLAYER, "Pokemon", true, 56, null, null, null, null, null);
			playerMob.setMultBounds(6, 50, 95, 37, 88, 62, 92, 62, 96);
			playerMob.setMoveAnim(32, 48, 40, 56, 3, 8);
			playerMob.addAttack("sword", 0, 5);
			playerMob.getAttack("sword").addMovingAnim(17, 25, 9, 1, 3, 8);
			playerMob.getAttack("sword").addAttackAnim(20, 28, 12, 4, 3, 6);
			playerMob.getAttack("sword").addInOutAnim(16, 24, 8, 0, 1, 10);
			playerMob.setCurrentAttack("sword"); //Starting attack
			playerMob.setHealth(35); //If you change the starting max health, dont forget to change it in inGameMenu.java max health also
			
			 while (listIterator.hasNext()) 
			 {
		            if (listIterator.next()._name == "Original")
		            {
		            	listIterator.remove();
		            	break;
		            }
		     }
			
			sprites.add(playerMob);
			Judgement.playerUpdate = true;
		}
		else if (playerNumber == 2)
		{
			mainCharacter = new SpriteSheet("/textures/characters/mainCharacter.png", 8, 8, 32, 4);
			playerMob = new Mob(jframe, graphics, mainCharacter, 40, TYPE.PLAYER, "Original", true, 56, null, null, null, null, null);
			playerMob.setMultBounds(6, 50, 95, 37, 88, 62, 92, 62, 96);
			playerMob.setMoveAnim(32, 48, 40, 56, 3, 8);
			playerMob.addAttack("sword", 0, 5);
			playerMob.getAttack("sword").addMovingAnim(17, 25, 9, 1, 3, 8);
			playerMob.getAttack("sword").addAttackAnim(20, 28, 12, 4, 3, 6);
			playerMob.getAttack("sword").addInOutAnim(16, 24, 8, 0, 1, 10);
			playerMob.setCurrentAttack("sword"); //Starting attack
			playerMob.setHealth(35); //If you change the starting max health, dont forget to change it in inGameMenu.java max health also
			
			 while (listIterator.hasNext()) 
			 {
		            if (listIterator.next()._name == "Pokemon")
		            {
		            	listIterator.remove();
		            	break;
		            }
		     }
			
			sprites.add(playerMob);
			Judgement.playerUpdate = true;
		}
		
		return playerMob;	
	}
	
	

}
