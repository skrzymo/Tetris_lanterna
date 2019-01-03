import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {

    private TetrisGUI tetrisGUI;
    private Color boardColor = new Color(153,255,153);
    private final static int COLS = 10;
    private final static int ROWS = 20;
    private final static int WIDTH = Blocks.TILE_SIZE * 10;
    private final static int HEIGHT = Blocks.TILE_SIZE * 20;

    int[][] tab = new int[12][22];

    Cube cube = new Cube();
    private int actualColumn, actualRow;

    public BoardPanel(TetrisGUI tetrisGUI) {
        this.tetrisGUI = tetrisGUI;

        setPreferredSize(new Dimension(WIDTH + 1 , HEIGHT + 1));
        setBackground(Color.BLACK);

        for(int i = 0; i < 12; i++) {
            tab[i][0] = 1;
            tab[i][21] = 1;
        }
        for(int j = 0; j < 22; j++) {
            tab[10][j] = 1;
        }

        this.actualColumn = 3;
        this.actualRow = -1;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(!tetrisGUI.isPaused()) {
            checkBoard();
            drawBlock(g, actualColumn, actualRow);

            if (isCubeBorder(actualColumn, actualRow + 2)) {
                actualRow++;
            } else {
                solidify();
                newBlock();
            }

            for (int i = 0; i < COLS + 1; i++) {
                for (int j = 1; j < ROWS + 1; j++) {

                    if (tab[i][j] > 0) {
                        g.setColor(Blocks.tilesColor[tab[i][j] - 1]);
                        g.fillRect(i * Blocks.TILE_SIZE, j * Blocks.TILE_SIZE - Blocks.TILE_SIZE, Blocks.TILE_SIZE - 1, Blocks.TILE_SIZE - 1);
                        g.setColor(Blocks.tilesColorBorder[tab[i][j] - 1]);
                        g.drawRect(i * Blocks.TILE_SIZE, j * Blocks.TILE_SIZE - Blocks.TILE_SIZE, Blocks.TILE_SIZE - 1, Blocks.TILE_SIZE - 1);
                    }
                }
            }

            g.setColor(boardColor);
            for (int i = 0; i < COLS + 1; i++) {
                for (int j = 1; j < ROWS + 1; j++) {
                    g.drawString(".", i * Blocks.TILE_SIZE, j * Blocks.TILE_SIZE);
                }
            }
            g.drawRect(0, 1, Blocks.TILE_SIZE * COLS, Blocks.TILE_SIZE * ROWS);
        }
    }

    private void drawCube(Graphics g, int i, int j, int b) {
        g.setColor(Blocks.tilesColor[b - 1]);
        g.fillRect(i * Blocks.TILE_SIZE, j * Blocks.TILE_SIZE, Blocks.TILE_SIZE, Blocks.TILE_SIZE);
        g.setColor(Blocks.tilesColorBorder[b - 1]);
        g.drawRect(i * Blocks.TILE_SIZE, j * Blocks.TILE_SIZE, Blocks.TILE_SIZE - 1, Blocks.TILE_SIZE - 1);
    }

    public void drawBlock(Graphics g ,int i, int j) {
        for(int ti = 0; ti < 4; ti++) {
            for(int tj = 0; tj < 4; tj++) {
                if(cube.tab[ti][tj]) {
                    drawCube(g,i + ti, j + tj, cube.actualBlock);
                }
            }
        }
    }

    private boolean isCubeBorder(int x, int y) {
        if(x == -2) {
            for(int i = 2; i < 4; i++) {
                for(int j = 0; j < 4; j++) {
                    if(cube.tab[i][j] && tab[i + x][j + y] > 0) {
                        return false;
                    }
                }
            }
            return true;
        } else if(x == -1) {
            for(int i = 1; i < 4; i++) {
                for(int j = 0; j < 4; j++) {
                    if(cube.tab[i][j] && tab[i + x][j + y] > 0) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            for(int i = 0; i < 4; i++) {
                for(int j = 0; j < 4; j++) {
                    if(cube.tab[i][j] && tab[i + x][j + y] > 0) {
                        return false;
                    }
                }
            }
            return true;
        }

    }

    public void solidify() {
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                if (cube.tab[i][j]) {
                    tab[i + actualColumn][j + actualRow + 1] = cube.actualBlock;
                }
            }
        }
    }

    private void checkBoard()
    {
        TetrisGUI.side.setClearedLinesAtOnce(0);
        for(int i = 1; i <= 20; i++) {
            if (tab[0][i] == 8) {
                clearLines(i);
            }
            if(isLine(i)) {
                setLine(i);
                TetrisGUI.side.setLines(TetrisGUI.side.getLines() + 1);
            }
        }
        if(isFull()) {
            tetrisGUI.setStart(false);
        }

        if(TetrisGUI.side.getScoreLevel() >= 1000) {
            TetrisGUI.side.increaseSpeed();
            TetrisGUI.side.increaseLevel();
            TetrisGUI.side.setScoreLevel(TetrisGUI.side.getScoreLevel() % 1000);
        }

        TetrisGUI.side.calculateScore();
    }

    private void clearLines(int x) {
        TetrisGUI.side.setClearedLinesAtOnce(TetrisGUI.side.getClearedLinesAtOnce() + 1);
        for(int i = x; i > 0; i--) {
            for(int j = 0; j < 10; j++) {
                tab[j][i] = tab[j][i-1];
            }
        }
        for(int i = 0; i < 10; i++) {
            tab[i][1] = 0;
        }
    }

    private boolean isLine(int x) {
        for(int i = 0; i < 10; i++) {
            if(tab[i][x] == 0) {
                return false;
            }
        }
        return true;
    }

    private void setLine(int x)
    {
        for(int i = 0; i < 10; i++) {
            tab[i][x] = 8;
        }
    }

    public boolean isFull()
    {
        for(int i = 0; i < 10; i++) {
            if(tab[i][1] != 0) {
                return true;
            }
        }
        return false;
    }

    public void newBlock() {
        actualColumn = 3;
        actualRow = -1;
        cube.setCube(TetrisGUI.side.getNextBlock());
        TetrisGUI.side.drawNextBlock();
        TetrisGUI.side.drawNextPiece(TetrisGUI.side.getGraphics());
    }

    public boolean canMoveLeft() {
        if(actualColumn < -1) {
            return false;
        }

        if(actualColumn == -1) {
            for(int i = 0; i < 4; i++) {
                if(cube.tab[0][i]) {
                    return false;
                }
                if(cube.tab[1][i]) {
                    return false;
                }
            }
            return true;
        }

        if(actualColumn == 0){
            for(int i = 0; i < 4; i++) {
                if(cube.tab[0][i]) {
                    return false;
                }
            }
            return true;
        }

        return isCubeBorder(actualColumn - 1, actualRow + 1);
    }

    public void canRotate() {
        if(actualColumn == -2) {
            for(int i = 0; i < 2; i++) {
                for(int j = 0; j < 4; j++) {
                    if(cube.tab[i][j]) {
                        actualColumn = 0;
                    }
                }
            }
        }

        if(actualColumn == -1) {
            for(int i = 0; i < 1; i++) {
                for(int j = 0; j < 4; j++) {
                    if(cube.tab[i][j]) {
                        actualColumn = 0;
                    }
                }
            }
        }

        if(actualColumn == 7 || actualColumn == 8) {
            for (int j = 0; j < 4; j++) {
                if (cube.tab[3][j]) {
                    actualColumn = 6;
                }
            }
        }
    }

    public boolean canMoveRight() {
        return isCubeBorder(actualColumn + 1, actualRow + 1);
    }

    public int getActualColumn() {
        return actualColumn;
    }

    public void setActualColumn(int actualColumn) {
        this.actualColumn = actualColumn;
    }

    public void setActualRow(int actualRow) {
        this.actualRow = actualRow;
    }

    public int getDropPoint() {
        int[] highestPoint = new int[4];
        int[] lowestPoint = new int[]{4, 4, 4, 4};
        int difference = 22;
        int dropRow = 0;
        int dropScore = 0;

        int l = 0;
        for(int j = 0; j < 4; j++) {
            for(int i = 0, k = 0; i < 4; i++, k++) {
                if(cube.tab[i][j]) {
                    lowestPoint[k] = j;
                    if(j > l) {
                        l = j;
                    }
                }
            }
        }

        for(int j = 21; j > actualRow - l && j > 0; j--) {
            if(actualColumn < 0) {
                for(int i = -actualColumn, k = -actualColumn; i < 4; i++, k++) {
                    if(tab[actualColumn + i][j] > 0) {
                        highestPoint[k] = j;
                    }
                }
            } else {
                for(int i = 0, k = 0; i < 4; i++, k++) {
                    if(tab[actualColumn + i][j] > 0) {
                        highestPoint[k] = j;
                    }
                }
            }
        }

        if(actualColumn < 0) {
            for(int k = -actualColumn; k < 4; k++) {
                if(highestPoint[k] - lowestPoint[k] < difference && lowestPoint[k] != 4) {
                    difference = highestPoint[k] - lowestPoint[k];
                    dropScore = highestPoint[k] - actualRow - lowestPoint[k] - 1;
                    dropRow = highestPoint[k] - lowestPoint[k] - 2;
                }
            }
        } else {
            for(int k = 0; k < 4; k++) {
                if(highestPoint[k] - lowestPoint[k] < difference && lowestPoint[k] != 4) {
                    difference = highestPoint[k] - lowestPoint[k];
                    dropScore = highestPoint[k] - actualRow - lowestPoint[k] - 1;
                    dropRow = highestPoint[k] - lowestPoint[k] - 2;
                }
            }
        }

        TetrisGUI.side.addScore(dropScore);
        TetrisGUI.side.calculateScoreForLevel(dropScore);

        return dropRow;
    }
}
