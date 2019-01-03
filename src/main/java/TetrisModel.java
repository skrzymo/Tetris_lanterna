import java.util.Arrays;

public class TetrisModel {

    private boolean gameOver = false;
    private boolean isStopped = true;
    private boolean paused = false;

    private int score;
    private int level;
    private int speed;
    private int scoreLevel;
    private int clearedLines;
    private int highscore;

    private final int gridCols = 20, gridRows = 24;

    private String[][][] blocks = {{{"[", "]", "[", "]", "[", "]", null, null},
                                    {null, null, null, null, "[", "]", null, null},
                                    {null, null, null, null, null, null, null, null},
                                    {null, null, null, null, null, null, null, null},},
                    {{"[", "]", "[", "]", "[", "]", null, null},
                    {"[", "]", null, null, null, null, null, null},
                    {null, null, null, null, null, null, null, null},
                    {null, null, null, null, null, null, null, null},},

                    {{"[", "]", "[", "]", "[", "]", "[", "]"},
                    {null, null, null, null, null, null, null, null},
                    {null, null, null, null, null, null, null, null},
                    {null, null, null, null, null, null, null, null},},

                    {{"[", "]", "[", "]", null, null, null, null},
                    {"[", "]", "[", "]", null, null, null, null},
                    {null, null, null, null, null, null, null, null},
                    {null, null, null, null, null, null, null, null},},

                            {{null, null, "[", "]", "[", "]", null, null},
                            {"[", "]", "[", "]", null, null, null, null},
                            {null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null},},

                            {{"[", "]", "[", "]", null, null, null, null},
                            {null, null, "[", "]", "[", "]", null, null},
                            {null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null},},

                    {{"[", "]", "[", "]", "[", "]", null, null},
                    {null, null, "[", "]", null, null, null, null},
                            {null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null},}};

    private String[][][] blocks90 = {{{null, null, "[", "]", null, null, null, null},
                                      {null, null, "[", "]", null, null, null, null},
                                      {"[", "]", "[", "]", null, null, null, null},
                                      {null, null, null, null, null, null, null, null},},

                            {{"[", "]", "[", "]", null, null, null, null},
                            {null, null, "[", "]", null, null, null, null},
                            {null, null, "[", "]", null, null, null, null},
                            {null, null, null, null, null, null, null, null},},

                            {{"[", "]", null, null, null, null, null, null},
                            {"[", "]", null, null, null, null, null, null},
                            {"[", "]", null, null, null, null, null, null},
                            {"[", "]", null, null, null, null, null, null},},

                            {{"[", "]", "[", "]", null, null, null, null},
                            {"[", "]", "[", "]", null, null, null, null},
                            {null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null},},

                            {{"[", "]", null, null, null, null, null, null},
                            {"[", "]", "[", "]", null, null, null, null},
                            {null, null, "[", "]", null, null, null, null},
                            {null, null, null, null, null, null, null, null},},

                            {{null, null, "[", "]", null, null, null, null},
                            {"[", "]", "[", "]", null, null, null, null},
                            {"[", "]", null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null},},

                            {{null, null, "[", "]", null, null, null, null},
                            {"[", "]", "[", "]", null, null, null, null},
                            {null, null, "[", "]", null, null, null, null},
                            {null, null, null, null, null, null, null, null},}};

    private String[][][] blocks180 = {{{"[", "]", null, null, null, null, null, null},
                                       {"[", "]", "[", "]", "[", "]", null, null},
                                       {null, null, null, null, null, null, null, null},
                                       {null, null, null, null, null, null, null, null},},

                            {{null, null, null, null, "[", "]", null, null},
                            {"[", "]", "[", "]", "[", "]", null, null},
                            {null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null},},

                            {{"[", "]", "[", "]", "[", "]", "[", "]"},
                            {null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null},},

                            {{"[", "]", "[", "]", null, null, null, null},
                            {"[", "]", "[", "]", null, null, null, null},
                            {null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null},},

                            {{null, null, "[", "]", "[", "]", null, null},
                            {"[", "]", "[", "]", null, null, null, null},
                            {null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null},},

                            {{"[", "]", "[", "]", null, null, null, null},
                            {null, null, "[", "]", "[", "]", null, null},
                            {null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null},},

                            {{null, null, "[", "]", null, null, null, null},
                            {"[", "]", "[", "]", "[", "]", null, null},
                            {null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null},}};

    private String[][][] blocks270 = {{{"[", "]", "[", "]", null, null, null, null},
                                       {"[", "]", null, null, null, null, null, null},
                                       {"[", "]", null, null, null, null, null, null},
                                       {null, null, null, null, null, null, null, null},},

                            {{"[", "]", null, null, null, null, null, null},
                            {"[", "]", null, null, null, null, null, null},
                            {"[", "]", "[", "]", null, null, null, null},
                            {null, null, null, null, null, null, null, null},},

                            {{"[", "]", null, null, null, null, null, null},
                            {"[", "]", null, null, null, null, null, null},
                            {"[", "]", null, null, null, null, null, null},
                            {"[", "]", null, null, null, null, null, null},},

                            {{"[", "]", "[", "]", null, null, null, null},
                            {"[", "]", "[", "]", null, null, null, null},
                            {null, null, null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null},},

                            {{"[", "]", null, null, null, null, null, null},
                            {"[", "]", "[", "]", null, null, null, null},
                            {null, null, "[", "]", null, null, null, null},
                            {null, null, null, null, null, null, null, null},},

                            {{null, null, "[", "]", null, null, null, null},
                            {"[", "]", "[", "]", null, null, null, null},
                            {"[", "]", null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null},},

                            {{"[", "]", null, null, null, null, null, null},
                            {"[", "]", "[", "]", null, null, null, null},
                            {"[", "]", null, null, null, null, null, null},
                            {null, null, null, null, null, null, null, null},}};

    private String[][] actualBlock;
    private String[][] nextBlock;

    private int actualColumn;
    private int actualRow;

    private int clearedLinesAtOnce;


    public TetrisModel() {
        this.actualColumn = 13;
        this.actualRow = 1;
        this.actualBlock = blocks[(int) (Math.random() * 7)];
        this.nextBlock = blocks[(int) (Math.random() * 7)];
        this.score = 0;
        this.scoreLevel = 0;
        this.highscore = 0;
        this.level = 0;
        this.speed = 600;
        this.clearedLinesAtOnce = 0;
        this.clearedLines = 0;
    }

    public int getGridRows() {
        return gridRows;
    }

    public int getGridCols() {
        return gridCols;
    }

    public void getNextPieceValue() {
        setActualBlock(this.nextBlock);
        setNextBlock(this.blocks[(int) (Math.random() * 7)]);
    }

    public int[] pieceDimensions(String[][] block) {
        int h = 0;
        int w = 0;
        for(int row = 0; row < 4; row++) {
            for(int col = 0; col < 8; col ++) {
                if(block[row][col] != null) {
                    h = row + 1;
                    if(col >= w) {
                        w = col + 1;
                    }
                }
            }
        }
        return new int[] {w, h};
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void start() {
        score = 0;
        level = 0;
        clearedLines = 0;
        gameOver = false;
        resetActualColumn();
        resetActualRow();
        actualBlock = blocks[(int) (Math.random() * 7)];
        nextBlock = blocks[(int) (Math.random() * 7)];
    }

    public void moveLeft() {
        if((this.actualColumn - 2) != 3) {
            setActualColumn(this.actualColumn - 2);
        }
    }

    public void moveRight() {
        if((this.actualColumn + pieceDimensions(this.actualBlock)[0]) != 25) {
            setActualColumn(this.actualColumn + 2);
        }
    }

    public void rotate() {
        for(int i = 0; i < 7; i++) {
            if (Arrays.deepEquals(this.actualBlock, blocks[i])) {
                if (this.actualColumn + pieceDimensions(blocks90[i])[0] > 24) {
                    this.actualColumn = this.actualColumn - (pieceDimensions(blocks90[i])[0] - pieceDimensions(this.actualBlock)[0]);
                }
                this.actualBlock = blocks90[i];
            } else if (Arrays.deepEquals(this.actualBlock, blocks90[i])) {
                if (this.actualColumn + pieceDimensions(blocks180[i])[0] > 24) {
                    this.actualColumn = this.actualColumn - (pieceDimensions(blocks180[i])[0] - pieceDimensions(this.actualBlock)[0]);
                }
                this.actualBlock = blocks180[i];
            } else if (Arrays.deepEquals(this.actualBlock, blocks180[i])) {
                if (this.actualColumn + pieceDimensions(blocks270[i])[0] > 24) {
                    this.actualColumn = this.actualColumn - (pieceDimensions(blocks270[i])[0] - pieceDimensions(this.actualBlock)[0]);
                }
                this.actualBlock = blocks270[i];
            } else if (Arrays.deepEquals(this.actualBlock, blocks270[i])) {
                if (this.actualColumn + pieceDimensions(blocks[i])[0] > 24) {
                    this.actualColumn = this.actualColumn - (pieceDimensions(blocks[i])[0] - pieceDimensions(this.actualBlock)[0]);
                }
                this.actualBlock = blocks[i];
            }
        }
    }

    public void down(int highest) {
        this.actualRow = highest;
    }

    public int getActualColumn() {
        return actualColumn;
    }

    public void setActualColumn(int actualColumn) {
        this.actualColumn = actualColumn;
    }

    public void resetActualColumn() {
        this.actualColumn = 13;
    }

    public int getActualRow() {
        return actualRow;
    }

    public void resetActualRow() {
        this.actualRow = 1;
    }

    public int getNextRow() {
        this.actualRow++;
        return this.actualRow;
    }

    public String[][] getActualBlock() {
        return actualBlock;
    }

    public void setActualBlock(String[][] actualBlock) {
        this.actualBlock = actualBlock;
    }

    public int[] getEachColumnHeight(String[][] block) {
        int h0 = 0, h1 = 0, h2 = 0, h3 = 0;
        if(pieceDimensions(block)[0] == 2) {
            for(int row = 0; row < 4; row++) {
                if(block[row][1] != null) {
                    h0 = row + 1;
                }
            }
        } else if(pieceDimensions(block)[0] == 4) {
            for(int row = 0; row < 4; row++) {
                for (int col = 0; col < 3; col += 2) {
                    if(block[row][0] != null) {
                        h0 = row + 1;
                    }
                    if(block[row][2] != null) {
                        h1 = row + 1;
                    }
                }
            }
        } else if(pieceDimensions(block)[0] == 6) {
            for(int row = 0; row < 4; row++) {
                for (int col = 0; col < 5; col += 2) {
                    if(block[row][0] != null) {
                        h0 = row + 1;
                    }
                    if(block[row][2] != null) {
                        h1 = row + 1;
                    }
                    if(block[row][4] != null) {
                        h2 = row + 1;
                    }
                }
            }
        } else {
            for(int row = 0; row < 4; row++) {
                for (int col = 0; col < 7; col += 2) {
                    if(block[row][0] != null) {
                        h0 = row + 1;
                    }
                    if(block[row][2] != null) {
                        h1 = row + 1;
                    }
                    if(block[row][4] != null) {
                        h2 = row + 1;
                    }
                    if(block[row][6] != null) {
                        h3 = row + 1;
                    }
                }
            }
        }
        return new int[] {h0, h1, h2, h3};
    }

    public String[][] getNextBlock() {
        return nextBlock;
    }

    public void setNextBlock(String[][] nextBlock) {
        this.nextBlock = nextBlock;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setClearedLinesAtOnce(int clearedLinesAtOnce) {
        this.clearedLinesAtOnce = clearedLinesAtOnce;
        this.clearedLines += clearedLinesAtOnce;
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

    public int getLevel() {
        return level;
    }

    public void increaseSpeed() {
        this.speed -= 30;
    }

    public int getSpeed() {
        return speed;
    }

    public void calculateScoreForLevel(int score) {
        this.scoreLevel += score;
    }

    public void setScoreLevel(int scoreLevel) {
        this.scoreLevel = scoreLevel;
    }

    public int getScoreLevel() {
        return scoreLevel;
    }

    public void increaseLevel() {
        this.level ++;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public int getClearedLines() {
        return clearedLines;
    }


    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

}
