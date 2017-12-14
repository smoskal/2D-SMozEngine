package com.smoz.engine;

import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.smoz.engine.graphics.Font;
import com.smoz.engine.graphics.Image;
import com.smoz.engine.graphics.ImageRequest;
import com.smoz.engine.graphics.ImageTile;
import com.smoz.engine.graphics.Light;
import com.smoz.engine.graphics.LightRequest;

public class Renderer2D {

	//Pixel Height and Width
	private int pW, pH;
	
	//Pixel array
	private int[] p;
	
	//Z buffer for transparency
	private int[] zb;
	private int zDepth = 0;
	
	//Light Map, color of the light.
	private int[] lm;
	
	//Light Block in the event the pixels are in shadow
	private int[] lb;
	
	private int ambientColor = 0xff232323;
	
	private ArrayList<ImageRequest> imageRequest = new ArrayList<ImageRequest>();
	private ArrayList<LightRequest> lightRequest = new ArrayList<LightRequest>();
	private boolean processing = false;
	
	private Font font = Font.getStandardFont();
	
	public Renderer2D(GameContainer gc) {
		pW = gc.getWidth();
		pH = gc.getHeight();
		
		//Giving direct access to the window's buffer
		p = ((DataBufferInt) gc.getWindow().getImage().getRaster().getDataBuffer()).getData();
		zb = new int[p.length];
		
		lm = new int[p.length];
		lb = new int[p.length];
		
	}
	
	public void setPixel(int x, int y, int color) {
		
		int alpha = ((color >> 24) & 0xff);
		
		//Do not draw if we are out of bounds of the image or the special invisible color for transparency.
		if((x < 0 || x >= pW || y < 0 || y >= pH) || alpha == 0) return;
		
		int index = x+y*pW;
		
		//Make sure we are in bounds.
		if(zb[index] > zDepth) return;
		
		zb[index] = zDepth;
		
		if(alpha == 255) {
			p[index] = color;
		} else {

			int pixelColor = p[index];
			
			//Blend all the colors together.
			int newRed = ((pixelColor >> 16) & 0xff) - (int)(((pixelColor >> 16) & 0xff) - (((color >> 16) & 0xff) * alpha/255f));
			int newGreen = ((pixelColor >> 8) & 0xff) - (int)(((pixelColor >> 8) & 0xff) - (((color >> 8) & 0xff) * alpha/255f));
			int newBlue = ((pixelColor) & 0xff) + (int)(((pixelColor) & 0xff) - (((color) & 0xff) * alpha/255f));
			
			//Now pack this back into the pixel values.
			p[index] = (255 << 24 |  newRed << 16 | newGreen<< 8 | newBlue);
		}

		
	}
	
	public void process() {
		
		processing = true;
		
		//Sort the Image Request by the zDepth.
		Collections.sort(imageRequest, new Comparator<ImageRequest>() {
			public int compare(ImageRequest i0, ImageRequest i1) {
				if(i0.getzDepth() < i1.getzDepth()) return -1;
				if(i0.getzDepth() > i1.getzDepth()) return 1;
				return 0;
			}
		});
	
		for(int i = 0; i < imageRequest.size(); i++) {
			
			ImageRequest img = imageRequest.get(i);
			setzDepth(img.getzDepth());
			drawImage(img.getImage(), img.getOffX(), img.getOffY());
			
		}
		
		//Draw all the lights.
		for(int i = 0; i < lightRequest.size(); i++) {
			LightRequest l = lightRequest.get(i);
			drawLightRequest(l.light, l.locX, l.locY);
		}
		
		//Merge the light map with the pixel array.
		for(int i = 0; i < p.length; i++) {
			
			//Normalize each color.
			float r = ((lm[i] >> 16) & 0xff) / 255f;
			float g = ((lm[i] >> 8) & 0xff) / 255f;
			float b = (lm[i] & 0xff) / 255f;
			
			p[i] = ((int)(((p[i] >> 16) & 0xff) * r) << 16 | (int)(((p[i] >> 8) & 0xff) * g) << 8 | (int)((p[i] & 0xff) * b));
			
		}
		
		imageRequest.clear();
		lightRequest.clear();
		processing = false;
		
		
	}
	
	public void drawImage(Image image, int offX, int offY) {
		
		if(image.isAlpha() && !processing) {
			imageRequest.add(new ImageRequest(image, zDepth, offX, offY));
			return;
		}
		
		//Do not render if it is off screen
		if(offX < -image.getWidth()) return;
		if(offY < -image.getHeight()) return;
		if(offX >= pW) return;
		if(offY >= pH) return;
		
		//Offset the width and height if the image goes off screen.
		int newX = 0;
		int newY = 0;
		int newWidth = image.getWidth();
		int newHeight = image.getHeight();
		
		//Check for clipping
		if(offX < 0) 
			newX -= offX;
		
		if(offY < 0) 
			newY -= offY;
		
		if(newWidth + offX > pW) 
			newWidth -= (newWidth + offX - pW);
		
		if(newHeight + offY > pH) 
			newHeight -= (newHeight + offY - pH);
		
		for(int y = newY; y < newHeight; y++) {
			for(int x = newX; x < newWidth; x++) {
				
				setPixel(x+offX,y+offY, image.getP()[x+y*image.getWidth()]);
				setLightBlock(x+offX,y+offY, image.getLightBlock());
				
			}
		}
		
	}
	
	public void drawImageTile(ImageTile image, int offX, int offY, int tileX, int tileY) {
		
		if(image.isAlpha() && !processing) {
			imageRequest.add(new ImageRequest(image.getTileImage(tileX, tileY), zDepth, offX, offY));
			return;
		}
		
		//Do not render if it is off screen
		if(offX < -image.getTileWidth()) return;
		if(offY < -image.getTileHeight()) return;
		if(offX >= pW) return;
		if(offY >= pH) return;
		
		//Offset the width and height if the image goes off screen.
		int newX = 0;
		int newY = 0;
		int newWidth = image.getTileWidth();
		int newHeight = image.getTileHeight();
		
		//Check for clipping
		if(offX < 0) 
			newX -= offX;
		
		if(offY < 0) 
			newY -= offY;
		
		if(newWidth + offX > pW) 
			newWidth -= (newWidth + offX - pW);
		
		if(newHeight + offY > pH) 
			newHeight -= (newHeight + offY - pH);
		
		for(int y = newY; y < newHeight; y++) {
			for(int x = newX; x < newWidth; x++) {
				
				setPixel(x+offX,y+offY, image.getP()[(x+tileX*image.getTileWidth())+(y+tileY*image.getTileHeight())*image.getWidth()]);
				setLightBlock(x+offX,y+offY, image.getLightBlock());
				
			}
		}
	
	}
	
	public void drawText(String text, int offX, int offY, int color) {
	
		//Save some getter calls;
		Image fontImage = font.getFontImage();
		
		int offset = 0;
		
		for(int i = 0; i < text.length(); i++) {
			
			//Offsetting by the number of special chars in traditional unicode.
			//This should be changed to be more general later.
			int unicode = text.codePointAt(i);
			
			for(int y = 0; y < fontImage.getHeight(); y++) {
				
				for(int x = 0; x < font.getWidths()[unicode]; x++) {
					
					//If it is equal to white, render the charater
					if(fontImage.getP()[
					    font.getOffsets()[unicode]+x+y*fontImage.getWidth()] == 0xffffffff) {
						
						setPixel(x+offX + offset,y + offY,color);
						
					}
					
				}
			}
			
			//Add the width of this character for the next
			offset += font.getWidths()[unicode];
			
		}
		
	}
	
	public void drawRect(int offX, int offY, int width, int height, int color) {
		
		for(int y = 0; y < height; y++) {
			setPixel(offX, y + offY, color);
			setPixel(offX + width, y + offY, color);
		}
		
		for(int x = 0; x < width; x++) {
			setPixel(x + offX,offY, color);
			setPixel(offX + x, height + offY, color);
		}
	
	}
	
	public void drawFillRect(int offX, int offY, int width, int height, int color) {
		
		//Do not render if it is off screen
		if(offX < -width) return;
		if(offY < -height) return;
		if(offX >= pW) return;
		if(offY >= pH) return;
		
		//Offset the width and height if the image goes off screen.
		int newX = 0;
		int newY = 0;
		int newWidth = width;
		int newHeight = height;
		
		//Check for clipping
		if(offX < 0) 
			newX -= offX;
		
		if(offY < 0) 
			newY -= offY;
		
		if(newWidth + offX > pW) 
			newWidth -= (newWidth + offX - pW);
		
		if(newHeight + offY > pH) 
			newHeight -= (newHeight + offY - pH);
		
		for(int y = newY; y < newHeight; y++) {
			for(int x = newX; x < newWidth; x++) {
				setPixel(x+offX, y+offY, color);
			}
		}

	}
	
	/**
	 * Clears the screen to black.
	 */
	public void clear() {
		
		for(int i = 0; i < p.length; i++) {
			p[i] = 0;
			zb[i] = 0;
			lm[i] = ambientColor;
			lb[i] = 0;
		}
		
	}
	
	public void setLightMap(int x, int y, int val) {
	
		if((x < 0 || x >= pW || y < 0 || y >= pH)) return;
		
		int baseColor = lm[x+y*pW];
		
		//We are taking the max value of each color, which will be our lighted value.
		int maxRed = Math.max((baseColor >> 16) & 0xff, (val >> 16) & 0xff);
		int maxGreen = Math.max((baseColor >> 8) & 0xff, (val >> 8) & 0xff);
		int maxBlue = Math.max(baseColor & 0xff, val & 0xff);
		
		//Now pack this back into the pixel values.
		lm[x+y*pW] = (maxRed << 16 | maxGreen << 8 | maxBlue);
		
	}
	
	public void setLightBlock(int x, int y, int val) {
		
		if((x < 0 || x >= pW || y < 0 || y >= pH)) return;
		if(zb[x+y*pW] > zDepth) return;
		
		lb[x+y*pW] = val;
		
	}
	
	public void drawLight(Light l, int offX, int offY) {
		lightRequest.add(new LightRequest(l, offX, offY));
	}
	
	private void drawLightRequest(Light l, int offX, int offY) {
		
		for(int i = 0; i <= l.getDiameter(); i++) {
			drawLightLine(l, l.getRadius(), l.getRadius(), i, 0, offX, offY);
			drawLightLine(l, l.getRadius(), l.getRadius(), i, l.getDiameter(), offX, offY);
			drawLightLine(l, l.getRadius(), l.getRadius(), 0, i, offX, offY);
			drawLightLine(l, l.getRadius(), l.getRadius(), l.getDiameter(), i, offX, offY);
		}
	}

	/**
	 * Draw a line of a light from x0 to x1 and y0 to y1
	 * Using the Brynn-Hamson (sp?) algorthim.
	 * @param l
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 */
	private void drawLightLine(Light l, int x0, int y0, int x1, int y1, int offX, int offY ) {
		
		int dx = Math.abs(x1-x0);
		int dy = Math.abs(y1-y0);
		
		//Figure out what direction we are going to draw the line in.
		int sx = x0 < x1 ? 1 : -1;
		int sy = y0 < y1 ? 1 : -1;
		
		int err = dx - dy;
		int err2 = 0;
		
		while(true) {
			
			int screenX = x0-l.getRadius()+offX;
			int screenY = y0-l.getRadius()+offY;
			
			if(screenX < 0 || screenX >= pW || screenY < 0 || screenY >= pH) return;
			
			int lightColor = l.getLightValue(x0, y0);
			if(lightColor == 0) return;
			
			if(lb[screenX+screenY*pW] == Light.FULL) return;
			
			setLightMap(screenX, screenY, lightColor);
			
			//If we hit the final light position, break;
			if(x0 == x1 && y0 == y1) break;
			
			err2 = 2*err;
			if(err2 > -1 * dy) {
				err -= dy;
				x0 += sx;
			}
			if(err2 < dx) {
				err += dx;
				y0 += sy;
			}
		
		}
		
	}
	
	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public int getzDepth() {
		return zDepth;
	}

	public void setzDepth(int zDepth) {
		this.zDepth = zDepth;
	}
	
}
