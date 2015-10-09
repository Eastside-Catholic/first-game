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
	
	List<Bullet> bullets = new ArrayList<Bullet>();
	List<Integer> bulletsToDelete = new ArrayList<Integer>();
	List<Integer> enemiesToDelete = new ArrayList<Integer>();
	List<Enemy> enemies = new ArrayList<Enemy>();
	List<Powerup> powerups = new ArrayList<Powerup>();
	List<Integer> powerupsToDelete = new ArrayList<Integer>();
	
	boolean shooting = false, fired = false;
	boolean playerAlive = true;
	
		
	Animation playerAnimation, enemyAnimation;
	Texture playerSpriteSheet, enemySpriteSheet;
	TextureRegion playerCurrentFrame, enemyCurrentFrame;
	TextureRegion[][] playerFrames, enemyFrames;
	float frameTime;
	
	final int DOWN = 0, LEFT = 1, RIGHT = 2, UP = 3, DOWNLEFT = 4, DOWNRIGHT = 5, UPLEFT = 6, UPRIGHT = 7;
	final int SCREEN_WIDTH = 640, SCREEN_HEIGHT = 480;
	int direction = DOWN, prevDirection;
	
	int screen = 0;
	final int GAME_SCREEN = 0, WIN_SCREEN = 1, LOSE_SCREEN = 2, TEST_SCREEN = 3;
	
	int playerPower = 0;
	final int NORMAL_SHOT = 0, TRIPLE_SHOT = 1;
	
	final int FULL_HEALTH = 6, HEALTH_1 = 5, HEALTH_2 = 4, HEALTH_3 = 3, HEALTH_4 = 2, LOW_HEALTH = 1, NO_HEALTH = 0;
	int playerHealth = FULL_HEALTH;
	
	int enemyMoveNumber = 1;
	
	Integer score = 0;
	int health = 10;
	
	@Override
	public void create () {
		
		Gdx.input.setInputProcessor(this);
		
		player = new Player(100,200, new Texture("playerSpriteSheet.png"), 6, 8);
		font = new BitmapFont();
		font.setColor(Color.RED);
		renderer = new ShapeRenderer();
		
		testEnemy = new Enemy( 200, 200, new Texture("enemy.png"), 4, 4);
		
		powerups.add(new Powerup(1, 500, 300, new Texture("pill.gif")));
		
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
		
		delta = Gdx.graphics.getDeltaTime();
		frameTime += delta;
		
		if(score >= 200)
			screen = WIN_SCREEN;
		if(!playerAlive)
			screen = LOSE_SCREEN;
		if(screen == GAME_SCREEN)
			drawGameScreen();
		else if(screen == WIN_SCREEN)
			drawWinScreen();
		else if(screen == LOSE_SCREEN)
			drawLoseScreen();

	}
	
	public void drawGameScreen(){
		player.updateFrameTime(frameTime);
		player.update();
		
		if(invincible)
			invincibilityCounter++;
		
		updateEnemies();
		
		for(Enemy enemy : enemies){
			enemy.updateFrameTime(frameTime);
			enemy.update();
		}
		
		if(shooting){
			shoot(player.getX(), player.getY(), player.getDirection(), playerPower);
			shooting = false;
			fired = true;
		}
		updateBullets(fired);
		
		if(playerHealth == 0)
			screen = 2;
		
		for(int j = bulletsToDelete.size()-1; j >=0; j--){					
			bullets.remove(bulletsToDelete.get(j).intValue());
		}
		bulletsToDelete.clear();
			
		for(int j = enemiesToDelete.size()-1; j >=0; j--){
			enemies.remove(enemiesToDelete.get(j).intValue());
		}
		enemiesToDelete.clear();
		
		if(!invincible){
			for(Enemy enemy : enemies)						
				checkEnemyCollision(enemy);
		}
		if(invincibilityCounter >= 120){
			invincible = false;
			invincibilityCounter = 0;		
		}
		
		checkPowerupCollision();
		
		for(int j = powerupsToDelete.size()-1; j >= 0; j--){
			powerups.remove(powerupsToDelete.get(j).intValue());
		}
		powerupsToDelete.clear();
		
		spriteBatch.begin();
		if(playerAlive)
			spriteBatch.draw(player.getCurrentFrame(), player.getX(), player.getY());
		for(Enemy enemy : enemies){
			spriteBatch.draw(enemy.getCurrentFrame(), enemy.getX(), enemy.getY());
		}
		font.draw(spriteBatch, score.toString(), 20, 470);
		for(Powerup powerup : powerups){
			spriteBatch.draw(powerup.getTexture(), powerup.getX(), powerup.getY(), 20, 20);
		}
		spriteBatch.draw((drawHealthBar()), 530, 450, 100, 15);
		spriteBatch.end();
	}
	
	public void drawWinScreen(){
		spriteBatch.begin();
		font.draw(spriteBatch, "YOU WIN!", 250, 300);
		spriteBatch.end();

	}
	
	public void drawLoseScreen(){
		spriteBatch.begin();
		font.draw(spriteBatch, "YOU LOSE!", 275, 300);
		spriteBatch.end();
	}
	
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
				if(!inScreen(bullet.getX(), bullet.getY())){
					bulletsToDelete.add(i);
				}
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
	
	public void updateEnemies(){
		Enemy enemyToAdd;
		float x, y;
		if(enemies.size() < 7){
			for(int i = (7 - enemies.size()); i > 0 ;i--){
				x = (float)Math.random()*500;
				y = (float)Math.random()*300;
				enemyToAdd = new Enemy(x, y, new Texture("enemy.png"), 4, 4);
				while(player.isTouching(enemyToAdd.getHitbox())){
					x = (float)Math.random()*500;
					y = (float)Math.random()*300;
					enemyToAdd.setX(x);
					enemyToAdd.setY(y);
				}
				enemies.add(new Enemy(x, y, new Texture("enemy.png"),4,4));
			}
		}
	}
	
	public boolean checkEnemyCollision(Enemy enemy){
		if(player.isTouching(enemy.getHitbox())){
			playerHealth--;
			invincible = true;
			return true;
		}
		return false;
	}
	
	public boolean checkPowerupCollision(){
		for(Powerup powerup : powerups){
			int i = 0;
			if(player.isTouching(powerup.getHitbox())){
					powerupPlayer(powerup.getPowerupAbility());
					powerupsToDelete.add(i);
				return true;
			}
			i++;
		}
		
		return false;
	}
	
	public void powerupPlayer(int powerupType){
		playerPower = powerupType;
	}
	
	public void shoot(float x, float y, int dir, int shotType){
		if(dir == DOWN){
			bullets.add(new Bullet(player.getX() + 15, player.getY(), DOWN));
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
