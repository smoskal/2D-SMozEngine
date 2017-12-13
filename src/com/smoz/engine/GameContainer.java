package com.smoz.engine;

import java.awt.event.KeyEvent;

public class GameContainer implements Runnable {

	private Thread gameThread = null;
	private GameWindow window = null;
	private Renderer2D renderer = null;
	private InputHandler input = null;
	private AbstractGame game = null;
	
	private boolean isRunning = false;
	private boolean renderFrame = false;
	
	//Update limit on the render, 60fps
	private final double UPDATE_LIMIT = 1.0/60.0;
	private final double EPSILON = 1.0e9;
	
	//Window parameters and settings;
	private int width = 320;
	private int height = 240;
	private float scale = 5f;
	private String title = "SMoz Engine";
	
	
	public GameContainer(AbstractGame game) {
		this.game = game;
	}
	
	public void start() {
		
		window = new GameWindow(this);
		input = new InputHandler(this);
		renderer = new Renderer2D(this);
		gameThread = new Thread(this);
		gameThread.run();
		
	}
	
	public void run() {
		
		isRunning = true;
		
		double firstTime = 0d;
		double lastTime = System.nanoTime() / EPSILON;
		double elapsedTime = 0d;
		double unprocessedTime = 0d;
		
		double frameTime = 0;
		int frameCount = 0;
		int fps = 0;
		
		while(isRunning) {
			
			renderFrame = false;
			
			firstTime = System.nanoTime() / EPSILON;
			elapsedTime = firstTime - lastTime;
			lastTime = firstTime;
			unprocessedTime += elapsedTime;  
			
			frameTime += elapsedTime;
			
			//If we missed a frame, update the game until we are caught up.
			while(unprocessedTime >= UPDATE_LIMIT) {
				
				unprocessedTime -= UPDATE_LIMIT;
				renderFrame = true;
				
				game.update(this, (float) UPDATE_LIMIT);
				input.update();
				
				//If we have been processing for more than a second, reset the fps counters.
				if(frameTime >= 1.0) {
					frameTime = 0;
					fps = frameCount;
					frameCount = 0;
				}
				
			}
			
			
			
			//If we have updated the game, render the frame;
			if(renderFrame) {
				
				renderer.clear();
				game.render(this, renderer);
				renderer.process();
				renderer.drawText("FPS:"+fps, 0, 0, 0xff00ffff);
				window.update();
				frameCount++;

			} else {
				//If we do not have a frame to render, sleep until we do.
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		dispose();
		
	}
	
	public void stop() {
		
		isRunning = false;
		
	}
	
	public void dispose() {
		
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public GameWindow getWindow() {
		return window;
	}

	public InputHandler getInput() {
		return input;
	}

	public Renderer2D getRenderer() {
		return renderer;
	}
	
}
