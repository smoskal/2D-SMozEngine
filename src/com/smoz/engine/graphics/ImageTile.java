package com.smoz.engine.graphics;

public class ImageTile extends Image {
	
	private int tileWidth = 0;
	private int tileHeight = 0;
	
	public ImageTile(String path, int tileW, int tileH) {
		
		super(path);
		this.setTileWidth(tileW);
		this.setTileHeight(tileH);
		
	}

	public Image getTileImage(int tileX, int tileY) {
		
		int[] p = new int[tileWidth*tileHeight];
		
		for(int y = 0; y < tileWidth; y++) {
			for(int x = 0; x < tileHeight; x++) {
				p[x+y*tileWidth] = this.getP()[(x+tileX*tileWidth)+(y+tileY*tileHeight)*this.getWidth()];
			}
		}
		
		return new Image(p, tileWidth, tileHeight);
	}
	
	public int getTileWidth() {
		return tileWidth;
	}

	public void setTileWidth(int tileW) {
		this.tileWidth = tileW;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public void setTileHeight(int tileH) {
		this.tileHeight = tileH;
	}
	
}
