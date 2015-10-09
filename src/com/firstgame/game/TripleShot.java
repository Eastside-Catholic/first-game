package com.firstgame.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class TripleShot{
	
	int powerupAbility;
	Texture texture;
	Vector2 position;
	
	public TripleShot(int pwrup, float x, float y, Texture txtr) {
		powerupAbility = pwrup;
		texture = txtr;
		position = new Vector2(x,y);	
	}

	public int getPowerupAbility(){
		return powerupAbility;
	}
	
	public void setPowerupAbility(int pwrup){
		powerupAbility = pwrup;
	}
	
	public boolean isTouching(float x, float y, int width, int height){
		if(position.x >= x && position.x <= x + width && position.y >= y && position.y <= y + height + 8)
			return true;
		return false;
	}
	
	public Texture getTexture(){
		return texture;
	}

}
