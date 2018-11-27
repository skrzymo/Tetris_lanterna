import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static java.lang.Thread.sleep;

public class TetrisView {

    private TetrisModel model = new TetrisModel();
    private Terminal terminal = new DefaultTerminalFactory().createTerminal();
    private Screen screen = new TerminalScreen(terminal);
    private TextGraphics tg = screen.newTextGraphics();
    private boolean keepRunning = true;
    private TextCharacter[][] savedScreen;
    private TextCharacter dot;
    private TextCharacter space;

    private File file;
    private AudioInputStream sound;
    private Clip clip;


    public TetrisView() throws IOException {
        this.savedScreen = new TextCharacter[screen.getTerminalSize().getRows()][screen.getTerminalSize().getColumns()];
        this.dot = new TextCharacter('.');
        this.space = new TextCharacter(' ');
        try{
            this.file = new File("E:/Projekty/IntelliJ/Tetris/src/main/resources/Tetris.wav");
            this.sound = AudioSystem.getAudioInputStream(file);
            this.clip = AudioSystem.getClip();
            this.clip.open(sound);
        } catch (UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void clear() {
        screen.clear();
    }

    private void print(int x, int y, String c) {
        tg.putString(x,y,c);
    }

    public void draw() throws IOException {
        clear();
        if(!model.isGameOver()) {
            if (!model.isStopped()) {
                print(32,22, "Press BACKSPACE to SOUND ON/OFF");
                print(32, 23, "Press ESC to go back to MAIN SCREEN");
                drawLevel();
                drawScore();
                drawLines();
                drawGrid();
            } else {
                drawStartScreen();
            }
        } else {
            drawGameOver();
        }
    }

    public void drawStartScreen() {
        print(0, 1, "          [][][][][][][][][][][][][][][][][][][][][][][][]          ");
        print(0, 2, "          []                                            []          ");
        print(0, 3, "          []                                            []          ");
        print(0, 4, "          /\\__  _\\/\\  ___\\/\\__  _\\/\\  == \\ /\\ \\ /\\  ___\\[]          ");
        print(0, 5, "          \\/_/\\ \\/\\ \\  ___\\/_/\\ \\/\\ \\  __<_\\ \\ \\. \\___  \\]          ");
        print(0, 6, "          [] \\ \\_\\ \\ \\_____\\ \\ \\_\\ \\ \\_\\_\\_\\. \\_\\./\\_____\\          ");
        print(0, 7, "          []  \\/_/  \\/_____/  \\/_/  \\/_/ /_/ \\/_/ \\/_____/          ");
        print(0, 8, "          []                                            []          ");
        print(0, 9, "          []                                            []          ");
        print(0, 10, "          []                                            []          ");
        print(0, 11, "          []                                            []          ");
        print(0, 12, "          []                                            []          ");
        print(0, 13, "          []      Press ENTER to play. ESC to quit.     []          ");
        print(0, 14, "          []                                            []          ");
        print(0, 15, "          []                                            []          ");
        print(0, 16, "          []                                            []          ");
        print(0, 17, "          []                                            []          ");
        print(0, 18, "          []                                            []          ");
        print(0, 19, "          []                                            []          ");
        print(0, 20, "          []                Skrzymo 2018                []          ");
        print(0, 21, "          [][][][][][][][][][][][][][][][][][][][][][][][]          ");
    }

    private void drawScore() {
        print(35, 1, "+--------------------+");
        print(35, 2, "|                    |");
        print(35, 3, "+--------------------+");
        print(37, 2, "SCORE: " + model.getScore());
    }

    public void drawLevel() {
        print(35, 12, "+--------------------+");
        print(35, 13, "|                    |");
        print(35, 14, "+--------------------+");
        print(37, 13, "LEVEL: " + model.getLevel());
    }

    public void drawLines() {
        print(35, 16, "+--------------------+");
        print(35, 17, "|                    |");
        print(35, 18, "+--------------------+");
        print(37, 17, "LINES: " + model.getClearedLines());
    }

    private void drawGrid() throws IOException {
        print(3, 1,  "<!====================!>");
        print(3, 22, "<!====================!>");
        print(5, 23, "\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/");
        String[][] next = model.getActualBlock();
        for (int row = 4; row < model.getGridRows(); row++) {
            print(3, row - 2, "<!                    !>");
        }
        screen.refresh();
        drawNextPiece();
        int j = 0;
        int lines = 0;
        if (savedScreen[0][0] != null) {
            for (int row2 = 2; row2 < 22; row2++) {
                for (int col = 5; col < 25; col++) {
                    screen.setCharacter(new TerminalPosition(col, row2), savedScreen[row2][col]);
                    j++;
                }
            }
        }
        screen.refresh();
        int row = model.getNextRow();
        int c = model.getActualColumn();
        int r = model.getActualRow();

        if(model.pieceDimensions(next)[0] == 2) {
            if ((screen.getBackCharacter(new TerminalPosition(model.getActualColumn(), row + model.getEachColumnHeight(model.getActualBlock())[0])).getCharacter() == '=') || (screen.getBackCharacter(new TerminalPosition(model.getActualColumn(), row + model.getEachColumnHeight(model.getActualBlock())[0])).getCharacter() == '[')) {
                tg.setForegroundColor(new TextColor.RGB(0,255,255));
                for (int row2 = 0; row2 < 4; row2++) {
                    for (int col = 0; col < 8; col++) {
                        if (next[row2][col] != null) {
                            print(c, r, next[row2][col]);
                        }
                        c++;
                    }
                    r++;
                    c = model.getActualColumn();
                }
                screen.refresh();
                tg.setForegroundColor(new TextColor.RGB(153,255,153));
                for (int row2 = 2; row2 < model.getGridRows() - 2; row2++) {
                    for (int col = 5; col < model.getGridCols() + 5; col = col + 2) {
                        if (screen.getBackCharacter(new TerminalPosition(col, row2)).getCharacter() != '[') {
                            print(col, row2, ".");
                        }
                    }
                }
                screen.refresh();
                model.resetActualRow();
                for (int row2 = 2; row2 < 22; row2++) {
                    for (int col = 5; col < 25; col++) {
                        savedScreen[row2][col] = screen.getBackCharacter(new TerminalPosition(col, row2));
                    }
                    if ((screen.getBackCharacter(new TerminalPosition(5, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(7, row2)).getCharacter() == '[') &&
                            (screen.getBackCharacter(new TerminalPosition(9, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(11, row2)).getCharacter() == '[') &&
                            (screen.getBackCharacter(new TerminalPosition(13, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(15, row2)).getCharacter() == '[') &&
                            (screen.getBackCharacter(new TerminalPosition(17, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(19, row2)).getCharacter() == '[') &&
                            (screen.getBackCharacter(new TerminalPosition(21, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(23, row2)).getCharacter() == '[')) {
                        for (int row3 = 21; row3 > 1; row3--) {
                            for (int col = 5; col < 25; col++) {
                                if (row3 == 2 && col % 2 == 1) {
                                    savedScreen[row3][col] = dot;
                                } else if (row3 == 2 && col % 2 == 0) {
                                    savedScreen[row3][col] = space;
                                } else {
                                    savedScreen[row3][col] = savedScreen[row3 - 1][col];
                                }
                            }
                        }
                        lines ++;
                    }
                }
                model.getNextPieceValue();
                model.resetActualColumn();
                model.setClearedLinesAtOnce(lines);
                model.calculateScore();
                getHighestRowGrid();
            } else {
                tg.setForegroundColor(new TextColor.RGB(0,255,255));
                for (int row2 = 0; row2 < 4; row2++) {
                    for (int col = 0; col < 8; col++) {
                        if (next[row2][col] != null) {
                            print(c, r, next[row2][col]);
                        }
                        c++;
                    }
                    r++;
                    c = model.getActualColumn();
                }
                screen.refresh();
                tg.setForegroundColor(new TextColor.RGB(153,255,153));
                for (int row2 = 2; row2 < model.getGridRows() - 2; row2++) {
                    for (int col = 5; col < model.getGridCols() + 5; col = col + 2) {
                        if (screen.getBackCharacter(new TerminalPosition(col, row2)).getCharacter() != '[') {
                            print(col, row2, ".");
                        }
                    }
                }
                screen.refresh();
            }
        } else if(model.pieceDimensions(next)[0] == 4) {
            if ((screen.getBackCharacter(new TerminalPosition(model.getActualColumn(), row + model.getEachColumnHeight(model.getActualBlock())[0])).getCharacter() == '=') || (screen.getBackCharacter(new TerminalPosition(model.getActualColumn(), row + model.getEachColumnHeight(model.getActualBlock())[0])).getCharacter() == '[')
                    || (screen.getBackCharacter(new TerminalPosition(model.getActualColumn() + 2, row + model.getEachColumnHeight(model.getActualBlock())[1])).getCharacter() == '=') || (screen.getBackCharacter(new TerminalPosition(model.getActualColumn() + 2, row + model.getEachColumnHeight(model.getActualBlock())[1])).getCharacter() == '[')) {

                if(model.pieceDimensions(next)[1] == 2) {
                    tg.setForegroundColor(new TextColor.RGB(255,255,0));
                } else if(model.getEachColumnHeight(model.getActualBlock())[0] == 1  ||
                        (model.getEachColumnHeight(model.getActualBlock())[0] == 3 && model.getEachColumnHeight(model.getActualBlock())[1] == 3 && model.getActualBlock()[0][2] == null)) {
                    tg.setForegroundColor(new TextColor.RGB(255,128,0));
                } else if(model.getEachColumnHeight(model.getActualBlock())[1] == 1 ||
                        (model.getEachColumnHeight(model.getActualBlock())[0] == 3 && model.getEachColumnHeight(model.getActualBlock())[1] == 3 && model.getActualBlock()[0][2] != null)) {
                    tg.setForegroundColor(new TextColor.RGB(51,51,255));
                } else if(model.getEachColumnHeight(model.getActualBlock())[0] == 2 && model.getEachColumnHeight(model.getActualBlock())[1] == 3 && model.getActualBlock()[0][0] != null) {
                    tg.setForegroundColor(new TextColor.RGB(0,255,0));
                } else if(model.getEachColumnHeight(model.getActualBlock())[0] == 3 && model.getEachColumnHeight(model.getActualBlock())[1] == 2 && model.getActualBlock()[0][0] == null) {
                    tg.setForegroundColor(new TextColor.RGB(255,0,0));
                } else if((model.getEachColumnHeight(model.getActualBlock())[0] == 2 && model.getEachColumnHeight(model.getActualBlock())[1] == 3 && model.getActualBlock()[0][0] == null) ||
                        (model.getEachColumnHeight(model.getActualBlock())[0] == 3 && model.getEachColumnHeight(model.getActualBlock())[1] == 2 && model.getActualBlock()[0][0] != null)) {
                    tg.setForegroundColor(new TextColor.RGB(153,0,153));
                }
                for (int row2 = 0; row2 < 4; row2++) {
                    for (int col = 0; col < 8; col++) {
                        if (next[row2][col] != null) {
                            print(c, r, next[row2][col]);
                        }
                        c++;
                    }
                    r++;
                    c = model.getActualColumn();
                }
                screen.refresh();
                tg.setForegroundColor(new TextColor.RGB(153,255,153));
                for (int row2 = 2; row2 < model.getGridRows() - 2; row2++) {
                    for (int col = 5; col < model.getGridCols() + 5; col = col + 2) {
                        if (screen.getBackCharacter(new TerminalPosition(col, row2)).getCharacter() != '[') {
                            print(col, row2, ".");
                        }
                    }
                }
                screen.refresh();
                model.resetActualRow();
                for (int row2 = 0; row2 < screen.getTerminalSize().getRows(); row2++) {
                    for (int col = 0; col < screen.getTerminalSize().getColumns(); col++) {
                        savedScreen[row2][col] = screen.getBackCharacter(new TerminalPosition(col, row2));
                    }
                    if ((screen.getBackCharacter(new TerminalPosition(5, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(7, row2)).getCharacter() == '[') &&
                            (screen.getBackCharacter(new TerminalPosition(9, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(11, row2)).getCharacter() == '[') &&
                            (screen.getBackCharacter(new TerminalPosition(13, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(15, row2)).getCharacter() == '[') &&
                            (screen.getBackCharacter(new TerminalPosition(17, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(19, row2)).getCharacter() == '[') &&
                            (screen.getBackCharacter(new TerminalPosition(21, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(23, row2)).getCharacter() == '[')) {
                        for (int row3 = 21; row3 > 1; row3--) {
                            for (int col = 5; col < 25; col++) {
                                if (row3 == 2 && col % 2 == 1) {
                                    savedScreen[row3][col] = dot;
                                } else if (row3 == 2 && col % 2 == 0) {
                                    savedScreen[row3][col] = space;
                                } else {
                                    savedScreen[row3][col] = savedScreen[row3 - 1][col];
                                }
                            }
                        }
                        lines++;
                    }
                }
                model.getNextPieceValue();
                model.resetActualColumn();
                model.setClearedLinesAtOnce(lines);
                model.calculateScore();
                getHighestRowGrid();
            } else {
                if(model.pieceDimensions(next)[1] == 2) {
                    tg.setForegroundColor(new TextColor.RGB(255,255,0));
                } else if(model.getEachColumnHeight(model.getActualBlock())[0] == 1  ||
                        (model.getEachColumnHeight(model.getActualBlock())[0] == 3 && model.getEachColumnHeight(model.getActualBlock())[1] == 3 && model.getActualBlock()[0][2] == null)) {
                    tg.setForegroundColor(new TextColor.RGB(255,128,0));
                } else if(model.getEachColumnHeight(model.getActualBlock())[1] == 1 ||
                        (model.getEachColumnHeight(model.getActualBlock())[0] == 3 && model.getEachColumnHeight(model.getActualBlock())[1] == 3 && model.getActualBlock()[0][2] != null)) {
                    tg.setForegroundColor(new TextColor.RGB(51,51,255));
                } else if(model.getEachColumnHeight(model.getActualBlock())[0] == 2 && model.getEachColumnHeight(model.getActualBlock())[1] == 3 && model.getActualBlock()[0][0] != null) {
                    tg.setForegroundColor(new TextColor.RGB(0,255,0));
                } else if(model.getEachColumnHeight(model.getActualBlock())[0] == 3 && model.getEachColumnHeight(model.getActualBlock())[1] == 2 && model.getActualBlock()[0][0] == null) {
                    tg.setForegroundColor(new TextColor.RGB(255,0,0));
                } else if((model.getEachColumnHeight(model.getActualBlock())[0] == 2 && model.getEachColumnHeight(model.getActualBlock())[1] == 3 && model.getActualBlock()[0][0] == null) ||
                        (model.getEachColumnHeight(model.getActualBlock())[0] == 3 && model.getEachColumnHeight(model.getActualBlock())[1] == 2 && model.getActualBlock()[0][0] != null)) {
                    tg.setForegroundColor(new TextColor.RGB(153,0,153));
                }
                for (int row2 = 0; row2 < 4; row2++) {
                    for (int col = 0; col < 8; col++) {
                        if (next[row2][col] != null) {
                            print(c, r, next[row2][col]);
                        }
                        c++;
                    }
                    r++;
                    c = model.getActualColumn();
                }
                screen.refresh();
                tg.setForegroundColor(new TextColor.RGB(153,255,153));
                for (int row2 = 2; row2 < model.getGridRows() - 2; row2++) {
                    for (int col = 5; col < model.getGridCols() + 5; col = col + 2) {
                        if (screen.getBackCharacter(new TerminalPosition(col, row2)).getCharacter() != '[') {
                            print(col, row2, ".");
                        }
                    }
                }
                screen.refresh();
            }
        } else if(model.pieceDimensions(next)[0] == 6) {
            if ((screen.getBackCharacter(new TerminalPosition(model.getActualColumn(), row + model.getEachColumnHeight(model.getActualBlock())[0])).getCharacter() == '=') || (screen.getBackCharacter(new TerminalPosition(model.getActualColumn(), row + model.getEachColumnHeight(model.getActualBlock())[0])).getCharacter() == '[')
                    || (screen.getBackCharacter(new TerminalPosition(model.getActualColumn() + 2, row + model.getEachColumnHeight(model.getActualBlock())[1])).getCharacter() == '=') || (screen.getBackCharacter(new TerminalPosition(model.getActualColumn() + 2, row + model.getEachColumnHeight(model.getActualBlock())[1])).getCharacter() == '[')
                    || (screen.getBackCharacter(new TerminalPosition(model.getActualColumn() + 4, row + model.getEachColumnHeight(model.getActualBlock())[2])).getCharacter() == '=') || (screen.getBackCharacter(new TerminalPosition(model.getActualColumn() + 4, row + model.getEachColumnHeight(model.getActualBlock())[2])).getCharacter() == '[')) {

                if((model.getEachColumnHeight(model.getActualBlock())[0] == 2 && model.getEachColumnHeight(model.getActualBlock())[2] == 2 && model.getActualBlock()[0][0] != null) ||
                        (model.getEachColumnHeight(model.getActualBlock())[0] == 1 && model.getEachColumnHeight(model.getActualBlock())[2] == 2 && model.getActualBlock()[0][4] != null)) {
                    tg.setForegroundColor(new TextColor.RGB(51,51,255));
                } else if((model.getEachColumnHeight(model.getActualBlock())[0] == 2 && model.getEachColumnHeight(model.getActualBlock())[2] == 2 && model.getActualBlock()[0][4] != null) ||
                        (model.getEachColumnHeight(model.getActualBlock())[0] == 2 && model.getEachColumnHeight(model.getActualBlock())[2] == 1 && model.getActualBlock()[0][0] != null)){
                    tg.setForegroundColor(new TextColor.RGB(255,128,0));
                } else if((model.getEachColumnHeight(model.getActualBlock())[0] == 2 && model.getEachColumnHeight(model.getActualBlock())[2] == 2 && model.getActualBlock()[0][2] != null) ||
                        (model.getEachColumnHeight(model.getActualBlock())[0] == 1 && model.getEachColumnHeight(model.getActualBlock())[2] == 1)) {
                    tg.setForegroundColor(new TextColor.RGB(153,0,153));
                } else if(model.getEachColumnHeight(model.getActualBlock())[0] == 1) {
                    tg.setForegroundColor(new TextColor.RGB(255,0,0));
                } else if(model.getEachColumnHeight(model.getActualBlock())[2] == 1) {
                    tg.setForegroundColor(new TextColor.RGB(0,255,0));
                }
                for (int row2 = 0; row2 < 4; row2++) {
                    for (int col = 0; col < 8; col++) {
                        if (next[row2][col] != null) {
                            print(c, r, next[row2][col]);
                        }
                        c++;
                    }
                    r++;
                    c = model.getActualColumn();
                }
                screen.refresh();
                tg.setForegroundColor(new TextColor.RGB(153,255,153));
                for (int row2 = 2; row2 < model.getGridRows() - 2; row2++) {
                    for (int col = 5; col < model.getGridCols() + 5; col = col + 2) {
                        if (screen.getBackCharacter(new TerminalPosition(col, row2)).getCharacter() != '[') {
                            print(col, row2, ".");
                        }
                    }
                }
                screen.refresh();
                model.resetActualRow();
                for (int row2 = 0; row2 < screen.getTerminalSize().getRows(); row2++) {
                    for (int col = 0; col < screen.getTerminalSize().getColumns(); col++) {
                        savedScreen[row2][col] = screen.getBackCharacter(new TerminalPosition(col, row2));
                    }
                    if ((screen.getBackCharacter(new TerminalPosition(5, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(7, row2)).getCharacter() == '[') &&
                            (screen.getBackCharacter(new TerminalPosition(9, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(11, row2)).getCharacter() == '[') &&
                            (screen.getBackCharacter(new TerminalPosition(13, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(15, row2)).getCharacter() == '[') &&
                            (screen.getBackCharacter(new TerminalPosition(17, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(19, row2)).getCharacter() == '[') &&
                            (screen.getBackCharacter(new TerminalPosition(21, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(23, row2)).getCharacter() == '[')) {
                        for (int row3 = 21; row3 > 1; row3--) {
                            for (int col = 5; col < 25; col++) {
                                if (row3 == 2 && col % 2 == 1) {
                                    savedScreen[row3][col] = dot;
                                } else if (row3 == 2 && col % 2 == 0) {
                                    savedScreen[row3][col] = space;
                                } else {
                                    savedScreen[row3][col] = savedScreen[row3 - 1][col];
                                }
                            }
                        }
                        lines++;
                    }
                }
                model.getNextPieceValue();
                model.resetActualColumn();
                model.setClearedLinesAtOnce(lines);
                model.calculateScore();
                getHighestRowGrid();
            } else {
                if((model.getEachColumnHeight(model.getActualBlock())[0] == 2 && model.getEachColumnHeight(model.getActualBlock())[2] == 2 && model.getActualBlock()[0][0] != null) ||
                        (model.getEachColumnHeight(model.getActualBlock())[0] == 1 && model.getEachColumnHeight(model.getActualBlock())[2] == 2 && model.getActualBlock()[0][4] != null)) {
                    tg.setForegroundColor(new TextColor.RGB(51,51,255));
                } else if((model.getEachColumnHeight(model.getActualBlock())[0] == 2 && model.getEachColumnHeight(model.getActualBlock())[2] == 2 && model.getActualBlock()[0][4] != null) ||
                        (model.getEachColumnHeight(model.getActualBlock())[0] == 2 && model.getEachColumnHeight(model.getActualBlock())[2] == 1 && model.getActualBlock()[0][0] != null)){
                    tg.setForegroundColor(new TextColor.RGB(255,128,0));
                } else if((model.getEachColumnHeight(model.getActualBlock())[0] == 2 && model.getEachColumnHeight(model.getActualBlock())[2] == 2 && model.getActualBlock()[0][2] != null) ||
                        (model.getEachColumnHeight(model.getActualBlock())[0] == 1 && model.getEachColumnHeight(model.getActualBlock())[2] == 1)) {
                    tg.setForegroundColor(new TextColor.RGB(153,0,153));
                } else if(model.getEachColumnHeight(model.getActualBlock())[0] == 1) {
                    tg.setForegroundColor(new TextColor.RGB(255,0,0));
                } else if(model.getEachColumnHeight(model.getActualBlock())[2] == 1) {
                    tg.setForegroundColor(new TextColor.RGB(0,255,0));
                }
                for (int row2 = 0; row2 < 4; row2++) {
                    for (int col = 0; col < 8; col++) {
                        if (next[row2][col] != null) {
                            print(c, r, next[row2][col]);
                        }
                        c++;
                    }
                    r++;
                    c = model.getActualColumn();
                }
                screen.refresh();
                tg.setForegroundColor(new TextColor.RGB(153,255,153));
                for (int row2 = 2; row2 < model.getGridRows() - 2; row2++) {
                    for (int col = 5; col < model.getGridCols() + 5; col = col + 2) {
                        if (screen.getBackCharacter(new TerminalPosition(col, row2)).getCharacter() != '[') {
                            print(col, row2, ".");
                        }
                    }
                }
                screen.refresh();
            }
        } else if(model.pieceDimensions(next)[0] == 8) {
            if ((screen.getBackCharacter(new TerminalPosition(model.getActualColumn(), row + model.getEachColumnHeight(model.getActualBlock())[0])).getCharacter() == '=') || (screen.getBackCharacter(new TerminalPosition(model.getActualColumn(), row + model.getEachColumnHeight(model.getActualBlock())[0])).getCharacter() == '[')
                    || (screen.getBackCharacter(new TerminalPosition(model.getActualColumn() + 2, row + model.getEachColumnHeight(model.getActualBlock())[1])).getCharacter() == '=') || (screen.getBackCharacter(new TerminalPosition(model.getActualColumn() + 2, row + model.getEachColumnHeight(model.getActualBlock())[1])).getCharacter() == '[')
                    || (screen.getBackCharacter(new TerminalPosition(model.getActualColumn() + 4, row + model.getEachColumnHeight(model.getActualBlock())[2])).getCharacter() == '=') || (screen.getBackCharacter(new TerminalPosition(model.getActualColumn() + 4, row + model.getEachColumnHeight(model.getActualBlock())[2])).getCharacter() == '[')
                    || (screen.getBackCharacter(new TerminalPosition(model.getActualColumn() + 6, row + model.getEachColumnHeight(model.getActualBlock())[3])).getCharacter() == '=') || (screen.getBackCharacter(new TerminalPosition(model.getActualColumn() + 6, row + model.getEachColumnHeight(model.getActualBlock())[3])).getCharacter() == '[')) {
                tg.setForegroundColor(new TextColor.RGB(0,255,255));
                for (int row2 = 0; row2 < 4; row2++) {
                    for (int col = 0; col < 8; col++) {
                        if (next[row2][col] != null) {
                            print(c, r, next[row2][col]);
                        }
                        c++;
                    }
                    r++;
                    c = model.getActualColumn();
                }
                screen.refresh();
                tg.setForegroundColor(new TextColor.RGB(153,255,153));
                for (int row2 = 2; row2 < model.getGridRows() - 2; row2++) {
                    for (int col = 5; col < model.getGridCols() + 5; col = col + 2) {
                        if (screen.getBackCharacter(new TerminalPosition(col, row2)).getCharacter() != '[') {
                            print(col, row2, ".");
                        }
                    }
                }
                screen.refresh();
                model.resetActualRow();
                for (int row2 = 0; row2 < screen.getTerminalSize().getRows(); row2++) {
                    for (int col = 0; col < screen.getTerminalSize().getColumns(); col++) {
                        savedScreen[row2][col] = screen.getBackCharacter(new TerminalPosition(col, row2));
                    }
                    if ((screen.getBackCharacter(new TerminalPosition(5, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(7, row2)).getCharacter() == '[') &&
                            (screen.getBackCharacter(new TerminalPosition(9, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(11, row2)).getCharacter() == '[') &&
                            (screen.getBackCharacter(new TerminalPosition(13, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(15, row2)).getCharacter() == '[') &&
                            (screen.getBackCharacter(new TerminalPosition(17, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(19, row2)).getCharacter() == '[') &&
                            (screen.getBackCharacter(new TerminalPosition(21, row2)).getCharacter() == '[') && (screen.getBackCharacter(new TerminalPosition(23, row2)).getCharacter() == '[')) {
                        for (int row3 = 21; row3 > 1; row3--) {
                            for (int col = 5; col < 25; col++) {
                                if (row3 == 2 && col % 2 == 1) {
                                    savedScreen[row3][col] = dot;
                                } else if (row3 == 2 && col % 2 == 0) {
                                    savedScreen[row3][col] = space;
                                } else {
                                    savedScreen[row3][col] = savedScreen[row3 - 1][col];
                                }
                            }
                        }
                        lines++;
                    }
                }
                model.getNextPieceValue();
                model.resetActualColumn();
                model.setClearedLinesAtOnce(lines);
                model.calculateScore();
                getHighestRowGrid();
            } else {
                tg.setForegroundColor(new TextColor.RGB(0,255,255));
                for (int row2 = 0; row2 < 4; row2++) {
                    for (int col = 0; col < 8; col++) {
                        if (next[row2][col] != null) {
                            print(c, r, next[row2][col]);
                        }
                        c++;
                    }
                    r++;
                    c = model.getActualColumn();
                }
                screen.refresh();
                tg.setForegroundColor(new TextColor.RGB(153,255,153));
                for (int row2 = 2; row2 < model.getGridRows() - 2; row2++) {
                    for (int col = 5; col < model.getGridCols() + 5; col = col + 2) {
                        if (screen.getBackCharacter(new TerminalPosition(col, row2)).getCharacter() != '[') {
                            print(col, row2, ".");
                        }
                    }
                }
                screen.refresh();
            }
        }
    }

    private void drawNextPiece() {
        print(35, 5,  "+--------------------+");
        print(35, 6,  "|                    |");
        print(35, 7,  "| NEXT:              |");
        print(35, 8,  "|                    |");
        print(35, 9,  "|                    |");
        print(35, 10, "+--------------------+");

        int c = 44;
        int r = 7;

        if(model.pieceDimensions(model.getNextBlock())[0] == 2){
            tg.setForegroundColor(new TextColor.RGB(0,255,255));
        } else if(model.pieceDimensions(model.getNextBlock())[0] == 4){
            tg.setForegroundColor(new TextColor.RGB(255, 255, 0));
        } else if(model.pieceDimensions(model.getNextBlock())[0] == 6){
            if(model.getEachColumnHeight(model.getNextBlock())[0] == 1 && model.getEachColumnHeight(model.getNextBlock())[2] == 2 && model.getNextBlock()[0][4] != null) {
                tg.setForegroundColor(new TextColor.RGB(51,51,255));
            } else if(model.getEachColumnHeight(model.getNextBlock())[0] == 2 && model.getEachColumnHeight(model.getNextBlock())[2] == 1 && model.getNextBlock()[0][0] != null) {
                tg.setForegroundColor(new TextColor.RGB(255,128,0));
            } else if(model.getEachColumnHeight(model.getNextBlock())[0] == 1 && model.getEachColumnHeight(model.getNextBlock())[2] == 1) {
                tg.setForegroundColor(new TextColor.RGB(153,0,153));
            } else if(model.getEachColumnHeight(model.getNextBlock())[0] == 1) {
                tg.setForegroundColor(new TextColor.RGB(255,0,0));
            } else if(model.getEachColumnHeight(model.getNextBlock())[2] == 1) {
                tg.setForegroundColor(new TextColor.RGB(0,255,0));
            }
        } else if(model.pieceDimensions(model.getNextBlock())[0] == 8){
            tg.setForegroundColor(new TextColor.RGB(0,255,255));
        }

        for (int row2 = 0; row2 < 4; row2++) {
            for (int col = 0; col < 8; col++) {
                if (model.getNextBlock()[row2][col] != null) {
                    print(c, r, model.getNextBlock()[row2][col]);
                }
                c++;
            }
            r++;
            c = 44;
        }
        tg.setForegroundColor(new TextColor.RGB(153,255,153));
    }

    public void drawGameOver() {
        print(0, 1, "          [][][][][][][][][][][][][][][][][][][][][][][][]          ");
        print(0, 2, "          []                                            []          ");
        print(0, 3, "          []                                            []          ");
        print(0, 4, "          /\\__  _\\/\\  ___\\/\\__  _\\/\\  == \\ /\\ \\ /\\  ___\\[]          ");
        print(0, 5, "          \\/_/\\ \\/\\ \\  ___\\/_/\\ \\/\\ \\  __<_\\ \\ \\. \\___  \\]          ");
        print(0, 6, "          [] \\ \\_\\ \\ \\_____\\ \\ \\_\\ \\ \\_\\_\\_\\. \\_\\./\\_____\\          ");
        print(0, 7, "          []  \\/_/  \\/_____/  \\/_/  \\/_/ /_/ \\/_/ \\/_____/          ");
        print(0, 8, "          []                                            []          ");
        print(0, 9, "          []                                            []          ");
        print(0, 10, "          []                                            []          ");
        print(0, 11, "          []                  GAME OVER                 []          ");
        print(0, 12, "          []                                            []          ");
        print(0, 13, "          []                                            []          ");
        print(0, 14, "          []                                            []          ");
        print(0, 15, "          []   Press ENTER to play again. ESC to quit.  []          ");
        print(0, 16, "          []                                            []          ");
        print(0, 17, "          []                                            []          ");
        print(0, 18, "          []                                            []          ");
        print(0, 19, "          []                                            []          ");
        print(0, 20, "          []                Skrzymo 2018                []          ");
        print(0, 21, "          [][][][][][][][][][][][][][][][][][][][][][][][]          ");
        print(24,13, "YOUR SCORE:    " + model.getScore());
    }



    public void keyPressed() throws IOException {
        KeyStroke keyPressed = terminal.pollInput();
        if(keyPressed != null) {
            switch (keyPressed.getKeyType()) {
                case Escape:
                    if (model.isStopped() || model.isGameOver()) {
                        stop();
                        close();
                        System.exit(0);
                    } else {
                        model.setStopped(true);
                        stop();
                    }
                    break;
                case Enter:
                    if(model.isStopped() || model.isGameOver()) {
                        model.setStopped(false);
                        model.setGameOver(false);
                        model.start();
                        savedScreen[0][0] = null;
                        loop();
                        play();
                    }
                    break;
                case ArrowRight:
                    model.moveRight();
                    break;
                case ArrowLeft:
                    model.moveLeft();
                    break;
                case ArrowDown:
                    model.down(getHighestPoint(model.getActualColumn()));
                    break;
                case ArrowUp:
                    model.rotate();
                    break;
                case Backspace:
                    if(!model.isStopped()) {
                        if (clip.isRunning()) {
                            stop();
                        } else {
                            loop();
                            play();
                        }
                    }
                    break;
            }
        }
        screen.refresh();
    }

    public int getHighestPoint(int actualColumn) {
        int h = model.pieceDimensions(model.getActualBlock())[1];
        int w = model.pieceDimensions(model.getActualBlock())[0];
        int col = 4;
        int highest = 0;
        for(int row = model.getActualRow() + h; row < model.getGridRows() - 2; row++) {
            if(w == 2) {
                if(screen.getBackCharacter(new TerminalPosition(actualColumn,row)).getCharacter() == '[') {
                    model.setScore(model.getScore() + (row - model.getActualRow() - h + 2));
                    model.calculateScoreForLevel(row - model.getActualRow() - h + 2);
                    return row - h - 1;
                }
            } else if(w == 4) {
                if(screen.getBackCharacter(new TerminalPosition(actualColumn,row)).getCharacter() == '[') {
                    col = 0;
                    highest = model.getEachColumnHeight(model.getActualBlock())[col];
                }
                if(screen.getBackCharacter(new TerminalPosition(actualColumn + 2,row)).getCharacter() == '[') {
                    if(col == 4 || model.getEachColumnHeight(model.getActualBlock())[1] > highest) {
                        col = 1;
                        highest = model.getEachColumnHeight(model.getActualBlock())[col];
                    }
                }
                if(col != 4) {
                    model.setScore(model.getScore() + (row - model.getActualRow() - h + 2));
                    model.calculateScoreForLevel(row - model.getActualRow() - h + 2);
                    return row - highest - 1;
                }
            } else if(w == 6) {
                if(screen.getBackCharacter(new TerminalPosition(actualColumn,row)).getCharacter() == '[') {
                    col = 0;
                    highest = model.getEachColumnHeight(model.getActualBlock())[col];
                }
                if(screen.getBackCharacter(new TerminalPosition(actualColumn + 2,row)).getCharacter() == '[') {
                    if(col == 4 || model.getEachColumnHeight(model.getActualBlock())[1] > highest) {
                        col = 1;
                        highest = model.getEachColumnHeight(model.getActualBlock())[col];
                    }
                }
                if(screen.getBackCharacter(new TerminalPosition(actualColumn + 4,row)).getCharacter() == '[') {
                    if(col == 4 || model.getEachColumnHeight(model.getActualBlock())[2] > highest) {
                        col = 2;
                        highest = model.getEachColumnHeight(model.getActualBlock())[col];

                    }
                }
                if(col != 4) {
                    model.setScore(model.getScore() + (row - model.getActualRow() - h + 2));
                    model.calculateScoreForLevel(row - model.getActualRow() - h + 2);
                    return row - highest - 1;
                }
            } else if(w == 8) {
                if((screen.getBackCharacter(new TerminalPosition(actualColumn,row)).getCharacter() == '[') || (screen.getBackCharacter(new TerminalPosition(actualColumn + 2,row)).getCharacter() == '[')
                        || (screen.getBackCharacter(new TerminalPosition(actualColumn + 4,row)).getCharacter() == '[') || (screen.getBackCharacter(new TerminalPosition(actualColumn + 6,row)).getCharacter() == '[')) {
                    model.setScore(model.getScore() + (row - model.getActualRow() - h + 2));
                    model.calculateScoreForLevel(row - model.getActualRow() - h + 2);
                    return row - h - 1;
                }
            }
        }
        model.setScore(model.getScore() + (21 - model.getActualRow() - h + 2));
        model.calculateScoreForLevel(21 - model.getActualRow() - h + 2);
        return 21 - h;
    }

    public void getHighestRowGrid() {
        for(int col = 5; col < 25; col += 2) {
            if(screen.getBackCharacter(new TerminalPosition(col,2)).getCharacter() == '[') {
                model.setGameOver(true);
                stop();
            }
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

    public void close() {
        clip.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        TetrisView view = new TetrisView();


        view.terminal.enterPrivateMode();
        view.tg.setForegroundColor(new TextColor.RGB(153,255,153));
        view.screen.startScreen();
        view.screen.refresh();
        while (view.keepRunning){
            view.draw();
            view.screen.refresh();
            if(view.model.getScoreLevel() >= 1000) {
                view.model.increaseSpeed();
                view.model.increaseLevel();
                view.model.setScoreLevel(view.model.getScoreLevel() % 1000);
            }
            sleep(view.model.getSpeed());
            view.keyPressed();
        }

        view.terminal.exitPrivateMode();
    }
}
