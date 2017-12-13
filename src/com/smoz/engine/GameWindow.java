package com.smoz.engine;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class GameWindow {

	private JFrame frame = null;
	private BufferedImage image = null;
	private Canvas canvas = null;
	
	private BufferStrategy bs = null;
	private Graphics g = null;
	
	private final int BUFFER_SIZE = 2;
	
	public GameWindow(GameContainer gc) {
		
		image = new BufferedImage(gc.getWidth(), gc.getHeight(), BufferedImage.TYPE_INT_RGB);
		canvas = new Canvas();
		Dimension dim = new Dimension((int)(gc.getWidth()*gc.getScale()), (int)(gc.getHeight()*gc.getScale()));
		
		canvas.setPreferredSize(dim);
		canvas.setMaximumSize(dim);
		canvas.setMinimumSize(dim);
		
		frame = new JFrame(gc.getTitle());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(canvas, BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		
		canvas.createBufferStrategy(BUFFER_SIZE);
		bs = canvas.getBufferStrategy();
		g = bs.getDrawGraphics();
		
	}
	
	public void update() {
		
		g.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
		bs.show();
		
	}

	public BufferedImage getImage() {
		return image;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public JFrame getFrame() {
		return frame;
	}
	
}
