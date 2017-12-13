package com.smoz.engine.graphics;

public class ImageRequest {

	private Image image = null;
	private int zDepth = 0;
	private int offX = 0, offY = 0;
	
	public ImageRequest(Image image, int zDepth, int offX, int offY) {
		
		this.image = image;
		this.zDepth = zDepth;
		this.offX = offX;
		this.offY = offY;
		
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public int getzDepth() {
		return zDepth;
	}

	public void setzDepth(int zDepth) {
		this.zDepth = zDepth;
	}

	public int getOffX() {
		return offX;
	}

	public void setOffX(int offX) {
		this.offX = offX;
	}

	public int getOffY() {
		return offY;
	}

	public void setOffY(int offY) {
		this.offY = offY;
	}
	
}
