package suika.game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("serial")
public class Suika extends JFrame {
    private List<Ball> balls;
    private static final int PAD_X = 24;
    private static final int PAD_Y = 160;
    private static final int WIDTH = 570;
    private static final int HEIGHT = 800;
    private BufferedImage offScreenImage;
    private static final int[] RADII = {27, 35, 42, 48, 60, 73, 85, 97, 110, 125, 145};
    private Ball previewBall;	
    private int randomType;
    static final Image[] TEXTURES = new Image[11];
    private int score = 0;
    private Image guide;
    private static final int GAME_OVER_DELAY = 10000;
    private boolean finish = false;
    
    static {
        for (int i = 0; i <= 10; i++) {
            TEXTURES[i] = loadImage("textures/" + getFileName(i));
        }
    }

    public Suika() {
        balls = new ArrayList<>();

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGame();
                repaint();
            }
        }, 0, 16);
        
        
        addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		if(!finish) {
        	    balls.add(previewBall);
        	    updatePreviewBall();
        		}
        	}
        });
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                previewBall = new Ball(e.getX(), e.getY(), RADII[randomType], randomType);
            }
        });

        offScreenImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setTitle("Suika");
        setResizable(false);

        setVisible(true);
    }
    
    private static String getFileName(int type) {
        return switch (type) {
            case 0 -> "straw.png";
            case 1 -> "peach.png";
            case 2 -> "mangosteen.png";
            case 3 -> "lemon.png";
            case 4 -> "orange.png";
            case 5 -> "kiwi.png";
            case 6 -> "apple.png";
            case 7 -> "pir.png";
            case 8 -> "melon.png";
            case 9 -> "pumpkin.png";
            case 10 -> "watermelon.png";
            default -> "straw.png";
        };
    }
    
    private static Image loadImage(String fileName) {
        Image img = null;
        InputStream inputStream = Suika.class.getResourceAsStream("/suika/game/" + fileName);
        try {
            img = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }
    
    private void drawGuide() {
        guide = new BufferedImage(WIDTH, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics g = guide.getGraphics();

        int guideX = PAD_X;
        int guideY = 0;
        int guideSpacing = 49;

        for (int i = 0; i < TEXTURES.length; i++) {
            Image texture = TEXTURES[i];
            g.drawImage(texture, guideX, guideY, 30, 30, null);
            guideX += guideSpacing;
        }
    }
    
    private void updatePreviewBall() {
    	randomType = (int) (Math.random() * 6);
    	previewBall.setY(PAD_Y - RADII[randomType]);
    }

    private void updateGame() {
        boolean type10Present = false;

        for (Ball ball : balls) {
            ball.move();

            if (ball.getType() == 10) {
                type10Present = true;
            }

            for (Ball other : balls) {
                if (ball != other && ball.intersects(other)) {
                    if (canMerge(ball, other)) {
                        ball.merge(other);
                        updateScore(ball.getType());
                    } else {
                        handleCollision(ball, other);
                    }
                }
            }

            checkWallCollision(ball);
        }

        balls.removeIf(ball -> !ball.isAlive());

        if (type10Present && !finish) {
        	finish = true;
        	System.out.println("Congratulations! You obtained Suika. Game over!");
            gameOver();
        }
    }
    
    private void gameOver() {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, GAME_OVER_DELAY);
    }


    private void handleCollision(Ball ball1, Ball ball2) {
        int dx = ball2.getX() - ball1.getX();
        int dy = ball2.getY() - ball1.getY();
        int distance = ball1.getSize() / 2 + ball2.getSize() / 2;

        int minSeparation = distance + 1;

        int actualDistance = (int) Math.sqrt(dx * dx + dy * dy);

        int overlap = minSeparation - actualDistance;

        double angle = Math.atan2(dy, dx);
        int moveX = (int) (overlap * Math.cos(angle));
        int moveY = (int) (overlap * Math.sin(angle));

        ball1.setX(ball1.getX() - moveX / 2);
        ball1.setY(ball1.getY() - moveY / 2);
        ball2.setX(ball2.getX() + moveX / 2);
        ball2.setY(ball2.getY() + moveY / 2);
    }

    private boolean canMerge(Ball ball1, Ball ball2) {
        return ball1.getType() == ball2.getType();
    }

    private void checkWallCollision(Ball ball) {
        int radius = ball.getSize() / 2;
        if (ball.getX() - radius < PAD_X) {
            ball.setX(PAD_X + radius);
        } else if (ball.getX() + radius > WIDTH - PAD_X) {
            ball.setX(WIDTH - PAD_X - radius);
        }

        if (ball.getY() - radius < PAD_Y) {
            ball.setY(PAD_Y + radius);
        } else if (ball.getY() + radius > HEIGHT - PAD_Y) {
            ball.setY(HEIGHT - PAD_Y - radius);
        }
    }
    
    private void updateScore(int ballType) {
        switch (ballType) {
            case 0:
                score += 1;
                break;
            case 1:
                score += 2;
                break;
            case 2:
                score += 3;
                break;
            case 3:
                score += 4;
                break;
            case 4:
                score += 5;
                break;
            case 5:
                score += 6;
                break;
            case 6:
                score += 7;
                break;
            case 7:
                score += 8;
                break;
            case 8:
                score += 9;
                break;
            case 9:
                score += 10;
                break;
            case 10:
                score += 11;
                break;
                
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics offScreenGraphics = offScreenImage.getGraphics();
        //background
        offScreenGraphics.setColor(new Color(173, 216, 230));
        offScreenGraphics.fillRect(0, 0, WIDTH, HEIGHT);
        
        //score
        offScreenGraphics.setColor(Color.BLACK);
        offScreenGraphics.drawString("Score: " + score, PAD_X, PAD_Y - 10);
        
        //box
        offScreenGraphics.setColor(Color.DARK_GRAY);
        offScreenGraphics.drawRect(PAD_X, PAD_Y, WIDTH - 2 * PAD_X, HEIGHT - 2 * PAD_Y);
        
     // guide
        if (guide == null) {
            drawGuide();
        }
        offScreenGraphics.drawImage(guide, 0, HEIGHT - 40 , this);
        

        if (previewBall != null) {
            previewBall.draw(offScreenGraphics);
        }

        for (Ball ball : balls) {
            ball.draw(offScreenGraphics);
        }

        g.drawImage(offScreenImage, 0, 0, this);
    }

    public static void main(String[] args) {
    	System.setProperty("sun.java2d.opengl", "true");
        SwingUtilities.invokeLater(() -> new Suika());
    }
}
