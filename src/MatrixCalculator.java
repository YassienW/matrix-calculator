
import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

public class MatrixCalculator{

	public static void main(String args[]){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//instead of setFocusPainted(false) on every button, we add this key
			UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
		}catch(Exception e){
			e.printStackTrace();
		}
		new UI();
	}
}
