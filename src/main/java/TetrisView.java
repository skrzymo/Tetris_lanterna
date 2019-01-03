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
import java.awt.*;
import java.io.*;

import static java.lang.Thread.sleep;

public class TetrisView extends Canvas {

    private TetrisModel model;
    private Terminal terminal;
    private Screen screen;
    private TextGraphics tg;
    private boolean keepRunning;
    private TextCharacter[][] savedScreen;
    private TextCharacter dot;
    private TextCharacter space;

    private File fileScore;
    private int highScore;


    private File file;
    private AudioInputStream sound;
    private Clip clip;
    private boolean musicPlay;

    public TetrisView() throws IOException {
        this.model = new TetrisModel();
        this.terminal = new DefaultTerminalFactory().createTerminal();
        this.screen = new TerminalScreen(this.terminal);
        this.tg = this.screen.newTextGraphics();
        this.keepRunning = true;
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

        this.highScore = 0;
        this.fileScore = new File("E:/Projekty/IntelliJ/Tetris/src/main/resources/Scores.txt");
    }

    private void clear() {
        screen.clear();
    }

    private void print(int x, int y, String c) {
        tg.putString(x,y,c);
    }

    public void draw() throws IOException {
        clear();
        if (!model.isPaused()) {
            if(!model.isGameOver()) {
                if (!model.isStopped()) {
                    print(32, 21, "Press ENTER to PAUSE");
                    print(32, 22, "Press BACKSPACE to SOUND ON/OFF");
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
        } else {
            drawPaused();
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
        print(0, 13, "          []             Press ENTER to play.           []          ");
        print(0, 14, "          []         ESC to go back to main menu.       []          ");
        print(0, 15, "          []                                            []          ");
        print(0, 16, "          []                                            []          ");
        print(0, 17, "          []                                            []          ");
        print(0, 18, "          []                                            []          ");
        print(0, 19, "          []                                            []          ");
        print(0, 20, "          []                Skrzymo 2018                []          ");
        print(0, 21, "          [][][][][][][][][][][][][][][][][][][][][][][][]          ");
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
                yourFile.createNewFile(); // if file already exists will do nothing
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                FileOutputStream oFile = new FileOutputStream(yourFile, false);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        print(27, 10, "HIGH SCORE:  " + highScore);
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
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileScore));
            String line = reader.readLine();
            while (line != null)
            {
                try {
                    highScore = Integer.parseInt(line.trim());
                    if (model.getScore() > highScore)
                    {
                        highScore = model.getScore();
                    }
                } catch (NumberFormatException e1) {
                }
                line = reader.readLine();
            }
            reader.close();

        } catch (IOException ex) {
            System.err.println("ERROR reading scores from file");
        }
        System.out.println(highScore);
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(fileScore));
            output.write(Integer.toString(highScore));
            output.close();

        } catch (IOException ex1) {
            System.out.printf("ERROR writing score to file: %s\n", ex1);
        }
        print(0, 1, "          [][][][][][][][][][][][][][][][][][][][][][][][]          ");
        print(0, 2, "          []                                            []          ");
        print(0, 3, "          []                                            []          ");
        print(0, 4, "          /\\__  _\\/\\  ___\\/\\__  _\\/\\  == \\ /\\ \\ /\\  ___\\[]          ");
        print(0, 5, "          \\/_/\\ \\/\\ \\  ___\\/_/\\ \\/\\ \\  __<_\\ \\ \\. \\___  \\]          ");
        print(0, 6, "          [] \\ \\_\\ \\ \\_____\\ \\ \\_\\ \\ \\_\\_\\_\\. \\_\\./\\_____\\          ");
        print(0, 7, "          []  \\/_/  \\/_____/  \\/_/  \\/_/ /_/ \\/_/ \\/_____/          ");
        print(0, 8, "          []                                            []          ");
        print(0, 9, "          []                   GAME OVER                []          ");
        print(0, 10, "          []                                            []          ");
        print(0, 11, "          []                                            []          ");
        print(0, 12, "          []                                            []          ");
        print(0, 13, "          []                                            []          ");
        print(0, 14, "          []                                            []          ");
        print(0, 15, "          []          Press ENTER to play again.        []          ");
        print(0, 16, "          []         ESC to go back to main menu.       []          ");
        print(0, 17, "          []                                            []          ");
        print(0, 18, "          []                                            []          ");
        print(0, 19, "          []                                            []          ");
        print(0, 20, "          []                Skrzymo 2018                []          ");
        print(0, 21, "          [][][][][][][][][][][][][][][][][][][][][][][][]          ");
        print(24,11, "YOUR SCORE:    " + model.getScore());
        print(24,13, "HIGH SCORE:    " + highScore);
    }

    public void drawPaused() throws IOException {
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
        print(0, 11, "          []                   PAUSED                   []          ");
        print(0, 12, "          []                                            []          ");
        print(0, 13, "          []                                            []          ");
        print(0, 14, "          []                                            []          ");
        print(0, 15, "          []            Press ENTER to resume.          []          ");
        print(0, 16, "          []                                            []          ");
        print(0, 17, "          []                                            []          ");
        print(0, 18, "          []                                            []          ");
        print(0, 19, "          []                                            []          ");
        print(0, 20, "          []                Skrzymo 2018                []          ");
        print(0, 21, "          [][][][][][][][][][][][][][][][][][][][][][][][]          ");
        screen.refresh();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        print(0, 13, "          []                                            []          ");
        print(0, 14, "          []                                            []          ");
        print(0, 15, "          []            Press ENTER to resume.          []          ");
        print(0, 16, "          []                                            []          ");
        print(0, 17, "          []                                            []          ");
        print(0, 18, "          []                                            []          ");
        print(0, 19, "          []                                            []          ");
        print(0, 20, "          []                Skrzymo 2018                []          ");
        print(0, 21, "          [][][][][][][][][][][][][][][][][][][][][][][][]          ");
    }

    public void keyPressed() throws IOException {
        KeyStroke keyPressed = terminal.pollInput();
        if(keyPressed != null) {
            switch (keyPressed.getKeyType()) {
                case Escape:
                    if(model.isPaused()) {
                        break;
                    }
                    if(model.isStopped() || model.isGameOver()) {
                        keepRunning = false;
                    } else {
                        model.setStopped(true);
                        stop();
                    }
                    break;
                case Enter:
                    if(!model.isStopped() && !model.isPaused()) {
                        model.setPaused(true);
                        stop();
                    } else if(!model.isStopped() && model.isPaused()) {
                        model.setPaused(false);
                        if(musicPlay) {
                            loop();
                            play();
                        }
                    }

                    if(model.isStopped() || model.isGameOver()) {
                        model.setStopped(false);
                        model.setGameOver(false);
                        model.setPaused(false);
                        model.start();
                        savedScreen[0][0] = null;
                        loop();
                        play();
                        setMusicPlay(true);
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
                            setMusicPlay(false);
                        } else {
                            loop();
                            play();
                            setMusicPlay(true);
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

    public void setMusicPlay(boolean musicPlay) {
        this.musicPlay = musicPlay;
    }

    public void run() throws IOException, InterruptedException {
        this.terminal.enterPrivateMode();
        System.out.println(this.terminal.getTerminalSize());
        this.tg.setForegroundColor(new TextColor.RGB(153,255,153));
        this.screen.startScreen();
        this.screen.refresh();
        while (keepRunning){
            draw();
            this.screen.refresh();
            if(this.model.getScoreLevel() >= 1000) {
                this.model.increaseSpeed();
                this.model.increaseLevel();
                this.model.setScoreLevel(this.model.getScoreLevel() % 1000);
            }
            sleep(this.model.getSpeed());
            keyPressed();
        }
        Menu menu = new Menu();
        menu.running();
        terminal.close();
        this.terminal.exitPrivateMode();
        //System.exit(0);
    }
}
