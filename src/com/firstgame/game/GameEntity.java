package com.firstgame.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class GameEntity {
	
	Vector2 position;
	final int DOWN = 0, LEFT = 1, RIGHT = 2, UP = 3, DOWNLEFT = 4, DOWNRIGHT = 5, UPLEFT = 6, UPRIGHT = 7;
	int direction = DOWN;
	Texture spriteSheet;
	TextureRegion currentFrame;
	TextureRegion[][] frames;
	Animation animation;
	int width, height;
	SpriteBatch spriteBatch;
	float frameTime, moveSpeed = 1.0f;
	
	public GameEntity(float x, float y, Texture texture, int w, int h){
		position = new Vector2(x, y);
		spriteSheet = texture;
		width = w;
		height = h;
		frames = TextureRegion.split(spriteSheet, spriteSheet.getWidth()/w, spriteSheet.getHeight()/h);
		animation = new Animation(0.10f, frames[0]);
	}

}
