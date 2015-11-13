package com.firstgame.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Powerup {
	
	int powerupAbility;
	Texture texture;
	Vector2 position;
	Rectangle hitbox;
	
	public Powerup(int pwrup, float x, float y, Texture txtr) {
		powerupAbility = pwrup;
		texture = txtr;
		position = new Vector2(x, y);
		hitbox = new Rectangle(position.x, position.y, 20, 20);
	}

	public int getPowerupAbility(){
		return powerupAbility;
	}
	
	public void setPowerupAbility(int pwrup){
		powerupAbility = pwrup;
	}
	
	public boolean isTouching(Rectangle otherHitbox){
		if(hitbox.overlaps(otherHitbox))
			return true;
		return false;
	}
	
	public Texture getTexture(){
		return texture;
	}
	
	public float getX(){
		return position.x;
	}
	
	public float getY(){
		return position.y;
	}
	
	public void setY(float y){
		position.y = y;
	}
	
	public void setX(float x){
		position.x = x;
	}
	
	public Rectangle getHitbox(){
		return hitbox;
	}
}
