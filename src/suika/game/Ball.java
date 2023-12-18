package suika.game;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Image;
import javax.imageio.ImageIO;

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
        Image texture = getTextureForType(type);

        g.drawImage(texture, x - size / 2, y - size / 2, size, size, null);
    }

    private Image getTextureForType(int type) {
        if (type >= 0 && type < BallGame.TEXTURES.length) {
            return BallGame.TEXTURES[type];
        } else {
            return BallGame.TEXTURES[0];
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
        y += 5;
    }

    public boolean isAlive() {
        return size > 0;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSize() {
        return size;
    }

    public int getType() {
        return type;
    }
}
