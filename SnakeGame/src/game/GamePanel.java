package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, KeyListener {

	public static final int WIDHT = 400;
	public static final int HEIGHT =400;
	// render
	private Graphics2D g2d;
	private BufferedImage image;

	// Game Loop
	private Thread thread;
	private boolean running;
	private long targetTime;

	// Game Stuff
	private int SIZE = 10;
	private Entity head, apple;
	private ArrayList<Entity> snake;
	private int score;
	private int level;
	private boolean gameover;

	// movement
	private int dx, dy;

	// key input
	private boolean up, down, right, left, start;

	@Override
	public void addNotify() {
		super.addNotify();
		thread = new Thread(this);
		thread.start();

	}

	private void setFPS(int fps) {
		targetTime = 1000 / fps;
	}

	public GamePanel() {
		setPreferredSize(new Dimension(WIDHT, HEIGHT));
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int k = e.getKeyCode();
		if (k == KeyEvent.VK_UP)
			up = true;
		if (k == KeyEvent.VK_DOWN)
			down = true;
		if (k == KeyEvent.VK_RIGHT)
			right = true;
		if (k == KeyEvent.VK_LEFT)
			left = true;
		if (k == KeyEvent.VK_ENTER)
			start = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int k = e.getKeyCode();
		if (k == KeyEvent.VK_UP)
			up = false;
		if (k == KeyEvent.VK_DOWN)
			down = false;
		if (k == KeyEvent.VK_RIGHT)
			right = false;
		if (k == KeyEvent.VK_LEFT)
			left = false;
		if (k == KeyEvent.VK_ENTER)
			start = false;
	}

	@Override
	public void run() {
		if (running)
			return;
		init();
		long startTime;
		long elapsed;
		long wait;
		while (running) {
			startTime = System.nanoTime();
			update();
			requestRender();

			elapsed = System.nanoTime() - startTime;
			wait = targetTime - elapsed / 1000000;
			if (wait > 0) {
				try {
					Thread.sleep(wait);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void init() {
		image = new BufferedImage(WIDHT, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		g2d = image.createGraphics();
		running = true;
		setUpLevel();
		gameover = false;
		
	}

	private void setUpLevel() {
		snake = new ArrayList<Entity>();
		head = new Entity(SIZE);
		head.setPosition(WIDHT / 2, HEIGHT / 2);
		snake.add(head);
		for (int i = 1; i < 3; i++) {
			Entity e = new Entity(SIZE);
			e.setPosition(head.getX() + (i * SIZE), head.getY());
			snake.add(e);
		}
		apple = new Entity(SIZE);
		setAplle();
		score = 0 ;
		gameover =false;
		level = 1;
		dx=dy=0;
		setFPS(level * 10);
	}

	public void setAplle() {
		
        int x = (int) ((Math.random() * (WIDHT - SIZE)) % WIDHT );
		int y = (int) ((Math.random() * (HEIGHT - SIZE)) % HEIGHT);
		x = x - (x % SIZE);
		y = y - (y % SIZE);
		apple.setPosition(x, y);
	}

	private void requestRender() {
		render(g2d);
		Graphics g = getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();

	}

	private void update() {
		if (gameover) {
			if (start) {
				setUpLevel();
			}
			return;
		}
		if (up && dy == 0) {
			dy = -SIZE;
			dx = 0;
		}
		if (down && dy == 0) {
			dy = SIZE;
			dx = 0;
		}
		if (right && dx == 0 && dy != 0) {
			dy = 0;
			dx = SIZE;
		}
		if (left && dx == 0) {
			dy = 0;
			dx = -SIZE;
		}
		if (dx != 0 || dy != 0) {
			for (int i = snake.size() - 1; i > 0; i--) {
				snake.get(i).setPosition(snake.get(i - 1).getX(), snake.get(i - 1).getY());

			}
			head.move(dx, dy);
		}
		for (Entity e : snake) {
			if (e.isCollision(head)) {
				gameover = true;
				break;
			}

		}
		if (apple.isCollision(head)) {
			score++;
			setAplle();

			Entity e = new Entity(SIZE);
			e.setPosition(-100, -100);
			snake.add(e);

			if (score % 10 == 0) {
				level++;
				if (level > 10)
					level = 10;
				setFPS(level * 10);

			}
		}
		if (head.getX() < 0)
			head.setX(WIDHT);

		if (head.getY() < 0)
			head.setY(HEIGHT);

		if (head.getX() > WIDHT)
			head.setX(0);

		if (head.getY() > HEIGHT)
			head.setY(0);

	}

	public void render(Graphics2D g2d) {
		g2d.clearRect(0, 0, WIDHT, HEIGHT);
		g2d.setColor(Color.GREEN);
		for (Entity e : snake) {
			e.render(g2d);
		}
		g2d.setColor(Color.RED);
		apple.render(g2d);
		if (gameover) {
			g2d.drawString("Game Over!", 150, 200);
		}
		g2d.setColor(Color.WHITE);
		g2d.drawString("Score:" + score + " Level:" + level, 5, 12);
		if (dx == 0 && dy == 0) {
			g2d.drawString("Ready!",150, 200);
		}
	}

}
