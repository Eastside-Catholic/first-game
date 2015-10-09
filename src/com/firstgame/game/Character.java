package com.firstgame.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Character {
	Vector2 position;
	Texture spriteSheet;
	TextureRegion currentFrame;
	TextureRegion[][] frames;
	Animation animation;
	int width, height;
	
	public Character(float x, float y, Texture texture, int w, int h){
		position = new Vector2(x, y);
		spriteSheet = texture;
		width = w;
		height = h;
		frames = TextureRegion.split(spriteSheet, spriteSheet.getWidth()/w, spriteSheet.getHeight()/h);
		animation = new Animation(0.10f, frames[0]);
	}
	
	public float getX(){
		return position.x;
	}
	
	public float getY(){
		return position.y;
	}
	
	public Animation getCurrentAnimation(int direction){
		return new Animation(0.20f, frames[direction]);
	}
	
	public boolean isTouching(float x, float y, int width, int height){
		if(position.x >= x && position.x <= x + width && position.y >= y && position.y <= y + height + 8)
			return true;
		return false;
	}

}
