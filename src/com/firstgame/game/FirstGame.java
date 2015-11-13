package com.firstgame.game;

import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class FirstGame implements ApplicationListener, InputProcessor {
	
	Player player;
	Enemy testEnemy;
	
	SpriteBatch spriteBatch;
	BitmapFont font;
	TextureRegion textureRegion;
	Vector2 playerPosition, enemyPosition;
	ShapeRenderer renderer;
	float moveSpeed, delta;
	Powerup pill;
	Texture healthBar;
	boolean invincible = false;
	int invincibilityCounter = 0;
	int powerupCounter = 0;
	
	//Initiate all the arrays of various game objects
	List<Bullet> bullets = new ArrayList<Bullet>();
	List<Integer> bulletsToDelete = new ArrayList<Integer>();
	List<Integer> enemiesToDelete = new ArrayList<Integer>();
	List<Enemy> enemies = new ArrayList<Enemy>();
	List<Powerup> powerups = new ArrayList<Powerup>();
	List<Integer> powerupsToDelete = new ArrayList<Integer>();
	
	Powerup currentPowerup;
	
	boolean shooting = false, fired = false;
	boolean playerAlive = true;
	
	Animation playerAnimation, enemyAnimation;
	Texture playerSpriteSheet, enemySpriteSheet;
	TextureRegion playerCurrentFrame, enemyCurrentFrame;
	TextureRegion[][] playerFrames, enemyFrames;
	float frameTime;
	
	final int DOWN = 0, LEFT = 1, RIGHT = 2, UP = 3, DOWNLEFT = 4, DOWNRIGHT = 5, UPLEFT = 6, UPRIGHT = 7;
	final int SCREEN_WIDTH = 660, SCREEN_HEIGHT = 480;
	
	Integer level = 1;
	
	int screen = 0;
	final int GAME_SCREEN = 0, LOSE_SCREEN = 1, TEST_SCREEN = 2;
	
	int playerPower = 0;
	final int NORMAL_SHOT = 0, TRIPLE_SHOT = 1;
	
	final int FULL_HEALTH = 6, HEALTH_1 = 5, HEALTH_2 = 4, HEALTH_3 = 3, HEALTH_4 = 2, LOW_HEALTH = 1, NO_HEALTH = 0;
	int playerHealth = FULL_HEALTH;
		
	int enemyMoveNumber = 1;
	
	Integer score = 0;
	int health = 10;
	
	//called once when the game is created
	@Override
	public void create () {
		
		//for keyboard input
		Gdx.input.setInputProcessor(this);
		
		//set screen size
		Gdx.graphics.setDisplayMode(SCREEN_WIDTH, SCREEN_HEIGHT, false);
		
		player = new Player(100,200, new Texture("playerSpriteSheet.png"), 6, 8);
		font = new BitmapFont();
		font.setColor(Color.RED);
		renderer = new ShapeRenderer();
		
		spriteBatch = new SpriteBatch();
		font = new BitmapFont(false);
		
		playerPosition = new Vector2(400,200);
		enemyPosition = new Vector2(100, 200);
		
		moveSpeed = 2.0f;
	}

	@Override
	public void render () {
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//update frametime
		delta = Gdx.graphics.getDeltaTime();
		frameTime += delta;
		
		//check which screen to draw
		if(!playerAlive)
			screen = LOSE_SCREEN;
		if(screen == GAME_SCREEN)
			drawGameScreen();
		else if(screen == LOSE_SCREEN)
			drawLoseScreen();

	}
	
	//main game screen that has all the action
	public void drawGameScreen(){
		
		//update player frames
		player.updateFrameTime(frameTime);
		player.update();
		
		//sets screen to game over if player's health is 0
		if(playerHealth == 0)
			screen = 2;
		
		updatePowerups();
		
		//draws the enemies at the start of each new level, and if they've already been drawn,
		//it updates their frametime and moves them
		updateEnemies();		
		
		//main method for shooting. shooting == true when LEFT_SHIFT is pressed
		if(shooting){
			shoot(player.getX(), player.getY(), player.getDirection(), playerPower);
			shooting = false;
			fired = true;
		}
		
		//updates position of bullets. Fired == true when at least one bullet is on screen
		updateBullets(fired);
		
		//checks if player is touching any powerups
		checkPowerupCollision();
		
		//method to remove bullets, enemies, and powerups from the screen if they've been destroyed
		removeAssetsFromScreen();
		
		//checks player and enemy to see if they're in contact.
		if(!invincible){
			for(Enemy enemy : enemies)						
				checkEnemyCollision(enemy);
		}
		
		//deactivates player's invincibility after 120 frames.
		if(invincibilityCounter >= 120){
			invincible = false;
			invincibilityCounter = 0;		
		}
		
		//draws all assets to screen
		drawGameAssets();
		
		//if all the enemies are defeated, you level up and your powerup degrades
		if(enemies.size() == 0){
			level++;
			powerupCounter--;
		}
	}
	
	public void drawGameAssets(){
		spriteBatch.begin();
		spriteBatch.draw(player.getCurrentFrame(), player.getX(), player.getY());
		for(Enemy enemy : enemies){
			spriteBatch.draw(enemy.getCurrentFrame(), enemy.getX(), enemy.getY());
		}
	
		font.draw(spriteBatch, "Level: " + level.toString(), 20, 470);
		font.draw(spriteBatch, "Score: " + score.toString(), 20, 450);
		for(Powerup powerup : powerups){
			spriteBatch.draw(powerup.getTexture(), powerup.getX(), powerup.getY(), 20, 20);
		}
		spriteBatch.draw((drawHealthBar()), 530, 450, 100, 15);
		spriteBatch.end();
	}
	
	public void updatePowerups(){
		//add powerups to screen if none exist and player doesn't already have a power
		if(playerPower == 0 && powerups.size() == 0){
			powerups.add(new Powerup(1, (float)Math.random()*615, (float)Math.random()*455, new Texture("pill.gif")));
		}
		
		//resets player's power after 3 levels
		if(powerupCounter <= 0){
			powerupPlayer(0);
			powerupCounter = 3;
		}
		
		//increments player invincibility after contact with an enemy
		if(invincible)
			invincibilityCounter++;
	}
	
	public void removeAssetsFromScreen(){
		//removes bullets if they leave the screen or come in contact with an enemy
		for(int j = bulletsToDelete.size()-1; j >=0; j--){	
			try{
			bullets.remove(bulletsToDelete.get(j).intValue());
			}catch(Exception e){
				
			}
		}
		bulletsToDelete.clear();
			
		//removes enemies that have been shot
		for(int j = enemiesToDelete.size()-1; j >=0; j--){
			try{
			enemies.remove(enemiesToDelete.get(j).intValue());
			}catch(Exception e){
				
			}
		}
		enemiesToDelete.clear();
		
		//removes powerups from screen if player is in contact
		for(int j = powerupsToDelete.size()-1; j >= 0; j--){
			powerups.remove(powerupsToDelete.get(j).intValue());
		}
		powerupsToDelete.clear();
	}
	
	
	public void drawWinScreen(){
		spriteBatch.begin();
		font.draw(spriteBatch, "YOU WIN!", 250, 300);
		spriteBatch.end();

	}
	
	//drawn if playerHealth == 0;
	public void drawLoseScreen(){
		spriteBatch.begin();
		font.draw(spriteBatch, "YOU LOSE!", 275, 300);
		spriteBatch.end();
	}
	
	//draws various levels of the health bar
	public Texture drawHealthBar(){
		if(playerHealth == 6)
			return new Texture("fullHealth.png");
		else if(playerHealth == 5)
			return new Texture("health-1.png");
		else if(playerHealth == 4)
			return new Texture("health-2.png");
		else if(playerHealth == 3)
			return new Texture("health-3.png");
		else if(playerHealth == 2)
			return new Texture("health-4.png");
		else if(playerHealth == 1)
			return new Texture("lowHealth.png");
		else{
			return new Texture("noHealth.png");
		}
	}
	
	//moves the bullets and checks for collision with enemies
	public void updateBullets(boolean fired){
		if(fired){
			int i = 0;
			
			for(Bullet bullet : bullets){
				int m = 0;
				if(bullet.getDirection() == DOWN){
					bullet.setY(bullet.getY()-10);
					drawBullet(bullet);
				}else if(bullet.getDirection() == UP){
					bullet.setY(bullet.getY() + 10);
					drawBullet(bullet);
				}else if(bullet.getDirection() == LEFT){
					bullet.setX(bullet.getX() - 10);
					drawBullet(bullet);
				}else if(bullet.getDirection() == RIGHT){
					bullet.setX(bullet.getX() + 10);
					drawBullet(bullet);
				}else if(bullet.getDirection() == UPRIGHT){
					bullet.setX(bullet.getX() + 10);
					bullet.setY(bullet.getY() + 10);
					drawBullet(bullet);
				}else if(bullet.getDirection() == UPLEFT){
					bullet.setX(bullet.getX() - 10);
					bullet.setY(bullet.getY() + 10);
					drawBullet(bullet);
				}else if(bullet.getDirection() == DOWNRIGHT){
					bullet.setX(bullet.getX() + 10);
					bullet.setY(bullet.getY() - 10);
					drawBullet(bullet);
				}else if(bullet.getDirection() == DOWNLEFT){
					bullet.setX(bullet.getX() - 10);
					bullet.setY(bullet.getY() - 10);
					drawBullet(bullet);
				}
				//adds bullet to list to be deleted if it exits the game screen
				if(!inScreen(bullet.getX(), bullet.getY())){
					bulletsToDelete.add(i);
				}
				//adds a bullet to the list to be deleted if it collides with an enemy
				for(Enemy enemy : enemies){
					if(bullet.isTouching(enemy.getHitbox())){
						bulletsToDelete.add(i);
						enemy.setLife(enemy.getLife() - 10);
						if(enemy.getLife() <= 0){
							enemiesToDelete.add(m);
							score += 10;
						}
						break;
					}
					m++;
				}
				i++;
			}
			
		}
		if(bullets.size() == 0){
			fired = false;
		}
	}
	
	//draws enemies to the screen if there are no more on screen. # of enemies varies with the level
	public void updateEnemies(){

		float x = 0, y = 0;
		int side, direction = DOWN;
		if(enemies.size() == 0){
			for(int i = (level); i > 0 ;i--){
				//draws enemies on 1 of the 4 sides of the screen at random
				side = (int)(Math.random()*4);
				if(side == 0){
					x = 0;
					y = (float)(Math.random()*440);
					direction = RIGHT;
				}else if(side == 1){
					y = 440;
					x = (float)(Math.random()*620);
					direction = DOWN;
				}else if(side == 2){
					x = 620;
					y = (float)(Math.random()*440);
					direction = LEFT;
				}else if(side == 3){
					y = 0;
					x = (float)(Math.random()*620);
					direction = UP;
				}
				enemies.add(new Enemy(x,y, new Texture("enemy.png"),4,4, direction));
			}
		}
		for(Enemy enemy : enemies){
			enemy.updateFrameTime(frameTime);
			enemy.update();
		}
	}
	
	//checks if player is touching an enemy, if so, player loses health, gains invincibility for 120 frames
	public boolean checkEnemyCollision(Enemy enemy){
		if(player.isTouching(enemy.getHitbox())){
			playerHealth--;
			invincible = true;
			return true;
		}
		return false;
	}
	
	//checks if player is touching any powerups. if so, adds it to list to be deleted and gives the player that specific powerup
	public boolean checkPowerupCollision(){
		for(Powerup powerup : powerups){
			int i = 0;
			if(player.isTouching(powerup.getHitbox())){
					powerupPlayer(powerup.getPowerupAbility());
					powerupsToDelete.add(i);
					powerupCounter = 3;
				return true;
			}
			i++;
		}
		
		return false;
	}
	
	//changes the player's shot type (powerup)
	public void powerupPlayer(int powerupType){
		playerPower = powerupType;
	}
	
	//adds a new bullet to the arraylist of bullets based on the direction the player is facing
	public void shoot(float x, float y, int dir, int shotType){
		if(dir == DOWN){
			bullets.add(new Bullet(player.getX() + 15, player.getY(), DOWN));
			//if the shotype is tripleshot, adds two more bullets in adjacent directions
			if(shotType == TRIPLE_SHOT){
				bullets.add(new Bullet(player.getX() + 15, player.getY(), DOWNRIGHT));
				bullets .add(new Bullet(player.getX() + 15, player.getY(), DOWNLEFT));
			}
		}else if(dir == UP){
			bullets.add(new Bullet(player.getX() + 15, player.getY() + 30, UP));
			if(shotType == TRIPLE_SHOT){
				bullets.add(new Bullet(player.getX() + 15, player.getY() + 30, UPRIGHT));
				bullets .add(new Bullet(player.getX() + 15, player.getY() + 30, UPLEFT));
			}
		}else if(dir == LEFT){
			bullets.add(new Bullet (player.getX(), player.getY() + 15, LEFT));
			if(shotType == TRIPLE_SHOT){
				bullets.add(new Bullet(player.getX(), player.getY() + 15, UPLEFT));
				bullets .add(new Bullet(player.getX(), player.getY() + 15, DOWNLEFT));
			}
		}else if(dir == RIGHT){
			bullets.add(new Bullet (player.getX() + 30, player.getY()+ 15, RIGHT));
			if(shotType == TRIPLE_SHOT){
				bullets.add(new Bullet(player.getX() + 30, player.getY() + 15, UPRIGHT));
				bullets .add(new Bullet(player.getX() + 30, player.getY() + 15, DOWNRIGHT));
			}
		}else if(dir == UPRIGHT){
			bullets.add(new Bullet (player.getX() + 30, player.getY()+30, UPRIGHT));
			if(shotType == TRIPLE_SHOT){
				bullets.add(new Bullet(player.getX() + 30, player.getY() + 30, UP));
				bullets .add(new Bullet(player.getX() + 30, player.getY() + 30, RIGHT));
			}
		}else if(dir == UPLEFT){
			bullets.add(new Bullet (player.getX(), player.getY() +30, UPLEFT));
			if(shotType == TRIPLE_SHOT){
				bullets.add(new Bullet(player.getX(), player.getY() + 30, UP));
				bullets .add(new Bullet(player.getX(), player.getY() + 30, LEFT));
			}
		}else if(dir == DOWNRIGHT){
			bullets.add(new Bullet (player.getX()+20, player.getY(), DOWNRIGHT));
			if(shotType == TRIPLE_SHOT){
				bullets.add(new Bullet(player.getX() + 20, player.getY(), RIGHT));
				bullets .add(new Bullet(player.getX() + 20, player.getY(), DOWN));
			}
		}else if(dir == DOWNLEFT){
			bullets.add(new Bullet (player.getX(), player.getY(), DOWNLEFT));
			if(shotType == TRIPLE_SHOT){
				bullets.add(new Bullet(player.getX(), player.getY(), LEFT));
				bullets .add(new Bullet(player.getX(), player.getY(), DOWN));
			}
		}
	}
	
	//method for drawing an individual bullet to the gamescreen
	public void drawBullet(Bullet bullet){
		renderer.begin(ShapeType.Filled);
		renderer.setColor(Color.RED);
		renderer.rect(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight());
		renderer.end();
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.SHIFT_LEFT){
			shooting = true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.RIGHT){
			player.setRightPressed(false);
		}
		if(keycode == Keys.LEFT){
			player.setLeftPressed(false);
		}
		if(keycode == Keys.UP){
			player.setUpPressed(false);
		}
		if(keycode == Keys.DOWN){
			player.setDownPressed(false);
		}		
		return false;
	}
	
	public float getFrameTime(){
		return frameTime;
	}
	
	//checks to see if a bullet is in the gamescreen
	public boolean inScreen(float x, float y){
		boolean inX = false, inY = false;
		if(x > 0 && x < Gdx.graphics.getWidth())
			inX = true;
		if(y > 0 && y < Gdx.graphics.getHeight())
			inY = true;
		if(inX && inY)
			return true;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		font.dispose();
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
