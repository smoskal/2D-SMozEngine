package com.smoz.engine;

public abstract class AbstractGame {
	public abstract void update(GameContainer gc, float dt);
	public abstract void render(GameContainer gc, Renderer2D r);
}
