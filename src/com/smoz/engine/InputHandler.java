package com.smoz.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	private GameContainer gc = null;
	
	//Total number of keys on the keyboard
	private final int NUM_KEYS = 256;
	
	//Total number of buttons on the mouse
	private final int NUM_BUTTONS = 5;
	
	//Checks if the key is held down.
	private boolean[] keysPressed = new boolean[NUM_KEYS];
	
	//Keys pressed on the last frame
	private boolean[] keysLastPressed = new boolean[NUM_KEYS];
	
	private boolean[] buttonsPressed = new boolean[NUM_BUTTONS];
	private boolean[] buttonsLastPressed = new boolean[NUM_BUTTONS];
	
	private int mouseX = 0, mouseY =0;
	private int scrollWheel = 0;

	public InputHandler(GameContainer gc) {
		this.gc = gc;
		
		//Set all the listeners for each of the classes imported.
		gc.getWindow().getCanvas().addKeyListener(this);
		gc.getWindow().getCanvas().addMouseListener(this);
		gc.getWindow().getCanvas().addMouseMotionListener(this);
		gc.getWindow().getCanvas().addMouseWheelListener(this);
	}
	
	public void update() {
		
		scrollWheel = 0;
		
		//Move the current pressed array into the last frame's array;
		for(int i = 0; i < NUM_KEYS; i++) 
			keysLastPressed[i] = keysPressed[i];
		
		for(int i = 0; i < NUM_BUTTONS; i++) 
			buttonsLastPressed[i] = buttonsPressed[i];
		

	}
	
	public boolean isKeyPressed(int keyCode) {
		
		return keysPressed[keyCode];
		
	}
	
	public boolean isKeyUp(int keyCode) {
		
		return !keysPressed[keyCode] && keysLastPressed[keyCode];
		
	}
	
	public boolean isKeyDown(int keyCode) {
		
		return keysPressed[keyCode] && !keysLastPressed[keyCode];
		
	}
	
	public boolean isButtonPressed(int keyCode) {
		
		return buttonsPressed[keyCode];
		
	}
	
	public boolean isButtonUp(int keyCode) {
		
		return !buttonsPressed[keyCode] && buttonsLastPressed[keyCode];
		
	}
	
	public boolean isButtonDown(int keyCode) {
		
		return buttonsPressed[keyCode] && !buttonsLastPressed[keyCode];
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		scrollWheel = e.getWheelRotation();
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
		mouseX = (int)(e.getX()/gc.getScale());
		mouseY = (int)(e.getY()/gc.getScale());
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		mouseX = (int)(e.getX()/gc.getScale());
		mouseY = (int)(e.getY()/gc.getScale());

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		buttonsPressed[e.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		buttonsPressed[e.getButton()] = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keysPressed[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysPressed[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public int getScrollWheel() {
		return scrollWheel;
	}
	
}
