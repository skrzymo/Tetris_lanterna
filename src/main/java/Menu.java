import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Menu extends Canvas implements Runnable {

    public static final String TITLE = "Tetris by skrzymo";
    public static final int WIDTH = 381;
    public static final int HEIGHT = 650;
    private boolean running;
    private final Button[] options;
    private int currentSelection;
    private BufferedImage image;
    private MouseInput mi;
    private JFrame frame;

    public static Menu INSTANCE;

    public Menu() {
        frame = new JFrame(TITLE);
        try {
            this.image = ImageIO.read(new File("E:\\Projekty\\IntelliJ\\Tetris\\src\\tetris-menu.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        options = new Button[3];
        options[0] = new Button("Play TUI mode", 350,
                new Font("Arial", Font.PLAIN, 20), new Font("Arial", Font.BOLD, 36),
                new Color(25,76,123), new Color(185,49,49));
        options[1] = new Button("Play GUI mode", 430,
                new Font("Arial", Font.PLAIN, 20), new Font("Arial", Font.BOLD, 36),
                new Color(25,76,123), new Color(185,49,49));
        options[2] = new Button("QUIT", 510,
                new Font("Arial", Font.PLAIN, 20), new Font("Arial", Font.BOLD, 36),
                new Color(25,76,123), new Color(185,49,49));

        addKeyListener(new KeyInput());
        this.mi = new MouseInput();
        addMouseListener(mi);
        addMouseMotionListener(mi);

        INSTANCE = this;
    }

    public void tick() {

        if(KeyInput.wasPressed(KeyEvent.VK_UP)) {
            currentSelection--;
            if(currentSelection < 0) {
                currentSelection = options.length - 1;
            }
        }

        if(KeyInput.wasPressed(KeyEvent.VK_DOWN)) {
            currentSelection++;
            if(currentSelection >= options.length) {
                currentSelection = 0;
            }
        }

        boolean clicked = false;
        for(int i = 0; i < this.options.length; i++) {
            if(this.options[i].intersects(new Rectangle(MouseInput.getX(), MouseInput.getY(), 1, 1))) {
                currentSelection = i;
                clicked = MouseInput.wasPressed(MouseEvent.BUTTON1);
            }
        }

        if(clicked || KeyInput.wasPressed(KeyEvent.VK_ENTER)) {
            select();
        }
    }

    public void run() {
        running = true;
        requestFocus();
        double target = 60.0;
        double nsPerTick = 1000000000.0 / target;
        long lastTime = System.nanoTime();
        double unprocessed = 0.0;
        boolean canRender = false;
        while(running) {
            long now = System.nanoTime();
            unprocessed += (now - lastTime) / nsPerTick;
            lastTime = now;

            if(unprocessed >= 1.0) {
                tick();
                KeyInput.update();
                MouseInput.update();
                unprocessed--;
                canRender = true;
            } else {
                canRender = false;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(canRender) {
                render();
            }
        }
    }

    public void running() {
        frame.add(Menu.INSTANCE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setResizable(false);
        frame.setFocusable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        Menu.INSTANCE.start();
    }

    private void stop() {
        if(!running) {
            return;
        }
        running = false;
    }

    private void start() {
        if(running) {
            return;
        }
        running = true;
        new Thread(this, "TetrisMenu-Thread").start();
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if(bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, WIDTH, HEIGHT, null);

        for(int i = 0; i < options.length; i++) {
            if(i == currentSelection) {
                options[i].setSelected(true);
            } else {
                options[i].setSelected(false);
            }

            options[i].render(g);
        }

        g.dispose();
        bs.show();
    }

    private void select() {
        switch(currentSelection) {
            case 0:
                Menu.INSTANCE.stop();
                frame.dispose();
                try {
                    TetrisView tv = new TetrisView();
                    tv.run();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                Menu.INSTANCE.stop();
                frame.dispose();
                KeyInput.clear();
                TetrisGUIMain tgm = new TetrisGUIMain();
                tgm.run();
                break;
            case 2:
                System.err.println("Exiting Game");
                Menu.INSTANCE.stop();
                System.exit(0);
                break;
        }
    }
}
