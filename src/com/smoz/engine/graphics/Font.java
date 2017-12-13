package com.smoz.engine.graphics;

public class Font { 
	
	private static final Font STANDARD_FONT = new Font("/fonts/arialn.png");
	
	private Image fontImage = null;
	private int[] offsets = null;
	private int[] widths = null;
	
	public Font(String path) {
		fontImage = new Image(path);
		
		//Set to the number of characters in the basic set of unicode;=
		offsets = new int[256];
		widths = new int[256];
		
		int unicode = 0;
		
		for(int i = 0; i < fontImage.getWidth(); i++) {
			
			//A blue pixel signifies the position of a start of the character.
			if(fontImage.getP()[i] == 0xff0000ff) {
				//We hit the position of the character
				offsets[unicode] = i;
			}
			
			//If the pixel is yellow, we have hit the end of a character
			if(fontImage.getP()[i] == 0xffffff00) {
				widths[unicode] = i - offsets[unicode];
				unicode++;
			}
			
		}
		
		
	}

	public Image getFontImage() {
		return fontImage;
	}

	public void setFontImage(Image fontImage) {
		this.fontImage = fontImage;
	}

	public int[] getOffsets() {
		return offsets;
	}

	public void setOffsets(int[] offsets) {
		this.offsets = offsets;
	}

	public int[] getWidths() {
		return widths;
	}

	public void setWidths(int[] widths) {
		this.widths = widths;
	}

	public static Font getStandardFont() {
		return STANDARD_FONT;
	}
	
}
