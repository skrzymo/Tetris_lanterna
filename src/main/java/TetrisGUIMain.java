import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;

public class TetrisGUIMain extends JFrame {

    private boolean running;
    private boolean isImage;
    private BufferedImage image;
    private Color boardColor = new Color(153,255,153);

    public static TetrisGUIMain INSTANCE;

    public static File fileScore = new File("E:/Projekty/IntelliJ/Tetris/src/main/resources/ScoresGUI.txt");
    private int highScore;

    private boolean gameOver;
    private int score;

    public TetrisGUIMain() {
        super("TetrisGUI by skrzymo");
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(Menu.WIDTH, Menu.HEIGHT);
        addKeyListener(new KeyInput());
        setLocationRelativeTo(null);
        setVisible(true);
        this.running = false;
        this.isImage = false;
        this.gameOver = false;
        try {
            this.image = ImageIO.read(new File("E:\\Projekty\\IntelliJ\\Tetris\\src\\tetrisGUIMain.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.highScore = 0;
        INSTANCE = this;
    }

    public int getHighScore() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileScore));
            String line = reader.readLine();
            while (line != null)
            {
                try {
                    highScore = Integer.parseInt(line.trim());
                } catch (NumberFormatException e1) {
                }
                line = reader.readLine();
            }
            reader.close();

        } catch (IOException ex) {
            File yourFile = new File("E:/Projekty/IntelliJ/Tetris/src/main/resources/Scores.txt");
            try {
                yourFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                FileOutputStream oFile = new FileOutputStream(yourFile, false);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return highScore;
    }

    public void run() {
        running = true;
        KeyInput.clear();
        Graphics g = this.getGraphics();
        while(running) {
            if(!isImage) {
                g.drawImage(image, 0, 0, Menu.WIDTH, Menu.HEIGHT, null);
                isImage = true;
            }
            if(isGameOver()) {
                drawGameOver(g);
            } else {
                drawNotGameOver(g);
            }
            key();
            KeyInput.update();
        }

    }

    private void drawNotGameOver(Graphics g) {
        Font f = new Font("Arial", Font.BOLD, 20);
        Font f2 = g.getFont();
        Fonts.drawString(g, f, boardColor, "HIGH SCORE    " + getHighScore(), 380);
        Fonts.drawString(g, f, boardColor, "Press ENTER to play.", 450);
        Fonts.drawString(g, f, boardColor, "ESC to back to main menu.", 480);
        Fonts.drawString(g, f2, boardColor, "Skrzymo 2018", 630);
    }

    private void drawGameOver(Graphics g) {
        Font f = new Font("Arial", Font.BOLD, 20);
        Font f2 = g.getFont();
        Font f3 = new Font("Arial", Font.PLAIN, 16);
        Fonts.drawString(g, f, Color.RED, "GAME OVER", 300);
        Fonts.drawString(g, f3, boardColor, "YOUR SCORE    " + this.score, 350);
        Fonts.drawString(g, f3, boardColor, "HIGH SCORE    " + getHighScore(), 380);
        Fonts.drawString(g, f, boardColor, "Press ENTER to play.", 450);
        Fonts.drawString(g, f, boardColor, "ESC to back to main menu.", 480);
        Fonts.drawString(g, f2, boardColor, "Skrzymo 2018", 630);
    }

    public void key() {
        if(KeyInput.wasPressed(KeyEvent.VK_ESCAPE)) {
            Menu menu = new Menu();
            menu.running();
            running = false;
            this.dispose();
        }
        if(KeyInput.wasPressed(KeyEvent.VK_ENTER)) {
            gameOver = false;
            isImage = false;
            this.setVisible(false);
            TetrisGUI tg = new TetrisGUI();
            tg.run();
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
