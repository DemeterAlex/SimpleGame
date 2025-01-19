package strapycodes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

public class Main extends JFrame implements Runnable {
    private static final long serialVersionUID = 1L;
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final double PLAYER_SPEED = 0.1;
    private static final double PLAYER_TURN_SPEED = 0.05;
    private static final int NUM_ZOMBIES = 5;

    private boolean[] keys;
    private double playerX, playerY; // Player position
    private double playerAngle; // Player's viewing angle
    private Zombie[] zombies;

    private boolean running;
    private Thread gameThread;
    private BufferStrategy bufferStrategy;

    public Main() {
        setTitle("3D FPS Game Example");
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBackground(Color.BLACK);

        keys = new boolean[256];
        zombies = new Zombie[NUM_ZOMBIES];

        for (int i = 0; i < NUM_ZOMBIES; i++) {
            zombies[i] = new Zombie(Math.random() * SCREEN_WIDTH, Math.random() * SCREEN_HEIGHT);
        }

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keys[e.getKeyCode()] = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keys[e.getKeyCode()] = false;
            }
        });

        playerX = SCREEN_WIDTH / 2;
        playerY = SCREEN_HEIGHT / 2;
        playerAngle = 0.0;

        setVisible(true);

        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();

        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (running) {
            update();
            render();
            try {
                Thread.sleep(10); // Adjust frame rate
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        // Handle player movement
        if (keys[KeyEvent.VK_W]) {
            playerX += Math.cos(playerAngle) * PLAYER_SPEED;
            playerY += Math.sin(playerAngle) * PLAYER_SPEED;
        }
        if (keys[KeyEvent.VK_S]) {
            playerX -= Math.cos(playerAngle) * PLAYER_SPEED;
            playerY -= Math.sin(playerAngle) * PLAYER_SPEED;
        }
        if (keys[KeyEvent.VK_A]) {
            playerAngle -= PLAYER_TURN_SPEED;
        }
        if (keys[KeyEvent.VK_D]) {
            playerAngle += PLAYER_TURN_SPEED;
        }

        // Update zombies
        for (Zombie zombie : zombies) {
            zombie.update(playerX, playerY);
        }
    }

    private void render() {
        do {
            do {
                Graphics2D g2d = (Graphics2D) bufferStrategy.getDrawGraphics();
                try {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    // Draw player
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect((int) playerX - 5, (int) playerY - 5, 10, 10);

                    // Draw player's view direction
                    int lineEndX = (int) (playerX + Math.cos(playerAngle) * 20.0);
                    int lineEndY = (int) (playerY + Math.sin(playerAngle) * 20.0);
                    g2d.drawLine((int) playerX, (int) playerY, lineEndX, lineEndY);

                    // Draw zombies
                    for (Zombie zombie : zombies) {
                        zombie.render(g2d);
                    }
                } finally {
                    g2d.dispose();
                }

                bufferStrategy.show();
            } while (bufferStrategy.contentsRestored());

            Toolkit.getDefaultToolkit().sync();
        } while (bufferStrategy.contentsLost());
    }

    private class Zombie {
        private double x, y;
        private double speed;

        public Zombie(double x, double y) {
            this.x = x;
            this.y = y;
            this.speed = 0.05 + Math.random() * 0.05;
        }

        public void update(double playerX, double playerY) {
            // Zombie movement towards the player
            double dx = playerX - x;
            double dy = playerY - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                x += speed * dx / distance;
                y += speed * dy / distance;
            }
        }

        public void render(Graphics2D g2d) {
            g2d.setColor(Color.RED);
            g2d.fillRect((int) x - 3, (int) y - 3, 6, 6);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
