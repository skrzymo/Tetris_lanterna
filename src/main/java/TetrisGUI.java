import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;

import static java.lang.Thread.sleep;

public class TetrisGUI extends JFrame {

    private BoardPanel board;
    public static SidePanel side;
    private boolean start;

    private boolean paused;

    private int highScore;

    private File file;
    private AudioInputStream sound;
    private Clip clip;
    private boolean musicPlay;

    public TetrisGUI() {
        super("TetrisGUI by skrzymo");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        side = new SidePanel(this);
        this.board = new BoardPanel(this);
        this.start = false;
        this.paused = false;
        add(board, BorderLayout.CENTER);
        add(side, BorderLayout.EAST);

        addKeyListener(new KeyInput());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        try{
            this.file = new File("E:/Projekty/IntelliJ/Tetris/src/main/resources/Tetris.wav");
            this.sound = AudioSystem.getAudioInputStream(file);
            this.clip = AudioSystem.getClip();
            this.clip.open(sound);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }

        loop();
        play();
        this.musicPlay = true;

        this.highScore = 0;
    }

    public void key() {
        if(KeyInput.wasPressed(KeyEvent.VK_ESCAPE)) {
            stop();
            TetrisGUIMain.INSTANCE.setVisible(true);
            start = false;
            this.dispose();
        }
        if(KeyInput.wasPressed(KeyEvent.VK_UP)) {
            board.cube.rotate();
            board.canRotate();
        }
        if(KeyInput.wasPressed(KeyEvent.VK_LEFT) && board.canMoveLeft()) {
            int actual = board.getActualColumn();
            actual--;
            board.setActualColumn(actual);
        }
        if(KeyInput.wasPressed(KeyEvent.VK_RIGHT) && board.canMoveRight()) {
            int actual = board.getActualColumn();
            actual++;
            board.setActualColumn(actual);
        }
        if(KeyInput.wasPressed(KeyEvent.VK_DOWN)) {
            int newRow = board.getDropPoint();
            board.setActualRow(newRow);
        }
        if(KeyInput.wasPressed(KeyEvent.VK_ENTER)) {
            if(!paused) {
                paused = true;
                stop();
            } else {
                paused = false;
                if(musicPlay) {
                    loop();
                    play();
                }
            }
        }
        if(KeyInput.wasPressed(KeyEvent.VK_BACK_SPACE)) {
            if(clip.isRunning()) {
                stop();
                musicPlay = false;
            } else {
                loop();
                play();
                musicPlay = true;
            }
        }
    }

    public void run() {
        start = true;
        KeyInput.update();
        while (start) {
            key();
            board.repaint();
            side.repaint();
            try {
                if(paused) {
                    sleep(600);
                } else {
                    sleep(side.getSpeed());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        saveHighScore();
        if(board.isFull()) {
            TetrisGUIMain.INSTANCE.setScore(side.getScore());
            TetrisGUIMain.INSTANCE.setGameOver(true);
        }
        TetrisGUIMain.INSTANCE.setVisible(true);
        this.dispose();
    }

    private void saveHighScore() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(TetrisGUIMain.fileScore));
            String line = reader.readLine();
            while (line != null)
            {
                try {
                    highScore = Integer.parseInt(line.trim());
                    if (side.getScore() > highScore)
                    {
                        highScore = side.getScore();
                    }
                } catch (NumberFormatException e1) {
                }
                line = reader.readLine();
            }
            reader.close();

        } catch (IOException ex) {
            System.err.println("ERROR reading scores from file");
        }
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(TetrisGUIMain.fileScore));
            output.write(Integer.toString(highScore));
            output.close();

        } catch (IOException ex1) {
            System.out.printf("ERROR writing score to file: %s\n", ex1);
        }
    }

    public void play() {
        clip.setFramePosition(0);
        clip.start();
    }

    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        clip.stop();
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setStart(boolean start) {
        this.start = start;
    }
}
