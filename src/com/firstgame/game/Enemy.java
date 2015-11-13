package com.firstgame.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {

	Vector2 position;
	final int DOWN = 0, LEFT = 1, RIGHT = 2, UP = 3, DOWNLEFT = 4, DOWNRIGHT = 5, UPLEFT = 6, UPRIGHT = 7;
	int direction;
	Texture spriteSheet;
	TextureRegion currentFrame;
	TextureRegion[][] frames;
	Animation animation;
	int width, height;
	SpriteBatch spriteBatch;
	float frameTime, moveSpeed = 1.0f;
	Rectangle hitbox;
	
	int enemyMoveNumber = 1;
	int life = 20;
	
	boolean firstMove = true;

	public Enemy(float x, float y, Texture texture, int w, int h, int initialDirection) {
		position = new Vector2(x, y);
		spriteSheet = texture;
		width = w;
		height = h;
		frames = TextureRegion.split(spriteSheet, spriteSheet.getWidth()/w, spriteSheet.getHeight()/h);
		animation = new Animation(0.10f, frames[0]);
		hitbox = new Rectangle(position.x + 9, position.y + 3, 55, 55);
		direction = initialDirection;
		currentFrame = animation.getKeyFrame(frameTime, true);
	}
	
	public void update(){
		//makes sure that the first move of the enemy is outward from the side it came rather than in a random direction
		if(firstMove){
			enemyMoveNumber = 100;
			firstMove = false;
		}
		move();
	}
	
	//responsible for physically moving the enemy
	public void move(){
		//if enemy moves into a wall, makes it pick a new direction
		if(position.y <= 0){
			position.y = 0;
			enemyMoveNumber = 0;
		}
		if(position.y + currentFrame.getRegionHeight() >= 480){
			position.y = 480-currentFrame.getRegionHeight();
			enemyMoveNumber = 0;
		}
		if(position.x <= 0){
			position.x = 0;
			enemyMoveNumber = 0;
		}
		if(position.x + currentFrame.getRegionWidth() >= 640){
			position.x = 640 - currentFrame.getRegionWidth();
			enemyMoveNumber = 0;
		}
		
		//randomly selects a new direction for the enemy to move if it's done with its previous move
		if(enemyMoveNumber <= 0){
			direction = (int)(Math.random()*4);
			enemyMoveNumber = (int)(Math.random()*100);
		}
		if(direction == UP){
			position.y += moveSpeed;
			enemyMoveNumber -= moveSpeed;
		}else if(direction == DOWN){
			position.y -= moveSpeed;
			enemyMoveNumber -= moveSpeed;
		}else if(direction == LEFT){
			position.x -= moveSpeed;
			enemyMoveNumber -= moveSpeed;
		}else if(direction == RIGHT){
			position.x += moveSpeed;
			enemyMoveNumber -= moveSpeed;
		}
		hitbox.setPosition(position);
		
		animate();
		
	}
	
	public void updateFrameTime(float ft){
		frameTime = ft;
	}
	
	//sets the correct animation based on direction of movement
	public void animate(){
		if(direction == UP){
			animation = new Animation(0.10f, frames[3]);
			currentFrame = animation.getKeyFrame(frameTime, true);
		}else if(direction == DOWN){
			animation = new Animation(0.10f, frames[0]);
			currentFrame = animation.getKeyFrame(frameTime, true);
		}else if(direction == RIGHT){
			animation = new Animation(0.10f, frames[2]);
			currentFrame = animation.getKeyFrame(frameTime, true);
		}else if(direction == LEFT){
			animation = new Animation(0.10f, frames[1]);
			currentFrame = animation.getKeyFrame(frameTime, true);
		}else if(direction == UPLEFT){
			position.x -= moveSpeed;
			position.y += moveSpeed;
			animation = new Animation(0.10f, frames[6]);
		}else if(direction == UPRIGHT){
			position.x += moveSpeed;
			position.y += moveSpeed;
			animation = new Animation(0.10f, frames[7]);
		}else if(direction == DOWNLEFT){
			position.x -= moveSpeed;
			position.y -= moveSpeed;
			animation = new Animation(0.10f, frames[4]);
		}else if(direction == DOWNRIGHT){
			position.x += moveSpeed;
			position.y -= moveSpeed;
			animation = new Animation(0.10f, frames[5]);
		}
	}
	
	public TextureRegion getCurrentFrame(){
		return currentFrame;
	}
	
	
	public float getX(){
		return position.x;
	}
	
	public float getY(){
		return position.y;
	}
	
	public void setX(float x){
		position.x = x;
		hitbox.setPosition(position);
	}
	
	public void setY(float y){
		position.y = y;
		hitbox.setPosition(position);
	}
	
	public int getDirection(){
		return direction;
	}
	
	public Animation getCurrentAnimation(int direction){
		return new Animation(0.20f, frames[direction]);
	}
	
	public boolean isTouching(float x, float y, int width, int height){
		if(position.x >= x && position.x <= x + width && position.y >= y && position.y <= y + height + 8)
			return true;
		return false;
	}
	
	public void setLife(int l){
		life = l;
	}
	
	public int getLife(){
		return life;
	}
	
	public Rectangle getHitbox(){
		return hitbox;
	}
}
