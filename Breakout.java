import java.awt.Color;
import java.awt.event.MouseEvent;
import acm.graphics.*;
import stanford.cs106.util.RandomGenerator;     

/*
 * This program allows the user to play breakout. 
 * A game in which you are given a ball, a paddle, and
 * you must break 100 bricks within 3 lives in order to win. 
 */

public class Breakout extends BreakoutProgram {
	private GRect rect;
	private GRect paddle; 
	private GOval daBall;
	private double velocityX;
	private double velocityY;
	private int score;
	private int turns = 3;
	private GLabel stats;


	public void run() {
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setTitle("CS 106A Breakout");
		bricks();
		showStats();
		paddleCreate();
		createDaBall();
	}


	// Creates 100 bricks row by row in a 10 x 10 size.
	// Colors every 2 rows of bricks a specific color.
	private void bricks() {
		for (int row = 0; row < NBRICK_ROWS; row++) {
			for (int col = 0; col < NBRICK_COLUMNS; col++) {
				double x = col * BRICK_WIDTH;
				double y = row * BRICK_HEIGHT;
				int col2 = 1 + col;
				rect = new GRect (x + BRICK_SEP * col2, y + BRICK_SEP * row + BRICK_Y_OFFSET, BRICK_WIDTH, BRICK_HEIGHT);
				if (true) {
					rect.setFilled(true);
					if(row == 0 || row == 1) {
						rect.setFillColor(Color.RED);
						rect.setColor(Color.RED);
					}

					if(row == 2 || row == 3) {
						rect.setFillColor(Color.ORANGE);
						rect.setColor(Color.ORANGE);
					}

					if(row == 4 || row == 5) {
						rect.setFillColor(Color.YELLOW);
						rect.setColor(Color.YELLOW);
					}

					if(row == 6 || row == 7) {
						rect.setFillColor(Color.GREEN);
						rect.setColor(Color.GREEN);
					}

					if(row == 8 || row == 9) {
						rect.setFillColor(Color.CYAN);
						rect.setColor(Color.CYAN);
					}

				}
				add(rect);
			}

		}

	}


	// Creates the paddle that the user must use and and centers it horizontally it at the bottom.
	private void paddleCreate() {
		double x = (CANVAS_WIDTH / 2) - (BRICK_WIDTH / 2);
		double y = (CANVAS_HEIGHT - PADDLE_Y_OFFSET) - PADDLE_HEIGHT;
		paddle = new GRect (x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}


	// Mouse event listener to find where the mouse is being moved and move it accordingly.
	// Checks for the side bounds to make sure the paddle does not go off the screen as well.
	public void mouseMoved(MouseEvent event) {
		double newX = event.getX();
		double mouseX = paddle.getX();
		if(newX <= CANVAS_WIDTH - PADDLE_WIDTH / 2 && newX >= PADDLE_WIDTH / 2)  {
			mouseX= newX - PADDLE_WIDTH / 2;
		}
		double y = (CANVAS_HEIGHT - PADDLE_Y_OFFSET) - PADDLE_HEIGHT;
		paddle.move(mouseX - paddle.getX(), 0);
	}


	// Creates the ball that the user will use to break the bricks.
	// Generates a random velocity of the ball every time the user plays.
	// Also breaks out of the loop if the user won or lost the game.
	private void createDaBall() {
		double x = CANVAS_WIDTH/2 - BALL_RADIUS/2;
		double y = CANVAS_HEIGHT/2 - BALL_RADIUS/2;
		daBall = new GOval(x, y, BALL_RADIUS, BALL_RADIUS);
		daBall.setFilled(true);
		add(daBall);
		velocityX = RandomGenerator.getInstance().nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		velocityY = 2 * VELOCITY_Y;
		while(true) {
			daBall.move(velocityX, velocityY);
			hitsSideWall(daBall);
			hitsTopWall (daBall);
			hitsBottomWall(daBall);
			hasHitPaddle(daBall);
			eliminateBricks(daBall);
			gameOver(daBall);
			gameWin(daBall);
			if (gameOver(daBall)) {
				break;
			}

			if (gameWin(daBall)) {
				break;
			}
			pause(DELAY);
		}

	}

	// Removes the specific brick that the ball hit and makes the ball bounce in the opposite direction.
	// The showStats method is called inside this method as well.
	private void eliminateBricks(GOval ball) {
		if (getElementAt(ball.getX(), ball.getY()) == rect && ball.getY() <= 150 && ball.getY() >= 70) {
			remove(getElementAt(ball.getX(), ball.getY())); 
			velocityY *= -1;
			if(ball.getY() != 0) {
				score++;
			}
		}

		if (getElementAt(ball.getX() + ball.getWidth(), ball.getY()) != null && ball.getY() <= 150 && ball.getY() >= 70) {
			remove(getElementAt(ball.getX() + ball.getWidth(), ball.getY())); 
			velocityY *= -1;
			if(ball.getY() != 0) {
				score++;
			}
		}

		if (getElementAt(ball.getX(), ball.getY() + ball.getHeight()) != null && ball.getY() <= 150 && ball.getY() >= 70) {
			remove(getElementAt(ball.getX(), ball.getY() + ball.getHeight())); 
			velocityY *= -1;
			if(ball.getY() != 0) {
				score++;
			}

		}

		if (getElementAt(ball.getX() + ball.getWidth(), ball.getY() + ball.getHeight()) != null && ball.getY() < 300) {
			remove(getElementAt(ball.getX() + ball.getWidth(), ball.getY() + ball.getHeight())); 
			velocityY *= -1;
			if(ball.getY() != 0) {
				score++;
			}
		}
		remove(stats);
		showStats();
	}


	// Checks to see if the ball has hit the paddle and flips the direction of its Y velocity to accommodate for this.
	private void hasHitPaddle(GOval ball) {
		double ballY = ball.getY() + ball.getHeight();
		double ballX = ball.getX() + ball.getWidth();
		boolean leftSideXOverlap = ballX >= paddle.getX() && ballX <= paddle.getX() + paddle.getWidth();
		boolean yCoordinateBoundary = ((ballY >= paddle.getY()) && (ballY < CANVAS_HEIGHT));
		boolean rightSideXOverlap = (ballX <= paddle.getX() + paddle.getWidth() && ballX >= paddle.getX()) && ball.getY() <= paddle.getY();
		if (yCoordinateBoundary && ((leftSideXOverlap || rightSideXOverlap))) {
			velocityY *= -1;
		}
	}


	// Checks to see if the ball has hit the side walls and flips its X velocity to accommodate for it.
	
	private void hitsSideWall (GOval ball) {
		if (ball.getX() <= 0 || ball.getX() >= CANVAS_WIDTH - ball.getWidth()) {
			velocityX *= -1;
		}
	}


	//Checks to see if the ball has hit the top wall and flips its Y velocity to accommodate for it.
	private void hitsTopWall (GOval ball) {
		if (ball.getY() < 0) {
			velocityY *= -1;
		}
	}


	// Shows the user their stats while and after they are playing the game. 
	
	private void showStats() {
		stats = new GLabel ("Score: " + score + ", Turns: " + turns);
		stats.setLocation(0, stats.getHeight());
		stats.setFont(SCREEN_FONT);
		add(stats);

	}


	// Checks to see if the ball has been lost below the bottom wall and decrements a turn if this is true.
	
	private void hitsBottomWall(GOval ball) {
		double x = CANVAS_WIDTH/2 - BALL_RADIUS/2;
		double y = CANVAS_HEIGHT/2 - BALL_RADIUS/2;
		if (ball.getY() >= CANVAS_HEIGHT) {
			ball.setLocation(x, y);
			if (turns != 0) {
				turns--;
			}
		}
	}


	// Checks to see if the user lost and removes the paddle and ball to display a "GAME OVER" message across the center of the canvas.
	private boolean gameOver(GOval ball) {
		if (turns == 0) {
			remove(ball);
			remove(paddle);
			GLabel gameOver = new GLabel("GAME OVER");
			gameOver.setFont(SCREEN_FONT);
			double x = CANVAS_WIDTH/2 - gameOver.getWidth() /2;
			double y = CANVAS_HEIGHT/2 - gameOver.getWidth() /2;
			gameOver.setLocation(x,y);
			add(gameOver);
		}
		return (turns == 0);
	}

	// Checks to see if the user won the game and removes the ball and paddle to display a "YOU WIN" message across the center of the canvas. 
	private boolean gameWin(GOval ball) {
		if (score == 100) {
			remove(ball);
			remove(paddle);
			GLabel gameWin = new GLabel("YOU WIN!");
			gameWin.setFont(SCREEN_FONT);
			double x = CANVAS_WIDTH/2 - gameWin.getWidth() /2;
			double y = CANVAS_HEIGHT/2 - gameWin.getWidth() /2;
			gameWin.setLocation(x,y);
			add(gameWin);
		}
		return (score == 100);
	}
}