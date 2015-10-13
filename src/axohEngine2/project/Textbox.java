package axohEngine2.project;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JFrame;

public class Textbox 
{
	// draws textbox
	public void renderTextBox(JFrame frame, Graphics2D g2d)
	{
		g2d.setColor(Color.white);
		g2d.drawRect(42, 440, 1100, 210);  
        g2d.setColor(Color.black);  
        g2d.fillRect(42, 440, 1100, 210);
	}
	
	// draws text in textbox
	public void renderText(String text)
	{
		
		
	}
	
}
