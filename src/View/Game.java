package View;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.*;

import DataAccessObject.DAO_Player;
import Model.*;
import Controller.GameController;

public class Game extends GameScreen {
	private BufferedImage birds;
	private Animation bird_anim;
	
	private Bird bird;
	private Ground ground;
	private ChimneyGroup chimneyGroup;


	private Player player;
	private Player topPlayer;
	private Player[] topPlayerList;
	private DAO_Player dao;

	private int score;
	private int highScore;
	private boolean isGameStarted = false;
	private boolean isGameOver = false;

	private ImageIcon menu;
	private ImageIcon start;
	private ImageIcon exit;
	private ImageIcon logo;
	private ImageIcon gameOver;
	private ImageIcon scoreBoard;
	private ImageIcon pauseMenu;

	private Rectangle startButton;
	private Rectangle exitButton;
	
	private int BEGIN_SCREEN = 0;
	private int GAMEPLAY_SCREEN = 1;
	private int GAMEOVER_SCREEN = 2;
	private int MENU_SCREEN = 3;
	private int CURRENT_SCREEN = MENU_SCREEN;


	public static float g = 0.15f;

	private GameController gameController;
	private boolean paused = false;
	private boolean isSlowDown = false;
	private boolean isSpeedUp = false;
	private boolean isTimerMode = false;

	private int gameTime;
	private Timer gameTimer;

	public void startTimerMode(int time){
		gameTime = time;
		gameTimer = new Timer();
		gameTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println(gameTime);
				gameTime--;
				if(gameTime <=0){
					if(!isGameOver){
						gameOver();
					}
				}
			}
		},0 , 1000);
	}

	public boolean isSlowDown() {
		return isSlowDown;
	}

	public void setSlowDown(boolean slowDown) {
		isSlowDown = slowDown;
	}

	public boolean isSpeedUp() {
		return isSpeedUp;
	}

	public void setSpeedUp(boolean speedUp) {
		isSpeedUp = speedUp;
	}

	public boolean isTimerMode() {
		return isTimerMode;
	}

	public void setTimerMode(boolean timerMode) {
		isTimerMode = timerMode;
	}

	public int getBEGIN_SCREEN() {
		return BEGIN_SCREEN;
	}

	public void setBEGIN_SCREEN(int BEGIN_SCREEN) {
		this.BEGIN_SCREEN = BEGIN_SCREEN;
	}

	public int getGAMEPLAY_SCREEN() {
		return GAMEPLAY_SCREEN;
	}

	public void setGAMEPLAY_SCREEN(int GAMEPLAY_SCREEN) {
		this.GAMEPLAY_SCREEN = GAMEPLAY_SCREEN;
	}

	public int getGAMEOVER_SCREEN() {
		return GAMEOVER_SCREEN;
	}

	public void setGAMEOVER_SCREEN(int GAMEOVER_SCREEN) {
		this.GAMEOVER_SCREEN = GAMEOVER_SCREEN;
	}

	public int getCURRENT_SCREEN() {
		return CURRENT_SCREEN;
	}

	public void setCURRENT_SCREEN(int CURRENT_SCREEN) {
		this.CURRENT_SCREEN = CURRENT_SCREEN;
	}

	public Bird getBird() {
		return bird;
	}

	public void setBird(Bird bird) {
		this.bird = bird;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isGameStarted() {
		return isGameStarted;
	}

	public void setGameStarted(boolean gameStarted) {
		isGameStarted = gameStarted;
	}

	public int getMENU_SCREEN() {
		return MENU_SCREEN;
	}

	public Rectangle getStartButton() {
		return startButton;
	}

	public Rectangle getExitButton() {
		return exitButton;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getHighScore() {
		return highScore;
	}

	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}





	private int frameCounter = 0;
	private long invincibleStartTime;
	private boolean cheatCodeOn = false;
	private boolean isReverse = false;


	public boolean isReverse() {
		return isReverse;
	}

	public void setReverse(boolean reverse) {
		isReverse = reverse;
	}

	public void ReverseGravity(){
		isReverse = !isReverse;
		g = -g;
	}

	public boolean isCheatCodeOn() {
		return cheatCodeOn;
	}

	public void setCheatCodeOn(boolean cheatCodeOn) {
		this.cheatCodeOn = cheatCodeOn;
	}


	public void slowDown(){
			if(isSlowDown){
				for(int i=0;i<chimneyGroup.SIZE;i++){
					chimneyGroup.getChimney(i).setRealOffset(chimneyGroup.getChimney(i).getOffset());
					chimneyGroup.getChimney(i).setOffset(1);
				}
			}
			else{
				for(int i=0;i<chimneyGroup.SIZE;i++){
					chimneyGroup.getChimney(i).setOffset(chimneyGroup.getChimney(i).getRealOffset());
				}
			}
	}



	public Game() {
		super(800, 600);
		loadImage();
		initVariables();
		createAnimation();

		BeginGame();
		initGameController();
	}

	private void loadImage(){
		try {
			menu = new ImageIcon("src/Sprites/menu.jpg");
			start  = new ImageIcon("src/Sprites/start_S.jpg");
			exit = new ImageIcon("src/Sprites/exit_S.jpg");
			logo = new ImageIcon("src/Sprites/Flappy_Logo.png");
			gameOver = new ImageIcon("src/Sprites/gameOver.png");
			scoreBoard = new ImageIcon("src/Sprites/scoreBoard.png");
			pauseMenu = new ImageIcon("src/Sprites/pause.png");
			birds = ImageIO.read(new File("src/Sprites/bird_sprite.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initVariables() {
		bird = new Bird(350, 250, 45, 45);
		ground = new Ground();
		chimneyGroup = new ChimneyGroup();
		score = 0;

		startButton = new Rectangle(180, 330, start.getIconWidth(), start.getIconHeight());
		exitButton = new Rectangle(190, 450, exit.getIconWidth(), exit.getIconHeight());

	}

	private void initGameController(){
		gameController = new GameController(this);
		addKeyListener(gameController);
		addMouseListener(gameController);

		bird.setBirdActionListener(gameController);
	}

	public void togglePause(){
		paused = !paused;
	}

	private void createAnimation(){
		bird_anim = new Animation(100);
		AFrameOnImage f;
		f = new AFrameOnImage(0, 0, 60, 60);
		bird_anim.AddFrame(f);
		f = new AFrameOnImage(60, 0, 60, 60);
		bird_anim.AddFrame(f);
		f = new AFrameOnImage(120, 0, 60, 60);
		bird_anim.AddFrame(f);
		f = new AFrameOnImage(60, 0, 60, 60);
		bird_anim.AddFrame(f);
	}

	public void resetGame() {
		bird.setPos(350, 250);
		bird.setV(0);
		bird.setLives(1);
		bird.setInvincible(false);
		bird.setLive(true);
		isGameOver = false;

		score = 0;
		chimneyGroup.resetChimneys();
	}

	public void gameOver() {
		CURRENT_SCREEN = GAMEOVER_SCREEN;
		isGameOver = true;
		dao = new DAO_Player();
		player.setScore(score);
		dao.insert(player);
		topPlayerList = dao.selectAll().toArray(new Player[0]);

		if(dao.selectTopPLayer() != null){
			topPlayer = dao.selectTopPLayer();
			highScore = topPlayer.getScore();
		}
		else{
			highScore = score;
		}
	}
	
	@Override
	public void GAME_UPDATE(long deltaTime) {
		if(isPaused()){
			return;
		}
		if(CURRENT_SCREEN == BEGIN_SCREEN) {
			resetGame();
		}else if(CURRENT_SCREEN == GAMEPLAY_SCREEN) {

			if(bird.getLive()) bird_anim.Update_Me(deltaTime);
			if(!isReverse)
				bird.update(deltaTime);
			else
				bird.updateReversed();
			ground.update();
			chimneyGroup.update();

			//chim va cham
//			for(int i = 0; i < ChimneyGroup.SIZE; i++) {
//				if(!bird.isInvincible()){
//					if(bird.getRect().intersects(chimneyGroup.getChimney(i).getRect())) {
//						if(bird.getLives() > 0){
//							bird.setLives(bird.getLives() - 1);
//							if(bird.getLives() == 0){
//								gameOver();
//								CURRENT_SCREEN = GAMEOVER_SCREEN;
//							}
//							bird.setInvincible(true);
//							invincibleStartTime = System.currentTimeMillis();
//						}
//					}
//				}
//				else{
//					if(System.currentTimeMillis() - invincibleStartTime >= 3000){
//						bird.setInvincible(false);
//					}
//				}
//			}

//			if(!bird.isInvincible()){
//				for(int i=0;i<chimneyGroup.SIZE;i++){
////				System.out.println(score % 2);
//					if(bird.getRect().intersects(chimneyGroup.getChimney(i).getRect()) && score % 2==0){	//chi xet ONG LE
//						//di qua ong chan khong mat mang
//						if(bird.getLives() > 0){
//							bird.setLives(bird.getLives() - 1);
//							bird.setInvincible(true);
//							if(bird.getLives() == 0){
//								gameOver();
//							}
//						}
//					}
//				}
//			}

			if(!bird.isInvincible()){
				for(int i=0;i<chimneyGroup.SIZE;i++){
//				System.out.println(score % 2);
					if(bird.getRect().intersects(chimneyGroup.getChimney(i).getRect()) && score % 2!=0){	// chi xet ONG chan
						//di qua ong chan khong mat mang
						if(bird.getLives() > 0){
							bird.setLives(bird.getLives() - 1);
							bird.setInvincible(true);
							if(bird.getLives() == 0){
								gameOver();
							}
						}
					}
				}
			}

			//chim pass ong
			for(int i = 0; i < ChimneyGroup.SIZE; i++) {
				if(bird.getPosX() > chimneyGroup.getChimney(i).getPosX() + chimneyGroup.getChimney(i).getW() && !chimneyGroup.getChimney(i).getIsBehindBird() && i%2==0) {
					score++;
					gameController.onBirdPass();
					chimneyGroup.getChimney(i).setIsBehindBird(true);
					if(score % 3 == 0){
						bird.setLives(bird.getLives() + 1);
						isSpeedUp = true;
						for(int j=0;j<chimneyGroup.SIZE;j++){
							chimneyGroup.getChimney(j).setOffset(chimneyGroup.getChimney(j).getOffset() + 1);
						}
					}

					if(bird.isInvincible() && !cheatCodeOn){
						bird.setInvincible(false);
					}
				}
			}

			//chim cham dat
			if(!bird.isInvincible()){
				if(bird.getPosY() + bird.getH() > ground.getYGround() || bird.getPosY() + bird.getH() <= 0) {
					if(!bird.isInvincible()){
						if(bird.getLives() > 0){
							bird.setLives(bird.getLives() - 1);
							if(bird.getLives() == 0){
								if(!isGameOver) {
									gameOver();
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void GAME_PAINT(Graphics2D g2) {
		g2.drawImage(menu.getImage(), 0, 0, 800, 600, this);
		g2.drawImage(start.getImage(), 180, 330, null);
		g2.drawImage(exit.getImage(), 190, 420, null);

		//ve game
		if(isGameStarted()){
			g2.setFont(new Font("Arial", Font.BOLD, 24));
			g2.setColor(new Color(184, 218, 239));

			//ve background
			g2.fillRect(0, 0, MASTER_WIDTH, MASTER_HEIGHT);

			chimneyGroup.paint(g2);
			ground.paint(g2);

			frameCounter++;
			if(!bird.isInvincible() || frameCounter % 2 == 0){
				if(bird.getIsFlying()) bird_anim.PaintAnims((int) bird.getPosX(), (int) bird.getPosY(), birds, g2, 0, -1);
				else bird_anim.PaintAnims((int) bird.getPosX(), (int) bird.getPosY(), birds, g2, 0, 0);
			}

			if(CURRENT_SCREEN == BEGIN_SCREEN) {
				if(isPaused())
					togglePause();
				g2.setColor(Color.white);
				g2.drawString("Press SPACE button to play game!", 200, 350);
				g2.drawImage(logo.getImage(), (800 - logo.getIconWidth())/2, 100, null);

			}else if(CURRENT_SCREEN == GAMEOVER_SCREEN) {
				g2.drawImage(gameOver.getImage(), (800 - logo.getIconWidth())/2, 50, null);
				g2.setColor(Color.white);
				g2.drawString("Press SPACE to turn back begin screen!", 200, 570);
				g2.setColor(Color.black);
				g2.drawString("Your score: " + score, 200, 500);
				g2.setColor(Color.black);
				if(highScore > 0){
					g2.drawImage(scoreBoard.getImage(), 16, 115, null);
					g2.drawString("Top players: ", 200, 200);
					g2.drawString("No ", 200, 250);
					g2.drawString("Name ", 350, 250);
					g2.drawString("Score ", 500, 250);
					for(int i = 0; i < topPlayerList.length; i++){
						g2.drawString(String.valueOf(i+1), 200, 300 + i*50);
						g2.drawString(topPlayerList[i].getName(), 350, 300 + i*50);
						g2.drawString(String.valueOf(topPlayerList[i].getScore()), 500, 300 + i*50);
					}
				}

			}

			g2.setColor(Color.white);
			g2.drawString("Score: " +score, 10, 20);
			g2.drawString("Lives: " + bird.getLives(),10,50 );
		}

		if(isPaused() && CURRENT_SCREEN == GAMEPLAY_SCREEN){
			g2.drawImage(pauseMenu.getImage(), 150, 100, 500, 330, this);
			g2.setColor(Color.white);
			g2.setFont(new Font("Arial", Font.BOLD, 32));
			g2.drawString("Game Paused", 300, 60);
			g2.setColor(Color.black);
			g2.setFont(new Font("Arial", Font.BOLD, 24));
			g2.drawString("Press 'P' to resume", 280, 230);
			g2.drawString("Press 'R' for new game", 280, 280);
			g2.drawString("Press 'Esc' to exit", 280, 330);
		}
	}
}
