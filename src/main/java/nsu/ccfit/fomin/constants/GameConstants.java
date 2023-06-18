package nsu.ccfit.fomin.constants;

import java.util.HashMap;

public class GameConstants {
    public static final String IGNORE_PARAMETERS = "i";
    public static final String EMPTY_TABLE = "Table is empty";
    public static final String EMPTY_STRING = "";

    private static final String GODLIKE = "Godlike";
    private static final String DIVINE = "Divine";
    private static final String CHAMPION = "Champion";
    private static final String KNIGHT = "Knight";
    private static final String Loon = "Loon";

    private static final HashMap<Integer, String> rankMap;

    static {
        rankMap = new HashMap<>();
        rankMap.put(1, GODLIKE);
        rankMap.put(2, DIVINE);
        rankMap.put(3, CHAMPION);
        rankMap.put(4, KNIGHT);
        rankMap.put(5, Loon);
    }

    public static String getPlayerRank(int placeInScoreTable) {
        return rankMap.get(placeInScoreTable);
    }
}