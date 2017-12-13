package com.smoz.game;

import java.awt.event.KeyEvent;

import com.smoz.engine.AbstractGame;
import com.smoz.engine.GameContainer;
import com.smoz.engine.Renderer2D;
import com.smoz.engine.graphics.Font;
import com.smoz.engine.graphics.Image;
import com.smoz.engine.graphics.ImageTile;
import com.smoz.engine.graphics.Light;
import com.smoz.engine.sound.SoundClip;

public class GameManager extends AbstractGame {

	private ImageTile image = null;
	private Image background = null;
	private SoundClip clip = null;
	private float tileX = 0;
	private Light light = null;
	
	public GameManager() {
		
		//image = new Image("/textures/glowLight.png");
		
		image = new ImageTile("/soniccd.png",48,48);
		image.setAlpha(true);
		background = new Image("/textures/grass.png");
		clip = new SoundClip("/sounds/jump.wav");
		light = new Light(128, 0xfffffcf5);
		
	}
	
	@Override
	public void update(GameContainer gc, float dt) {
		
		if(gc.getInput().isKeyDown(KeyEvent.VK_SPACE)) clip.play();
		
		tileX += dt*15;
		if(tileX > 6) tileX = 0;
		
	}

	@Override
	public void render(GameContainer gc, Renderer2D r) {


		
		r.setzDepth(1);
		r.drawImageTile(image, gc.getInput().getMouseX()-24, gc.getInput().getMouseY()-24,(int)tileX,0);
		
		r.drawLight(light, gc.getInput().getMouseX(), gc.getInput().getMouseY());
		
		r.setzDepth(0);
		r.drawImage(background, 0, 0);
		//r.drawImage(image, gc.getInput().getMouseX(), gc.getInput().getMouseY());
		
		
		
//		for(int x = 0; x < light.getDiameter(); x++) {
//			for(int y = 0; y < light.getDiameter(); y++) {
//				r.setLightMap(x, y, light.getLm()[x+y*light.getDiameter()]);
//			}
//		}
		
		
		
	}
	
	public static void main(String args[]) {
		
		System.out.println("Starting the SMoz Engine!");
		GameContainer gc = new GameContainer(new GameManager());
		gc.start();
		
	}

}
