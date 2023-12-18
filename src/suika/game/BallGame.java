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
public class BallGame extends JFrame {
    private List<Ball> balls;
    private static final int PAD_X = 24;
    private static final int PAD_Y = 160;
    private static final int WIDTH = 570;
    private static final int HEIGHT = 770;
    private BufferedImage offScreenImage;
    private static final int[] RADII = {17, 25, 32, 38, 50, 63, 75, 87, 100, 115, 135};
    private Ball previewBall;	
    private int randomType;
    static final Image[] TEXTURES = new Image[11];
    
    static {
        for (int i = 0; i <= 10; i++) {
            TEXTURES[i] = loadImage("textures/" + getTextureFileName(i));
        }
    }

    public BallGame() {
        balls = new ArrayList<>();

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGame();
                repaint();
            }
        }, 0, 4);
        
        
        addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        	    balls.add(previewBall);
        	    updatePreviewBall();
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
    
    private static String getTextureFileName(int type) {
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
        InputStream inputStream = BallGame.class.getResourceAsStream("/suika/game/" + fileName);
        try {
            img = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }
    
    
    
    private void updatePreviewBall() {
    	randomType = (int) (Math.random() * 6);
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
                    } else {
                        handleCollision(ball, other);
                    }
                }
            }

            checkWallCollision(ball);
        }

        balls.removeIf(ball -> !ball.isAlive());

        if (type10Present) {
            System.out.println("Congratulations! You obtained Suika. Game over!");
            System.exit(0);
        }
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

    @Override
    public void paint(Graphics g) {
        Graphics offScreenGraphics = offScreenImage.getGraphics();
        offScreenGraphics.setColor(Color.WHITE);
        offScreenGraphics.fillRect(0, 0, WIDTH, HEIGHT);

        offScreenGraphics.setColor(Color.BLACK);
        offScreenGraphics.drawRect(PAD_X, PAD_Y, WIDTH - 2 * PAD_X, HEIGHT - 2 * PAD_Y);

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
        SwingUtilities.invokeLater(() -> new BallGame());
    }
}
