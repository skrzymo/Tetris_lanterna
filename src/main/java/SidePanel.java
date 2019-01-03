import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SidePanel extends JPanel {

    private TetrisGUI tetrisGUI;
    private Color boardColor = new Color(153,255,153);

    private int nextBlock;
    private Random random = new Random();

    private int lines;
    private int clearedLinesAtOnce;
    private int score;

    private int level;
    private int scoreLevel;

    private int speed;

    private int newRandom;
    private int lastRandom;

    public SidePanel(TetrisGUI tetrisGUI) {
        this.tetrisGUI = tetrisGUI;
        this.lines = 0;
        this.score = 0;
        this.clearedLinesAtOnce = 0;
        this.level = 0;
        this.scoreLevel = 0;
        this.speed = 600;
        this.lastRandom = 1;

        setPreferredSize(new Dimension(400, 490));
        setBackground(Color.BLACK);

        drawNextBlock();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(!tetrisGUI.isPaused()) {
            drawScore(g);
            drawNextPiece(g);
            drawLevel(g);
            drawLines(g);
            g.drawString("Press ENTER to PAUSE", 30, 440);
            g.drawString("Press BACKSPACE to SOUND ON/OFF", 30, 460);
            g.drawString("Press ESC to go back to MAIN MENU", 30, 480);
        } else {
            newRandom = random.nextInt(7);
            if(newRandom == lastRandom) {
                newRandom += 1;
                lastRandom = newRandom;
            }
            drawPauseView(g, Blocks.tilesColor[newRandom]);
        }
    }

    public void drawPauseView(Graphics g, Color color) {
        g.setColor(boardColor);
        g.drawString("Press ENTER to resume.", 5, 290);
        g.drawString("Skrzymo 2018", 30, 470);
        Font f = new Font("Arial", Font.BOLD, 36);
        Fonts.drawString(g, f, color, "PAUSED", 0, 240);
    }

    public void drawNextBlock() {
        nextBlock = random.nextInt(7) + 1;
    }

    public void drawNextPiece(Graphics g) {
        g.setColor(boardColor);
        g.drawString("NEXT:", 35, 130);
        g.drawRect(30, 70, 220, 120);
        drawBlock(g);
    }

    private void drawCube(Graphics g, int i, int j, int b) {
        g.setColor(Blocks.tilesColor[b - 1]);
        g.fillRect(i * Blocks.TILE_SIZE, j * Blocks.TILE_SIZE, Blocks.TILE_SIZE, Blocks.TILE_SIZE);
        g.setColor(Blocks.tilesColorBorder[b - 1]);
        g.drawRect(i * Blocks.TILE_SIZE, j * Blocks.TILE_SIZE, Blocks.TILE_SIZE - 1, Blocks.TILE_SIZE - 1);
    }

    private void drawBlock(Graphics g) {
        for(int ti = 0; ti < 4; ti++) {
            for(int tj = 0; tj < 4; tj++) {
                if(Blocks.BLOCKS[nextBlock][tj][ti]) {
                    drawCube(g, 5 + ti, 4 + tj, nextBlock);
                }
            }
        }
    }


    private void drawScore(Graphics g) {
        g.setColor(boardColor);
        g.drawString("SCORE:    " + score,35, 30);
        g.drawRect(30, 10, 220, 30);
    }

    private void drawLevel(Graphics g) {
        g.setColor(boardColor);
        g.drawString("LEVEL:    " + level,35, 240);
        g.drawRect(30, 220, 220, 30);
    }

    private void drawLines(Graphics g) {
        g.setColor(boardColor);
        g.drawString("LINES:    " + lines,35, 300);
        g.drawRect(30, 280, 220, 30);

    }

    public int getNextBlock() {
        return nextBlock;
    }

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    public int getLevel() {
        return level;
    }

    public void calculateScoreForLevel(int score) {
        this.scoreLevel += score;
    }

    public int getScoreLevel() {
        return scoreLevel;
    }

    public void setScoreLevel(int scoreLevel) {
        this.scoreLevel = scoreLevel;
    }

    public void addScore(int score) {
        this.score = this.score + score;
    }

    public void calculateScore() {
        if(this.clearedLinesAtOnce == 1) {
            this.score += (40*(getLevel() + 1));
            calculateScoreForLevel(40*(getLevel() + 1));
        } else if(this.clearedLinesAtOnce == 2) {
            this.score += (100*(getLevel() + 1));
            calculateScoreForLevel(100*(getLevel() + 1));
        } else if(this.clearedLinesAtOnce == 3) {
            this.score += (300*(getLevel() + 1));
            calculateScoreForLevel(300*(getLevel() + 1));
        } else if(this.clearedLinesAtOnce == 4) {
            this.score += (1200*(getLevel() + 1));
            calculateScoreForLevel(1200*(getLevel() + 1));
        }
    }

    public void increaseSpeed() {
        this.speed -= 30;
    }

    public void increaseLevel() {
        this.level ++;
    }

    public int getClearedLinesAtOnce() {
        return clearedLinesAtOnce;
    }

    public void setClearedLinesAtOnce(int clearedLinesAtOnce) {
        this.clearedLinesAtOnce = clearedLinesAtOnce;
    }

    public int getSpeed() {
        return speed;
    }

    public int getScore() {
        return score;
    }
}
