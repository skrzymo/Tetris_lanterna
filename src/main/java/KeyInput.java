import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

    private static final int NUM_KEYS = 256;

    private static final boolean[] keys = new boolean[NUM_KEYS];
    private static final boolean[] lastKeys = new boolean[NUM_KEYS];

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    public static void update() {
        for(int i = 0; i < NUM_KEYS; i++) {
            lastKeys[i] = keys[i];
        }
    }

    public static void clear() {
        for(int i = 0; i < NUM_KEYS; i++) {
            keys[i] = false;
        }
    }

    public static boolean isDown(int keyCode) {
        return keys[keyCode];
    }

    public static boolean wasPressed(int keyCode) {
        return isDown(keyCode) && !lastKeys[keyCode];
    }
}
