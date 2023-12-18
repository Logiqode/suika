package suika.game;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Ball {
    private int x, y, size, type;
    private static final int[] RADII = {17, 25, 32, 38, 50, 63, 75, 87, 100, 115, 135};

    public Ball(int x, int y, int size, int type) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.type = type;
    }

    public void draw(Graphics g) {
        // Load an image based on the ball's type
        Image texture = loadTextureForType(type);

        // Draw the image instead of filling an oval
        g.drawImage(texture, x - size / 2, y - size / 2, size, size, null);
    }

    public Image loadImage(String fileName) {
    	Image img = null;
    	InputStream inputStream = getClass().getResourceAsStream("/suika/game/textures/" + fileName);
        try {
            img = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    private Image loadTextureForType(int type) {
        switch (type) {
            case 0:
                return loadImage("straw.png");
            case 1:
                return loadImage("peach.png");
            case 2:
                return loadImage("mangosteen.png");
            case 3:
                return loadImage("lemon.png");
            case 4:
                return loadImage("orange.png");
            case 5:
                return loadImage("kiwi.png");
            case 6:
                return loadImage("apple.png");
            case 7:
                return loadImage("pir.png");
            case 8:
                return loadImage("melon.png");
            case 9:
                return loadImage("pumpkin.png");
            case 10:
                return loadImage("watermelon.png");	
            default:
            	return loadImage("straw.png");
        }
    }
    
    public boolean intersects(Ball other) {
        int dx = x - other.x;
        int dy = y - other.y;
        int distance = size / 2 + other.size / 2;
        return dx * dx + dy * dy <= distance * distance;
    }

    public void merge(Ball other) {
    	size = RADII[type + 1];
        other.size = 0;
        type = calculateTypeFromSize(size); 
    }

    private int calculateTypeFromSize(int size) {
        // Adjust the logic to follow the specific progression
        if (size <= RADII[0]) return 0;
        else if (size <= RADII[1]) return 1;
        else if (size <= RADII[2]) return 2;
        else if (size <= RADII[3]) return 3;
        else if (size <= RADII[4]) return 4;
        else if (size <= RADII[5]) return 5;
        else if (size <= RADII[6]) return 6;
        else if (size <= RADII[7]) return 7;
        else if (size <= RADII[8]) return 8;
        else if (size <= RADII[9]) return 9;
        else return 10;
    }


    public void move() {
        y += 5; // Adjust the value based on the gravity you want
    }

    public boolean isAlive() {
        return size > 0;
    }

    // Getter and Setter for x
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    // Getter and Setter for y
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    // Getter for size
    public int getSize() {
        return size;
    }

    // Getter for type
    public int getType() {
        return type;
    }
}
