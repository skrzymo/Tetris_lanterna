import java.awt.*;

public class Blocks {

    final static Color[] tilesColor = {new Color(51,51,255), new Color(255,128,0), new Color(0,255,255), new Color(255,255,0),
                                        new Color(0,255,0), new Color(255,0,0), new Color(153,0,153), new Color(255,255,255)};
    final static Color[] tilesColorBorder = {new Color(0,0,153), new Color(153,76,0), new Color(0,153,153), new Color(153,153,0),
                                            new Color(0,153,0), new Color(153,0,0), new Color(51, 0, 51), new Color(64,64,64)};

    final static int TILE_SIZE = 24;

    final static boolean[][][] BLOCKS =
            {
                    {},
                    {
                            {false, false, false, false},
                            {true , true , true , false},
                            {false, false, true , false},
                            {false, false, false, false}
                    },
                    {
                            {false, false, false, false},
                            {true , true , true , false},
                            {true , false, false, false},
                            {false, false, false, false}
                    },
                    {
                            {false, false, false, false},
                            {true , true , true , true },
                            {false, false, false, false},
                            {false, false, false, false}
                    },
                    {
                            {false, false, false, false},
                            {false, true , true , false},
                            {false, true , true , false},
                            {false, false, false, false}
                    },
                    {
                            {false, false, false, false},
                            {false, true , true , false},
                            {true , true , false, false},
                            {false, false, false, false}
                    },
                    {
                            {false, false, false, false},
                            {true , true , false, false},
                            {false, true , true , false},
                            {false, false, false, false}
                    },
                    {
                            {false, false, false, false},
                            {true , true , true , false},
                            {false, true , false, false},
                            {false, false, false, false}
                    }
            };
}


