public class Cube {

    boolean[][] tab = new boolean[4][4];
    private boolean[][] tabRotate = new boolean[4][4];
    int actualBlock;

    Cube() {

        setCube(TetrisGUI.side.getNextBlock());
    }

    public void setCube(int b) {
        actualBlock = b;
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                tab[j][i] = Blocks.BLOCKS[actualBlock][i][j];
            }
        }
    }

    public void rotate() {
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                tabRotate[i][j] = tab[i][j];
            }
        }
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                tab[3 - j][i] = tabRotate[i][j];
            }
        }
    }
}
