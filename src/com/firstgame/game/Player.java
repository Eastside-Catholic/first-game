package com.firstgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {

	Vector2 position;
	final int DOWN = 0, LEFT = 1, RIGHT = 2, UP = 3, DOWNLEFT = 4, DOWNRIGHT = 5, UPLEFT = 6, UPRIGHT = 7;
	int direction = DOWN;
	Texture spriteSheet;
	TextureRegion currentFrame;
	TextureRegion[][] frames;
	Animation animation;
	int width, height;
	SpriteBatch spriteBatch;
	float frameTime, moveSpeed = 3.0f;
	boolean playerMoving  = false, enemyMoving = false, rightPressed = false, leftPressed = false, upPressed = false, downPressed = false, movingDownRight = false, movingUpRight = false, movingDownLeft = false, movingUpLeft = false;
	boolean shooting = false, fired = false;
	Rectangle hitbox;


	public Player(float x, float y, Texture texture, int w, int h) {
		position = new Vector2(x, y);
		spriteSheet = texture;
		width = w;
		height = h;
		frames = TextureRegion.split(spriteSheet, spriteSheet.getWidth()/w, spriteSheet.getHeight()/h);
		animation = new Animation(0.10f, frames[0]);
		hitbox = new Rectangle(position.x , position.y, 32, 32);
	}
	
	//checks what keys the user is pressing and calls movement methods accordingly
	public void update(){
		checkKeysPressed();
		setPlayerDirection();
		move(playerMoving);
	}
	
	//method to check collision of player with another object
	public boolean isTouching(Rectangle otherHitbox){
		if(hitbox.overlaps(otherHitbox))
			return true;
		return false;
	}
	
	//updates the player and hitbox's position
	public void move(boolean moving){
		if(moving){
			playerMove(direction);
			currentFrame = animation.getKeyFrame(frameTime, true);
			hitbox.setPosition(position);
		}else
			currentFrame = frames[direction][1];
	}
	
	public void updateFrameTime(float ft){
		frameTime = ft;
	}
	
	//physically changes players position and sets corrisponding animation
	public void playerMove(int dir){
		if(dir == UP){
			animation = new Animation(0.10f, frames[3]);
			position.y += moveSpeed;
		}else if(dir == DOWN){
			position.y -= moveSpeed;
			animation = new Animation(0.10f, frames[0]);
		}else if(dir == RIGHT){
			position.x += moveSpeed;
			animation = new Animation(0.10f, frames[2]);
		}else if(dir == LEFT){
			position.x -= moveSpeed;
			animation = new Animation(0.10f, frames[1]);
		}else if(dir == UPLEFT){
			position.x -= moveSpeed;
			position.y += moveSpeed;
			animation = new Animation(0.10f, frames[6]);
		}else if(dir == UPRIGHT){
			position.x += moveSpeed;
			position.y += moveSpeed;
			animation = new Animation(0.10f, frames[7]);
		}else if(dir == DOWNLEFT){
			position.x -= moveSpeed;
			position.y -= moveSpeed;
			animation = new Animation(0.10f, frames[4]);
		}else if(dir == DOWNRIGHT){
			position.x += moveSpeed;
			position.y -= moveSpeed;
			animation = new Animation(0.10f, frames[5]);
		}
		if(position.y <= 0)
			position.y = 0;
		if(position.y >= 447)
			position.y = 447;
		if(position.x <= 0)
			position.x = 0;
		if(position.x + 30 >= 640)
			position.x = 610;
			
	}
	
	public void setPlayerDirection(){
		if(downPressed && leftPressed)
			direction = DOWNLEFT;
		else if(downPressed && rightPressed)
			direction = DOWNRIGHT;
		else if(upPressed && rightPressed)
			direction = UPRIGHT;
		else if(upPressed && leftPressed)
			direction = UPLEFT;
		else if(upPressed)
			direction = UP;
		else if(downPressed)
			direction = DOWN;
		else if(rightPressed)
			direction = RIGHT;
		else if(leftPressed)
			direction = LEFT;
		else playerMoving = false;		
	}
	
	public void checkKeysPressed(){
		if(Gdx.input.isKeyPressed(Keys.UP)){
			upPressed = true;
			playerMoving = true;
		}
		if(Gdx.input.isKeyPressed(Keys.DOWN)){
			downPressed = true;
			playerMoving = true;
		}
		if(Gdx.input.isKeyPressed(Keys.RIGHT)){
			rightPressed = true;
			playerMoving = true;
		}
		if(Gdx.input.isKeyPressed(Keys.LEFT)){
			leftPressed = true;
			playerMoving = true;
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
	
	public int getDirection(){
		return direction;
	}
	
	public Animation getCurrentAnimation(int direction){
		return new Animation(0.20f, frames[direction]);
	}
	
	public void setRightPressed(boolean b){
		rightPressed = b;
	}
	
	public void setLeftPressed(boolean b){
		leftPressed = b;
	}
	
	public void setUpPressed(boolean b){
		upPressed = b;
	}
	
	public void setDownPressed(boolean b){
		downPressed = b;
	}
	
	public Rectangle getHitbox(){
		return hitbox;
	}

	
	
}
