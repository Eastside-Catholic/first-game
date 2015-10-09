package com.firstgame.game;

import com.badlogic.gdx.math.Rectangle;

public class Bullet {
	
	final float SPEED = 10.0f;
	final int WIDTH = 5;
	final int HEIGHT = 5;
	final int DOWN = 0, LEFT = 1, RIGHT = 2, UP = 3;
	
	float posX, posY;
	
	int direction;
	
	Rectangle hitbox;
	
	public Bullet(float x, float y, int dir){
		posX = x;
		posY = y;
		direction = dir;
		hitbox = new Rectangle(posX, posY, WIDTH, HEIGHT);
	}
	
	public float getSpeed(){
		return SPEED;
	}
	
	public int getWidth(){
		return WIDTH;
	}
	
	public int getHeight(){
		return HEIGHT;
	}
	
	public float getX(){
		return posX;
	}
	
	public float getY(){
		return posY;
	}
	
	public int getDirection(){
		return direction;
	}
	
	public void setX(float x){
		posX = x;
		hitbox.setPosition(posX, posY);
	}
	
	public void setY(float y){
		posY = y;
		hitbox.setPosition(posX, posY);
		
	}
	
	public void updatePosition(float x, float y, int dir){
		posX = x;
		posY = y;
		direction = dir;
		hitbox.setPosition(posX, posY);
	}
	
	public boolean isTouching(Rectangle otherHitbox){
		if(hitbox.overlaps(otherHitbox))
			return true;
		return false;
	}
}
